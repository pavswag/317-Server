package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.MysteryBoxLootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class f2pDivisionBox  extends MysteryBoxLootable {
    /**
     * A map containing a List of {@link GameItem}'s that contain items relevant to their LootRarity.
     */
    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    /*
      Stores an array of items into each map with the corresponding rarity to the list
     */
    static {

        items.put(LootRarity.COMMON,
                Arrays.asList(
                        new GameItem(6679, 100),
                        new GameItem(6677, 50),
                        new GameItem(6678, 25),
                        new GameItem(12585),
                        new GameItem(19895),
                        new GameItem(6680)
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(6570),   // Fire cape
                        new GameItem(23842),  // Corrupted helm (perfected)
                        new GameItem(23845),  // Corrupted body (perfected)
                        new GameItem(23848),  // Corrupted legs (perfected)
                        new GameItem(26488),  // Book of balance or
                        new GameItem(26490),  // Book of darkness or
                        new GameItem(26492),  // Book of law or
                        new GameItem(26494),  // Book of war or
                        new GameItem(26496),  // Holy book or
                        new GameItem(26498),  // Unholy book or
                        new GameItem(21012),  // Dhcb
                        new GameItem(12924)   // blowpipe
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(23995),  // Blade of saeldor

                        new GameItem(26714),  // Armadyl helmet or
                        new GameItem(26715),  // Armadyl chestplate or
                        new GameItem(26716),  // Armadyl chainskirt or

                        new GameItem(26718),  // Bandos chestplate or
                        new GameItem(26719),  // Bandos tassets or

                        new GameItem(13239),  // Primordial boots
                        new GameItem(13237),  // Pegasian boots
                        new GameItem(13235),  // Eternal boots

                        new GameItem(11770),  // Seers ring i
                        new GameItem(11771),  // Archers ring i
                        new GameItem(11773),  // Berserker ring i

                        new GameItem(21034),  // Dex prayer scroll
                        new GameItem(21079),  // Arcane prayer scroll

                        new GameItem(13576)   // Dragon warhammer
                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(
                        new GameItem(6829),   // p2p division box

                        new GameItem(22325),  // Scythe

                        new GameItem(20997),  // Twisted bow

                        new GameItem(22323),  // Sanguinesti staff

                        new GameItem(22326),  // Justiciar helm
                        new GameItem(22326),  // Justiciar helm

                        new GameItem(22327),  // Justiciar body
                        new GameItem(22327),  // Justiciar body

                        new GameItem(22328),  // Justiciar legs
                        new GameItem(22328),  // Justiciar legs

                        new GameItem(21018),  // Ancestral hat
                        new GameItem(21018),  // Ancestral hat

                        new GameItem(21021),  // Ancestral body
                        new GameItem(21021),  // Ancestral body

                        new GameItem(21024),  // Ancesetral legs
                        new GameItem(21024),  // Ancesetral legs


                        new GameItem(22324),  // Ghrazi rapier
                        new GameItem(22324),  // Ghrazi rapier



                        new GameItem(25918),  // Dhcb b
                        new GameItem(25918)   // Dhcb b
                ));

    }

    /**
     * Constructs a new myster box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public f2pDivisionBox(Player player) {
        super(player);
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    @Override
    public int getItemId() {
        return 6831;
    }
}
