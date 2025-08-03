package io.xeros.content.instances;

import io.xeros.model.entity.player.Player;

/**
 * Grants players rewards for completing a boss instance tier.
 */
public class TierRewardManager {

    /**
     * Gives the player a small reward for finishing the specified tier.
     * This implementation simply grants coins but can be expanded to
     * include items, tier points, or other benefits.
     */
    public static void reward(Player player, BossInstanceManager.BossTier tier) {
        // Placeholder reward: 100k coins
        player.getItems().addItem(995, 100_000);
        player.sendMessage("You receive 100,000 coins for completing " + BossInstanceManager.getTierDisplayNameSafe(tier) + ".");
    }
}
