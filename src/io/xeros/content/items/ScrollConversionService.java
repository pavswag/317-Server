package io.xeros.content.items;

import io.xeros.content.dialogue.impl.ClaimDonatorScrollDialogue.DonationScroll;
import io.xeros.model.entity.player.Player;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles converting donation scrolls between denominations.
 */
public class ScrollConversionService {

    private static final int FIVE_SCROLL = DonationScroll.FIVE.getItemId();

    public static void convertUp(Player player, int required, DonationScroll result) {
        if (player.getItems().getInventoryCount(FIVE_SCROLL) < required) {
            player.sendMessage("You need " + required + " $5 scrolls to do that.");
            player.getPA().closeAllWindows();
            return;
        }
        player.getItems().deleteItem2(FIVE_SCROLL, required);
        player.getItems().addItem(result.getItemId(), 1);
        player.sendMessage("Converted " + required + "x $5 scrolls into a $" + result.getDonationAmount() + " scroll.");
        ScrollConversionLogger.log(player, "Converted " + required + "x $5 into $" + result.getDonationAmount());
        player.getPA().closeAllWindows();
    }

    public static void convertDown(Player player, DonationScroll from, int fiveScrolls) {
        if (!player.getItems().playerHasItem(from.getItemId())) {
            player.sendMessage("You need a $" + from.getDonationAmount() + " scroll to do that.");
            player.getPA().closeAllWindows();
            return;
        }
        int requiredSlots = fiveScrolls - 1; // removing one scroll, adding many
        if (player.getItems().freeSlots() < requiredSlots) {
            player.sendMessage("You need " + requiredSlots + " free inventory slots to do that.");
            player.getPA().closeAllWindows();
            return;
        }
        player.getItems().deleteItem2(from.getItemId(), 1);
        player.getItems().addItem(FIVE_SCROLL, fiveScrolls);
        player.sendMessage("Converted a $" + from.getDonationAmount() + " scroll into " + fiveScrolls + "x $5 scrolls.");
        ScrollConversionLogger.log(player, "Converted $" + from.getDonationAmount() + " into " + fiveScrolls + "x $5");
        player.getPA().closeAllWindows();
    }

    public static void batchConvert(Player player) {
        int total = player.getItems().getInventoryCount(FIVE_SCROLL);
        if (total <= 0) {
            player.sendMessage("You don't have any $5 scrolls to convert.");
            player.getPA().closeAllWindows();
            return;
        }
        int remaining = total;
        Map<DonationScroll, Integer> results = new LinkedHashMap<>();

        int num100 = remaining / 20;
        if (num100 > 0) {
            results.put(DonationScroll.ONE_HUNDRED, num100);
            remaining -= num100 * 20;
            player.getItems().addItem(DonationScroll.ONE_HUNDRED.getItemId(), num100);
        }
        int num50 = remaining / 10;
        if (num50 > 0) {
            results.put(DonationScroll.FIFTY, num50);
            remaining -= num50 * 10;
            player.getItems().addItem(DonationScroll.FIFTY.getItemId(), num50);
        }
        int num25 = remaining / 5;
        if (num25 > 0) {
            results.put(DonationScroll.TWENTY_FIVE, num25);
            remaining -= num25 * 5;
            player.getItems().addItem(DonationScroll.TWENTY_FIVE.getItemId(), num25);
        }
        int num10 = remaining / 2;
        if (num10 > 0) {
            results.put(DonationScroll.TEN, num10);
            remaining -= num10 * 2;
            player.getItems().addItem(DonationScroll.TEN.getItemId(), num10);
        }
        int used = total - remaining;
        if (used > 0) {
            player.getItems().deleteItem2(FIVE_SCROLL, used);
        }
        player.sendMessage("Converted " + used + " x $5 scrolls into:");
        for (Map.Entry<DonationScroll, Integer> entry : results.entrySet()) {
            player.sendMessage("- " + entry.getValue() + " x $" + entry.getKey().getDonationAmount());
        }
        if (remaining > 0) {
            player.sendMessage("- " + remaining + " x $5");
        }
        ScrollConversionLogger.log(player, "Batch converted " + used + " of " + total + " $5 scrolls");
        player.getPA().closeAllWindows();
    }
}
