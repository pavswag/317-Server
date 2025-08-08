package io.xeros.content.activityboss;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

/**
 * Simple loot table for global bosses.
 */
public class GlobalBossLootTable {

    private static final GameItem[] COMMON = {
            new GameItem(995, 5_000_000),
            new GameItem(13307, 300),
            new GameItem(1631, 1),
            new GameItem(6199, 1),
            new GameItem(11235, 1)
    };

    private GlobalBossLootTable() {
    }

    public static GameItem rollRewardFor(Player player) {
        return COMMON[Misc.random(COMMON.length - 1)];
    }
}
