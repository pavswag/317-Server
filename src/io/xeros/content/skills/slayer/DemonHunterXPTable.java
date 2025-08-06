package io.xeros.content.skills.slayer;

/**
 * Utility table used for calculating Demon Hunter experience rewards.
 */
public class DemonHunterXPTable {

    /**
     * Returns the amount of Demon Hunter experience granted per kill based on the
     * boss tier and the player's current Demon Hunter level.
     */
    public static int getXPFor(DemonSlayerMaster.Tier tier, int playerLevel) {
        int base = 50 * tier.getMultiplier();
        return base + (playerLevel * 5 * tier.getMultiplier());
    }
}
