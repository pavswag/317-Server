package io.xeros.content.instances.aoe;

import io.xeros.content.combat.Hitmark;
import io.xeros.content.instances.aoe.AoeTierRepo;
import io.xeros.content.instances.aoe.AoeBossTierDef;
import io.xeros.model.collisionmap.RegionProvider;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.npc.actions.AggressionHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Spawns and tracks NPCs for an {@link AoeInstance}.
 */
public final class AoeNpcSpawner {

    private static final Logger logger = LoggerFactory.getLogger(AoeNpcSpawner.class);
    private static final boolean AOE_DEBUG = false;

    private static final Map<UUID, AoeZoneInstance> ACTIVE = new ConcurrentHashMap<>();
    private static final Map<Integer, UUID> NPC_INSTANCE = new ConcurrentHashMap<>();
    private static final Map<UUID, CycleEventContainer> TICKERS = new ConcurrentHashMap<>();
    private static final CopyOnWriteArraySet<UUID> FORCE_AGGRO = new CopyOnWriteArraySet<>();

    private AoeNpcSpawner() {}

    public static void spawnForInstance(Player owner, AoeInstance inst, AoeZoneMapDef map) {
        if (owner == null || inst == null || map == null) {
            return;
        }

        despawnForInstance(inst);

        AoeZoneDefinition def = buildDefinition(inst, map);
        Boundary bounds = def.getBounds();
        AoeZoneInstance zone = new AoeZoneInstance(inst.id(), def, bounds);
        ACTIVE.put(inst.id(), zone);

        List<AoeZoneInstance.SpawnPoint> assigned = assignSpawnPoints(def);
        for (AoeZoneInstance.SpawnPoint spawn : assigned) {
            NPC npc = spawnNpc(owner, inst, spawn);
            if (npc != null) {
                zone.registerSpawn(spawn, npc);
                NPC_INSTANCE.put(npc.getIndex(), inst.id());
            }
        }

        startTicker(zone);
    }

    public static void despawnForInstance(AoeInstance inst) {
        if (inst == null) {
            return;
        }
        AoeZoneInstance zone = ACTIVE.remove(inst.id());
        CycleEventContainer ticker = TICKERS.remove(inst.id());
        if (ticker != null) {
            ticker.stop();
        }

        if (zone == null) {
            return;
        }
        for (Integer idx : zone.liveNpcs().values()) {
            if (idx == null) continue;
            NPC npc = NPCHandler.npcs[idx];
            if (npc != null) {
                NPC_INSTANCE.remove(npc.getIndex());
                npc.unregister();
                logger.info("[AOE-MAP] Despawned npc index={} from instance={}", idx, inst.id());
            }
        }
        zone.pendingRespawn().clear();
        zone.spawnPoints().clear();
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
                int radius = Optional.ofNullable(ACTIVE.get(instance.id()))
                        .map(z -> z.definition().getAggressionRadius()).orElse(10);
                AggressionHandler.forceAggro(owner, radius);
            }
        }
        return enabled;
    }

    public static List<String> debugCounts(AoeInstance instance) {
        if (instance == null) {
            return Collections.singletonList("Invalid AOE instance.");
        }
        AoeZoneInstance zone = ACTIVE.get(instance.id());
        if (zone == null || zone.liveNpcs().isEmpty()) {
            return Collections.singletonList("No active NPCs for this instance.");
        }

        Map<Integer, Long> counts = zone.liveNpcs().values().stream()
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
        AoeZoneInstance zone = ACTIVE.get(instance.id());
        if (zone == null || zone.liveNpcs().isEmpty()) {
            return 0;
        }
        int killed = 0;
        for (Integer idx : zone.liveNpcs().values()) {
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
        UUID instanceId = NPC_INSTANCE.remove(idx);
        if (instanceId == null) {
            return;
        }
        AoeZoneInstance zone = ACTIVE.get(instanceId);
        if (zone == null) {
            return;
        }

        AoeZoneInstance.SpawnPoint spawn = zone.liveNpcs().entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue().equals(idx))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        if (spawn != null) {
            zone.liveNpcs().remove(spawn);
        }
        scheduleRespawn(zone, spawn, npc);

        if (FORCE_AGGRO.contains(instanceId)) {
            AoeTierRepo.instanceById(instanceId)
                    .flatMap(inst -> java.util.Optional.ofNullable(PlayerHandler.players[inst.ownerPid()]))
                    .ifPresent(player -> AggressionHandler.forceAggro(player, zone.definition().getAggressionRadius()));
        }
    }

    private static void scheduleRespawn(AoeZoneInstance zone, AoeZoneInstance.SpawnPoint spawn, NPC npc) {
        if (zone == null || spawn == null || npc == null) {
            return;
        }
        if (zone.pendingRespawn().contains(spawn)) {
            return;
        }
        zone.pendingRespawn().add(spawn);
        int delay = Math.max(1, zone.definition().getRespawnDelayTicks());
        CycleEventHandler.getSingleton().addEvent(zone.id(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (!ACTIVE.containsKey(zone.id())) {
                    container.stop();
                    return;
                }
                AoeInstance inst = AoeTierRepo.instanceById(zone.id()).orElse(null);
                Player owner = inst != null ? PlayerHandler.players[inst.ownerPid()] : null;
                NPC spawned = spawnNpc(owner, inst, spawn);
                if (spawned == null) {
                    if (AOE_DEBUG) {
                        logger.warn("[AOE-MAP] Respawn failed for npc {} at ({},{}.{})", spawn.getNpcId(), spawn.getX(), spawn.getY(), spawn.getZ());
                    }
                    return;
                }
                zone.liveNpcs().put(spawn, spawned.getIndex());
                NPC_INSTANCE.put(spawned.getIndex(), zone.id());
                zone.pendingRespawn().remove(spawn);
                container.stop();
                if (AOE_DEBUG) {
                    logger.info("[AOE-MAP] Respawned npc {} index={} at ({},{}.{})", spawn.getNpcId(), spawned.getIndex(), spawn.getX(), spawn.getY(), spawn.getZ());
                }
            }
        }, delay);
    }

    private static void startTicker(AoeZoneInstance zone) {
        CycleEventContainer existing = TICKERS.get(zone.id());
        if (existing != null) {
            existing.stop();
        }
        CycleEventContainer container = CycleEventHandler.getSingleton().addEvent(zone.id(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer c) {
                if (!ACTIVE.containsKey(zone.id())) {
                    c.stop();
                    return;
                }

                List<Player> players = playersInZone(zone);
                if (players.isEmpty()) {
                    leashAll(zone);
                    return;
                }

                for (Map.Entry<AoeZoneInstance.SpawnPoint, Integer> entry : zone.liveNpcs().entrySet()) {
                    Integer idx = entry.getValue();
                    if (idx == null || idx < 0) {
                        continue;
                    }
                    NPC npc = NPCHandler.npcs[idx];
                    if (npc == null || npc.isDeadOrDying()) {
                        continue;
                    }
                    Position spawn = entry.getKey().toPosition();
                    enforceBounds(zone, npc, spawn);

                    Player currentTarget = npc.getPlayerAttackingIndex() > 0 ? PlayerHandler.players[npc.getPlayerAttackingIndex()] : null;
                    if (currentTarget != null && (!isInside(zone, currentTarget.getPosition()) || currentTarget.isDead)) {
                        resetTarget(npc);
                        currentTarget = null;
                    }

                    if (currentTarget == null) {
                        Player closest = nearest(players, npc, zone.definition().getAggressionRadius());
                        if (closest != null) {
                            npc.setPlayerAttackingIndex(closest.getIndex());
                            closest.underAttackByNpc = npc.getIndex();
                            npc.underAttack = true;
                            if (AOE_DEBUG) {
                                logger.info("[AOE-MAP] target acquired npc={} -> {}", npc.getIndex(), closest.getLoginName());
                            }
                        }
                    }
                }
            }
        }, 2);
        TICKERS.put(zone.id(), container);
    }

    private static List<Player> playersInZone(AoeZoneInstance zone) {
        List<Player> players = new ArrayList<>();
        for (Player p : PlayerHandler.getPlayers()) {
            if (p == null || p.isDead) {
                continue;
            }
            if (isInside(zone, p.getPosition())) {
                players.add(p);
            }
        }
        return players;
    }

    private static void leashAll(AoeZoneInstance zone) {
        for (Map.Entry<AoeZoneInstance.SpawnPoint, Integer> entry : zone.liveNpcs().entrySet()) {
            Integer idx = entry.getValue();
            NPC npc = idx != null && idx >= 0 ? NPCHandler.npcs[idx] : null;
            if (npc == null) continue;
            resetTarget(npc);
            npc.walkingHome = true;
        }
    }

    private static void enforceBounds(AoeZoneInstance zone, NPC npc, Position spawn) {
        if (npc == null || spawn == null) {
            return;
        }
        int leashDistance = Math.max(zone.definition().getSpacing() * Math.max(zone.definition().getRows(), zone.definition().getCols()), 3);
        if (!isInside(zone, npc.getPosition()) || npc.distanceToPoint(spawn.getX(), spawn.getY()) > leashDistance) {
            resetTarget(npc);
            npc.walkingHome = true;
            npc.makeX = spawn.getX();
            npc.makeY = spawn.getY();
            npc.heightLevel = spawn.getHeight();
        }
    }

    private static void resetTarget(NPC npc) {
        npc.setPlayerAttackingIndex(0);
        npc.underAttack = false;
        npc.underAttackBy = 0;
    }

    private static Player nearest(List<Player> players, NPC npc, int radius) {
        Player closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Player p : players) {
            double dist = npc.getDistance(p.getX(), p.getY());
            if (dist <= (double) radius && dist < closestDist) {
                closest = p;
                closestDist = dist;
            }
        }
        return closest;
    }

    private static NPC spawnNpc(Player owner, AoeInstance inst, AoeZoneInstance.SpawnPoint spawn) {
        if (inst == null || spawn == null) {
            return null;
        }
        NPC npc = NPCSpawning.spawnNpc(owner, spawn.getNpcId(), spawn.getX(), spawn.getY(), spawn.getZ(), 1, 0, false, false);
        if (npc != null) {
            npc.randomWalk = false;
            npc.makeX = spawn.getX();
            npc.makeY = spawn.getY();
            npc.getBehaviour().setAggressive(true);
            npc.getBehaviour().setRespawn(false);
            npc.heightLevel = spawn.getZ();
            if (AOE_DEBUG) {
                logger.info("[AOE-MAP] Spawned npc id={} index={} at ({},{}.{})", spawn.getNpcId(), npc.getIndex(), spawn.getX(), spawn.getY(), spawn.getZ());
            }
        } else {
            logger.warn("[AOE-MAP] Failed to spawn npc id={} at ({},{}.{})", spawn.getNpcId(), spawn.getX(), spawn.getY(), spawn.getZ());
        }
        return npc;
    }

    private static List<AoeZoneInstance.SpawnPoint> assignSpawnPoints(AoeZoneDefinition def) {
        List<AoeZoneInstance.SpawnPoint> points = new ArrayList<>();
        int total = def.getSpawns().stream().mapToInt(AoeZoneDefinition.SpawnTemplate::getCount).sum();
        int rows = Math.max(1, def.getRows());
        int cols = Math.max(1, def.getCols());
        if (rows * cols < total) {
            rows = (int) Math.ceil((double) total / cols);
        }
        int startX = def.getCenterX() - ((cols - 1) * def.getSpacing()) / 2;
        int startY = def.getCenterY() - ((rows - 1) * def.getSpacing()) / 2;

        Iterator<AoeZoneDefinition.SpawnTemplate> templateIter = def.getSpawns().iterator();
        AoeZoneDefinition.SpawnTemplate current = templateIter.hasNext() ? templateIter.next() : null;
        int remainingFromCurrent = current != null ? current.getCount() : 0;

        for (int i = 0; i < total; i++) {
            int row = i / cols;
            int col = i % cols;
            int candidateX = startX + col * def.getSpacing();
            int candidateY = startY + row * def.getSpacing();
            Position tile = findValidTile(def, candidateX, candidateY);
            if (current == null) {
                break;
            }
            points.add(new AoeZoneInstance.SpawnPoint(tile.getX(), tile.getY(), tile.getHeight(), current.getNpcId()));
            remainingFromCurrent--;
            if (remainingFromCurrent <= 0 && templateIter.hasNext()) {
                current = templateIter.next();
                remainingFromCurrent = current.getCount();
            }
        }
        return points;
    }

    private static Position findValidTile(AoeZoneDefinition def, int x, int y) {
        if (isWalkable(def, x, y)) {
            return new Position(x, y, def.getHeight());
        }
        for (int radius = 1; radius <= 3; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    int nx = x + dx;
                    int ny = y + dy;
                    if (isWalkable(def, nx, ny)) {
                        if (AOE_DEBUG) {
                            logger.info("[AOE-MAP] Adjusted spawn to ({},{}) from ({},{})", nx, ny, x, y);
                        }
                        return new Position(nx, ny, def.getHeight());
                    }
                }
            }
        }
        return new Position(def.getCenterX(), def.getCenterY(), def.getHeight());
    }

    private static boolean isWalkable(AoeZoneDefinition def, int x, int y) {
        if (!isInside(def.getBounds(), x, y, def.getHeight())) {
            return false;
        }
        RegionProvider provider = RegionProvider.getGlobal();
        return !provider.hasClipping(x, y, def.getHeight()) && !provider.isOccupiedByNpc(x, y, def.getHeight());
    }

    private static boolean isInside(AoeZoneInstance zone, Position pos) {
        if (pos == null || zone == null) {
            return false;
        }
        return isInside(zone.bounds(), pos.getX(), pos.getY(), pos.getHeight());
    }

    private static boolean isInside(Boundary bounds, int x, int y, int height) {
        if (bounds == null) {
            return false;
        }
        return Boundary.isIn(new Position(x, y, height), bounds);
    }

    private static AoeZoneDefinition buildDefinition(AoeInstance inst, AoeZoneMapDef map) {
        AoeBossTierDef tier = inst.tier();
        int widthTiles = map.getSource().getWidth() * 8;
        int heightTiles = map.getSource().getHeight() * 8;
        int minX = inst.baseX();
        int minY = inst.baseY();
        int maxX = minX + widthTiles - 1;
        int maxY = minY + heightTiles - 1;
        int height = inst.z();
        Boundary bounds = new Boundary(minX, minY, maxX, maxY, height);

        AoeZoneMapDef.Spawn spawn = map.getSpawn();
        int centerX = spawn != null ? spawn.getX() : minX + widthTiles / 2;
        int centerY = spawn != null ? spawn.getY() : minY + heightTiles / 2;
        if (spawn != null) {
            height += spawn.getZ();
            bounds = new Boundary(minX, minY, maxX, maxY, height);
        }
        int rows = tier.getAoeGrid() != null && tier.getAoeGrid().rows > 0 ? tier.getAoeGrid().rows : 0;
        int cols = tier.getAoeGrid() != null && tier.getAoeGrid().cols > 0 ? tier.getAoeGrid().cols : 0;
        int spacing = tier.getAoeGrid() != null && tier.getAoeGrid().spacing > 0 ? tier.getAoeGrid().spacing : 3;

        List<AoeZoneDefinition.SpawnTemplate> spawns = new ArrayList<>();
        if (tier.getBoss() != null) {
            spawns.add(new AoeZoneDefinition.SpawnTemplate(tier.getBoss().npcId, 1));
        }
        if (tier.getMinions() != null) {
            for (AoeBossTierDef.Npc npc : tier.getMinions()) {
                spawns.add(new AoeZoneDefinition.SpawnTemplate(npc.npcId, npc.count));
            }
        }
        if (spawns.isEmpty()) {
            spawns.add(new AoeZoneDefinition.SpawnTemplate(map.getNpcs().isEmpty() ? 1 : map.getNpcs().get(0).getId(), 1));
        }

        int totalSpawns = spawns.stream().mapToInt(AoeZoneDefinition.SpawnTemplate::getCount).sum();
        if (rows <= 0 || cols <= 0) {
            int side = (int) Math.ceil(Math.sqrt(totalSpawns));
            rows = side;
            cols = side;
        }

        int respawnTicks = Misc.toCycles(Math.max(1, tier.getRespawnSeconds()), TimeUnit.SECONDS);
        int aggroRange = Math.max(4, tier.getAggroRange());

        return new AoeZoneDefinition(tier.getZoneName(), bounds, centerX, centerY, height, rows, cols, spacing, respawnTicks, aggroRange, spawns);
    }
}

