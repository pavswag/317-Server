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


public class ArboBox extends MysteryBoxLootable {

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
                        new GameItem(6678),   //Mini umb
                        new GameItem(12585),  //Mini cox
                        new GameItem(19895),  //Mini tob
                        new GameItem(6680),   //Mini arbo

                        new GameItem(10350),  //3rd age helm
                        new GameItem(10350),  //3rd age helm

                        new GameItem(10346),  //3rd age platelegs
                        new GameItem(10346),  //3rd age platelegs

                        new GameItem(10348),  //3rd age platebody
                        new GameItem(10348),  //3rd age platebody

                        new GameItem(10352),  //3rd age kite
                        new GameItem(10352),  //3rd age kite

                        new GameItem(10334),  //3rd age range coif
                        new GameItem(10334),  //3rd age range coif

                        new GameItem(10330),  //3rd age range top
                        new GameItem(10330),  //3rd age range top

                        new GameItem(10332),  //3rd age range bottom
                        new GameItem(10332),  //3rd age range bottom

                        new GameItem(10342),  //3rd age mage hat
                        new GameItem(10342),  //3rd age mage hat

                        new GameItem(10338),  //3rd age robe top
                        new GameItem(10338),  //3rd age robe top

                        new GameItem(10340),  //3rd age robe bottom
                        new GameItem(10340),  //3rd age robe bottom

                        new GameItem(10344),  //3rd age amulet
                        new GameItem(10344),  //3rd age amulet

                        new GameItem(12691),  //Tyrannical Ring
                        new GameItem(12692)   //Treasonous ring
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(

                        new GameItem(696, 20),  //5m Nomad
                        new GameItem(12806),  //Maledtiction ward
                        new GameItem(12807),  //Odium ward

                        new GameItem(26498),  //Book (or) unholy
                        new GameItem(26498),  //Book (or) unholy

                        new GameItem(26496),  //Book (or) holy
                        new GameItem(26496),  //Book (or) holy

                        new GameItem(26494),  //Book (or) war
                        new GameItem(26494),  //Book (or) war

                        new GameItem(26492),  //Book (or) law
                        new GameItem(26492),  //Book (or) law

                        new GameItem(26490),  //Book (or) darkness
                        new GameItem(26490),  //Book (or) darkness

                        new GameItem(26488),  //Book (or) balance
                        new GameItem(26488)   //Book (or) balance
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(

                        new GameItem(696, 20),  //10m Nomad

                        new GameItem(26714),  //Arma (or) helm
                        new GameItem(26714),  //Arma (or) helm

                        new GameItem(26715),  //Arma (or) chest
                        new GameItem(26715),  //Arma (or) chest

                        new GameItem(26716),  //Arma (or) legs
                        new GameItem(26716),  //Arma (or) legs

                        new GameItem(26225),  //Ancient Ceremonial mask
                        new GameItem(26221),  //Ancient Ceremonial chest
                        new GameItem(26223),  //Ancient Ceremonial legs
                        new GameItem(2400),   //Arbo key
                        new GameItem(30014),  //K'klilk
                        new GameItem(20370),  //Bandos godsword (or)
                        new GameItem(20374),  //Zamorak godsword (or)
                        new GameItem(20372),  //Saradomin godsword (or)
                        new GameItem(20368)   //Armadyl godsword (or)
                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(

                        new GameItem(696, 20),  //50m Nomad

                        new GameItem(2400),   //Arbo key
                        new GameItem(2400),   //Arbo key

                        new GameItem(27285),  //Eye of corrupter
                        new GameItem(25979),  //Keris partison
                        new GameItem(26219),  //Osmumten's fang
                        new GameItem(33149),  //Noxious staff
                        new GameItem(25739),  //Sanguine scythe
                        new GameItem(25985),  //Elidinis' ward
                        new GameItem(25975),  //Lightbearer ring

                        new GameItem(12582),   //Coxbox
                        new GameItem(12582),   //Coxbox

                        new GameItem(19891),   //Tobbox
                        new GameItem(19891)    //Tobbox
                ));
    }

    /**
     * Constructs a new mystery box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public ArboBox(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 12579;
    }


    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items; }
}
