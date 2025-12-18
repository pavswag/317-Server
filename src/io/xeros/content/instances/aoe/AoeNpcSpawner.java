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
import io.xeros.Server;
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
    private static final boolean AOE_DEBUG = Boolean.getBoolean("aoe.debug");
    private static final long TIMEOUT_TICKS = 500; // ~5 minutes

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
        AoeZoneInstance zone = new AoeZoneInstance(inst.id(), def, bounds, inst.ownerPid(), owner != null ? owner.getLoginName() : "unknown");
        ACTIVE.put(inst.id(), zone);
        zone.touchHeartbeat(true);

        if (AOE_DEBUG) {
            logger.info("[AOE-MAP][{}] SPAWN_START zoneId={} templates={} totalCount={} grid={}x{} spacing={} center=({},{}.{}) bounds=({}-{},{}-{},h={})",
                    inst.id(), def.getId(), def.getSpawns().stream()
                            .map(t -> t.getNpcId() + "x" + t.getCount())
                            .collect(Collectors.joining(";")),
                    def.getSpawns().stream().mapToInt(AoeZoneDefinition.SpawnTemplate::getCount).sum(),
                    def.getRows(), def.getCols(), def.getSpacing(), def.getCenterX(), def.getCenterY(), def.getHeight(),
                    bounds.getMinimumX(), bounds.getMaximumX(), bounds.getMinimumY(), bounds.getMaximumY(), def.getHeight());
        }

        List<AoeZoneInstance.SpawnPoint> assigned = assignSpawnPoints(def, inst.id());
        assigned.forEach(zone::ensureSpawnPoint);
        int attempt = 0;
        int successCount = 0;
        for (AoeZoneInstance.SpawnPoint spawn : assigned) {
            attempt++;
            NPC npc = spawnNpc(owner, inst, zone, spawn, attempt, "initial");
            if (npc != null) {
                zone.registerSpawn(spawn, npc);
                NPC_INSTANCE.put(npc.getIndex(), inst.id());
                successCount++;
                if (AOE_DEBUG) {
                    logger.info("[AOE-MAP][{}] REGISTER spawn=({},{}.{}) npcId={} index={} tracked={}",
                            inst.id(), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getNpcId(), npc.getIndex(), zone.spawnRecords().size());
                }
            } else {
                zone.registerSpawn(spawn, null);
            }
        }

        if (AOE_DEBUG) {
            logger.info("[AOE-MAP][{}] SPAWN_SUMMARY successCount={} attempted={}", inst.id(), successCount, assigned.size());
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
        for (AoeZoneInstance.SpawnRecord record : zone.spawnRecords().values()) {
            if (record == null) continue;
            int idx = record.getNpcIndex();
            if (idx < 0) continue;
            NPC npc = NPCHandler.npcs[idx];
            if (npc != null) {
                NPC_INSTANCE.remove(npc.getIndex());
                npc.unregister();
                logger.info("[AOE-MAP][{}] Despawned npc index={}", inst.id(), idx);
            }
        }
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
        if (zone == null || zone.spawnRecords().isEmpty()) {
            return Collections.singletonList("No active NPCs for this instance.");
        }

        Map<Integer, Long> counts = zone.spawnRecords().values().stream()
                .map(r -> r == null || r.getNpcIndex() < 0 ? null : NPCHandler.npcs[r.getNpcIndex()])
                .filter(Objects::nonNull)
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
        if (zone == null || zone.spawnRecords().isEmpty()) {
            return 0;
        }
        int killed = 0;
        for (AoeZoneInstance.SpawnRecord record : zone.spawnRecords().values()) {
            if (record == null || record.getNpcIndex() < 0) {
                continue;
            }
            NPC npc = NPCHandler.npcs[record.getNpcIndex()];
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

        AoeZoneInstance.SpawnPoint spawn = zone.findByNpcIndex(idx);
        if (spawn == null) {
            spawn = locateSpawnByPosition(zone, npc);
        }
        long respawnAt = Server.getTickCount() + Math.max(1, zone.definition().getRespawnDelayTicks());
        zone.markDead(spawn, respawnAt);
        System.out.println("[AOE-RESPAWN] death zone=" + zone.id() + " npcId=" + npc.getNpcId() + " idx=" + idx + " respawnAt=" + respawnAt);
        if (AOE_DEBUG) {
            logger.info("[AOE-MAP][{}] DEATH npcId={} idx={} respawnAtTick={}", zone.id(), npc.getNpcId(), idx, respawnAt);
        }

        if (FORCE_AGGRO.contains(instanceId)) {
            AoeTierRepo.instanceById(instanceId)
                    .flatMap(inst -> java.util.Optional.ofNullable(PlayerHandler.players[inst.ownerPid()]))
                    .ifPresent(player -> AggressionHandler.forceAggro(player, zone.definition().getAggressionRadius()));
        }
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

                long now = Server.getTickCount();

                List<Player> players = playersInZone(zone);
                if (!players.isEmpty()) {
                    zone.touchHeartbeat(true);
                }

                maybeRespawn(zone, players.isEmpty() ? null : players.get(0));
                if (players.isEmpty()) {
                    leashAll(zone);
                    return;
                }

                for (Map.Entry<AoeZoneInstance.SpawnPoint, AoeZoneInstance.SpawnRecord> entry : zone.spawnRecords().entrySet()) {
                    AoeZoneInstance.SpawnRecord record = entry.getValue();
                    if (record == null) {
                        continue;
                    }
                    int idx = record.getNpcIndex();
                    if (idx < 0) {
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
                                logger.info("[AOE-MAP][{}] target acquired npc={} -> {}", zone.id(), npc.getIndex(), closest.getLoginName());
                            }
                        }
                    }
                }
            }
        }, 2);
        TICKERS.put(zone.id(), container);
    }

    private static void maybeRespawn(AoeZoneInstance zone, Player ownerContext) {
        if (zone == null) return;
        long now = Server.getTickCount();
        AoeInstance inst = AoeTierRepo.instanceById(zone.id()).orElse(null);
        Player owner = inst != null ? PlayerHandler.players[inst.ownerPid()] : ownerContext;
        if (owner == null && zone.getOwnerName() != null) {
            // Fallback: look up by username if the pid slot has rotated.
            for (Player p : PlayerHandler.getPlayers()) {
                if (p != null && zone.getOwnerName().equalsIgnoreCase(p.getLoginName())) {
                    owner = p;
                    break;
                }
            }
        }
        if (inst == null && (AOE_DEBUG || true)) {
            System.out.println("[AOE-RESPAWN] instance missing for zone=" + zone.id());
        }
        for (Map.Entry<AoeZoneInstance.SpawnPoint, AoeZoneInstance.SpawnRecord> entry : zone.spawnRecords().entrySet()) {
            AoeZoneInstance.SpawnRecord record = entry.getValue();
            if (record == null) continue;
            if (record.getNpcIndex() >= 0) continue;
            if (record.getRespawnAtTick() <= 0 || now < record.getRespawnAtTick()) continue;
            System.out.println("[AOE-RESPAWN] attempt zone=" + zone.id() + " npcId=" + entry.getKey().getNpcId() + " at " + entry.getKey().getX() + "," + entry.getKey().getY() + " tick=" + now);
            NPC spawned = spawnNpc(owner, inst, zone, entry.getKey(), -1, "respawn");
            if (spawned != null) {
                record.setNpcIndex(spawned.getIndex());
                record.setRespawnAtTick(0);
                NPC_INSTANCE.put(spawned.getIndex(), zone.id());
                if (AOE_DEBUG) {
                    logger.info("[AOE-MAP][{}] Respawned npc {} index={} at ({},{}.{})", zone.id(), entry.getKey().getNpcId(), spawned.getIndex(), entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ());
                }
            } else {
                if (AOE_DEBUG) {
                    logger.warn("[AOE-MAP][{}] Respawn failed npcId={} at ({},{}.{})", zone.id(), entry.getKey().getNpcId(), entry.getKey().getX(), entry.getKey().getY(), entry.getKey().getZ());
                }
            }
        }
    }

    private static void endInstance(AoeZoneInstance zone, String reason) {
        if (zone == null) return;
        logger.info("[AOE-MAP][{}] Ending instance reason={} owner={}", zone.id(), reason, zone.getOwnerName());
        CycleEventContainer ticker = TICKERS.remove(zone.id());
        if (ticker != null) {
            ticker.stop();
        }
        AoeTierRepo.instanceById(zone.id()).ifPresent(inst -> new AoeInstanceService().teardown(inst, reason));
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
        for (AoeZoneInstance.SpawnRecord record : zone.spawnRecords().values()) {
            if (record == null) continue;
            NPC npc = record.getNpcIndex() >= 0 ? NPCHandler.npcs[record.getNpcIndex()] : null;
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
        double maxRange = radius > 0 ? (double) radius : Double.MAX_VALUE;

        for (Player p : players) {
            double dist = npc.getDistance(p.getX(), p.getY());
            if (dist <= maxRange && dist < closestDist) {
                closest = p;
                closestDist = dist;
            }
        }
        return closest;
    }

    private static AoeZoneInstance.SpawnPoint locateSpawnByPosition(AoeZoneInstance zone, NPC npc) {
        if (zone == null || npc == null) {
            return null;
        }
        return zone.spawnPoints().stream()
                .filter(spawn -> spawn.getNpcId() == npc.getNpcId())
                .min(Comparator.comparingDouble(spawn -> npc.distanceToPoint(spawn.getX(), spawn.getY())))
                .orElse(null);
    }

    private static List<AoeZoneInstance.SpawnPoint> assignSpawnPoints(AoeZoneDefinition def, UUID instanceId) {
        List<Integer> npcIds = new ArrayList<>();
        for (AoeZoneDefinition.SpawnTemplate spawn : def.getSpawns()) {
            for (int i = 0; i < spawn.getCount(); i++) {
                npcIds.add(spawn.getNpcId());
            }
        }

        if (npcIds.isEmpty()) {
            return Collections.emptyList();
        }

        int rows = Math.max(1, def.getRows());
        int cols = Math.max(1, def.getCols());
        int total = npcIds.size();
        if (rows <= 0 || cols <= 0) {
            int side = (int) Math.ceil(Math.sqrt(total));
            rows = side;
            cols = side;
        }
        if (rows * cols < total) {
            rows = (int) Math.ceil((double) total / cols);
            if (rows * cols < total) {
                cols = (int) Math.ceil((double) total / rows);
            }
        }

        int startX = def.getCenterX() - ((cols - 1) * def.getSpacing()) / 2;
        int startY = def.getCenterY() - ((rows - 1) * def.getSpacing()) / 2;

        List<AoeZoneInstance.SpawnPoint> points = new ArrayList<>(total);
        Set<String> reserved = new HashSet<>();
        for (int i = 0; i < npcIds.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            int candidateX = startX + col * def.getSpacing();
            int candidateY = startY + row * def.getSpacing();
            Position tile = findValidTile(def, candidateX, candidateY, reserved, instanceId);
            reserved.add(key(tile));
            points.add(new AoeZoneInstance.SpawnPoint(tile.getX(), tile.getY(), tile.getHeight(), npcIds.get(i)));
        }

        return points;
    }

    private static Position findValidTile(AoeZoneDefinition def, int x, int y, Set<String> reserved, UUID instanceId) {
        if (def == null) {
            return new Position(x, y, 0);
        }
        if (isWalkable(def, x, y) && !reserved.contains(key(x, y, def.getHeight()))) {
            return new Position(x, y, def.getHeight());
        }
        for (int radius = 1; radius <= 5; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    int nx = x + dx;
                    int ny = y + dy;
                    if (isWalkable(def, nx, ny) && !reserved.contains(key(nx, ny, def.getHeight()))) {
                        if (AOE_DEBUG) {
                            logger.info("[AOE-MAP][{}] Adjusted spawn to ({},{}) from ({},{})", instanceId, nx, ny, x, y);
                        }
                        return new Position(nx, ny, def.getHeight());
                    }
                }
            }
        }
        if (AOE_DEBUG) {
            logger.warn("[AOE-MAP][{}] Failed to find walkable tile near ({},{}), defaulting to center ({},{})", instanceId, x, y, def.getCenterX(), def.getCenterY());
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

        return new AoeZoneDefinition(tier.getZoneName(), bounds, centerX, centerY, height, rows, cols, Math.max(1, spacing), respawnTicks, aggroRange, spawns);
    }

    private static NPC spawnNpc(Player owner, AoeInstance inst, AoeZoneInstance zone, AoeZoneInstance.SpawnPoint spawn, int attempt, String phase) {
        if (inst == null || spawn == null || zone == null) {
            return null;
        }
        AoeZoneDefinition def = zone.definition();
        RegionProvider provider = RegionProvider.getGlobal();
        boolean withinBounds = isInside(def.getBounds(), spawn.getX(), spawn.getY(), spawn.getZ());
        boolean clipped = provider.hasClipping(spawn.getX(), spawn.getY(), spawn.getZ());
        boolean occupied = provider.isOccupiedByNpc(spawn.getX(), spawn.getY(), spawn.getZ());
        boolean walkable = withinBounds && !clipped && !occupied;
        Position tile = adjustSpawnTile(def, spawn, zone.id());

        if (AOE_DEBUG) {
            logger.info("[AOE-MAP][{}] SPAWN_ATTEMPT phase={} idx={} npcId={} intended=({},{}.{}) withinBounds={} walkable={} occupied={} final=({},{}.{}) moved={}",
                    inst.id(), phase, attempt, spawn.getNpcId(), spawn.getX(), spawn.getY(), spawn.getZ(), withinBounds, walkable, occupied,
                    tile.getX(), tile.getY(), tile.getHeight(), !(tile.getX() == spawn.getX() && tile.getY() == spawn.getY() && tile.getHeight() == spawn.getZ()));
        }

        NPC npc = NPCSpawning.spawnNpc(owner, spawn.getNpcId(), tile.getX(), tile.getY(), tile.getHeight(), 1, 0, false, false);
        if (npc != null) {
            npc.randomWalk = false;
            npc.makeX = tile.getX();
            npc.makeY = tile.getY();
            npc.getBehaviour().setAggressive(true);
            npc.getBehaviour().setRespawn(false);
            npc.heightLevel = tile.getHeight();
            if (AOE_DEBUG) {
                logger.info("[AOE-MAP][{}] SPAWN_OK phase={} idx={} npcId={} index={} tile=({},{}.{})", inst.id(), phase, attempt, spawn.getNpcId(), npc.getIndex(), tile.getX(), tile.getY(), tile.getHeight());
            }
        } else {
            logger.warn("[AOE-MAP][{}] SPAWN_FAIL phase={} idx={} npcId={} tile=({},{}.{}) reason=spawn_null", inst.id(), phase, attempt, spawn.getNpcId(), tile.getX(), tile.getY(), tile.getHeight());
        }
        return npc;
    }

    private static Position adjustSpawnTile(AoeZoneDefinition def, AoeZoneInstance.SpawnPoint spawn, UUID instanceId) {
        if (def == null || spawn == null) {
            return new Position(spawn != null ? spawn.getX() : 0, spawn != null ? spawn.getY() : 0, spawn != null ? spawn.getZ() : 0);
        }
        if (isWalkable(def, spawn.getX(), spawn.getY())) {
            return spawn.toPosition();
        }
        Position fallback = findValidTile(def, spawn.getX(), spawn.getY(), Collections.emptySet(), instanceId);
        return fallback == null ? spawn.toPosition() : fallback;
    }

    private static String key(Position pos) {
        return key(pos.getX(), pos.getY(), pos.getHeight());
    }

    private static String key(int x, int y, int z) {
        return x + ":" + y + ":" + z;
    }
