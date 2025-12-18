package io.xeros.content.instances.aoe;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Position;
import io.xeros.Server;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Runtime state for an active AOE zone. Tracks which npc index belongs to which spawn point
 * and handles respawn bookkeeping.
 */
public class AoeZoneInstance {

    public static class SpawnPoint {
        private final int x;
        private final int y;
        private final int z;
        private final int npcId;

        public SpawnPoint(int x, int y, int z, int npcId) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.npcId = npcId;
        }

        public Position toPosition() {
            return new Position(x, y, z);
        }

        public int getNpcId() {
            return npcId;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SpawnPoint)) return false;
            SpawnPoint that = (SpawnPoint) o;
            return x == that.x && y == that.y && z == that.z && npcId == that.npcId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z, npcId);
        }
    }

    public static class SpawnRecord {
        private final int npcId;
        private volatile int npcIndex = -1;
        private volatile long respawnAtTick = 0;

        public SpawnRecord(int npcId) {
            this.npcId = npcId;
        }

        public int getNpcId() {
            return npcId;
        }

        public int getNpcIndex() {
            return npcIndex;
        }

        public void setNpcIndex(int npcIndex) {
            this.npcIndex = npcIndex;
        }

        public long getRespawnAtTick() {
            return respawnAtTick;
        }

        public void setRespawnAtTick(long respawnAtTick) {
            this.respawnAtTick = respawnAtTick;
        }
    }

    private final UUID id;
    private final AoeZoneDefinition definition;
    private final Boundary bounds;
    private final List<SpawnPoint> spawnPoints = new CopyOnWriteArrayList<>();
    private final Map<SpawnPoint, SpawnRecord> spawnRecords = new ConcurrentHashMap<>();
    private final int ownerPid;
    private final String ownerName;
    private volatile long lastSeenTick;
    private volatile boolean hasPlayerContext;

    public AoeZoneInstance(UUID id, AoeZoneDefinition definition, Boundary bounds, int ownerPid, String ownerName) {
        this.id = id;
        this.definition = definition;
        this.bounds = bounds;
        this.ownerPid = ownerPid;
        this.ownerName = ownerName;
        this.lastSeenTick = Server.getTickCount();
        this.hasPlayerContext = false;
    }

    public UUID id() {
        return id;
    }

    public AoeZoneDefinition definition() {
        return definition;
    }

    public Boundary bounds() {
        return bounds;
    }

    public List<SpawnPoint> spawnPoints() {
        return spawnPoints;
    }

    public Map<SpawnPoint, SpawnRecord> spawnRecords() {
        return spawnRecords;
    }

    public void addSpawnPoint(SpawnPoint spawn) {
        if (spawn != null) {
            spawnPoints.add(spawn);
            spawnRecords.putIfAbsent(spawn, new SpawnRecord(spawn.getNpcId()));
        }
    }

    public void addSpawnPoint(SpawnPoint spawn) {
        if (spawn != null) {
            spawnPoints.add(spawn);
        }
    }

    public void registerSpawn(SpawnPoint spawn, NPC npc) {
        if (spawn == null) return;
        if (!spawnPoints.contains(spawn)) {
            spawnPoints.add(spawn);
        }
        spawnRecords.putIfAbsent(spawn, new SpawnRecord(spawn.getNpcId()));
        SpawnRecord record = spawnRecords.get(spawn);
        if (npc != null) {
            record.setNpcIndex(npc.getIndex());
            record.setRespawnAtTick(0);
        }
    }

    public void markDead(SpawnPoint spawn, long respawnAtTick) {
        if (spawn == null) return;
        SpawnRecord record = spawnRecords.get(spawn);
        if (record != null) {
            record.setNpcIndex(-1);
            record.setRespawnAtTick(respawnAtTick);
        }
    }

    public SpawnPoint findByNpcIndex(int index) {
        return spawnRecords.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue().getNpcIndex() == index)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    public long getLastSeenTick() {
        return lastSeenTick;
    }

    public void touchHeartbeat(boolean hasPlayer) {
        this.lastSeenTick = Server.getTickCount();
        this.hasPlayerContext = hasPlayer;
    }

    public int getOwnerPid() {
        return ownerPid;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public boolean hasPlayerContext() {
        return hasPlayerContext;
    }
}
