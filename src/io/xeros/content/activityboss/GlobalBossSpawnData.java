package io.xeros.content.activityboss;

import io.xeros.model.entity.player.Position;

/**
 * Tracks data about a global boss spawn.
 */
public class GlobalBossSpawnData {

    private final long spawnTime;
    private final String topContributor;
    private final Position spawnPosition;
    private boolean alive;

    public GlobalBossSpawnData(long spawnTime, String topContributor, Position spawnPosition, boolean alive) {
        this.spawnTime = spawnTime;
        this.topContributor = topContributor;
        this.spawnPosition = spawnPosition;
        this.alive = alive;
    }

    public long getSpawnTime() {
        return spawnTime;
    }

    public String getTopContributor() {
        return topContributor;
    }

    public Position getSpawnPosition() {
        return spawnPosition;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}

