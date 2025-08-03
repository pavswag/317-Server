package io.xeros.content.item.lootable.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.content.minigames.raids.Raids;
import io.xeros.content.prestige.PrestigePerks;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public class RaidsChestCommon implements Lootable {

    private static final int KEY = Raids.COMMON_KEY;
    private static final int ANIMATION = 881;

    public static void main(String[] args) throws Exception {
        ItemDef.load();
        HashMap<GameItem, Integer> map = new HashMap<>();

        for (int i = 0; i < 10000; i++) {
            GameItem gameItem = randomChestRewards();
            int amount = map.getOrDefault(gameItem, 0);
            map.put(gameItem, amount + 1);
        }

        map.forEach((gameItem, amount) -> {
            String dropChance = String.format("%.2f", ((double) amount / 10000) * 100);
            String itemName = ItemDef.forId(gameItem.getId()).getName();
            System.out.println("Rolled a " + itemName + " " + amount + " times. " + dropChance);
        });

    }

    public static GameItem randomChestRewards() {
        List<GameItem> itemList = RaidsChestItems.getItems().get(LootRarity.COMMON);
        return Misc.getRandomItem(itemList);
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return RaidsChestItems.getItems();
    }

    @Override
    public void roll(Player c) {
        int twistedhornsroll = Misc.random(120);
        if (twistedhornsroll == 1) {
            giveReward(c, new GameItem(24466, 1), false);
            PlayerHandler.executeGlobalMessage("@bla@[@blu@RAIDS@bla@] " + c.getDisplayName() + "@pur@ has just received twisted horns.");
        }
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            c.startAnimation(ANIMATION);
            GameItem reward = randomChestRewards();
            GameItem reward2 = randomChestRewards();
            GameItem reward3 = randomChestRewards();

            giveReward(c, reward, true);
            giveReward(c, reward2, true);
            giveReward(c, reward3, true);
            c.sendMessage("@blu@You received a common item out of the storage unit.");
        } else {
            c.sendMessage("@blu@The chest is locked, it won't budge!");
        }
    }

    private void giveReward(Player c, GameItem reward, boolean allowDouble) {
        int amount = reward.getAmount();
        if (allowDouble && PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10)) {
            amount *= 2;
        }
        if (c.getItems().addItem(reward.getId(), amount)) {
            c.sendMessage("You receive " + reward.getDef().getName() + " x" + amount + ".");
        } else {
            c.getItems().addItemToBankOrDrop(reward.getId(), amount);
        }
    }
}
