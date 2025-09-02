package io.xeros.content.instances.aoe;

import java.util.List;

/**
 * Data driven definition for an AOE boss tier. Instances of this class are
 * loaded from {@code data/aoe/aoe_boss_tiers.json} via {@link AoeBossTierLoader}.
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
}
