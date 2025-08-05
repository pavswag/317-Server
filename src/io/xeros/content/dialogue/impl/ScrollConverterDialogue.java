package io.xeros.content.dialogue.impl;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.items.ScrollConversionService;
import io.xeros.content.dialogue.impl.ClaimDonatorScrollDialogue.DonationScroll;
import io.xeros.model.Npcs;
import io.xeros.model.entity.player.Player;

/**
 * Dialogue for exchanging donation scrolls between denominations.
 */
public class ScrollConverterDialogue extends DialogueBuilder {

    public ScrollConverterDialogue(Player player) {
        super(player);
        setNpcId(Npcs.SCROLL_CONVERTER);
        npc("Hello! Want to consolidate your scrolls?");
        option(
                new DialogueOption("Convert $5 scrolls", ScrollConverterDialogue::openConvertMenu),
                new DialogueOption("Reverse convert scrolls", ScrollConverterDialogue::openReverseMenu),
                new DialogueOption("Nevermind", p -> p.getPA().closeAllWindows())
        );
    }

    private static void openConvertMenu(Player player) {
        player.start(new DialogueBuilder(player).setNpcId(Npcs.SCROLL_CONVERTER).option(
                new DialogueOption("Convert $5 into $10", p -> ScrollConversionService.convertUp(p, 2, DonationScroll.TEN)),
                new DialogueOption("Convert $5 into $25", p -> ScrollConversionService.convertUp(p, 5, DonationScroll.TWENTY_FIVE)),
                new DialogueOption("Convert $5 into $50", p -> ScrollConversionService.convertUp(p, 10, DonationScroll.FIFTY)),
                new DialogueOption("Convert $5 into $100", p -> ScrollConversionService.convertUp(p, 20, DonationScroll.ONE_HUNDRED)),
                new DialogueOption("Batch Convert All Scrolls", ScrollConversionService::batchConvert)
        ));
    }

    private static void openReverseMenu(Player player) {
        player.start(new DialogueBuilder(player).setNpcId(Npcs.SCROLL_CONVERTER).option(
                new DialogueOption("Convert $10 into $5s", p -> ScrollConversionService.convertDown(p, DonationScroll.TEN, 2)),
                new DialogueOption("Convert $25 into $5s", p -> ScrollConversionService.convertDown(p, DonationScroll.TWENTY_FIVE, 5)),
                new DialogueOption("Convert $50 into $5s", p -> ScrollConversionService.convertDown(p, DonationScroll.FIFTY, 10)),
                new DialogueOption("Convert $100 into $5s", p -> ScrollConversionService.convertDown(p, DonationScroll.ONE_HUNDRED, 20)),
                new DialogueOption("Nevermind", p -> p.getPA().closeAllWindows())
        ));
    }
}
