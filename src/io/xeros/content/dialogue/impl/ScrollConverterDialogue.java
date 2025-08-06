package io.xeros.content.dialogue.impl;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.items.ScrollConversionService;
import io.xeros.content.dialogue.impl.ClaimDonatorScrollDialogue.DonationScroll;
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
                new DialogueOption("Convert $5 scrolls", ScrollConverterDialogue::openConvertMenu),
                new DialogueOption("Reverse convert scrolls", ScrollConverterDialogue::openReverseMenu),
                new DialogueOption("Nevermind", p -> p.getPA().closeAllWindows())
        );
    }
    }
}
