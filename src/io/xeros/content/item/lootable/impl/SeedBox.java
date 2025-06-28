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

public class SeedBox implements Lootable {

    /**
     * The item id of the mystery box required to trigger the event
     */
    public static final int SeedBox = 22993;

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
                        new GameItem(5318, 3),  //potato
                        new GameItem(5319, 3),  //onion
                        new GameItem(5324, 3),  //cabbage
                        new GameItem(5322, 3),  //tomato
                        new GameItem(5320, 3),  //sweetcorn
                        new GameItem(5096, 3),  //marigolds
                        new GameItem(5097, 3),  //rosemary
                        new GameItem(5098, 3),  //nasturtiums
                        new GameItem(5099, 3),  //woad
                        new GameItem(5291, 3),  //guam
                        new GameItem(5292, 3),  //marrentill
                        new GameItem(5293, 3),  //tarromin
                        new GameItem(5294, 3)   //harralander
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(5323, 3),  //strawberry
                        new GameItem(5323, 3),  //strawberry
                        new GameItem(5321, 3),  //watermelon
                        new GameItem(5321, 3),  //watermelon
                        new GameItem(5100, 3),  //limpwurt
                        new GameItem(5100, 3),  //limpwurt
                        new GameItem(5295, 3),  //ranarr
                        new GameItem(5295, 3),  //ranarr
                        new GameItem(5296, 3),  //toadflax
                        new GameItem(5296, 3),  //toadflax
                        new GameItem(5297, 3),  //irit
                        new GameItem(5297, 3),  //irit
                        new GameItem(5298, 3),  //avantoe
                        new GameItem(5298, 3)   //avantoe
                        ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(5299, 3),  //kwuarm
                        new GameItem(5299, 3),  //kwuarm
                        new GameItem(5300, 3),  //snapdragon
                        new GameItem(5300, 3),  //snapdragon
                        new GameItem(5301, 3),  //cadanite
                        new GameItem(5301, 3),  //cadanite
                        new GameItem(5302, 3),  //lantadyme
                        new GameItem(5302, 3),  //lantadyme
                        new GameItem(5303, 3),  //dwarf weed
                        new GameItem(5303, 3),  //dwarf weed
                        new GameItem(5304, 3),  //torstol
                        new GameItem(5304, 3)   //torstol
                ));

        items.put(VERY_RARE,
                Arrays.asList(
                        new GameItem(13646),  //farming hat
                        new GameItem(13646),  //farming hat
                        new GameItem(13642),  //farming body
                        new GameItem(13642),  //farming body
                        new GameItem(13640),  //farming legs
                        new GameItem(13640),  //farming legs
                        new GameItem(13644),  //farming boots
                        new GameItem(13644),  //farming boots
                        new GameItem(7409),   //magic secateurs
                        new GameItem(13679)   //cabbage cape
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
            player.sendMessage("You need at least two free slots to open a seed box.");
            return;
        }
        if (!player.getItems().playerHasItem(SeedBox)) {
            player.sendMessage("You need a seed box to do this.");
            return;
        }
        player.getItems().deleteItem(SeedBox, 1);
        player.lastMysteryBox = System.currentTimeMillis();
        CycleEventHandler.getSingleton().stopEvents(this);
        CycleEventHandler.getSingleton().addEvent(this, getCycleEvent(player), 1);
    }
    public void openall(Player player) {
        int amount = player.getItems().getInventoryCount(22993);
        if (amount <1){
            return;
        }

        if (amount > 10000) {
            amount = 10000;
        }

        player.getItems().deleteItem(SeedBox, amount);

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
