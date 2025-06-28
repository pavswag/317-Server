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


public class MiniDonoBox extends MysteryBoxLootable {

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
                        new GameItem(12585),  //Mini cox box
                        new GameItem(19895),  //Mini tob box
                        new GameItem(6680),   //Mini Arbo box
                        new GameItem(19887)   //Mini dono box
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(6769),    //$5

                        new GameItem(11212, 500),  //Dragon arrows
                        new GameItem(11212, 250),  //Dragon arrows
                        new GameItem(11212, 250),  //Dragon arrows
                        new GameItem(11212, 250),  //Dragon arrows
                        new GameItem(11212, 250),  //Dragon arrows
                        new GameItem(11212, 250),  //Dragon arrows
                        new GameItem(11212, 250),  //Dragon arrows
                        new GameItem(11212, 250),  //Dragon arrows
                        new GameItem(11212, 250),  //Dragon arrows
                        new GameItem(11212, 250),  //Dragon arrows

                        new GameItem(11230, 500),  //Dragon darts
                        new GameItem(11230, 250),  //Dragon darts
                        new GameItem(11230, 250),  //Dragon darts
                        new GameItem(11230, 250),  //Dragon darts
                        new GameItem(11230, 250),  //Dragon darts
                        new GameItem(11230, 250),  //Dragon darts
                        new GameItem(11230, 250),  //Dragon darts
                        new GameItem(11230, 250),  //Dragon darts
                        new GameItem(11230, 250),  //Dragon darts
                        new GameItem(11230, 250),  //Dragon darts

                        new GameItem(21046, 50),  //Chest rate bonus
                        new GameItem(21046, 5),  //Chest rate bonus
                        new GameItem(21046, 5),  //Chest rate bonus
                        new GameItem(21046, 5),  //Chest rate bonus
                        new GameItem(21046, 5),  //Chest rate bonus
                        new GameItem(21046, 5),  //Chest rate bonus
                        new GameItem(21046, 5),  //Chest rate bonus
                        new GameItem(21046, 5),  //Chest rate bonus
                        new GameItem(21046, 5),  //Chest rate bonus
                        new GameItem(21046, 5),  //Chest rate bonus

                        new GameItem(21295),  //Inferno cape
                        new GameItem(21295),  //Inferno cape

                        new GameItem(10557),  //Collector
                        new GameItem(10557),  //Collector

                        new GameItem(10556),  //Attacker
                        new GameItem(10556),  //Attacker

                        new GameItem(10558)   //Defender
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(696,10),  //nomad
                        new GameItem(696,10),
                        new GameItem(696,10),
                        new GameItem(696,10),
                        new GameItem(696,10),
                        new GameItem(696,10),
                        new GameItem(696,10),
                        new GameItem(696,10),
                        new GameItem(696,10),
                        new GameItem(696,10),
                        new GameItem(696,10),
                        new GameItem(696,10),
                        new GameItem(696,10),
                        new GameItem(696,10),
                        new GameItem(696,10),
                        new GameItem(696,10),


                        new GameItem(12817),  //Elysian
                        new GameItem(12817),  //Elysian
                        new GameItem(12817),  //Elysian
                        new GameItem(12817),  //Elysian
                        new GameItem(12817),  //Elysian
                        new GameItem(12817),  //Elysian

                        new GameItem(24423),  //Harmonised nightmare staff
                        new GameItem(24423),  //Harmonised nightmare staff
                        new GameItem(24423),  //Harmonised nightmare staff
                        new GameItem(24423),  //Harmonised nightmare staff
                        new GameItem(24423),  //Harmonised nightmare staff
                        new GameItem(24423),  //Harmonised nightmare staff

                        new GameItem(24425),  //Eldritch nightmare staff
                        new GameItem(24425),  //Eldritch nightmare staff
                        new GameItem(24425),  //Eldritch nightmare staff
                        new GameItem(24425),  //Eldritch nightmare staff
                        new GameItem(24425),  //Eldritch nightmare staff
                        new GameItem(24425),  //Eldritch nightmare staff

                        new GameItem(24424),  //Volatile nightmare staff
                        new GameItem(24424),  //Volatile nightmare staff
                        new GameItem(24424),  //Volatile nightmare staff
                        new GameItem(24424),  //Volatile nightmare staff
                        new GameItem(24424),  //Volatile nightmare staff
                        new GameItem(24424),  //Volatile nightmare staff

                        new GameItem(27226),  //Masori mask
                        new GameItem(27229),  //Masori body
                        new GameItem(27232),  //Masori chaps
                        new GameItem(26235)   //Zaryte vembraces
                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(
                        new GameItem(2403),    //$10
                        new GameItem(2396),    //$25

                        new GameItem(30022),   //Kratos

                        new GameItem(30021),   //Roc

                        new GameItem(25736),   //Holy scythe

                        new GameItem(25731),   //Holy sanguinesti staff

                        new GameItem(25734),   //Holy ghrazi rapier

                        new GameItem(22323),   //Sanguinesti staff

                        new GameItem(20997),   //Tbow

                        new GameItem(22325),   //scythe

                        new GameItem(26374),   //Zaryte cbow

                        new GameItem(26382),   //Torva helm

                        new GameItem(26384),   //Torva body

                        new GameItem(26386),   //Torva legs

                        new GameItem(26225),   //Ancestral mask

                        new GameItem(26221),   //Ancestral body

                        new GameItem(26223),   //Ancestral legs

                        new GameItem(13681),   //Cruciferous codex

                        new GameItem(27251),   //Elidinis' ward (f)

                        new GameItem(10559),   //Healer
                        new GameItem(10559),   //Healer
                        new GameItem(10559),   //Healer

                        new GameItem(20786),   //Row (i5)

                        new GameItem(33112),   //Pot of gold

                        new GameItem(33122),   //Pure skills

                        new GameItem(696,50),  //nomad
                        new GameItem(696,10),  //nomad
                        new GameItem(696,10),  //nomad
                        new GameItem(696,10),  //nomad
                        new GameItem(696,10),  //nomad
                        new GameItem(696,10),  //nomad
                        new GameItem(696,10),  //nomad
                        new GameItem(696,10),  //nomad
                        new GameItem(696,10),  //nomad
                        new GameItem(696,10)   //nomad




                ));
    }

    /**
     * Constructs a new mystery box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public MiniDonoBox(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 19887;
    }


    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }
}