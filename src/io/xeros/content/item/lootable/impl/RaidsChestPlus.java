package io.xeros.content.item.lootable.impl;

import io.xeros.content.combat.death.NPCDeath;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.content.prestige.PrestigePerks;
import io.xeros.model.Npcs;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RaidsChestPlus implements Lootable {

    private static final int KEY = 25_432;
    private static final int ANIMATION = 881;

    private static GameItem randomChestRewards() {
        List<GameItem> itemList = RaidPlusItems.getItems().get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);
    }

    public static ArrayList<GameItem> getRareDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = RaidPlusItems.getItems().get(LootRarity.RARE);
        for (GameItem f : found) {
            boolean foundItem = false;
            for (GameItem drop : drops) {
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

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return RaidPlusItems.getItems();
    }

    @Override
    public void roll(Player c) {
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);
            GameItem reward = randomChestRewards();

            if (reward.getId() == 22325 || reward.getId() == 22323 || reward.getId() == 22324) {
                c.getCollectionLog().handleDrop(c, Npcs.THE_MAIDEN_OF_SUGADINTI, reward.getId(), 1);
            } else if (reward.getId() == 20997) {
                c.getCollectionLog().handleDrop(c, 7554, reward.getId(), reward.getAmount());
            }
            c.getItems().addItem(reward.getId(), (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? reward.getAmount() * 2 : reward.getAmount()));
            c.sendMessage("@blu@You have received a rare item out of the storage unit.");
            NPCDeath.announceKc(c, reward, c.raidCount);
        } else {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }

}