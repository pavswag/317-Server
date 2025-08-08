package io.xeros.content.instances;

import io.xeros.model.entity.player.Player;

import static io.xeros.content.instances.InstanceMutatorManager.getActiveDisplay;

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
        BossInstanceManager.BossTier next = tier.getNextTier();

        player.getPA().sendFrame126("\uD83E\uDDF1 Tier: " + BossInstanceManager.getTierDisplayNameSafe(tier), 8144);
        player.getPA().sendFrame126("\u2620\uFE0F Kills: " + current + " / " + tier.getRequiredKillCountToUnlockNext(), 8145);
        player.getPA().sendFrame126("\uD83D\uDD13 Unlocks: " + (next != null ? BossInstanceManager.getTierDisplayNameSafe(next) : "Maxed"), 8146);
        player.getPA().sendFrame126("Mutators: " + getActiveDisplay(), 8147);
        player.getPA().sendFrame126("Danger: " + InstanceMutatorManager.getDangerLevel() + "%", 8148);
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
