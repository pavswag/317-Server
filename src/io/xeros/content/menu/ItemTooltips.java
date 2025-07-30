package io.xeros.content.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.content.upgrade.UpgradeMaterials;
import io.xeros.model.definitions.ItemDef;
import io.xeros.util.Misc;

import io.xeros.model.Items;

/**
 * Central registry of item tooltip text.
 */
public class ItemTooltips {

    /**
     * Map of item id to tooltip entry.
     */
    public static final Map<Integer, HoverMenu> MENUS = new HashMap<>();

    static {
        // Fire of Exchange burn values from the server definitions
        for (int id = 0; id < 45_000; id++) {
            int burn = FireOfExchangeBurnPrice.getBurnPrice(null, id, false);
            if (burn > 0) {
                MENUS.put(id, new HoverMenu("Dissolves in the Fire of Exchange for @lre@" +
                        Misc.insertCommas(burn) + "@whi@ FOE."));
            }
        }

        // Upgradeable items
        for (UpgradeMaterials upgrade : UpgradeMaterials.values()) {
            MENUS.put(upgrade.getRequired().getId(), new HoverMenu(
                    "Upgrades into " + ItemDef.forId(upgrade.getReward().getId()).getName() +
                            " for @lre@" + Misc.insertCommas((int) upgrade.getCost()) +
                            "@whi@ coins (" + upgrade.getSuccessRate() + "% success)."));
        }

        // Item option effects
        MENUS.put(Items.FISHING_PASS, new HoverMenu("Activates a membership pass when used."));
        MENUS.put(11740, new HoverMenu("Teleports you to the Island activity when used."));
        MENUS.put(Items.DWARVEN_ROCK_CAKE, new HoverMenu("Deals damage to lower your hitpoints (\"URGHHHHH!\")."));
        MENUS.put(6644, new HoverMenu("Opens a crystal container and grants several reward items."));

        // Herblore secondary ingredients
        MENUS.put(145, new HoverMenu("Used to create Super Combat 3 potions."));  // Super attack(3)
        MENUS.put(157, new HoverMenu("Used to create Super Combat 3 potions."));  // Super strength(3)
        MENUS.put(163, new HoverMenu("Used to create Super Combat 3 potions."));  // Super defence(3)
        MENUS.put(221, new HoverMenu("Used to create Attack, Super Attack potions."));  // Eye of newt
        MENUS.put(223, new HoverMenu("Used to create Guthix Balance, Restore, Super Restore, Weapon Poison Plus potions."));  // Red spiders' eggs
        MENUS.put(225, new HoverMenu("Used to create Strength, Super Strength potions."));  // Limpwurt root
        MENUS.put(231, new HoverMenu("Used to create Fishing, Prayer potions."));  // Snape grass
        MENUS.put(235, new HoverMenu("Used to create Antipoison, Sanfew 3, Sanfew 4, Super Antipoison potions."));  // Unicorn horn dust
        MENUS.put(239, new HoverMenu("Used to create Defence, Super Defence potions."));  // White berries
        MENUS.put(241, new HoverMenu("Used to create Antifire, Weapon Poison potions."));  // Dragon scale dust
        MENUS.put(245, new HoverMenu("Used to create Ranging potions."));  // Wine of zamorak
        MENUS.put(247, new HoverMenu("Used to create Zamorak Brew potions."));  // Jangerberries
        MENUS.put(269, new HoverMenu("Used to create Anti Venom Plus potions."));  // Clean torstol
        MENUS.put(1550, new HoverMenu("Used to create Guthix Balance potions."));  // Garlic
        MENUS.put(1975, new HoverMenu("Used to create Energy potions."));  // Chocolate dust
        MENUS.put(2152, new HoverMenu("Used to create Agility potions."));  // Toad's legs
        MENUS.put(2398, new HoverMenu("Used to create Weapon Poison Plus Plus potions."));  // Nightshade
        MENUS.put(2436, new HoverMenu("Used to create Super Combat 4 potions."));  // Super attack(4)
        MENUS.put(2440, new HoverMenu("Used to create Super Combat 4 potions."));  // Super strength(4)
        MENUS.put(2442, new HoverMenu("Used to create Super Combat 4 potions."));  // Super defence(4)
        MENUS.put(2970, new HoverMenu("Used to create Super Energy potions."));  // Mort myre fungi
        MENUS.put(3138, new HoverMenu("Used to create Magic potions."));  // Potato cactus
        MENUS.put(6016, new HoverMenu("Used to create Weapon Poison Plus potions."));  // Cactus spine
        MENUS.put(6018, new HoverMenu("Used to create Weapon Poison Plus Plus potions."));  // Poisonivy berries
        MENUS.put(6049, new HoverMenu("Used to create Antidote Plus potions."));  // Yew roots
        MENUS.put(6051, new HoverMenu("Used to create Antidote Plus Plus potions."));  // Magic roots
        MENUS.put(6693, new HoverMenu("Used to create Saradomin Brew potions."));  // Crushed nest
        MENUS.put(7650, new HoverMenu("Used to create Guthix Balance potions."));  // Silver dust
        MENUS.put(9736, new HoverMenu("Used to create Combat potions."));  // Goat horn dust
        MENUS.put(11994, new HoverMenu("Used to create Extended Antifire, Extended Super Antifire potions."));  // Lava scale shard
        MENUS.put(12640, new HoverMenu("Used to create Stamina potions."));  // Amylase crystal
        MENUS.put(12934, new HoverMenu("Used to create Anti Venom 1, Anti Venom 2, Anti Venom 3, Anti Venom 4 potions."));  // Zulrah's scales
        MENUS.put(20698, new HoverMenu("Used to create Rejuv Pot potions."));  // Bruma herb
        MENUS.put(21975, new HoverMenu("Used to create Super Antifire potions."));  // Crushed Superior Dragon dust
        MENUS.put(23867, new HoverMenu("Used to create Divine Magic Potion 3, Divine Magic Potion 4, Divine Ranging Potion 3, Divine Ranging Potion 4, Divine Super Combat 3, Divine Super Combat 4 potions."));  // Crystal dust

        // After populating the map, dump to json for clients
        exportToJson("item_tooltips.json");
    }

    private ItemTooltips() {
        // Utility class
    }

    /**
     * Writes the current tooltip mapping to a JSON file so the client can load it.
     */
    public static void exportToJson(String file) {
        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Map<Integer, String> data = MENUS.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getText()));
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
