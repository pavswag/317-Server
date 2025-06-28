package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import lombok.Getter;

import java.util.*;

public class DonoVault implements Lootable {
    @Getter
    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

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
                new GameItem(6679, 20),  //Mini Mystery Box
                new GameItem(6679, 20),
                new GameItem(6679, 50),
                new GameItem(6679, 50),
                new GameItem(6679, 70),
                new GameItem(6679, 70),
                new GameItem(6679, 10),
                new GameItem(6679, 10),

                new GameItem(6677, 20),  //Mini Super Mystery Box
                new GameItem(6677, 5),
                new GameItem(6677, 10),

                new GameItem(6678,20),    //Start Mini Ultra Mystery Box
                new GameItem(6678,5),
                new GameItem(6678,7),
                new GameItem(6678,10)
        ));

        items.put(LootRarity.UNCOMMON,Arrays.asList(
                new GameItem(21003,1),  //Elder Maul
                new GameItem(21012,1),  //Dragon Hunter Crossbow
                new GameItem(21018,1),  //Ancestral Hat
                new GameItem(21021,1),  //Ancestral Top
                new GameItem(21024,1),  //ancestral Bottom
                new GameItem(21006,1),  //Kodai
                new GameItem(26714,1),  //Armadyl Helm(or)
                new GameItem(26715,1),  //Armadyl Body(or)
                new GameItem(26716,1),  //Armadyl Legs(or)
                new GameItem(25916,1),  //DHCB (t)
                new GameItem(21000,1),  //Twisted Buckler
                new GameItem(26718,1),  //Bandos Chestplate(or)
                new GameItem(26719,1),  //Bandos Tassets(or)
                new GameItem(26720,1),  //Bandos Boots(or)
                new GameItem(20784,1),  //Dragon Claws
                new GameItem(22322,1),  //Avernic Defender (not hilt)
                new GameItem(21079,1),  //Arcane Prayer Scroll
                new GameItem(21015,1),  //Dexterous Prayer Scroll
                new GameItem(20788,1),  //Row (i3)
                new GameItem(22881,3),  //Attas Seed
                new GameItem(22869,3),  //Celastrus
                new GameItem(6112,3),   //Kelda
                new GameItem(20909,3),  //Buchu
                new GameItem(20906,3),  //Golpar
                new GameItem(20903,3)   //Noxifer
        ));

        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(19887,5),  //Mini Dono Box
                new GameItem(6769,1),   //$5
                new GameItem(2403,1),   //$10
                new GameItem(20786,1),  //row(i5)
                new GameItem(22883,2),  //Iasor

                new GameItem(22885,2),  //Kronos

                new GameItem(22875,1),  //Hespori Seed
                new GameItem(6805,3),   //Fortune Spin
                new GameItem(21000,1),  //Twisted Buckler
                new GameItem(24419,1),  //Inquisitor Helm
                new GameItem(24420,1),  //Inquisitor Top
                new GameItem(24421,1),  //Inquisitor Bottoms
                new GameItem(24417,1),  //Inquisitor Mace
                new GameItem(22326,1),  //Justiciar Faceguard
                new GameItem(22327,1),  //Justiciar Chestguard
                new GameItem(22328,1),  //Justiciar Legguard
                new GameItem(22324,1),  //Ghrazi Rapier
                new GameItem(26219,1),  //Osmummy's Fang
                new GameItem(24664,1),  //Tiwsted Ancestral Hat
                new GameItem(24666,1),  //Twisted Ancestral Body
                new GameItem(24668,1),  //Twisted Ancestral Bottoms
                new GameItem(10559,1),  //Healer Icon
                new GameItem(10558,1),  //Defender Icon
                new GameItem(10557,1),  //Collector Icon
                new GameItem(10556,1),  //Attacker Icon
                new GameItem(30014,1),  //K'klik
                new GameItem(30021,1),  //Roc
                new GameItem(30020,1)   //Corrupt Beast

        ));
    }

    public static ArrayList<GameItem> getRare() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = items.get(LootRarity.RARE);
        for(GameItem f : found) {
            boolean foundItem = false;
            for(GameItem drop : drops) {
                if(drop.getId() == f.getId()) {
                    foundItem = true;
                    break;
                }
            }
            if(!foundItem) {
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
    public Map<LootRarity, List<GameItem>> getLoot() { return getItems(); }

    @Override
    public void roll(Player player) {

    }
}