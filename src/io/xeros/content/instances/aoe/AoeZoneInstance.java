package io.xeros.content.instances.aoe;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Position;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

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

    private final UUID id;
    private final AoeZoneDefinition definition;
    private final Boundary bounds;
    private final List<SpawnPoint> spawnPoints = new CopyOnWriteArrayList<>();
    private final Map<SpawnPoint, Integer> liveNpcs = new ConcurrentHashMap<>();
    private final Set<SpawnPoint> pendingRespawn = new CopyOnWriteArraySet<>();

    public AoeZoneInstance(UUID id, AoeZoneDefinition definition, Boundary bounds) {
        this.id = id;
        this.definition = definition;
        this.bounds = bounds;
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

    public Map<SpawnPoint, Integer> liveNpcs() {
        return liveNpcs;
    }

    public Set<SpawnPoint> pendingRespawn() {
        return pendingRespawn;
    }

    public void addSpawnPoint(SpawnPoint spawn) {
        if (spawn != null) {
            spawnPoints.add(spawn);
        }
    }

    public void registerSpawn(SpawnPoint spawn, NPC npc) {
        if (npc == null || spawn == null) {
            return;
        }
        if (!spawnPoints.contains(spawn)) {
            spawnPoints.add(spawn);
        }
        liveNpcs.put(spawn, npc.getIndex());
    }
}
