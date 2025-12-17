package io.xeros.content.instances.aoe;

import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spawns and maintains AOE NPC groups inside an {@link AoeInstance}.
 */
public final class AoeNpcSpawner {

    private static final Logger logger = LoggerFactory.getLogger(AoeNpcSpawner.class);

    public static final class GroupRuntime {
        final int npcId;
        final Rect box;
        final int targetCount;
        final int respawnTicks;
        final int agroRadius;
        final int wanderRadius;
        final Map<Integer, Integer> aliveNpcIdxToSlot = new ConcurrentHashMap<>();
        final boolean[] slotAlive;

        GroupRuntime(int npcId, Rect box, int targetCount, int respawnTicks, int agroRadius, int wanderRadius) {
            this.npcId = npcId;
            this.box = box;
            this.targetCount = targetCount;
            this.respawnTicks = Math.max(1, respawnTicks);
            this.agroRadius = Math.max(1, agroRadius);
            this.wanderRadius = Math.max(0, wanderRadius);
            this.slotAlive = new boolean[targetCount];
        }
    }

    public static final class Rect {
        final int x1;
        final int y1;
        final int x2;
        final int y2;

        Rect(int x1, int y1, int x2, int y2) {
            this.x1 = Math.min(x1, x2);
            this.y1 = Math.min(y1, y2);
            this.x2 = Math.max(x1, x2);
            this.y2 = Math.max(y1, y2);
        }

        int randX() {
            return Misc.random(x1, x2);
        }

        int randY() {
            return Misc.random(y1, y2);
        }
    }

    private static final Map<UUID, List<GroupRuntime>> INSTANCE_GROUPS = new ConcurrentHashMap<>();
    private static final Map<Integer, UUID> NPC_INDEX_TO_INSTANCE = new ConcurrentHashMap<>();
    private static final Map<Integer, GroupRuntime> NPC_INDEX_TO_GROUP = new ConcurrentHashMap<>();
    private static final Set<UUID> FORCE_AGGRO = ConcurrentHashMap.newKeySet();

    private AoeNpcSpawner() {
    }

    public static void spawnForInstance(AoeInstance inst, AoeZoneMapDef mapDef) {
        if (inst == null || mapDef == null) {
            return;
        }
        List<AoeZoneMapDef.Group> groups = mapDef.getGroups();
        if (groups.isEmpty()) {
            logger.info("[AOE-NPC] No npc groups configured for instance={} tier={}", inst.getId(), inst.getTier().getTier());
            return;
        }

        List<GroupRuntime> runtimes = new ArrayList<>(groups.size());
        for (AoeZoneMapDef.Group group : groups) {
            AoeZoneMapDef.Group.Box box = group.getBox();
            if (box == null) {
                logger.warn("[AOE-NPC] Missing box definition for npcId={} map={} inst={}", group.getNpcId(), mapDef.getId(), inst.getId());
                continue;
            }
            GroupRuntime runtime = new GroupRuntime(
                    group.getNpcId(),
                    new Rect(box.getX1(), box.getY1(), box.getX2(), box.getY2()),
                    group.getCount(),
                    group.getRespawnTicks(),
                    group.getAgroRadius(),
                    group.getWanderRadius()
            );
            runtimes.add(runtime);
        }

        if (runtimes.isEmpty()) {
            logger.warn("[AOE-NPC] No valid npc groups to spawn for inst={}", inst.getId());
            return;
        }

        INSTANCE_GROUPS.put(inst.getId(), runtimes);

        for (GroupRuntime runtime : runtimes) {
            for (int slot = 0; slot < runtime.targetCount; slot++) {
                spawnOne(inst, runtime, slot);
            }
        }

        CycleEventHandler.getSingleton().addEvent(inst, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                maintainGroups(inst);
                driveAggression(inst);
            }
        }, 4);
    }

    public static void despawnForInstance(AoeInstance inst) {
        if (inst == null) {
            return;
        }
        List<GroupRuntime> groups = INSTANCE_GROUPS.remove(inst.getId());
        CycleEventHandler.getSingleton().stopEvents(inst);
        FORCE_AGGRO.remove(inst.getId());
        if (groups == null) {
            return;
        }
        for (GroupRuntime runtime : groups) {
            for (Integer npcIndex : new ArrayList<>(runtime.aliveNpcIdxToSlot.keySet())) {
                removeNpcReference(npcIndex, true);
            }
            runtime.aliveNpcIdxToSlot.clear();
            Arrays.fill(runtime.slotAlive, false);
        }
    }

    private static void spawnOne(AoeInstance inst, GroupRuntime runtime, int slot) {
        if (runtime == null) {
            return;
        }
        int x = runtime.box.randX();
        int y = runtime.box.randY();
        Player owner = null;
        if (inst.getOwnerPid() >= 0 && inst.getOwnerPid() < PlayerHandler.players.length) {
            owner = PlayerHandler.players[inst.getOwnerPid()];
        }
        NPC npc = owner != null
                ? NPCSpawning.spawnNpc(owner, runtime.npcId, x, y, inst.getZ(), 1, 0, false, false)
                : NPCSpawning.spawnNpc(runtime.npcId, x, y, inst.getZ(), 1, 0);
        if (npc == null) {
            runtime.slotAlive[slot] = false;
            logger.warn("[AOE-NPC] Failed to spawn npcId={} inst={} slot={} at ({},{},{})", runtime.npcId, inst.getId(), slot, x, y, inst.getZ());
            return;
        }

        npc.randomWalk = runtime.wanderRadius > 0;
        npc.walkingType = runtime.wanderRadius > 0 ? 1 : 0;
        npc.makeX = x;
        npc.makeY = y;
        npc.getBehaviour().setAggressive(true);

        runtime.slotAlive[slot] = true;
        runtime.aliveNpcIdxToSlot.put(npc.getIndex(), slot);
        NPC_INDEX_TO_INSTANCE.put(npc.getIndex(), inst.getId());
        NPC_INDEX_TO_GROUP.put(npc.getIndex(), runtime);
        logger.info("[AOE-NPC] Spawned npc id={} idx={} inst={} slot={} ({},{},{})", runtime.npcId, npc.getIndex(), inst.getId(), slot, x, y, inst.getZ());
    }

    private static void maintainGroups(AoeInstance inst) {
        List<GroupRuntime> groups = INSTANCE_GROUPS.get(inst.getId());
        if (groups == null) {
            return;
        }
        for (GroupRuntime runtime : groups) {
            runtime.aliveNpcIdxToSlot.entrySet().removeIf(entry -> {
                int npcIndex = entry.getKey();
                NPC npc = getNpcByIndex(npcIndex);
                if (npc == null || npc.isDeadOrDying() || npc.isUnregister()) {
                    runtime.slotAlive[entry.getValue()] = false;
                    removeNpcReference(npcIndex, false);
                    return true;
                }
                return false;
            });

            for (int slot = 0; slot < runtime.targetCount; slot++) {
                if (!runtime.slotAlive[slot]) {
                    scheduleRespawn(inst, runtime, slot);
                }
            }
        }
    }

    private static void scheduleRespawn(AoeInstance inst, GroupRuntime runtime, int slot) {
        runtime.slotAlive[slot] = true;
        CycleEventHandler.getSingleton().addEvent(inst, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                spawnOne(inst, runtime, slot);
                if (!runtime.aliveNpcIdxToSlot.containsValue(slot)) {
                    runtime.slotAlive[slot] = false;
                }
                container.stop();
            }
        }, runtime.respawnTicks);
    }

    private static void driveAggression(AoeInstance inst) {
        List<Player> players = inst.playersPresent();
        if (players.isEmpty()) {
            return;
        }
        List<GroupRuntime> groups = INSTANCE_GROUPS.get(inst.getId());
        if (groups == null) {
            return;
        }
        boolean forceAggro = FORCE_AGGRO.contains(inst.getId());
        for (GroupRuntime runtime : groups) {
            for (Integer npcIndex : runtime.aliveNpcIdxToSlot.keySet()) {
                NPC npc = getNpcByIndex(npcIndex);
                if (npc == null || npc.isDead() || npc.isUnregister()) {
                    continue;
                }
                int radius = forceAggro ? Math.max(runtime.agroRadius, 15) : runtime.agroRadius;
                Player best = null;
                int bestDistance = Integer.MAX_VALUE;
                for (Player player : players) {
                    int distance = Math.abs(player.absX - npc.absX) + Math.abs(player.absY - npc.absY);
                    if (distance <= radius && distance < bestDistance) {
                        best = player;
                        bestDistance = distance;
                    }
                }
                if (best == null) {
                    continue;
                }
                npc.getBehaviour().setAggressive(true);
                npc.underAttack = true;
                npc.underAttackBy = best.getIndex();
                npc.setPlayerAttackingIndex(best.getIndex());
                npc.facePlayer(best.getIndex());
                if (npc.attackTimer <= 0) {
                    Server.npcHandler.attackPlayer(best, npc);
                }
            }
        }
    }

    public static void onNpcDeath(NPC npc) {
        if (npc == null) {
            return;
        }
        Integer slot = null;
        UUID instId = NPC_INDEX_TO_INSTANCE.remove(npc.getIndex());
        GroupRuntime runtime = NPC_INDEX_TO_GROUP.remove(npc.getIndex());
        if (instId == null || runtime == null) {
            return;
        }
        slot = runtime.aliveNpcIdxToSlot.remove(npc.getIndex());
        if (slot != null) {
            runtime.slotAlive[slot] = false;
        }
    }

    public static List<String> debugCounts(AoeInstance inst) {
        List<GroupRuntime> groups = INSTANCE_GROUPS.get(inst.getId());
        if (groups == null || groups.isEmpty()) {
            return Collections.singletonList("No NPC groups active.");
        }
        List<String> lines = new ArrayList<>(groups.size());
        for (GroupRuntime runtime : groups) {
            long alive = runtime.aliveNpcIdxToSlot.size();
            lines.add("npc=" + runtime.npcId + " alive=" + alive + "/" + runtime.targetCount + " respawn=" + runtime.respawnTicks);
        }
        return lines;
    }

    public static int killAll(AoeInstance inst) {
        List<GroupRuntime> groups = INSTANCE_GROUPS.get(inst.getId());
        if (groups == null) {
            return 0;
        }
        int killed = 0;
        for (GroupRuntime runtime : groups) {
            for (Integer npcIndex : new ArrayList<>(runtime.aliveNpcIdxToSlot.keySet())) {
                NPC npc = getNpcByIndex(npcIndex);
                if (npc != null && !npc.isDead()) {
                    npc.appendDamage(null, npc.getHealth().getCurrentHealth() + 1, Hitmark.HIT);
                    killed++;
                }
            }
        }
        return killed;
    }

    public static boolean toggleForceAggro(AoeInstance inst) {
        if (FORCE_AGGRO.contains(inst.getId())) {
            FORCE_AGGRO.remove(inst.getId());
            return false;
        }
        FORCE_AGGRO.add(inst.getId());
        return true;
    }

    private static NPC getNpcByIndex(int index) {
        if (index <= 0 || index >= NPCHandler.npcs.length) {
            return null;
        }
        return NPCHandler.npcs[index];
    }

    private static void removeNpcReference(Integer npcIndex, boolean unregister) {
        if (npcIndex == null) {
            return;
        }
        NPC_INDEX_TO_GROUP.remove(npcIndex);
        UUID instId = NPC_INDEX_TO_INSTANCE.remove(npcIndex);
        if (unregister) {
            NPC npc = getNpcByIndex(npcIndex);
            if (npc != null) {
                npc.unregister();
            }
        }
        if (instId != null) {
            List<GroupRuntime> groups = INSTANCE_GROUPS.get(instId);
            if (groups != null) {
                for (GroupRuntime runtime : groups) {
                    Integer slot = runtime.aliveNpcIdxToSlot.remove(npcIndex);
                    if (slot != null) {
                        runtime.slotAlive[slot] = false;
                        break;
                    }
                }
            }
        }
    }
}
