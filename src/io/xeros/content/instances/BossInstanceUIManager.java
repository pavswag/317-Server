package io.xeros.content.instances;

import io.xeros.model.entity.player.Player;

/**
 * Handles displaying boss instance progress information to players.
 */
public class BossInstanceUIManager {

    /**
     * Sends an overlay to the player showing their current tier progress.
     * Displays the tier name, current kill count within the tier, and the next
     * tier that will be unlocked.
     */
    public static void sendKillOverlay(Player player) {
        BossInstanceManager.BossInstanceArea area = BossInstanceManager.get(player);
        if (area == null) {
            return;
        }

        BossInstanceManager.BossTier tier = area.getTier();
        int current = player.getTierKillCounts().getOrDefault(tier, 0);
        int required = tier.getRequiredKillCountToUnlockNext();
        BossInstanceManager.BossTier next = tier.getNextTier();
        String nextName = next != null ? BossInstanceManager.getTierDisplayNameSafe(next) : "Maxed";

        player.sendMessage("\uD83E\uDDF1 Tier: " + BossInstanceManager.getTierDisplayNameSafe(tier));
        player.sendMessage("\u2620\uFE0F Kills: " + current + " / " + required);
        player.sendMessage("\uD83D\uDD13 Unlocks: " + nextName);
    }
}
