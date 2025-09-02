package io.xeros.content.instances.aoe;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.List;

/**
 * Adapter for displaying loot using the Treasure Trails reward interface.
 */
public class AoeTreasureTrailsAdapter {

    public static void openLootViewer(Player player, String title, List<GameItem> items) {
        if (player == null) return;
        player.getPA().sendString(title, 6961);
        player.getTrails().displayRewards(items);
    }
}
