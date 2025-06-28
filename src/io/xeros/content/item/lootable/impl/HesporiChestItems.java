package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.model.Items;
import io.xeros.model.items.GameItem;

import java.util.*;

public class HesporiChestItems {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static Map<LootRarity, List<GameItem>> getItems() {
        return items;
    }

    public static ArrayList<GameItem> getRareDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = items.get(LootRarity.RARE);
        List<GameItem> found2 = items.get(LootRarity.VERY_RARE);
        for(GameItem f : found) {
            boolean foundItem = false;
            for(GameItem drop : drops) {
                if (drop.getId() == f.getId()) {
                    foundItem = true;
                    break;
                }
            }
            if (!foundItem) {
                drops.add(f);
            }
        }
        for (GameItem f : found2) {

            boolean foundItem = false;
            for(GameItem drop : drops) {
                if (drop.getId() == f.getId()) {
                    foundItem = true;
                    break;
                }
            }
            if (!foundItem) {
                drops.add(f);
            }
        }
        return drops;
    }

    static {
        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(21046, 2),//15% chest rate tomb
                new GameItem(Items.BURNT_PAGE, 15),

                new GameItem(Items.OVERLOAD_4, 2),

                new GameItem(Items.ADAMANT_DART_TIP, 75),
                new GameItem(Items.RUNE_DART_TIP, 50),
                new GameItem(Items.DRAGON_DART_TIP, 25),

                new GameItem(Items.RUNE_ARROWTIPS, 50),
                new GameItem(Items.DRAGON_ARROWTIPS, 25),

                new GameItem(Items.WILLOW_LOGS_NOTED, 200),
                new GameItem(Items.MAPLE_LOGS_NOTED, 150),
                new GameItem(Items.YEW_LOGS_NOTED, 100),
                new GameItem(Items.MAGIC_LOGS_NOTED, 50),

                new GameItem(Items.YEW_ROOTS_NOTED, 50),
                new GameItem(Items.MAGIC_ROOTS_NOTED, 50),

                new GameItem(Items.CRUSHED_NEST_NOTED, 50),

                new GameItem(Items.WINE_OF_ZAMORAK_NOTED, 50),

                new GameItem(Items.SNAPE_GRASS_NOTED, 50),

                new GameItem(Items.RUNITE_BOLTS_UNF, 50),
                new GameItem(Items.DRAGON_BOLTS_UNF, 25),

                new GameItem(Items.RAW_LOBSTER_NOTED, 10),
                new GameItem(Items.RAW_SHARK_NOTED, 25),

                new GameItem(Items.STEEL_BAR_NOTED, 250),
                new GameItem(Items.MITHRIL_BAR_NOTED, 200),
                new GameItem(Items.ADAMANTITE_BAR_NOTED, 100),
                new GameItem(Items.RUNITE_BAR_NOTED, 25),

                new GameItem(Items.UNCUT_SAPPHIRE_NOTED, 50),
                new GameItem(Items.UNCUT_DIAMOND_NOTED, 25),

                new GameItem(Items.HARRALANDER_POTION_UNF_NOTED, 50),
                new GameItem(Items.RANARR_POTION_UNF_NOTED, 25),
                new GameItem(Items.DWARF_WEED_POTION_UNF_NOTED, 25),

                new GameItem(Items.HARRALANDER_NOTED, 50),
                new GameItem(Items.IRIT_LEAF_NOTED, 50),
                new GameItem(Items.RANARR_WEED_NOTED, 25),

                new GameItem(Items.IRIT_SEED, 25),
                new GameItem(Items.RANARR_SEED, 25),
                new GameItem(Items.TOADFLAX_SEED, 15)
        ));

        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(Items.DRAGON_AXE, 1),
                new GameItem(Items.DRAGON_AXE, 1),
                new GameItem(Items.DRAGON_AXE, 1),
                new GameItem(Items.DRAGON_AXE, 1),

                new GameItem(Items.ATTAS_SEED, 1),
                new GameItem(Items.ATTAS_SEED, 1),
                new GameItem(Items.ATTAS_SEED, 1),

                new GameItem(Items.GOLPAR_SEED, 1),
                new GameItem(Items.GOLPAR_SEED, 1),
                new GameItem(Items.GOLPAR_SEED, 1),

                new GameItem(Items.NOXIFER_SEED, 1),
                new GameItem(Items.NOXIFER_SEED, 1),
                new GameItem(Items.NOXIFER_SEED, 1),

                new GameItem(Items.BUCHU_SEED, 1),
                new GameItem(Items.BUCHU_SEED, 1),
                new GameItem(Items.BUCHU_SEED, 1),

                new GameItem(Items.CELASTRUS_SEED, 1),
                new GameItem(Items.CELASTRUS_SEED, 1),
                new GameItem(Items.CELASTRUS_SEED, 1),

                new GameItem(Items.CONSECRATION_SEED, 1),
                new GameItem(Items.CONSECRATION_SEED, 1),
                new GameItem(Items.CONSECRATION_SEED, 1)
        ));
        items.put(LootRarity.VERY_RARE, Arrays.asList(
                new GameItem(Items.TOME_OF_FIRE_EMPTY, 1),
                new GameItem(Items.TOME_OF_FIRE_EMPTY, 1),

                new GameItem(Items.IASOR_SEED, 1),
                new GameItem(Items.KRONOS_SEED, 1)
        ));
    }

}
