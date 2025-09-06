package io.xeros.content.instances.aoe;

import java.util.List;

/**
 * Data driven definition for an AOE boss tier. Instances of this class are
 * loaded from {@code data/aoe_tiers.json} via {@link AoeBossTierLoader}.
 */
public class AoeBossTierDef {

    public static class Grid {
        public int rows;
        public int cols;
        public int spacing;
    }

    public static class Npc {
        public String name;
        public int npcId;
        public int count = 1;
    }

    public static class Rewards {
        public double xpMultOnTask = 1.0;
        public double xpMultOffTask = 1.0;
        public double dropMult = 1.0;
        public int fortuneXp = 0;
    }

    public int tier;
    public String zoneName;
    public int unlockKills;
    public Grid aoeGrid;
    public int aggroRange;
    public int respawnSeconds;
    public Npc boss;
    public List<Npc> minions;
    public Rewards rewards;

    // Map template fields for dynamic instance construction
    public int templateRegionId;
    public int templateX;
    public int templateY;
    public int widthChunks = 8;
    public int heightChunks = 8;
    public int rotation = 0;
    public int z = 0;
    public int spawnOffsetX = 4;
    public int spawnOffsetY = 4;
    public boolean useDynamicChunks = true;

    // Set by loader when a tier cannot be used.
    public boolean disabled = false;
    public String disabledReason;

    public int getTier() {
        return tier;
    }

    public String getZoneName() {
        return zoneName;
    }

    public int getUnlockKills() {
        return unlockKills;
    }

    public Grid getAoeGrid() {
        return aoeGrid;
    }

    public int getAggroRange() {
        return aggroRange;
    }

    public int getRespawnSeconds() {
        return respawnSeconds;
    }

    public Npc getBoss() {
        return boss;
    }

    public List<Npc> getMinions() {
        return minions;
    }

    public Rewards getRewards() {
        return rewards;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public String getDisabledReason() {
        return disabledReason;
    }

    public int getTemplateRegionId() {
        return templateRegionId;
    }

    public int getTemplateX() {
        return templateX;
    }

    public int getTemplateY() {
        return templateY;
    }

    public int getWidthChunks() {
        return widthChunks;
    }

    public int getHeightChunks() {
        return heightChunks;
    }

    public int getRotation() {
        return rotation;
    }

    public int getZ() {
        return z;
    }

    public int getSpawnOffsetX() {
        return spawnOffsetX;
    }

    public int getSpawnOffsetY() {
        return spawnOffsetY;
    }

    public boolean isUseDynamicChunks() {
        return useDynamicChunks;
    }
}
