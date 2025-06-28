package io.xeros.model.items;

import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 04/03/2024
 */
public interface ItemAction {

    void handle(Player player, GameItem item);

    static void registerInventory(int itemId, int option, ItemAction action) {
        ItemDef def = ItemDef.forId(itemId);
        if(def.inventoryActions == null)
            def.inventoryActions = new ItemAction[5];
        def.inventoryActions[option - 1] = action;
    }

}
