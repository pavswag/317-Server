package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.model.items.GameItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RaidPlusItems {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static Map<LootRarity, List<GameItem>> getItems() {
        return items;
    }

    static {
        items.put(LootRarity.RARE, Arrays.asList( // Decent Reward's only
                new GameItem(22325, 1),  //Scythe
                new GameItem(22323, 1),  //Sanguinesti Staff
                new GameItem(22324, 1),  //Ghrazi Rapier
                new GameItem(20997, 1),  //Twisted Bow
                new GameItem(21012, 1),  //Dragon Hunter C'Bow
                new GameItem(26233, 1),  //Ancient Godsword
                new GameItem(25739, 1),  //Sanguine Scythe Of Vitur
                new GameItem(25736, 1),  //Holy Scythe Of Vitur
                new GameItem(25916, 1),  //Dragon Hunter C'Bow (t)
                new GameItem(26482, 1),  //Abyssal Whip (or)
                new GameItem(26484, 1),  //Abyssal Tentacle (or)
                new GameItem(20786, 1),  //ROW (i4)
                new GameItem(20787, 1)   //ROW (i5)
        ));
    }
}
