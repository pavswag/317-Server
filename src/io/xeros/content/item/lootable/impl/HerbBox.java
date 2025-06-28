package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

import java.util.*;

import static io.xeros.content.item.lootable.LootRarity.VERY_RARE;

public class HerbBox implements Lootable {

    /**
     * The item id of the mystery box required to trigger the event
     */
    public static final int HerbBox = 11738;

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
                        new GameItem(200, 3),   //Guam
                        new GameItem(202, 3),   //Marrentil
                        new GameItem(204, 3),   //Tarromin
                        new GameItem(206, 3),   //Harralander
                        new GameItem(208, 3),   //Ranarr
                        new GameItem(3050, 3),  //Toadflax
                        new GameItem(210, 3),   //Irit
                        new GameItem(212, 3),   //Avantoe
                        new GameItem(214, 3),   //Kwuarm
                        new GameItem(3052, 3),  //Snapdragon
                        new GameItem(216, 3),   //Cadanite
                        new GameItem(2486, 3),  //Lantadyme
                        new GameItem(218, 3),   //Dwarf weed
                        new GameItem(220, 3)    //Torstol
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(200, 3),   //Guam
                        new GameItem(202, 3),   //Marrentil
                        new GameItem(204, 3),   //Tarromin
                        new GameItem(206, 3),   //Harralander
                        new GameItem(208, 3),   //Ranarr
                        new GameItem(3050, 3),  //Toadflax
                        new GameItem(210, 3),   //Irit
                        new GameItem(212, 3),   //Avantoe
                        new GameItem(214, 3),   //Kwuarm
                        new GameItem(3052, 3),  //Snapdragon
                        new GameItem(216, 3),   //Cadanite
                        new GameItem(2486, 3),  //Lantadyme
                        new GameItem(218, 3),   //Dwarf weed
                        new GameItem(220, 3)    //Torstol
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(200, 3),   //Guam
                        new GameItem(202, 3),   //Marrentil
                        new GameItem(204, 3),   //Tarromin
                        new GameItem(206, 3),   //Harralander
                        new GameItem(208, 3),   //Ranarr
                        new GameItem(3050, 3),  //Toadflax
                        new GameItem(210, 3),   //Irit
                        new GameItem(212, 3),   //Avantoe
                        new GameItem(214, 3),   //Kwuarm
                        new GameItem(3052, 3),  //Snapdragon
                        new GameItem(216, 3),   //Cadanite
                        new GameItem(2486, 3),  //Lantadyme
                        new GameItem(218, 3),   //Dwarf weed
                        new GameItem(220, 3)    //Torstol
                ));

        items.put(VERY_RARE,
                Arrays.asList(
                        new GameItem(200, 3),   //Guam
                        new GameItem(202, 3),   //Marrentil
                        new GameItem(204, 3),   //Tarromin
                        new GameItem(206, 3),   //Harralander
                        new GameItem(208, 3),   //Ranarr
                        new GameItem(3050, 3),  //Toadflax
                        new GameItem(210, 3),   //Irit
                        new GameItem(212, 3),   //Avantoe
                        new GameItem(214, 3),   //Kwuarm
                        new GameItem(3052, 3),  //Snapdragon
                        new GameItem(216, 3),   //Cadanite
                        new GameItem(2486, 3),  //Lantadyme
                        new GameItem(218, 3),   //Dwarf weed
                        new GameItem(220, 3)    //Torstol
                ));
    }

    public static CycleEvent getCycleEvent(final Player player) {
        return new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (player.isDisconnected() || Objects.isNull(player)) {
                    container.stop();
                    return;
                }

                int random = Misc.random(100);
                List<GameItem> itemList = random < 55 ? items.get(LootRarity.COMMON) : random >= 55 && random <= 80 ? items.get(LootRarity.UNCOMMON) : items.get(LootRarity.RARE);
                GameItem item = Misc.getRandomItem(itemList);
                GameItem itemDouble = Misc.getRandomItem(itemList);

                if (Misc.random(10) == 0) {
                    player.getItems().addItem(item.getId(), item.getAmount());
                    player.getItems().addItem(itemDouble.getId(), itemDouble.getAmount());
                    player.sendMessage("You receive <col=255>" + item.getAmount() + " x " + ItemAssistant.getItemName(item.getId()) + "</col>.");
                    player.sendMessage("You receive <col=255>" + itemDouble.getAmount() + " x " + ItemAssistant.getItemName(itemDouble.getId()) + "</col>.");
                } else {
                    player.getItems().addItem(item.getId(), item.getAmount());
                    player.sendMessage("You receive <col=255>" + item.getAmount() + " x " + ItemAssistant.getItemName(item.getId()) + "</col>.");
                }
                container.stop();
            }
        };
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    /**
     * Opens a mystery box if possible, and ultimately triggers and event, if possible.
     */
    public void roll(Player player) {
        if (System.currentTimeMillis() - player.lastMysteryBox < 600 * 4) {
            return;
        }
        if (player.getItems().freeSlots() < 2) {
            player.sendMessage("You need at least two free slots to open a herb box.");
            return;
        }
        if (!player.getItems().playerHasItem(HerbBox)) {
            player.sendMessage("You need a herb box to do this.");
            return;
        }
        player.getItems().deleteItem(HerbBox, 1);
        player.lastMysteryBox = System.currentTimeMillis();
        CycleEventHandler.getSingleton().stopEvents(this);
        CycleEventHandler.getSingleton().addEvent(this, getCycleEvent(player), 1);
    }
    public void openall(Player player) {
        int amount = player.getItems().getInventoryCount(11738);
        if (amount <1){
            return;
        }

        if (amount > 10000) {
            amount = 10000;
        }

        player.getItems().deleteItem(HerbBox, amount);

        List<GameItem> rewards = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            int random = Misc.random(100);
            List<GameItem> itemList = random < 55 ? items.get(LootRarity.COMMON) : random >= 55 && random <= 80 ? items.get(LootRarity.UNCOMMON) : items.get(LootRarity.RARE);
            GameItem item = Misc.getRandomItem(itemList);
            rewards.add(item);
        }
        if (rewards.isEmpty())
            return;

        rewards.forEach(items -> player.getItems().addItemUnderAnyCircumstance(items.getId(), items.getAmount()));
    }
}
