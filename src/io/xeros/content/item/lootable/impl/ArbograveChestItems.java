package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.*;

public class ArbograveChestItems implements Lootable {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    public static Map<LootRarity, List<GameItem>> getItems() {
        return items;
    }

    public static ArrayList<GameItem> getRareDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = items.get(LootRarity.RARE);
        for(GameItem f : found) {
            boolean foundItem = false;
            for(GameItem drop : drops) {
                if (drop.getId() == f.getId()) {
                    foundItem = true;
                    break;
                }
            }
            if (!foundItem) {
                drops.add(f);
            }
        }
        return drops;
    }

    static {
        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(2400, 2), //Arbo key

                new GameItem(26714, 1),  //Arma (or) helm
                new GameItem(26715, 1),  //Arma (or) chest
                new GameItem(26716, 1),  //Arma (or) legs
                new GameItem(26714, 2),  //Arma (or) helm
                new GameItem(26715, 2),  //Arma (or) chest
                new GameItem(26716, 2),  //Arma (or) legs

                new GameItem(10350, 1),  //3rd age helm
                new GameItem(10348, 1),  //3rd age platebody
                new GameItem(10346, 1),  //3rd age platelegs
                new GameItem(10352, 1),  //3rd age kite
                new GameItem(10350, 2),  //3rd age helm
                new GameItem(10348, 2),  //3rd age platebody
                new GameItem(10346, 2),  //3rd age platelegs
                new GameItem(10352, 2),  //3rd age kite

                new GameItem(10334, 1),  //3rd age range coif
                new GameItem(10330, 1),  //3rd age range top
                new GameItem(10332, 1),  //3rd age range bottom
                new GameItem(10334, 2),  //3rd age range coif
                new GameItem(10330, 2),  //3rd age range top
                new GameItem(10332, 2),  //3rd age range bottom

                new GameItem(10338, 1),  //3rd age robe top
                new GameItem(10340, 1),  //3rd age robe bottom
                new GameItem(10344, 1),  //3rd age amulet
                new GameItem(10338, 2),  //3rd age robe top
                new GameItem(10340, 2),  //3rd age robe bottom
                new GameItem(10344, 2),  //3rd age amulet

                new GameItem(12691, 1),  //Tyrannical Ring
                new GameItem(12692, 1),  //Treasonous ring
                new GameItem(12691, 2),  //Tyrannical Ring
                new GameItem(12692, 2),  //Treasonous ring

                new GameItem(12806, 1),  //Maledtiction ward
                new GameItem(12807, 1),  //Odium ward
                new GameItem(12806, 2),  //Maledtiction ward
                new GameItem(12807, 2),  //Odium ward

                new GameItem(26498, 1),  //Book (or) unholy
                new GameItem(26496, 1),  //Book (or) holy
                new GameItem(26494, 1),  //Book (or) war
                new GameItem(26492, 1),  //Book (or) law
                new GameItem(26490, 1),  //Book (or) darkness
                new GameItem(26488, 1),  //Book (or) balance
                new GameItem(26498, 2),  //Book (or) unholy
                new GameItem(26496, 2),  //Book (or) holy
                new GameItem(26494, 2),  //Book (or) war
                new GameItem(26492, 2),  //Book (or) law
                new GameItem(26490, 2),  //Book (or) darkness
                new GameItem(26488, 2)   //Book (or) balance
        ));

        items.put(LootRarity.UNCOMMON,Arrays.asList(
                new GameItem(2400, 2), //Arbo key

                new GameItem(26225),  //Ancient Ceremonial mask
                new GameItem(26221),  //Ancient Ceremonial chest
                new GameItem(26223),  //Ancient Ceremonial legs

                new GameItem(26714),  //Arma (or) helm
                new GameItem(26715),  //Arma (or) chest
                new GameItem(26716),  //Arma (or) legs

                new GameItem(10350, 1),  //3rd age helm
                new GameItem(10348, 1),  //3rd age platebody
                new GameItem(10346, 1),  //3rd age platelegs
                new GameItem(10352, 1),  //3rd age kite
                new GameItem(10350, 2),  //3rd age helm
                new GameItem(10348, 2),  //3rd age platebody
                new GameItem(10346, 2),  //3rd age platelegs
                new GameItem(10352, 2),  //3rd age kite

                new GameItem(10334, 1),  //3rd age range coif
                new GameItem(10330, 1),  //3rd age range top
                new GameItem(10332, 1),  //3rd age range bottom
                new GameItem(10334, 2),  //3rd age range coif
                new GameItem(10330, 2),  //3rd age range top
                new GameItem(10332, 2),  //3rd age range bottom

                new GameItem(10338, 1),  //3rd age robe top
                new GameItem(10340, 1),  //3rd age robe bottom
                new GameItem(10344, 1),  //3rd age amulet
                new GameItem(10338, 2),  //3rd age robe top
                new GameItem(10340, 2),  //3rd age robe bottom
                new GameItem(10344, 2),  //3rd age amulet

                new GameItem(12691, 1),  //Tyrannical Ring
                new GameItem(12692, 1),  //Treasonous ring
                new GameItem(12691, 2),  //Tyrannical Ring
                new GameItem(12692, 2),  //Treasonous ring

                new GameItem(12806, 1),  //Maledtiction ward
                new GameItem(12807, 1),  //Odium ward
                new GameItem(12806, 2),  //Maledtiction ward
                new GameItem(12807, 2),  //Odium ward

                new GameItem(26498, 1),  //Book (or) unholy
                new GameItem(26496, 1),  //Book (or) holy
                new GameItem(26494, 1),  //Book (or) war
                new GameItem(26492, 1),  //Book (or) law
                new GameItem(26490, 1),  //Book (or) darkness
                new GameItem(26488, 1),   //Book (or) balance
                new GameItem(26498, 2),  //Book (or) unholy
                new GameItem(26496, 2),  //Book (or) holy
                new GameItem(26494, 2),  //Book (or) war
                new GameItem(26492, 2),  //Book (or) law
                new GameItem(26490, 2),  //Book (or) darkness
                new GameItem(26488, 2)   //Book (or) balance
        ));

        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(2400, 2),   //silverlight key

                new GameItem(27285, 2),  //eyes

                new GameItem(25739, 1),  //sanguinesti scythe

                new GameItem(33149, 1),  //nox staff

                new GameItem(26219, 1),  //osmumtens fang

                new GameItem(25979, 1),  //keris partisan

                new GameItem(25975, 1),  //lightbearer ring

                new GameItem(25985, 1),  //elidinis' ward

                new GameItem(30020, 1),  //Corrupt Beast

                new GameItem(30014, 1),  //K'klik

                new GameItem(20368, 1),  //Godsword (or) Arma
                new GameItem(20368, 1),  //Godsword (or) Arma
                new GameItem(20368, 1),  //Godsword (or) Arma
                new GameItem(20368, 1),  //Godsword (or) Arma
                new GameItem(20368, 1),  //Godsword (or) Arma
                new GameItem(20368, 1),  //Godsword (or) Arma
                new GameItem(20368, 1),  //Godsword (or) Arma

                new GameItem(20370, 1),  //Godsword (or) Bandos
                new GameItem(20370, 1),  //Godsword (or) Bandos
                new GameItem(20370, 1),  //Godsword (or) Bandos
                new GameItem(20370, 1),  //Godsword (or) Bandos
                new GameItem(20370, 1),  //Godsword (or) Bandos
                new GameItem(20370, 1),  //Godsword (or) Bandos
                new GameItem(20370, 1),  //Godsword (or) Bandos

                new GameItem(20372, 1),  //Godsword (or) Sara
                new GameItem(20372, 1),  //Godsword (or) Sara
                new GameItem(20372, 1),  //Godsword (or) Sara
                new GameItem(20372, 1),  //Godsword (or) Sara
                new GameItem(20372, 1),  //Godsword (or) Sara
                new GameItem(20372, 1),  //Godsword (or) Sara
                new GameItem(20372, 1),  //Godsword (or) Sara

                new GameItem(20374, 1),  //Godsword (or) Zammy
                new GameItem(20374, 1),  //Godsword (or) Zammy
                new GameItem(20374, 1),  //Godsword (or) Zammy
                new GameItem(20374, 1),  //Godsword (or) Zammy
                new GameItem(20374, 1),  //Godsword (or) Zammy
                new GameItem(20374, 1),  //Godsword (or) Zammy
                new GameItem(20374, 1)   //Godsword (or) Zammy
        ));
    }

    public static ArrayList<GameItem> getRare() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = items.get(LootRarity.RARE);
        for(GameItem f : found) {
            boolean foundItem = false;
            for(GameItem drop : drops) {
                if (drop.getId() == f.getId()) {
                    foundItem = true;
                    break;
                }
            }
            if (!foundItem) {
                drops.add(f);
            }
        }
        return drops;
    }

    public static ArrayList<GameItem> getAllDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        items.forEach((lootRarity, gameItems) -> {
            gameItems.forEach(g -> {
                if (!drops.contains(g)) {
                    drops.add(g);
                }
            });
        });
        return drops;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return getItems();
    }

    @Override
    public void roll(Player player) {

    }
}
