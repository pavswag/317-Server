package io.xeros.content.item.lootable.impl;

import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.event.eventcalendar.EventChallenge;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.content.prestige.PrestigePerks;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HesporiChest implements Lootable {

    private static final int KEY = Hespori.KEY;
    private static final int ANIMATION = 881;

    private static GameItem randomChestRewardsCommon() {
        List<GameItem> itemList = HesporiChestItems.getItems().get(LootRarity.COMMON);
        return Misc.getRandomItem(itemList);
    }

    private static GameItem randomChestRewardsRare() {
        List<GameItem> itemList = HesporiChestItems.getItems().get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return HesporiChestItems.getItems();
    }

    @Override
    public void roll(Player c) {
        int random = Misc.random(1000);
        int rareChance = 950;
        if (c.getItems().playerHasItem(21046)) {
            rareChance = 770;
            c.getItems().deleteItem(21046, 1);
            c.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate." );
            c.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
        }
        if (random < rareChance) {
            if (c.getItems().playerHasItem(KEY)) {
                c.getItems().deleteItem(KEY, 1);
                c.getItems().addItem(995, 500_000 + Misc.random(1_000_000));
                c.startAnimation(ANIMATION);
                GameItem reward = randomChestRewardsCommon();
                GameItem reward2 = randomChestRewardsCommon();
                c.getItems().addItem(reward.getId(), (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? reward.getAmount() * 2 : reward.getAmount()));
                c.getItems().addItem(reward2.getId(), (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? reward2.getAmount() * 2 : reward2.getAmount()));
                c.sendMessage("@blu@You received common items out of the chest.");

            } else {
                c.sendMessage("@blu@The chest is locked, it won't budge!");
            }
        } else if (random >= rareChance) {
            if (c.getItems().playerHasItem(KEY)) {
                c.getItems().deleteItem(KEY, 1);
                c.getItems().addItem(995, 500_000 + Misc.random(1_000_000));
                c.startAnimation(ANIMATION);
                GameItem reward = randomChestRewardsRare();
                c.getItems().addItem(reward.getId(), (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? reward.getAmount() * 2 : reward.getAmount()));
                c.getCollectionLog().handleDrop(c, 8583, reward.getId(), 1);
                if (reward.getId() != Items.TOME_OF_FIRE_EMPTY) {
                    ItemDef def = ItemDef.forId(reward.getId());
                    PlayerHandler.executeGlobalMessage("@bla@[<col=3d7018>HESPORI@bla@] The Hespori chest rewarded a <col=47831c>rare seed!");
                }
            } else {
                c.sendMessage("@blu@The chest is locked, it won't budge!");
            }
        }
    }

    public static void main(String[] args) throws Exception {
        ItemDef.load();
        HashMap<GameItem, Integer> map = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            int random = Misc.random(100);
            int rareChance = 77;
            GameItem gameItem = (random >= rareChance ? Misc.getRandomItem(HesporiChestItems.getItems().get(LootRarity.RARE)) :
                    Misc.getRandomItem(HesporiChestItems.getItems().get(LootRarity.COMMON)));
            int amount = map.getOrDefault(gameItem, 0);
            map.put(gameItem, amount + 1);
        }

        map.forEach((gameItem, amount) -> {
            String dropChance = String.format("%.2f", ((double) amount / 10000) * 100);
            String itemName = ItemDef.forId(gameItem.getId()).getName();
            System.out.println("Rolled a " + itemName + " " + amount + " times. " + dropChance);
        });

    }
}
