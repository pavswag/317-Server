package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.MysteryBoxLootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

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


public class CoxBox extends MysteryBoxLootable {

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
                        new GameItem(11681, Misc.random(1, 20)), //scrap paper
                        new GameItem(11681, Misc.random(1, 20)), //scrap paper
                        new GameItem(6678),  //Mini umb
                        new GameItem(12585), //Mini cox

                        new GameItem(452, 500),    //Rune ore
                        new GameItem(452, 500),    //Rune ore

                        new GameItem(2364, 500),   //Rune bars
                        new GameItem(2364, 500),   //Rune bars

                        new GameItem(1164, 10),    //Rune helm
                        new GameItem(1164, 10),    //Rune helm
                        new GameItem(1164, 10),    //Rune helm

                        new GameItem(1128, 10),    //Rune body
                        new GameItem(1128, 10),    //Rune body
                        new GameItem(1128, 10),    //Rune body

                        new GameItem(1080, 10),    //Rune legs
                        new GameItem(1080, 10),    //Rune legs
                        new GameItem(1080, 10),    //Rune legs


                        new GameItem(1202, 10),    //Rune kite
                        new GameItem(1202, 10),    //Rune kite
                        new GameItem(1202, 10),    //Rune kite

                        new GameItem(23686, 50),   //Divine combat
                        new GameItem(23686, 50),   //Divine combat
                        new GameItem(23686, 50),   //Divine combat
                        new GameItem(23686, 50),   //Divine combat
                        new GameItem(23686, 50),   //Divine combat

                        new GameItem(23734,50),    //Divine range
                        new GameItem(23734,50),    //Divine range
                        new GameItem(23734,50),    //Divine range
                        new GameItem(23734,50),    //Divine range
                        new GameItem(23734,50),    //Divine range

                        new GameItem(23746, 50),   //Divine mage
                        new GameItem(23746, 50),   //Divine mage
                        new GameItem(23746, 50),   //Divine mage
                        new GameItem(23746, 50),   //Divine mage
                        new GameItem(23746, 50)    //Divine mage
                ));
        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(11681, Misc.random(1, 20)), //scrap paper
                        new GameItem(11681, Misc.random(1, 20)), //scrap paper

                        new GameItem(696, 5),  //5m Nomad

                        new GameItem(21027, 4),    //Dark relic
                        new GameItem(21027, 4),    //Dark relic

                        new GameItem(11730, 4),    //Overload (4)
                        new GameItem(11730, 4),    //Overload (4)

                        new GameItem(11212, 500),  //Dragon arrows
                        new GameItem(11212, 500),  //Dragon arrows

                        new GameItem(11230, 500),  //Dragon darts
                        new GameItem(11230, 500),  //Dragon darts

                        new GameItem(21948, 500),  //Dragonston dragon bolts (e)
                        new GameItem(21948, 500),  //Dragonston dragon bolts (e)

                        new GameItem(26486),  //Rune C'bow (or)
                        new GameItem(26486)   //Rune C'bow (or)
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(11681, Misc.random(1, 20)), //scrap paper
                        new GameItem(11681, Misc.random(1, 20)), //scrap paper

                        new GameItem(696, 20),  //10m Nomad

                        new GameItem(6678),  //Mini umb
                        new GameItem(6678),  //Mini umb
                        new GameItem(6678),  //Mini umb
                        new GameItem(6678),  //Mini umb
                        new GameItem(12585), //Mini cox


                        new GameItem(21079),  //Arcane pray

                        new GameItem(21034),  //Dex pray

                        new GameItem(21018),  //Ancestral hat

                        new GameItem(21021),  //Ancestral top

                        new GameItem(21024),  //Ancestral bottoms

                        new GameItem(21006),  //Kodai

                        new GameItem(21003),  //Elder maul

                        new GameItem(13576),  //Dwarhammer

                        new GameItem(20784),  //Dclaws

                        new GameItem(21015),  //Dinhs

                        new GameItem(21000),  //Twisted buckler

                        new GameItem(21012)   //Dragon hunter c'bow

                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(
                        new GameItem(11681, Misc.random(1, 20)), //scrap paper
                        new GameItem(11681, Misc.random(1, 20)), //scrap paper

                        new GameItem(696, 20), //50m Nomad
                        new GameItem(33109),  //Raiders luck
                        new GameItem(20851),  //Olm
                        new GameItem(20997),  //Tbow
                        new GameItem(25918),  //Dragon hunter c'bow (b)
                        new GameItem(19891),   //Tob box
                        new GameItem(12579)    //Arbo Box
                ));
    }

    /**
     * Constructs a new mystery box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public CoxBox(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 12582;
    }


    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items; }
    }