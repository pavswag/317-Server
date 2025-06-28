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


public class TobBox extends MysteryBoxLootable {

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

                        new GameItem(19895),  //Mini tob
                        new GameItem(12585),  //Mini cox
                        new GameItem(6678),   //Mini umb
                        new GameItem(6678),   //Mini umb
                        new GameItem(6678),   //Mini umb
                        new GameItem(6678),   //Mini umb
                        new GameItem(6678),   //Mini umb

                        new GameItem(995, 2_500_000),  //2.3m Gp
                        new GameItem(995, 2_500_000),  //2.3m Gp

                        new GameItem(560, 2500),       //2.5k Deaths
                        new GameItem(560, 2500),       //2.5k Deaths
                        new GameItem(560, 2500),       //2.5k Deaths
                        new GameItem(560, 2500),       //2.5k Deaths
                        new GameItem(560, 2500),       //2.5k Deaths

                        new GameItem(565, 2500),       //2,5k bloods
                        new GameItem(565, 2500),       //2,5k bloods
                        new GameItem(565, 2500),       //2,5k bloods
                        new GameItem(565, 2500),       //2,5k bloods
                        new GameItem(565, 2500),       //2,5k bloods

                        new GameItem(11730, 8),        //Overload (4)
                        new GameItem(11730, 8),        //Overload (4)

                        new GameItem(11212, 2500),     //Dragon arrows
                        new GameItem(11212, 2500),     //Dragon arrows
                        new GameItem(11212, 2500),     //Dragon arrows
                        new GameItem(11212, 2500),     //Dragon arrows
                        new GameItem(11212, 2500),     //Dragon arrows

                        new GameItem(11230, 2500),     //Dragon darts
                        new GameItem(11230, 2500),     //Dragon darts
                        new GameItem(11230, 2500),     //Dragon darts
                        new GameItem(11230, 2500),     //Dragon darts
                        new GameItem(11230, 2500),     //Dragon darts

                        new GameItem(21948, 2500),     //Dragonstone dragon bolts (e)
                        new GameItem(21948, 2500),     //Dragonstone dragon bolts (e)
                        new GameItem(21948, 2500),     //Dragonstone dragon bolts (e)
                        new GameItem(21948, 2500),     //Dragonstone dragon bolts (e)
                        new GameItem(21948, 2500),     //Dragonstone dragon bolts (e)

                        new GameItem(452, 1500),       //Rune ore
                        new GameItem(452, 1500),       //Rune ore
                        new GameItem(452, 1500),       //Rune ore
                        new GameItem(452, 1500),       //Rune ore
                        new GameItem(452, 1500),       //Rune ore

                        new GameItem(2364, 1500),      //Rune bars
                        new GameItem(2364, 1500),      //Rune bars
                        new GameItem(2364, 1500),      //Rune bars
                        new GameItem(2364, 1500),      //Rune bars
                        new GameItem(2364, 1500)       //Rune bars
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(

                        new GameItem(696, 20),   //5m Nomad
                        new GameItem(6677, 50),  //Mini Smb
                        new GameItem(6677, 50),  //Mini Smb
                        new GameItem(6677, 50),  //Mini Smb
                        new GameItem(6677, 50),  //Mini Smb
                        new GameItem(6677, 50),  //Mini Smb
                        new GameItem(13346, 2),  //Umb

                        new GameItem(27624),  //Ancient sceptre

                        new GameItem(22613),  //Vesta long
                        new GameItem(22613)   //Vesta long
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(

                        new GameItem(696, 40),  //10m Nomad
                        new GameItem(6678, 50), //Mini Umb
                        new GameItem(6678, 50), //Mini Umb
                        new GameItem(6678, 50), //Mini Umb
                        new GameItem(6678, 50), //Mini Umb
                        new GameItem(6678, 50), //Mini Umb

                        new GameItem(25918),   //Dragon hunter c'bow (b)

                        new GameItem(25904),   //Vamp slayer helm
                        new GameItem(25904),   //Vamp slayer helm
                        new GameItem(25904),   //Vamp slayer helm

                        new GameItem(22477),   //Avernic defender
                        new GameItem(22477),   //Avernic defender
                        new GameItem(22477),   //Avernic defender
                        new GameItem(22477),   //Avernic defender
                        new GameItem(22477),   //Avernic defender

                        new GameItem(22326),   //Justicar helm
                        new GameItem(22327),   //Justicar body
                        new GameItem(22328),   //Justicar legs

                        new GameItem(24664),   //Twisted ancestral hat
                        new GameItem(24664),   //Twisted ancestral hat

                        new GameItem(24666),   //Twisted ancestral top
                        new GameItem(24666),   //Twisted ancestral top

                        new GameItem(24668),   //Twisted ancestral bottom
                        new GameItem(24668)    //Twisted ancestral bottom
                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(

                        new GameItem(696, 20),  //50m Nomad
                        new GameItem(696, 50),    //Nomad
                        new GameItem(696, 50),    //Nomad
                        new GameItem(696, 50),    //Nomad
                        new GameItem(696, 50),    //Nomad
                        new GameItem(696, 50),    //Nomad
                        new GameItem(696, 50),    //Nomad
                        new GameItem(696, 50),    //Nomad
                        new GameItem(696, 50),    //Nomad
                        new GameItem(696, 50),    //Nomad

                        new GameItem(12582),              //Cox box
                        new GameItem(12582),              //Cox box
                        new GameItem(12582),              //Cox box
                        new GameItem(12582),              //Cox box
                        new GameItem(19891),              //Tob box
                        new GameItem(19891),              //Tob box
                        new GameItem(19891),              //Tob box
                        new GameItem(19891),              //Tob box
                        new GameItem(12579),              //Arbo box
                        new GameItem(22325),              //Scythe
                        new GameItem(22323),              //Sanguinesti staff

                        new GameItem(22324),              //Ghrazi rapier
                        new GameItem(22324),              //Ghrazi rapier
                        new GameItem(22324),              //Ghrazi rapier
                        new GameItem(22324),              //Ghrazi rapier
                        new GameItem(22324)               //Ghrazi rapier
                ));
    }

    /**
     * Constructs a new mystery box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public TobBox(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 19891;
    }


    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items; }
}
