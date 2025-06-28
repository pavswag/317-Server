package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.MysteryBoxLootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Revamped a simple means of receiving a random item based on chance.
 *
 * @author Junior
 * @date Feb 14, 2024 12:04 AM
 */


public class CosmeticBox extends MysteryBoxLootable {

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
                        new GameItem(24315),  //Spooky robes
                        new GameItem(24317),  //Spooky robes
                        new GameItem(24319),  //Spooky robes
                        new GameItem(24323),  //Spooky robes
                        new GameItem(24321),  //Spooky robes
                        new GameItem(13385),  //xerician robes
                        new GameItem(13387),  //xerician robes
                        new GameItem(13389)   //xerician robes
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(20720),  //bruma torch
                        new GameItem(4508),   //decorative sword
                        new GameItem(4068),   //decorative sword
                        new GameItem(4503),   //decorative sword
                        new GameItem(24219),  //Swift blade
                        new GameItem(27804),  //cake hat
                        new GameItem(28118),  //colourful jumper
                        new GameItem(28119),  //colourful jumper
                        new GameItem(28120),  //colourful jumper
                        new GameItem(28121),  //colourful jumper
                        new GameItem(28122),  //colourful jumper
                        new GameItem(28123),  //colourful jumper
                        new GameItem(28124),  //colourful jumper
                        new GameItem(28125)   //colourful jumper
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(24305),  //Spooky robes
                        new GameItem(24307),  //Spooky robes
                        new GameItem(24309),  //Spooky robes
                        new GameItem(24313),  //Spooky robes
                        new GameItem(24311),  //Spooky robes
                        new GameItem(28115),  //Colourful scarf
                        new GameItem(28113),  //Colourful scarf
                        new GameItem(28114),  //Colourful scarf
                        new GameItem(28108),  //Colourful scarf
                        new GameItem(28109),  //Colourful scarf
                        new GameItem(28110),  //Colourful scarf
                        new GameItem(28111),  //Colourful scarf
                        new GameItem(28112),  //Colourful scarf
                        new GameItem(13359),  //Shayzien (1)
                        new GameItem(13361),  //Shayzien (1)
                        new GameItem(13360),  //Shayzien (1)
                        new GameItem(13358),  //Shayzien (1)
                        new GameItem(13357)   //Shayzien (1)
                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(
                        new GameItem(2581),   //Robin hood
                        new GameItem(12596),  //Rangers tunic
                        new GameItem(23249),  //Rangers tights
                        new GameItem(2577),   //Ranger boots
                        new GameItem(19994),  //Ranger gloves
                        new GameItem(10858),  //Shadow sword
                        new GameItem(25606),  //propeller hat
                        new GameItem(20125),  //shayzien hood
                        new GameItem(19955),  //shayzien scarf
                        new GameItem(20263)   //shayzien banner
                ));
    }

    /**
     * Constructs a new mystery box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public CosmeticBox(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 19897;
    }


    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }
}