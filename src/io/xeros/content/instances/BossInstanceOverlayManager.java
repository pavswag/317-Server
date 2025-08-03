package io.xeros.content.instances;

import io.xeros.model.entity.player.Player;

/**
 * Handles displaying boss instance progress information to players.
 */
public class BossInstanceOverlayManager {

    /**
     * Writes the player's current boss instance progress to interface text
     * fields so it can appear in the sidebar or chatbox.
     */
    public static void sendKillOverlay(Player player) {
        BossInstanceManager.BossInstanceArea area = BossInstanceManager.get(player);
        if (area == null) {
            return;
        }

        BossInstanceManager.BossTier tier = area.getTier();
        int current = player.getTierKillCounts().getOrDefault(tier, 0);
        int required = tier.getRequiredKillCountToUnlockNext();
        int remaining = Math.max(0, required - current);
        BossInstanceManager.BossTier next = tier.getNextTier();

        player.getPA().sendFrame126("\uD83E\uDDF1 Instance Tier: " + BossInstanceManager.getTierDisplayNameSafe(tier), 8144);
        player.getPA().sendFrame126("\u2694\uFE0F Kills: " + current + "/" + required, 8145);
        String unlockText = next == null ? "Maxed" : (remaining + " more kills");
        player.getPA().sendFrame126("\uD83D\uDD13 Next Tier: " + unlockText, 8146);
        player.getPA().sendFrame126("", 8147);
        player.getPA().sendFrame126("", 8148);
    }

    /**
     * Clears any overlay text previously written by {@link #sendKillOverlay(Player)}.
     */
    public static void clear(Player player) {
        player.getPA().sendFrame126("", 8144);
        player.getPA().sendFrame126("", 8145);
        player.getPA().sendFrame126("", 8146);
        player.getPA().sendFrame126("", 8147);
        player.getPA().sendFrame126("", 8148);
    }
}
