package io.xeros.content.instances;

import io.xeros.model.entity.player.Player;
import io.xeros.model.Items;

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
        // Base completion reward scales with tougher bosses
        player.getItems().addItem(Items.COINS, 50_000);
        player.sendMessage("You receive 50,000 coins for completing " + BossInstanceManager.getTierDisplayNameSafe(tier) + ".");
    }

    /** Grants a tier-scaled bonus when a player hits a killstreak milestone. */
    public static void rewardKillstreak(Player player, BossInstanceManager.BossTier tier, int streak) {
        int coins = calculateKillstreakReward(tier, streak);
        addCoinsOrTokens(player, coins,
                "@blu@Killstreak " + streak + "! You receive %s.");
    }

    /** Grants a bonus for AoE killstreak milestones. */
    public static void rewardAoeKillstreak(Player player, BossInstanceManager.BossTier tier, int streak) {
        int coins = calculateAoeKillstreakReward(tier, streak);
        addCoinsOrTokens(player, coins,
                "@red@AoE streak " + streak + "! Bonus %s.");
    }

    /** Small reward from direct hazard hits scaled by tier. */
    public static void rewardHazardDrop(Player player, BossInstanceManager.BossTier tier) {
        int coins = 200 * (tier.ordinal() + 1);
        player.getItems().addItem(Items.COINS, coins);
    }

    /** Calculates the coin reward for a killstreak without modifying player state. */
    public static int calculateKillstreakReward(BossInstanceManager.BossTier tier, int streak) {
        int level = streak / 10;
        return 10_000 * level * (tier.ordinal() + 1);
    }

    /** Calculates the coin reward for an AoE killstreak milestone. */
    public static int calculateAoeKillstreakReward(BossInstanceManager.BossTier tier, int streak) {
        int level = streak / 5;
        return 4_000 * level * (tier.ordinal() + 1);
    }

    private static final int TOKEN_VALUE = 1_000; // Platinum token value
    private static final int TOKEN_CONVERSION_THRESHOLD = 100_000_000;

    /** Adds coins to the player, converting to platinum tokens if amount exceeds threshold. */
    private static void addCoinsOrTokens(Player player, int coins, String messagePattern) {
        if (coins >= TOKEN_CONVERSION_THRESHOLD) {
            int tokens = coins / TOKEN_VALUE;
            int remainder = coins % TOKEN_VALUE;
            if (tokens > 0) {
                player.getItems().addItem(Items.PLATINUM_TOKEN, tokens);
            }
            if (remainder > 0) {
                player.getItems().addItem(Items.COINS, remainder);
            }
            String rewardMsg = tokens + " platinum tokens" + (remainder > 0 ? " and " + remainder + " coins" : "");
            player.sendMessage(String.format(messagePattern, rewardMsg));
        } else {
            player.getItems().addItem(Items.COINS, coins);
            player.sendMessage(String.format(messagePattern, coins + " coins"));
        }
    }
}
