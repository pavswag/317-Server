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
 * @author Sponge
 */

public class MiniSmb extends MysteryBoxLootable {

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
                        new GameItem(11681, Misc.random(1, 10)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 10)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 10)),  //scrap paper

                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery

                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677)    //Mini smb
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(11681, Misc.random(1, 20)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 20)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 20)),  //scrap paper

                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery

                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb

                        new GameItem(384, 10),   //shark
                        new GameItem(384, 10),   //shark
                        new GameItem(384, 10),   //shark
                        new GameItem(384, 10),   //shark
                        new GameItem(384, 10),   //shark
                        new GameItem(384, 10),   //shark
                        new GameItem(384, 10),   //shark
                        new GameItem(384, 10),   //shark
                        new GameItem(384, 10),   //shark
                        new GameItem(13442, 5),  //angler
                        new GameItem(13442, 5),  //angler
                        new GameItem(13442, 5),  //angler
                        new GameItem(13442, 5),  //angler
                        new GameItem(13442, 5),  //angler
                        new GameItem(13442, 5),  //angler
                        new GameItem(13442, 5),  //angler
                        new GameItem(13442, 5),  //angler
                        new GameItem(13442, 5),  //angler

                        new GameItem(452, 2),    //runite ore
                        new GameItem(452, 2),    //runite ore
                        new GameItem(452, 2),    //runite ore

                        new GameItem(2364, 2),   //runite bar
                        new GameItem(2364, 2),   //runite bar
                        new GameItem(2364, 2),   //runite bar

                        new GameItem(23745),   //divine magic
                        new GameItem(23745),   //divine magic
                        new GameItem(23745),   //divine magic

                        new GameItem(23733),   //divine range
                        new GameItem(23733),   //divine range
                        new GameItem(23733),   //divine range

                        new GameItem(4716),     //dharok helm
                        new GameItem(4720),     //dharok body
                        new GameItem(4722),     //dharok legs
                        new GameItem(4718),     //dharok axe

                        new GameItem(11838),    //Sara sword
                        new GameItem(11838),    //Sara sword
                        new GameItem(11838)     //Sara sword
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(11681, Misc.random(1, 30)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 30)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 30)),  //scrap paper

                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery

                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb

                        new GameItem(4151),   //Whip
                        new GameItem(4151),   //Whip
                        new GameItem(4151),   //Whip

                        new GameItem(6585),   //Fury
                        new GameItem(6585),   //Fury
                        new GameItem(6585),   //Fury

                        new GameItem(12002),  //Ocult
                        new GameItem(12002),  //Ocult
                        new GameItem(12002)   //Ocult
                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(
                        new GameItem(691),    //10k Nomad
                        new GameItem(691),    //10k Nomad
                        new GameItem(691),    //10k Nomad
                        new GameItem(692),    //25k Nomad

                        new GameItem(11681, Misc.random(1, 40)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 40)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 40)),  //scrap paper

                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery

                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6677),   //Mini smb
                        new GameItem(6678),   //Mini Umb
                        new GameItem(6678),   //Mini Umb
                        new GameItem(6678),   //Mini Umb

                        new GameItem(11826),  //armadyl helm
                        new GameItem(11828),  //armadyl body
                        new GameItem(11830),  //armadyls legs
                        new GameItem(11785),  //armadyl crossbow
                        new GameItem(11832),  //bandos chestplate
                        new GameItem(11834),  //bandos tassets

                        new GameItem(11804),  //Bgs
                        new GameItem(11808),  //Zgs
                        new GameItem(11806),  //Sgs
                        new GameItem(11802),  //Ags

                        new GameItem(10350),  //3rd age helm
                        new GameItem(10348),  //3rd age plate
                        new GameItem(10346),  //3rd age legs
                        new GameItem(10352),  //3rd age kite
                        new GameItem(10330),  //3rd age range top
                        new GameItem(10332),  //3rd age range legs
                        new GameItem(10338),  //3rd age robe top
                        new GameItem(10340),  //3rd age robe
                        new GameItem(12437),  //3rd age cape
                        new GameItem(12424),  //3rd age bow
                        new GameItem(12426),  //3rd age longsword
                        new GameItem(12422),  //3rd age wand
                        new GameItem(20014),  //3rd age pickaxe

                        new GameItem(22374),  //Hespori key
                        new GameItem(23776),  //Hespori key
                        new GameItem(23776),  //Hespori key
                        new GameItem(23776),  //Hespori key
                        new GameItem(23776),  //Hespori key

                        new GameItem(13302),  //Bank key
                        new GameItem(13239),  //primordial boots
                        new GameItem(13235),  //eternal boots
                        new GameItem(13237),  //pegasian boots
                        new GameItem(6889),   //mages book

                        new GameItem(4084),   //sled
                        new GameItem(4084),   //sled

                        new GameItem(12785),  //Row (i)
                        new GameItem(12785),  //Row (i)
                        new GameItem(12785),  //Row (i)

                        new GameItem(6678),   //Mini umb
                        new GameItem(6678),   //Mini umb
                        new GameItem(6678),   //Mini umb
                        new GameItem(6678)    //Mini umb
                ));
    }

    /**
     * Constructs a new mystery box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public MiniSmb(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 6677;
    }


    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }
}
