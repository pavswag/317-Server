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


public class MiniCoxBox extends MysteryBoxLootable {

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
                        new GameItem(11681, Misc.random(1, 20)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 20)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 20)),  //scrap paper
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb

                        new GameItem(6678),  //Mini umb
                        new GameItem(6678),  //Mini umb
                        new GameItem(6678),  //Mini umb
                        new GameItem(6678),  //Mini umb
                        new GameItem(6678),  //Mini umb

                        new GameItem(12585), //Mini cox
                        new GameItem(12585), //Mini cox
                        new GameItem(12585), //Mini cox
                        new GameItem(12585), //Mini cox
                        new GameItem(12585)  //Mini cox
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(11681, Misc.random(1, 30)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 30)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 30)),  //scrap paper

                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb

                        new GameItem(6678),  //Mini umb
                        new GameItem(6678),  //Mini umb
                        new GameItem(6678),  //Mini umb
                        new GameItem(6678),  //Mini umb
                        new GameItem(6678),  //Mini umb

                        new GameItem(12585), //Mini cox
                        new GameItem(12585), //Mini cox
                        new GameItem(12585), //Mini cox
                        new GameItem(12585), //Mini cox
                        new GameItem(12585), //Mini cox

                        new GameItem(696, 10),    //Nomad
                        new GameItem(21027, 2),   //Dark relic
                        new GameItem(21027, 2),   //Dark relic

                        new GameItem(11730, 2),   //Overload (4)
                        new GameItem(11730, 2),   //Overload (4)

                        new GameItem(11212, 10), //Dragon arrows
                        new GameItem(11212, 10), //Dragon arrows

                        new GameItem(11230, 10), //Dragon darts
                        new GameItem(11230, 10), //Dragon darts

                        new GameItem(21948, 10), //Dragonston dragon bolts (e)
                        new GameItem(21948, 10), //Dragonston dragon bolts (e)

                        new GameItem(21348, 10), //amethyst
                        new GameItem(21348, 10), //amethyst

                        new GameItem(452, 10),   //Rune ore
                        new GameItem(452, 10),   //Rune ore

                        new GameItem(2364, 10),  //Rune bars
                        new GameItem(2364, 10),  //Rune bars


                        new GameItem(892, 25),   //Rune Arrow
                        new GameItem(892, 25),   //Rune Arrow

                        new GameItem(9144, 25),  //runite bolts
                        new GameItem(9144, 25),  //runite bolts

                        new GameItem(23686, 10),  //Divine combat
                        new GameItem(23686, 10),  //Divine combat

                        new GameItem(23734,10),   //Divine range
                        new GameItem(23734,10),   //Divine range

                        new GameItem(23746, 10),  //Divine mage
                        new GameItem(23746, 10)   //Divine mage
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(11681, Misc.random(1, 40)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 40)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 40)),  //scrap paper

                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb
                        new GameItem(6677),  //Mini smb

                        new GameItem(6678),  //Mini umb
                        new GameItem(6678),  //Mini umb
                        new GameItem(6678),  //Mini umb
                        new GameItem(6678),  //Mini umb
                        new GameItem(6678),  //Mini umb

                        new GameItem(12585), //Mini cox
                        new GameItem(12585), //Mini cox
                        new GameItem(12585), //Mini cox
                        new GameItem(12585), //Mini cox
                        new GameItem(12585), //Mini cox

                        new GameItem(696, 2),  //Nomad
                        new GameItem(696, 2),  //Nomad
                        new GameItem(696, 2),  //Nomad
                        new GameItem(696, 2),  //Nomad
                        new GameItem(696, 2),  //Nomad
                        new GameItem(696, 2),  //Nomad
                        new GameItem(696, 2),  //Nomad
                        new GameItem(696, 2),  //Nomad
                        new GameItem(696, 2),  //Nomad
                        new GameItem(696, 2),  //Nomad
                        new GameItem(696, 2),  //Nomad
                        new GameItem(696, 2),  //Nomad


                        new GameItem(13576),  //Dwarhammer
                        new GameItem(13576),  //Dwarhammer
                        new GameItem(13576),  //Dwarhammer
                        new GameItem(13576),  //Dwarhammer

                        new GameItem(26486),  //Rune C'bow (or)
                        new GameItem(26486),  //Rune C'bow (or)
                        new GameItem(26486),  //Rune C'bow (or)
                        new GameItem(26486)   //Rune C'bow (or)
                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(
                        new GameItem(11681, Misc.random(1, 50)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 50)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 50)),  //scrap paper

                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb

                        new GameItem(6678),   //Mini umb
                        new GameItem(6678),   //Mini umb
                        new GameItem(6678),   //Mini umb
                        new GameItem(6678),   //Mini umb
                        new GameItem(6678),   //Mini umb
                        new GameItem(6678),   //Mini umb
                        new GameItem(6678),   //Mini umb
                        new GameItem(6678),   //Mini umb
                        new GameItem(6678),   //Mini umb

                        new GameItem(12585),  //Mini cox
                        new GameItem(12585),  //Mini cox
                        new GameItem(12585),  //Mini cox
                        new GameItem(12585),  //Mini cox
                        new GameItem(12585),  //Mini cox
                        new GameItem(12585),  //Mini cox
                        new GameItem(12585),  //Mini cox
                        new GameItem(12585),  //Mini cox
                        new GameItem(12585),  //Mini cox
                        new GameItem(12585),  //Mini cox
                        new GameItem(12585),  //Mini cox
                        new GameItem(12585),  //Mini cox
                        new GameItem(12585),  //Mini cox
                        new GameItem(19895),  //mini Tob box
                        new GameItem(6680),   //mini Arbo Box

                        new GameItem(20997),  //Tbow
                        new GameItem(20851),  //Olm
                        new GameItem(33109),  //Raiders luck

                        new GameItem(696, 10), //Nomad
                        new GameItem(696, 1), //Nomad
                        new GameItem(696, 1), //Nomad
                        new GameItem(696, 1), //Nomad
                        new GameItem(696, 1), //Nomad
                        new GameItem(696, 1), //Nomad
                        new GameItem(696, 1), //Nomad
                        new GameItem(696, 1), //Nomad
                        new GameItem(696, 1), //Nomad
                        new GameItem(696, 1), //Nomad
                        new GameItem(696, 1), //Nomad
                        new GameItem(696, 1), //Nomad

                        new GameItem(21079),  //Arcane pray
                        new GameItem(21079),  //Arcane pray
                        new GameItem(21079),  //Arcane pray
                        new GameItem(21079),  //Arcane pray

                        new GameItem(21034),  //Dex pray
                        new GameItem(21034),  //Dex pray
                        new GameItem(21034),  //Dex pray
                        new GameItem(21034),  //Dex pray

                        new GameItem(21018),  //Ancestral hat
                        new GameItem(21018),  //Ancestral hat
                        new GameItem(21018),  //Ancestral hat
                        new GameItem(21018),  //Ancestral hat

                        new GameItem(21021),  //Ancestral top
                        new GameItem(21021),  //Ancestral top
                        new GameItem(21021),  //Ancestral top
                        new GameItem(21021),  //Ancestral top

                        new GameItem(21024),  //Ancestral bottoms
                        new GameItem(21024),  //Ancestral bottoms
                        new GameItem(21024),  //Ancestral bottoms
                        new GameItem(21024),  //Ancestral bottoms

                        new GameItem(21006),  //Kodai
                        new GameItem(21006),  //Kodai
                        new GameItem(21006),  //Kodai
                        new GameItem(21006),  //Kodai

                        new GameItem(21003),  //Elder maul
                        new GameItem(21003),  //Elder maul

                        new GameItem(20784),  //Dclaws
                        new GameItem(20784),  //Dclaws

                        new GameItem(21015),  //Dinhs
                        new GameItem(21015),  //Dinhs

                        new GameItem(21000),  //Twisted buckler
                        new GameItem(21000),  //Twisted buckler

                        new GameItem(21012),  //Dragon hunter c'bow
                        new GameItem(21012)   //Dragon hunter c'bow
                ));
    }

    /**
     * Constructs a new mystery box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public MiniCoxBox(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 12585;
    }


    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items; }
    }