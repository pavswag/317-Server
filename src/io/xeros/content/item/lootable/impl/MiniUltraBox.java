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

import static io.xeros.content.item.lootable.LootRarity.VERY_RARE;

/**
 * Revamped a simple means of receiving a random item based on chance.
 *
 * @author Jason MacKeigan
 * @date Oct 29, 2014, 1:43:44 PM
 */


public class MiniUltraBox extends MysteryBoxLootable {

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

                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb

                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677)    //mini smb

                ));
        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(11681, Misc.random(1, 25)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 25)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 25)),  //scrap paper

                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb

                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb

                        new GameItem(13271),  //Abby Dagger (p++)
                        new GameItem(13271),  //Abby Dagger (p++)
                        new GameItem(13271),  //Abby Dagger (p++)
                        new GameItem(13271),  //Abby Dagger (p++)

                        new GameItem(12924),  //blowpipe
                        new GameItem(12924),  //blowpipe
                        new GameItem(12924),  //blowpipe
                        new GameItem(12924),  //blowpipe

                        new GameItem(19481),  //heavy ballista

                        new GameItem(19547),  //anguish

                        new GameItem(19553),  //Amulet of Torture

                        new GameItem(21633),  //ancient wyvern shield

                        new GameItem(19544),  //tormented bracelet

                        new GameItem(19550),  //ring of suffering

                        new GameItem(21000),  //twisted buckler

                        new GameItem(13576),  //dragon warhammer

                        new GameItem(13239),  //primordial boots

                        new GameItem(13235),  //eternal boots

                        new GameItem(13237),   //pegasian boots

                        new GameItem(21295),  //infernal cape
                        new GameItem(21295),  //infernal cape

                        new GameItem(10557)   //collector icon
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(11681, Misc.random(1, 30)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 30)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 30)),  //scrap paper

                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb

                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677)    //mini smb
                ));

        items.put(VERY_RARE,
                Arrays.asList(
                        new GameItem(11681, Misc.random(1, 35)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 35)),  //scrap paper
                        new GameItem(11681, Misc.random(1, 35)),  //scrap paper

                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb
                        new GameItem(6678),   //mini umb

                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb
                        new GameItem(6677),   //mini smb

                        new GameItem(22325),  //scythe
                        new GameItem(20997),  //twisted bow

                        new GameItem(10559),  //Healer icon
                        new GameItem(10558),  //defender icon
                        new GameItem(10556),  //attacker icon

                        new GameItem(22326),  //justiciar helm
                        new GameItem(22326),  //justiciar helm
                        new GameItem(22326),  //justiciar helm

                        new GameItem(22327),  //justiciar body
                        new GameItem(22327),  //justiciar body
                        new GameItem(22327),  //justiciar body

                        new GameItem(22328),  // justiciar legs
                        new GameItem(22328),  // justiciar legs
                        new GameItem(22328),  // justiciar legs


                        new GameItem(21018),  //ancestral hat
                        new GameItem(21018),  //ancestral hat
                        new GameItem(21018),  //ancestral hat

                        new GameItem(21021),  //ancestral body
                        new GameItem(21021),  //ancestral body
                        new GameItem(21021),  //ancestral body

                        new GameItem(21024),  //ancesetral legs
                        new GameItem(21024),  //ancesetral legs
                        new GameItem(21024),  //ancesetral legs

                        new GameItem(22324),  //ghrazi rapier
                        new GameItem(22324),  //ghrazi rapier
                        new GameItem(22324),  //ghrazi rapier

                        new GameItem(21006),  // Kodai

                        new GameItem(21003),  //Elder Maul

                        new GameItem(21012),  //Dragon hunter Crossbow

                        new GameItem(21295),  //infernal cape

                        new GameItem(20784),  //dragon claws
                        new GameItem(20784),  //dragon claws
                        new GameItem(20784),  //dragon claws

                        new GameItem(12817),  //elysian
                        new GameItem(12817),  //elysian
                        new GameItem(12817),  //elysian

                        new GameItem(12825),  //arcane spirit shield
                        new GameItem(12825),  //arcane spirit shield
                        new GameItem(12825),  //arcane spirit shield

                        new GameItem(12821),  //spectral spirit shield
                        new GameItem(12821),  //spectral spirit shield
                        new GameItem(12821)   //spectral spirit shield
                ));
    }

    /**
     * Constructs a new mystery box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public MiniUltraBox(Player player) {
        super(player);
    }

    @Override
    public int getItemId() {
        return 6678;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }
}