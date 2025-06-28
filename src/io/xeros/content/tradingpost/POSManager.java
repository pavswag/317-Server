package io.xeros.content.tradingpost;

import io.xeros.Configuration;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerAssistant;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 05/04/2024
 */
public class POSManager {

    private static final int MAX_MY_OFFERS = 6;
    private static final int MAX_VIEW_OFFERS = 50;
    private final int MyOffersView = 48600;
    private final int OfferViewInterface = 48000;

    public List<TradePostOffer> tradePostOffers = new ArrayList<>();
    private List<TradePostOffer> viewOffers = new ArrayList<>();
    private List<TradePostHistory> historyList = new ArrayList<>();

    private TradePostSort sort = TradePostSort.AGE_ASCENDING;

    private Player player;
    private String searchText;
    @Getter
    @Setter
    private long nomadCoffer;
    @Getter
    @Setter
    private long coinCoffer;

    public void init(Player player) {
        this.player = player;

        if (!player.tempTradeOffers.isEmpty()) {
            for (TradePostOffer tradePostOffer : player.tempTradeOffers) {
                tradePostOffer.setUsername(player.getDisplayName());
            }
        }

        if (!player.tempTradeOffers.isEmpty()) {
            tradePostOffers.addAll(player.tempTradeOffers);

            player.tempTradeOffers.clear();
        }

        nomadCoffer = player.tempNomadCoffer;
        coinCoffer = player.tempPlatCoffer;

        player.tempNomadCoffer = 0;
        player.tempPlatCoffer = 0;//Set to zero after setting up the player.
        handleGEScript(player);
    }

    public void openViewOffers() {
        if (Configuration.DISABLE_TRADING_POST) {
            player.sendErrorMessage("The Trading Post is currently Disabled!");
            return;
        }
        player.setInTradingPost(true);
        player.setSidebarInterface(3, 3213);
        updateViewOffers();
        player.setOpenInterface(OfferViewInterface);
        player.getPA().showInterface(OfferViewInterface);
    }

    public void openMyOffers() {
        if (Configuration.DISABLE_TRADING_POST) {
            player.sendErrorMessage("The Trading Post is currently Disabled!");
            return;
        }
        player.setInTradingPost(true);
        sendTabInterface();
        updateMyOffers();
        player.setSidebarInterface(3, 48500);
        player.setOpenInterface(MyOffersView);
        player.getPA().showInterface(MyOffersView);
    }

    private void sendTabInterface() {
        for (int k = 0; k < 28; k++) {
            player.getPA().sendTradingPost(48501, player.playerItems[k] - 1, k, player.playerItemsN[k]);
        }
    }

    public void handleInput(int interfaceId, int container, int itemId) {
        if (interfaceId == 48500) {
            if (!player.isInTradingPost()) {
                return;
            }
            if (player.getItems().getInventoryCount(itemId) <= 0) {
                player.sendErrorMessage("You don't have any of these items.");
                return;
            }
            switch (container) {
                case 1:
                    promptCreateOffer(itemId, 1);
                    break;
                case 2:
                    promptCreateOffer(itemId, 5);
                    break;
                case 3:
                    promptCreateOffer(itemId, 10);
                    break;
                case 4:
                    promptCreateOffer(itemId, player.getItems().getInventoryCount(itemId));
                    break;
                case 5:
                    player.getPA().sendEnterAmount("How much would you like to sell?", (plr, amt) -> promptCreateOffer(itemId, amt));
                    break;
            }
        }

        if (interfaceId == 26022) {
            if (!player.isInTradingPost()) {
                return;
            }
            switch (container) {
                case 1:
                    buy(itemId, 1);
                    break;
                case 2:
                    buy(itemId, 5);
                    break;
                case 3:
                    buy(itemId, 10);
                    break;
                case 4:
                    buy(itemId, 99999);
                    break;
                case 5:
                    player.getPA().sendEnterAmount("How many would you like to purchase?", (plr, amount) -> buy(itemId, amount));
                    break;
            }
        }

        if (interfaceId == 48847) {
            if (!player.isInTradingPost()) {
                return;
            }
            cancelOffer(container);
        }


    }

    private void cancelOffer(int index) {
        if (index == -1) {
            return;
        }

        if (index >= tradePostOffers.size()) {
            return;
        }

        TradePostOffer getter = tradePostOffers.get(index);
        final String message =
                "Offered Name:  **__" + player.getLoginName().toUpperCase() + "__** \n" +
                        "Item Name: **__" + GameItem.get(getter.getItem().getId()).getDef().getName() + "__** \n" +
                        "Amount: **__" + getter.getItem().getAmount() + "__** \n" +
                        "Price: **__" + formatPrice((long) getter.getPricePerItem() * getter.getItem().getAmount()) + "__** \n" +
                        "TimeStamp: " + "**__<t:" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + ":f>__**";

        Discord.sendEmbeddedServerLog(
                Discord.TP_CANCEL_CHANNEL,
                Color.RED,
                "TP Removal Logs",
                message,
                player.getLoginName(),
                "IP Address: " + player.getIpAddress());

        TradePostOffer offer = tradePostOffers.remove(index);
        player.getItems().addItemUnderAnyCircumstance(offer.getItem().getId(), offer.getItem().getAmount());
        openMyOffers();
        handleGEScript(player);
    }

    private void promptCreateOffer(int itemId, int amount) {
        if (tradePostOffers.size() > MAX_MY_OFFERS) {
            player.sendErrorMessage("You cannot create more offers.");
            return;
        }

        ItemDef itemDef = ItemDef.forId(itemId);
        if (itemDef == null || !itemDef.isTradable() || itemId == 995 || itemId == 13204) {
            player.sendErrorMessage("You cannot trade this item.");
            return;
        }

        final int unnotedId = itemDef.getUnNotedIdIfNoted();
        if (tradePostOffers.stream().anyMatch(offer -> offer.getItem().getId() == unnotedId)) {
            player.sendErrorMessage("You already have offer for this item.");
            return;
        }

        if (player.getItems().getInventoryCount(itemId) < amount) {
            player.sendErrorMessage("You need the item, before you can sell it.");
            return;
        }


        player.start(new DialogueBuilder(player).option("Would you like to sell this for nomad or coins?",
                new DialogueOption("Platinum", p -> handleItemListing(itemId, false, unnotedId, amount)),
                new DialogueOption("Nomad", p -> handleItemListing(itemId, true, unnotedId, amount)),
                new DialogueOption("Nevermind.", p -> p.getPA().closeAllWindows())));
    }

    private void handleItemListing(int itemId, boolean nomad, int unnotedId, int amount) {
        if (amount < 0 || amount == Integer.MAX_VALUE) {
            player.sendErrorMessage("You entered an invalid amount!");
            player.getPA().closeAllWindows();
            return;
        }
        if (!player.getItems().playerHasItem(itemId, amount)) {
            player.sendErrorMessage("You do not have that many of this item!");
            player.getPA().closeAllWindows();
            return;
        }
        player.getPA().sendEnterAmount("Enter price per item:", (plr, price) -> {
            if (((long) price * ((long) amount)) > Integer.MAX_VALUE) {
                plr.sendErrorMessage("That offer requires too much money to buy, it's invalid.");
                return;
            }
            if (!plr.getItems().playerHasItem(itemId, amount)) {
                plr.sendErrorMessage("You do not have that many of this item!");
                plr.getPA().closeAllWindows();
                return;
            }
            plr.getItems().deleteItem2(itemId, amount);

            final String message =
                    "Offered Name:  **__" + player.getLoginName().toUpperCase() + "__** \n" +
                            "Item Name: **__" + GameItem.get(itemId).getDef().getName() + "__** \n" +
                            "Amount: **__" + amount + "__** \n" +
                            "Price: **__" + formatPrice((long) price * amount) + "__** \n" +
                            "TimeStamp: " + "**__<t:" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + ":f>__**";

            Discord.sendEmbeddedServerLog(
                    Discord.TP_LISTING_CHANNEL,
                    Color.YELLOW,
                    "TP Offer Logs",
                    message,
                    player.getLoginName(),
                    "IP Address: " + player.getIpAddress());

            tradePostOffers.add(
                    new TradePostOffer(
                            plr.getDisplayName(),
                            new GameItem(unnotedId, amount),
                            price,
                            System.currentTimeMillis(),
                            nomad,
                            0)
            );

            handleGEScript(plr);

            openMyOffers();
        });
    }

    private void handleGEScript(Player player) {
        int i = 0;

        for (TradePostOffer offer : player.getTradePost().tradePostOffers) {
            player.getPA().runClientScript(13025, i, offer.getPricePerItem(), (offer.isNomad() ? 696 : 13204), offer.getItem().getId(), offer.getTotalSold(), (offer.getItem().getAmount() + offer.getTotalSold()));
            i++;
        }
    }

    private void loadHistory() {
        if (historyList.isEmpty()) {
            return;
        }

        for (int i = 0, start1 = 48636, start2 = 48637; i < historyList.size(); i++) {
            TradePostHistory history = historyList.get(i);

            player.getPA().sendString(start1, history.getItem().getAmount() + " x " + history.getItem().getDef().getName());
            player.getPA().sendString(start2, "sold for " + history.getCost() + " " + (history.isNomad() ? "NoMad" : "Plat") + ".");

            start1 += 2;
            start2 += 2;
        }
    }

    private void buy(int index, int amount) {
        if (index == -1) {
            return;
        }
        if (amount <= 0) {
            return;
        }
        if (index >= viewOffers.size()) {
            return;
        }

        TradePostOffer offer = viewOffers.get(index);
        String sellerName = offer.getUsername();
        Player seller = PlayerHandler.getPlayerByDisplayName(sellerName);

        if (Objects.isNull(seller) || !seller.isOnline()) {
            player.sendMessage("That player is not online!");
            return;
        }

        if (seller == player) {
            player.sendMessage("You cannot buy items from yourself!");
            return;
        }

        if (amount < 1) {
            player.sendMessage("You can't buy 0 of an item idiot.");
            return;
        }

        if (amount > offer.getItem().getAmount()) {
            amount = offer.getItem().getAmount();
        }

        long price = (long) offer.getPricePerItem() * amount;

        if (price > Integer.MAX_VALUE) {
            player.sendMessage("You can't buy this many, do a lower amount");
            return;
        }

        if ((offer.isNomad() && player.foundryPoints < price) || (!offer.isNomad() && player.getItems().getInventoryCount(13204) < price)) {
            player.sendMessage("You do not have enough to purchase this.");
            return;
        }

        int finalAmount = amount;

        player.start(new DialogueBuilder(player).option("Are you sure you want to purchase: " + finalAmount + "x " +
                        offer.getItem().getDef().getName() + " for a price of: " + formatPrice(price) + "?",
                new DialogueOption("Yes", p -> {
                    p.getPA().closeAllWindows();
                    if (!seller.isOnline()) {
                        p.sendMessage("That player is not online!");
                        return;
                    }

                    if ((offer.isNomad() && p.foundryPoints < price) ||
                            (!offer.isNomad() && p.getItems().getInventoryCount(13204) < price)) {
                        p.sendMessage("You do not have enough to purchase this.");
                        return;
                    }

                    Optional<TradePostOffer> sellersOffer = seller.getTradePost().tradePostOffers.stream().filter(toChange
                            -> (toChange.getItem().getId() == offer.getItem().getId()) && (toChange.getItem().getAmount() >= finalAmount)).findAny();

                    if (sellersOffer.isEmpty()) {
                        p.sendMessage("This item has been removed or sold already!");
                        p.getPA().closeAllWindows();
                        return;
                    }

                    sellersOffer.ifPresent(tradePostOffer -> {

                        int amountLeft = tradePostOffer.getItem().getAmount() - finalAmount;

                        if (amountLeft < 0) {
                            p.sendMessage("TRADING POST [ERROR: 1] Contact a Developer!");
                            p.getPA().closeAllWindows();
                            return;
                        }

                        if (offer.isNomad()) {
                            p.foundryPoints -= price;
                            p.getItems().addItemUnderAnyCircumstance(offer.getItem().getId(), finalAmount);
                            seller.getTradePost().nomadCoffer += price;
                        } else {
                            p.getItems().deleteItem2(13204, (int) price);
                            p.getItems().addItemUnderAnyCircumstance(offer.getItem().getId(), finalAmount);
                            seller.getTradePost().coinCoffer += price;
                        }
                        boolean outOfStock = amountLeft == 0;

                        if (outOfStock) {
                            seller.getTradePost().tradePostOffers.remove(tradePostOffer);
                        } else {
                            TradePostOffer newOffer = new TradePostOffer(
                                    offer.getUsername(),
                                    new GameItem(offer.getItem().getId(), amountLeft),
                                    offer.getPricePerItem(),
                                    offer.getTimestamp(),
                                    offer.isNomad(),
                                    offer.getTotalSold() + finalAmount);
                            seller.getTradePost().tradePostOffers.set(seller.getTradePost().tradePostOffers.indexOf(tradePostOffer), newOffer);
                        }

                        if (outOfStock) {
                            seller.sendMessage("<col=00c203>" + "Trading Post: Finished selling all of " + offer.getItem().getDef().getName() + ".</col>");
                        } else {
                            seller.sendMessage("<col=00c203>" + "Trading Post: " + finalAmount + "/" + offer.getItem().getAmount() + " of " + offer.getItem().getDef().getName() + " sold.</col>");
                        }

                        final String message =
                                "Buyer:  **__" + player.getLoginName().toUpperCase() + "__** \n" +
                                        "Seller: **__" + seller.getDisplayName().toUpperCase() + "__**" + "\n" +
                                        "Bought: **__" + offer.getItem().getDef().getName() + "__**  \n" +
                                        "Amount: **__" + finalAmount + "__** \n" +
                                        "Price: **__" + offer.getPricePerItem() * finalAmount + "__** \n" +
                                        "IsNomad: **__" + offer.isNomad() + "__** \n" +
                                        "TimeStamp: " + "**__<t:" + TimeUnit.MILLISECONDS.toSeconds(offer.getTimestamp()) + ":f>__**";

                        Discord.sendEmbeddedServerLog(
                                Discord.TP_BUY_CHANNEL,
                                Color.GREEN,
                                "TP Buy Logs",
                                message,
                                player.getLoginName() + " & " + seller.getLoginName(),
                                "User1 IP: " + player.getIpAddress() + " \n" +
                                        "User2 IP: " + seller.getIpAddress()
                        );


                        addHistory(seller, new TradePostHistory(player.getDisplayName(),
                                sellerName,
                                new GameItem(offer.getItem().getId(),
                                        (offer.getTotalSold() + finalAmount)),
                                System.currentTimeMillis(),
                                offer.isNomad(),
                                offer.getPricePerItem()));

                        handleGEScript(seller);

                        p.sendMessage("<col=ff0000>" + "You have purchased " + finalAmount + "x " + offer.getItem().getDef().getName() + " for a price of " +
                                formatPrice(price) + " " + (offer.isNomad() ? "NoMad" : "Platinum") + ".</col>");
                        p.getTradePost().openViewOffers();
                        if (seller.isInterfaceOpen(MyOffersView)) {
                            seller.getTradePost().openMyOffers();
                        }
                    });
                }), new DialogueOption("No", p -> p.getPA().closeAllWindows())));

    }

    private void updateMyOffers() {
        loadHistory();
        int start = 48788, id = 0;

        for (TradePostOffer tradePostOffer : tradePostOffers) {
            player.getPA().itemOnInterface(tradePostOffer.getItem().getId(), tradePostOffer.getItem().getAmount(), 48847, id);
            id++;
            player.getPA().sendString(start, formatItemName(tradePostOffer.getItem().getId()));
            start++;
            player.getPA().sendString(Misc.formatCoins(tradePostOffer.getPricePerItem()) + (tradePostOffer.isNomad() ? " NoMad" : " Plat"), start);
            start++;
            player.getPA().sendString(start, tradePostOffer.getTotalSold() + "/" + (tradePostOffer.getItem().getAmount() + tradePostOffer.getTotalSold()));
            start += 2;
        }

        String formatCollection = (Misc.formatCoins(nomadCoffer) + " NoMad, " + Misc.formatCoins(coinCoffer) + " Plat");
        player.getPA().sendString(formatCollection, 48610);

        for (int k = id; k < 15; k++) {
            player.getPA().sendTradingPost(48847, -1, k, -1);
        }
        for (int i = start; i < 48850; i++) {
            player.getPA().sendFrame126("", i);
        }
    }

    private void updateViewOffers() {
        viewOffers = findViewOffers();
        int total = 0, start = 26023;
        List<GameItem> result = new ArrayList<>();

        for (int index = 0; index < (Math.min(viewOffers.size(), 50)); index++) {
            if (viewOffers.get(index) != null) {
                TradePostOffer offer = viewOffers.get(index);
                result.add(offer.getItem());
                player.getPA().sendString(start, formatItemName(offer.getItem().getId()));
                start++;
                player.getPA().sendString(start, Misc.formatCoins(offer.getPricePerItem()) + (offer.isNomad() ? "nomad" : " plat") + " each");
                start++;
                player.getPA().sendString(start, offer.getUsername());
                start++;
                player.getPA().sendString(start, String.valueOf(offer.getTotalSold()));
                start++;
                total++;
                if (total == 250) {
                    break;
                }
            }
        }
        PlayerAssistant.sendItems(player, 26022, result, 250);
        for (int i = start; i < 27023; i++) {
            player.getPA().sendFrame126("", i);
        }
    }

    private static String formatPrice(long price) {
        return Misc.formatCoins(price);
    }

    private List<TradePostOffer> findViewOffers() {
        return PlayerHandler.getPlayerStream()
                .flatMap(p -> p.getTradePost().tradePostOffers.stream())
                .filter(offer -> {
                    if (searchText == null) {
                        return true;
                    }

                    return offer.getItem().getDef().getName().toLowerCase().contains(searchText) || searchText.equalsIgnoreCase(offer.getUsername());
                }).sorted((offerA, offerB) -> {
                    switch (sort) {
                        case PRICE_DESCENDING:
                            return offerB.getPricePerItem() - offerA.getPricePerItem();
                        case PRICE_ASCENDING:
                            return offerA.getPricePerItem() - offerB.getPricePerItem();
                        case AGE_DESCENDING:
                            return (int) (offerA.getTimestamp() - offerB.getTimestamp());
                        case AGE_ASCENDING:
                        default:
                            return (int) (offerB.getTimestamp() - offerA.getTimestamp());
                    }
                }).limit(MAX_VIEW_OFFERS)
                .collect(Collectors.toList());
    }

    public static String formatItemName(int id) {
        String name = ItemDef.forId(id).getName();
        if (name.length() < 21)
            return name;
        return name.substring(0, 20) + ".";
    }

    public boolean handleButton(int realButtonId) {
        if (realButtonId == 48618) {
            openViewOffers();
            searchText = null;
            return true;
        }
        if (realButtonId == 48005) {
            openMyOffers();
            searchText = null;
            return true;
        }
        if (realButtonId == 48615) {
            player.getPA().sendEnterString("Who would you like to look for?", (plr, string) -> {
                for (Player player1 : PlayerHandler.getPlayers()) {
                    if (player1 == null)
                        continue;
                    if (!player1.getDisplayName().equalsIgnoreCase(string))
                        continue;
                    plr.getPA().closeAllWindows();
                    searchText = string;
                    openViewOffers();
                }
            });
            return true;
        }

        if (realButtonId == 48612) {
            player.getPA().sendEnterString("What item would you like to look for?", (plr, string) -> {
                plr.getPA().closeAllWindows();
                searchText = string;
                openViewOffers();
            });
            return true;
        }

        if (realButtonId == 48607) {
            if (nomadCoffer > 0 || coinCoffer > 0) {
                if (nomadCoffer > 0) {
                    player.foundryPoints += nomadCoffer;
                    player.sendErrorMessage(nomadCoffer + " Upgrade Points have been added to your account!");
                }
                if (coinCoffer > 0) {
                    player.getItems().addItemUnderAnyCircumstance(13204, (int) coinCoffer);
                }

                nomadCoffer = 0;
                coinCoffer = 0;
                openMyOffers();
            } else {
                player.sendErrorMessage("You don't have any outstanding currency!");
            }
            return true;
        }

        if (realButtonId == 15333) {
            player.getPA().closeAllWindows();
            return true;
        }

        return false;
    }

    public void addHistory(Player other, TradePostHistory history) {
        if (!other.getTradePost().historyList.isEmpty()) {
            other.getTradePost().historyList.removeIf(tradePostHistory -> tradePostHistory.getItem().getId() == history.getItem().getId());
        }

        if (other.getTradePost().historyList.size() >= 13) {
            other.getTradePost().historyList.remove(0);
        }

        other.getTradePost().historyList.add(history);
    }


}
