package io.xeros.content.item.lootable.impl;

import com.google.common.collect.Lists;
import io.xeros.content.item.lootable.ItemLootable;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.List;
import java.util.Map;

public class ResourceBoxSmall extends ItemLootable {

    public static final int BOX_ITEM = 30_000;

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return Map.of(LootRarity.COMMON, Lists.newArrayList(
                new GameItem(200, 1),  //grimy guam
                new GameItem(202, 1),  //grimy marrentill
                new GameItem(204, 1),  //grimy tarromin
                new GameItem(206, 1),  //grimy harralander
                new GameItem(208, 1),  //grimy ranarr weed
                new GameItem(441, 1),   //iron ore
                new GameItem(454, 1),   //coal
                new GameItem(448, 1),   //mithril ore
                new GameItem(2350, 1),  //bronze bar
                new GameItem(2352, 1),  //iron bar
                new GameItem(2354, 1),  //steel bar
                new GameItem(2360, 1),  //mithril bar
                new GameItem(1512, 1),  //logs
                new GameItem(1522, 1),  //oak logs
                new GameItem(1520, 1),  //willow logs
                new GameItem(1518, 1),  //maple logs
                new GameItem(1624, 1),  //uncut sapphire
                new GameItem(1622, 1),  //uncut emerald
                new GameItem(360, 1),   //raw tuna
                new GameItem(378, 1),   //raw lobster
                new GameItem(364, 1),   //raw bass
                new GameItem(372, 1),   //raw swordfish
                new GameItem(Items.LIMPWURT_ROOT_NOTED, 3),
                new GameItem(Items.RED_SPIDERS_EGGS_NOTED, 5),


                new GameItem(200, 3),  //grimy guam
                new GameItem(202, 3),  //grimy marrentill
                new GameItem(204, 3),  //grimy tarromin
                new GameItem(206, 3),  //grimy harralander
                new GameItem(208, 3),  //grimy ranarr weed
                new GameItem(441, 3),   //iron ore
                new GameItem(454, 3),   //coal
                new GameItem(448, 3),   //mithril ore
                new GameItem(2350, 3),  //bronze bar
                new GameItem(2352, 3),  //iron bar
                new GameItem(2354, 3),  //steel bar
                new GameItem(2360, 3),  //mithril bar
                new GameItem(1512, 3),  //logs
                new GameItem(1522, 3),  //oak logs
                new GameItem(1520, 3),  //willow logs
                new GameItem(1518, 3),  //maple logs
                new GameItem(1624, 3),  //uncut sapphire
                new GameItem(1622, 3),  //uncut emerald
                new GameItem(360, 3),   //raw tuna
                new GameItem(378, 3),   //raw lobster
                new GameItem(364, 3),   //raw bass
                new GameItem(372, 3),   //raw swordfish
                new GameItem(Items.LIMPWURT_ROOT_NOTED, 6),
                new GameItem(Items.RED_SPIDERS_EGGS_NOTED, 6)
        ));
    }
    /**
     * Quickly open all large resource boxes in the player's inventory.
     */
    public void quickOpen(Player player) {
        int amount = player.getItems().getInventoryCount(BOX_ITEM);

        if (amount > 1000) {
            amount = 1000;
        }

        for (int i = 0; i < amount; i++) {
            if (!player.getItems().playerHasItem(BOX_ITEM)) {
                break;
            }
            roll(player);
        }
    }
    @Override
    public int getLootableItem() {
        return BOX_ITEM;
    }

    @Override
    public int getRollCount() {
        return 3;
    }
}
