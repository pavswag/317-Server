package io.xeros.content.minigames.coinflip;

import io.xeros.annotate.PostInit;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.ItemAction;
import io.xeros.util.Misc;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 05/03/2024
 */
public class CoinFlip {

    @PostInit
    public static void itemActionHandler() {
        ItemAction.registerInventory(33256, 1, (player, item) -> openInterface(player, item.getId()));
    }

    private static void sendGifChange(Player player, String image) {
        player.getPA().sendGifChange(24490, image);
    }

    public static void openInterface(Player player, int cardID) {
        if (cardID == -1) return;
        player.coinFlipCard = cardID;
        reDrawInterface(player);
    }

    private static void reDrawInterface(Player player) {
        sendGifChange(player, Misc.random(1) == 0 ? "Blue-Coin-STILL" : "Red-Coin-STILL");

        if (player.coinFlipColor.equalsIgnoreCase("red")) {
            player.getPA().sendConfig(2001, 1);
            player.getPA().sendConfig(2000, 0);
        }
        else if (player.coinFlipColor.equalsIgnoreCase("blue")) {
            player.getPA().sendConfig(2001, 0);
            player.getPA().sendConfig(2000, 1);
        }
        else {
            player.getPA().sendConfig(2001, 0);
            player.getPA().sendConfig(2000, 1);
        }

        if (player.coinFlipPrize != -1) {
            player.getPA().itemOnInterface(player.coinFlipPrize,1,24496,0);
            player.getPA().sendString(24498, ItemDef.forId(player.coinFlipPrize).getName());
        } else {
            player.getPA().itemOnInterface(-1,1, 24496,0);
            player.getPA().sendString(24498, "");
        }

        player.getPA().showInterface(24485);
    }

    public static void handleSpin(Player player) {
        if (player.coinFlipProgress) {
            player.sendMessage("@red@Don't interrupt the spin, or else you face loosing rewards!");
            return;
        }
        player.getItems().deleteItem2(player.coinFlipCard, 1);
        player.coinFlipProgress = true;
        var redCoin = player.coinFlipColor.equalsIgnoreCase("red");
        int rng = Misc.random(0, 100);
        var winner = false;
        var odds = 45;
        if (player.coinFlipPrize == 27275 ||
            player.coinFlipPrize == 26269 ||
            player.coinFlipPrize == 28547 ||
            player.coinFlipPrize == 33207 ||
            player.coinFlipPrize == 33205)
            odds = 35;
        if (rng < odds) {
            winner = true;
            sendGifChange(player, redCoin ? "Red-Winner" : "Blue-Winner");
        }
        else
            sendGifChange(player, redCoin ? "Red-Loser" : "Blue-Loser");

        boolean finalWinner = winner;
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (container.getTotalTicks() == 5) {
                    if (finalWinner)
                        player.getItems().addItemUnderAnyCircumstance(player.coinFlipPrize, 1);
                    PlayerHandler.executeGlobalMessage(String.format(
                            "<img=19><col=FFD500><shad=0>[CoinFlip]@bla@ %s has just @red@%s @bla@ a @gr1@%s @bla@from a coin flip!",
                            player.getDisplayName(),
                            finalWinner ? "won" : "failed @bla@at obtaining",
                            ItemDef.forId(player.coinFlipPrize).getName()));
                }
                if (container.getTotalTicks() == 7) {
                    sendGifChange(player, redCoin ? "Red-Coin-STILL" : "Blue-Coin-STILL");
                    player.coinFlipProgress = false;
                    container.stop();
                }
            }
        },1);
    }

    public static boolean handleButton(Player player, int id) {
        if (id == 24499) { // item pick
            if (player.coinFlipCard == -1) {
                return true;
            }
            if (player.getItems().getInventoryCount(player.coinFlipCard) <= 0) {
                player.sendMessage("You don't have any card's left!");
                return true;
            }
            player.getPA().closeAllWindows();
            var coinFlipJson = CoinFlipJson.getInstance();
            int itemListSize = coinFlipJson.getLootItemsForCardId(player.coinFlipCard).size();
            player.getPA().setScrollableMaxHeight(24502, (int) (2.37 * itemListSize));
            
            for (int i = 0; i < itemListSize; i++) {
                var reward = coinFlipJson.getLootItemsForCardId(player.coinFlipCard).get(i);
                player.getPA().itemOnInterface(reward.getId(), reward.getAmount(), 24503, i);
            }
            //Settings choose item
            player.getPA().showInterface(24500);
            return true;
        }
        if (id == 24491) {
            if (player.coinFlipCard == -1) {
                return true;
            }
            if (player.getItems().getInventoryCount(player.coinFlipCard) <= 0) {
                player.sendMessage("You don't have any card's left!");
                return true;
            }
            if (player.coinFlipColor.equalsIgnoreCase("")) {
                player.sendMessage("@red@You need to select a side first!");
                return true;
            }
            if (player.coinFlipPrize == -1) {
                player.sendMessage("@red@You need to select a prize before flipping the coin!");
                return true;
            }
            handleSpin(player);
            return true;
        }
        if (id == 24489) {
            if (player.coinFlipCard == -1) {
                return true;
            }
            if (player.getItems().getInventoryCount(player.coinFlipCard) <= 0) {
                player.sendMessage("You don't have any card's left!");
                return true;
            }
            //Red Coin
            player.getPA().sendConfig(2001, 1);
            player.getPA().sendConfig(2000, 0);
            player.coinFlipColor = "red";
            sendGifChange(player, "Red-Coin-STILL");
            return true;
        }
        if (id == 24488) {
            if (player.coinFlipCard == -1) {
                return true;
            }
            if (player.getItems().getInventoryCount(player.coinFlipCard) <= 0) {
                player.sendMessage("You don't have any card's left!");
                return true;
            }
            //Blue Coin
            player.getPA().sendConfig(2001, 0);
            player.getPA().sendConfig(2000, 1);
            player.coinFlipColor = "blue";
            sendGifChange(player, "Blue-Coin-STILL");
            return true;
        }
        return false;
    }

    public static boolean handleItemChoice(Player player, int interfaceID, int slot, int ItemID) {
        if (interfaceID == 24503) {
            player.coinFlipPrize = ItemID;
            reDrawInterface(player);
            return true;
        }
        return false;
    }
}
