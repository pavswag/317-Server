package io.xeros.content.instances.aoe;

import io.xeros.content.combat.Hitmark;
import io.xeros.content.instances.aoe.AoeTierRepo;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.npc.actions.AggressionHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * Spawns and tracks NPCs for an {@link AoeInstance}.
 */
public final class AoeNpcSpawner {

    private static final Logger logger = LoggerFactory.getLogger(AoeNpcSpawner.class);
    private static final Map<UUID, List<Integer>> ACTIVE = new ConcurrentHashMap<>();
    private static final CopyOnWriteArraySet<UUID> FORCE_AGGRO = new CopyOnWriteArraySet<>();

    private AoeNpcSpawner() {}

    public static void spawnForInstance(Player owner, AoeInstance inst, AoeZoneMapDef map) {
        List<Integer> spawned = new ArrayList<>();
        for (AoeZoneMapDef.Npc def : map.getNpcs()) {
            int z = inst.z() + def.getZ();
            NPC npc = NPCSpawning.spawnNpc(owner, def.getId(), def.getX(), def.getY(), z, 1, 0, false, false);
            if (npc != null) {
                npc.randomWalk = def.isWalk();
                spawned.add(npc.getIndex());
                logger.info("[AOE-MAP] Spawned npc id={} index={} at ({},{},{})", def.getId(), npc.getIndex(), def.getX(), def.getY(), z);
            } else {
                logger.warn("[AOE-MAP] Failed to spawn npc id={} at ({},{},{})", def.getId(), def.getX(), def.getY(), z);
            }
        }
        ACTIVE.put(inst.id(), spawned);
    }

    public static void despawnForInstance(AoeInstance inst) {
        List<Integer> indices = ACTIVE.remove(inst.id());
        if (indices == null) {
            return;
        }
        for (Integer idx : indices) {
            if (idx == null) continue;
            NPC npc = NPCHandler.npcs[idx];
            if (npc != null) {
                npc.unregister();
                logger.info("[AOE-MAP] Despawned npc index={} from instance={}", idx, inst.id());
            }
        }
    }

    public static boolean toggleForceAggro(AoeInstance instance) {
        if (instance == null) {
            return false;
        }
        boolean enabled;
        if (FORCE_AGGRO.contains(instance.id())) {
            FORCE_AGGRO.remove(instance.id());
            enabled = false;
        } else {
            FORCE_AGGRO.add(instance.id());
            enabled = true;
            Player owner = PlayerHandler.players[instance.ownerPid()];
            if (owner != null) {
                AggressionHandler.forceAggro(owner, 10);
            }
        }
        return enabled;
    }

    public static List<String> debugCounts(AoeInstance instance) {
        if (instance == null) {
            return Collections.singletonList("Invalid AOE instance.");
        }
        List<Integer> indices = ACTIVE.get(instance.id());
        if (indices == null || indices.isEmpty()) {
            return Collections.singletonList("No active NPCs for this instance.");
        }

        Map<Integer, Long> counts = indices.stream()
                .map(idx -> idx == null || idx < 0 ? null : NPCHandler.npcs[idx])
                .filter(npc -> npc != null)
                .collect(Collectors.groupingBy(NPC::getNpcId, Collectors.counting()));

        List<String> lines = new ArrayList<>();
        long total = counts.values().stream().mapToLong(Long::longValue).sum();
        lines.add("AOE NPCs active: " + total);
        counts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> lines.add(" - id " + entry.getKey() + ": " + entry.getValue()));
        return lines;
    }

    public static int killAll(AoeInstance instance) {
        if (instance == null) {
            return 0;
        }
        List<Integer> indices = ACTIVE.get(instance.id());
        if (indices == null || indices.isEmpty()) {
            return 0;
        }
        int killed = 0;
        for (Integer idx : indices) {
            if (idx == null || idx < 0) {
                continue;
            }
            NPC npc = NPCHandler.npcs[idx];
            if (npc == null || npc.isDeadOrDying()) {
                continue;
            }
            npc.appendDamage(null, npc.getHealth().getCurrentHealth(), Hitmark.HIT);
            killed++;
        }
        return killed;
    }

    public static void onNpcDeath(NPC npc) {
        if (npc == null) {
            return;
        }
        int idx = npc.getIndex();
        UUID instanceId = null;
        for (Map.Entry<UUID, List<Integer>> entry : ACTIVE.entrySet()) {
            if (entry.getValue().remove((Integer) idx)) {
                instanceId = entry.getKey();
                if (entry.getValue().isEmpty()) {
                    ACTIVE.remove(entry.getKey());
                }
                break;
            }
        }

        if (instanceId != null && FORCE_AGGRO.contains(instanceId)) {
            AoeTierRepo.instanceById(instanceId)
                    .flatMap(inst -> java.util.Optional.ofNullable(PlayerHandler.players[inst.ownerPid()]))
                    .ifPresent(player -> AggressionHandler.forceAggro(player, 10));
        }
    }
}

