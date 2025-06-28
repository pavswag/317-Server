package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.minigames.raids.Raids;
import io.xeros.model.Items;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaidsChestItems {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static Map<LootRarity, List<GameItem>> getItems() {
        return items;
    }

    static {
        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(21027, 1),     //dark relic
                new GameItem(11730,  1),    //overload

                new GameItem(21948,  100),  //dragonstone dragon bolts (e)
                new GameItem(11230,  100),  //dragon dart
                new GameItem(11212, 100),   //dragon arrow
                new GameItem(19484,  100),  //dragon javelin
                new GameItem(892,  200),    //rune arrow

                new GameItem(23686,  10),   //divine combat 4
                new GameItem(23734,  10),   //divine range 4
                new GameItem(23746,  10),   //divine mage 4

                new GameItem(384,  25),     //raw shark
                new GameItem(13440,  10),   //anglerfish

                new GameItem(1128,  5),     //rune platebody
                new GameItem(1080,  5),     //rune platelegs
                new GameItem(1374,  5),     //rune b axe

                new GameItem(1392,  25),    //battle staff
                new GameItem(3055, 1),      //lava battlestaff
                new GameItem(566,  250),    //soul rune
                new GameItem(560,  250),    //death rune
                new GameItem(565,  250),    //blood rune

                new GameItem(7937,  250),   //pure essence
                new GameItem(454,  200),    //coal
                new GameItem(448,  100),    //mith ore
                new GameItem(450,  50),     //addy ore
                new GameItem(452,  25),     //runite ore

                new GameItem(1624,  50),    //uncut saphire
                new GameItem(1622,  50),    //uncut emerald
                new GameItem(1620,  50),    //uncut ruby
                new GameItem(1618,  25),    //uncut diamonds

                new GameItem(3050,  50),    //grimy toadflax
                new GameItem(210,  50),     //grimy irit
                new GameItem(212,  50),     //grimy avantoe
                new GameItem(214,  50),     //grimy kwuarm
                new GameItem(216,  50),     //grimy candatine
                new GameItem(2486,  50),    //grimy landatyme
                new GameItem(218,  50),     //dwarf weed
                new GameItem(220,  50),     //torstol
                new GameItem(3052, 50)      //grimy snap dragons
        ));

        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(20997, 1),    //Twisted bow

                new GameItem(20851, 1),    //Olmlet pet

                new GameItem(21003, 1),    //Elder maul
                new GameItem(21003, 1),    //Elder maul
                new GameItem(21003, 1),    //Elder maul

                new GameItem(21012, 1),    //Dragon hunter crossbow
                new GameItem(21012, 1),    //Dragon hunter crossbow
                new GameItem(21012, 1),    //Dragon hunter crossbow

                new GameItem(21018, 1),    //Ancestral hat
                new GameItem(21018, 1),    //Ancestral hat
                new GameItem(21018, 1),    //Ancestral hat

                new GameItem(21021, 1),    //Ancestral top
                new GameItem(21021, 1),    //Ancestral top
                new GameItem(21021, 1),    //Ancestral top

                new GameItem(21024, 1),    //Ancestral bottom
                new GameItem(21024, 1),    //Ancestral bottom
                new GameItem(21024, 1),    //Ancestral bottom

                new GameItem(21006, 1),    //Kodai wand
                new GameItem(21006, 1),    //Kodai wand
                new GameItem(21006, 1),    //Kodai wand

                new GameItem(21079, 1),    //Dex scroll
                new GameItem(21079, 1),    //Dex scroll
                new GameItem(21079, 1),    //Dex scroll
                new GameItem(21079, 1),    //Dex scroll
                new GameItem(21079, 1),    //Dex scroll
                new GameItem(21079, 1),    //Dex scroll
                new GameItem(21079, 1),    //Dex scroll

                new GameItem(21034, 1),    //Arcane scroll
                new GameItem(21034, 1),    //Arcane scroll
                new GameItem(21034, 1),    //Arcane scroll
                new GameItem(21034, 1),    //Arcane scroll
                new GameItem(21034, 1),    //Arcane scroll
                new GameItem(21034, 1),    //Arcane scroll
                new GameItem(21034, 1),    //Arcane scroll

                new GameItem(21015, 1),    //Dinh's bulwark
                new GameItem(21015, 1),    //Dinh's bulwark
                new GameItem(21015, 1),    //Dinh's bulwark
                new GameItem(21015, 1),    //Dinh's bulwark

                new GameItem(21000, 1),    //Twisted buckler
                new GameItem(21000, 1),    //Twisted buckler
                new GameItem(21000, 1),    //Twisted buckler

                new GameItem(20784, 1),    //D claws
                new GameItem(20784, 1),    //D claws
                new GameItem(20784, 1)     //D claws
        ));
    }


}
