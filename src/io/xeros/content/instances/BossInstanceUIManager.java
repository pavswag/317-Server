package io.xeros.content.instances;

import io.xeros.model.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for boss instance related user interfaces.
 */
public class BossInstanceUIManager {

    /**
     * Opens a scrollable interface showing all boss tiers and the player's progress.
     */
    public static void openOverview(Player player) {
        List<String> lines = new ArrayList<>();
        lines.add("Tier | Zone | Unlocked | Killcount");

        BossInstanceManager.BossInstanceArea area = BossInstanceManager.get(player);
        BossInstanceManager.BossTier current = area != null ? area.getTier() : null;

        for (BossInstanceManager.BossTier tier : BossInstanceManager.BossTier.values()) {
            boolean unlocked = player.getUnlockedBossTiers().contains(tier);
            int kc = player.getTierKillCounts().getOrDefault(tier, 0);
            String icon = unlocked ? "\u2705" : "\uD83D\uDD12";
            String prefix = current == tier ? "@yel@" : "";
            String line = prefix + (tier.ordinal() + 1) + "  " + tier.getZoneName() + "  " + icon + "  " + kc + "/" + tier.getRequiredKillCountToUnlockNext();
            lines.add(line);
        }

        player.getPA().openQuestInterface("Boss Instances", lines);
    }
}
