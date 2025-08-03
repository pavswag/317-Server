package io.xeros.content.instances;

import io.xeros.model.entity.player.Player;

/**
 * Handles displaying boss instance progress information to players.
 */
public class BossInstanceUIManager {

    /**
     * Shows an overlay containing the player's current boss instance progress.
     * The information is written to interface text fields so it can appear in
     * the player's sidebar or chatbox.
     */
    public static void show(Player player) {
        BossInstanceManager.BossInstanceArea area = BossInstanceManager.get(player);
        if (area == null) {
            return;
        }

        BossInstanceManager.BossTier tier = area.getTier();
        int current = player.getTierKillCounts().getOrDefault(tier, 0);
        BossInstanceManager.BossTier next = tier.getNextTier();

        player.getPA().sendFrame126("\uD83E\uDDF1 Tier: " + BossInstanceManager.getTierDisplayNameSafe(tier), 8144);
        player.getPA().sendFrame126("\u2620\uFE0F Kills: " + current + " / " + tier.getRequiredKillCountToUnlockNext(), 8145);
        player.getPA().sendFrame126("\uD83D\uDD13 Next: " + (next != null ? BossInstanceManager.getTierDisplayNameSafe(next) : "Maxed"), 8146);
        player.getPA().sendFrame126("", 8147);
        player.getPA().sendFrame126("", 8148);
    }
}
