package io.xeros.content.dialogue.impl;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.Npcs;
import io.xeros.model.entity.player.Player;

/**
 * Dialogue for exchanging multiple $5 donation scrolls into higher value scrolls.
 */
public class ScrollConverterDialogue extends DialogueBuilder {

    private static final int FIVE_SCROLL = ClaimDonatorScrollDialogue.DonationScroll.FIVE.getItemId();

    public ScrollConverterDialogue(Player player) {
        super(player);
        setNpcId(Npcs.SCROLL_CONVERTER);
        npc("Hello! Want to consolidate your scrolls?");
        option(
                new DialogueOption("Convert $5 into $10", p -> convert(p, 2, ClaimDonatorScrollDialogue.DonationScroll.TEN)),
                new DialogueOption("Convert $5 into $25", p -> convert(p, 5, ClaimDonatorScrollDialogue.DonationScroll.TWENTY_FIVE)),
                new DialogueOption("Convert $5 into $50", p -> convert(p, 10, ClaimDonatorScrollDialogue.DonationScroll.FIFTY)),
                new DialogueOption("Convert $5 into $100", p -> convert(p, 20, ClaimDonatorScrollDialogue.DonationScroll.ONE_HUNDRED)),
                new DialogueOption("Nevermind", p -> p.getPA().closeAllWindows())
        );
    }

    private void convert(Player player, int required, ClaimDonatorScrollDialogue.DonationScroll result) {
        if (player.getItems().getInventoryCount(FIVE_SCROLL) < required) {
            player.sendMessage("You need " + required + " $5 scrolls to do that.");
            player.getPA().closeAllWindows();
            return;
        }
        player.getItems().deleteItem2(FIVE_SCROLL, required);
        player.getItems().addItem(result.getItemId(), 1);
        player.sendMessage("Converted " + required + "x $5 scrolls into a $" + result.getDonationAmount() + " scroll.");
        player.getPA().closeAllWindows();
    }
}
