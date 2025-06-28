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
public class MiniNormalMysteryBox extends MysteryBoxLootable {

    /**
     * A map containing a List of {@link GameItem}'s that contain items relevant to their LootRarity.
     */
    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    /**
     * Stores an array of items into each map with the corresponding rarity to the list
     */
    static {

        items.put(LootRarity.COMMON,
                Arrays.asList(

                        new GameItem(6679),  //mini mystery
                        new GameItem(6679),  //mini mystery
                        new GameItem(6679),  //mini mystery
                        new GameItem(6679),  //mini mystery
                        new GameItem(6679),  //mini mystery
                        new GameItem(6679)   //mini mystery
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(11681, Misc.random(1, 3)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 3)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 3)),  //scrap paper

                        new GameItem(6679),  //mini mystery
                        new GameItem(6679),  //mini mystery
                        new GameItem(6679),  //mini mystery
                        new GameItem(6679),  //mini mystery
                        new GameItem(6679),  //mini mystery
                        new GameItem(6679),  //mini mystery

                        new GameItem(990),    //crystal key
                        new GameItem(990),    //crystal key

                        new GameItem(268),    //dwarf weed
                        new GameItem(268),    //dwarf weed

                        new GameItem(270),    //torstol
                        new GameItem(270),    //torstol

                        new GameItem(3001),   //snapdragon
                        new GameItem(3001),   //snapdragon

                        new GameItem(1516),   //yew log
                        new GameItem(1516),   //yew log
                        new GameItem(1516),   //yew log

                        new GameItem(12696),  //super combat potion(4)
                        new GameItem(12696),  //super combat potion(4)

                        new GameItem(3025),   //super restore(4)
                        new GameItem(3025),   //super restore(4)

                        new GameItem(3145),   //cooked karambwan
                        new GameItem(3145),   //cooked karambwan

                        new GameItem(450),    //adamant ore
                        new GameItem(450),    //adamant ore
                        new GameItem(2362),   //adamant bar
                        new GameItem(2362),   //adamant bar

                        new GameItem(1516),   //yew log

                        new GameItem(1624),   //uncut sapphire
                        new GameItem(1622),   //uncut emerald

                        new GameItem(535),    //babydragon bones
                        new GameItem(535)     //babydragon bones
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(11681, Misc.random(1, 5)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 5)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 5)),  //scrap paper

                        new GameItem(6679),  //mini mystery
                        new GameItem(6679),  //mini mystery
                        new GameItem(6679),  //mini mystery
                        new GameItem(6679),  //mini mystery
                        new GameItem(6679),  //mini mystery
                        new GameItem(6679),  //mini mystery

                        new GameItem(12002),  //occult necklace
                        new GameItem(6585),   //fury
                        new GameItem(1712),   //glory (4)
                        new GameItem(11840)   //dragon boots
                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(
                        new GameItem(11681, Misc.random(1, 10)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 10)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 10)),  //scrap paper

                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery
                        new GameItem(6679),   //mini mystery

                        new GameItem(6677),   //Mini smb
                        new GameItem(6678),   //Mini umb

                        new GameItem(12873),  //guthan set
                        new GameItem(12873),  //guthan set
                        new GameItem(12873),  //guthan set

                        new GameItem(12875),  //verac set
                        new GameItem(12875),  //verac set
                        new GameItem(12875),  //verac set

                        new GameItem(12877),  //dharok set
                        new GameItem(12877),  //dharok set
                        new GameItem(12877),  //dharok set

                        new GameItem(12879),  //torags set
                        new GameItem(12879),  //torags set
                        new GameItem(12879),  //torags set

                        new GameItem(12881),  //ahrim set
                        new GameItem(12881),  //ahrim set
                        new GameItem(12881),  //ahrim set

                        new GameItem(12883),  //karil set
                        new GameItem(12883),  //karil set
                        new GameItem(12883),  //karil set

                        new GameItem(4587),   //d scim
                        new GameItem(5698),   //d dagger

                        new GameItem(11235),  //dark bow
                        new GameItem(11235),  //dark bow
                        new GameItem(11235),  //dark bow

                        new GameItem(4151),   //abyssal whip
                        new GameItem(4151),   //abyssal whip
                        new GameItem(4151),   //abyssal whip

                        new GameItem(6735),   //warrior ring
                        new GameItem(6735),   //warrior ring
                        new GameItem(6733),   //archers ring
                        new GameItem(6731),   //seers ring

                        new GameItem(11804),  //BGS
                        new GameItem(11806),  //SGS
                        new GameItem(11808),  //ZGS
                        new GameItem(11802),  //Ags
                        new GameItem(11832),  //bandos chestplate
                        new GameItem(11834),  //bandos tassets
                        new GameItem(11826),  //armadyl helmet
                        new GameItem(11828),  //armadyl chestplate
                        new GameItem(11830)   //armadyl chainskirt
                ));

    }

    /**
     * Constructs a new myster box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public MiniNormalMysteryBox(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 6679;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }
}
