package io.xeros.content.instances.aoe;

import io.xeros.model.entity.player.Boundary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Static configuration for a single AOE instance run. Built from the tier definition and
 * map copy parameters and used to derive spawn locations, aggression settings, etc.
 */
public class AoeZoneDefinition {

    public static class SpawnTemplate {
        private final int npcId;
        private final int count;

        public SpawnTemplate(int npcId, int count) {
            this.npcId = npcId;
            this.count = Math.max(1, count);
        }

        public int getNpcId() {
            return npcId;
        }

        public int getCount() {
            return count;
        }
    }

    private final String id;
    private final Boundary bounds;
    private final int centerX;
    private final int centerY;
    private final int height;
    private final int rows;
    private final int cols;
    private final int spacing;
    private final int respawnDelayTicks;
    private final int aggressionRadius;
    private final List<SpawnTemplate> spawns;

    public AoeZoneDefinition(String id, Boundary bounds, int centerX, int centerY, int height, int rows, int cols,
                             int spacing, int respawnDelayTicks, int aggressionRadius, List<SpawnTemplate> spawns) {
        this.id = id;
        this.bounds = bounds;
        this.centerX = centerX;
        this.centerY = centerY;
        this.height = height;
        this.rows = rows;
        this.cols = cols;
        this.spacing = spacing;
        this.respawnDelayTicks = respawnDelayTicks;
        this.aggressionRadius = aggressionRadius;
        this.spawns = Collections.unmodifiableList(new ArrayList<>(spawns));
    }

    public String getId() {
        return id;
    }

    public Boundary getBounds() {
        return bounds;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getHeight() {
        return height;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getSpacing() {
        return spacing;
    }

    public int getRespawnDelayTicks() {
        return respawnDelayTicks;
    }

    public int getAggressionRadius() {
        return aggressionRadius;
    }

    public List<SpawnTemplate> getSpawns() {
        return spawns;
    }
}
