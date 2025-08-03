package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.MysteryBoxLootable;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChristmasBox extends MysteryBoxLootable {
    /**
     * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
     */
    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    /**
     * Stores an array of items into each map with the corresponding rarity to the list
     */
    static {
        items.put(LootRarity.COMMON,
                Arrays.asList(
                        new GameItem(12892,1),  //Anti-santa mask
                        new GameItem(12893,1),  //Anti-santa body
                        new GameItem(12894,1),  //Anti-santa legs
                        new GameItem(12895,1),  //Anti-santa gloves
                        new GameItem(12896,1)  //Anti-santa boots
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(6769,1),   //$5 scroll
                        new GameItem(696, 20),  //5m Foe
                        new GameItem(13346,1),  //Ultra Mystery Box
                        new GameItem(6828, 2),  //Smb
                        new GameItem(27566,1),  //Christmas jumper
                        new GameItem(27566,1)   //Gnome child backpack
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(33159,1),  //Christmas imp
                        new GameItem(33212,1),  //Snowman pet
                        new GameItem(33211,1),  //Elf pet
                        new GameItem(33210,1),  //Gingie pet
                        new GameItem(12161,1),  //Re-Roll
                        new GameItem(2403,1),   //$10
                        new GameItem(12161,1),  //Re-Roll
                        new GameItem(8167, 2),  //Nomad chest
                        new GameItem(12161,1),  //Re-Roll
                        new GameItem(21695,1),  //RuneFest shield
                        new GameItem(12161,1),  //Re-Roll
                        new GameItem(26219,1),  //Osmumtens fang
                        new GameItem(12161,1),  //Re-Roll
                        new GameItem(33149,1),  //Nox staff
                        new GameItem(12161,1),  //Re-Roll
                        new GameItem(962,1),    //Christmas Cracker
                        new GameItem(12161,1)   //Re-Roll
                ));
    }

    /**
     * Constructs a new myster box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public ChristmasBox(Player player) {
        super(player);
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    @Override
    public int getItemId() {
        return 12161;
    }

    public static void openChristmasBox(Player player, int boxId) {
        if (player == null) {
            return;
        }
        if (!player.getItems().playerHasItem(boxId)) {
            return;
        }

        player.getItems().deleteItem(boxId, 1);
        player.sendMessage("\uD83C\uDF81 You open the Christmas Box and receive:");

        int rolls = 1 + Misc.random(2); // 1-3 items
        for (int i = 0; i < rolls; i++) {
            LootRarity rarity = rollRarity();
            List<GameItem> possible = items.get(rarity);
            if (possible == null || possible.isEmpty()) {
                continue;
            }
            GameItem reward = Misc.getRandomItem(possible);
            if (reward == null) {
                continue;
            }

            player.getItems().addItemUnderAnyCircumstance(reward.getId(), reward.getAmount());
            String name = ItemDef.forId(reward.getId()).getName();
            player.sendMessage("- " + name + (reward.getAmount() > 1 ? " x " + reward.getAmount() : ""));

            if (rarity == LootRarity.RARE) {
                PlayerHandler.executeGlobalMessage("@blu@[Christmas Box] " + player.getDisplayNameFormatted() +
                        " received a drop: " + name + (reward.getAmount() > 1 ? " x " + reward.getAmount() : "") + ".");
            }
        }
    }

    private static LootRarity rollRarity() {
        double rand = Math.random();
        if (rand < 0.05) {
            return LootRarity.RARE;
        }
        if (rand < 0.35) {
            return LootRarity.UNCOMMON;
        }
        return LootRarity.COMMON;
    }
}
