package io.xeros.content.dialogue.impl;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.instances.BossInstanceManager;
import io.xeros.content.instances.BossInstanceManager.BossTier;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

/**
 * Dialogue allowing players to enter or unlock personal boss tiers.
 */
public class BossInstanceDialogue extends DialogueBuilder {

    private static final int NPC_ID = io.xeros.model.Npcs.INSTANCE_MASTER;

    public BossInstanceDialogue(Player player) {
        super(player);
        setNpcId(NPC_ID);
        tierMenu();
    }

    /**
     * Displays the tier selection menu.
     */
    private void tierMenu() {
        BossTier[] tiers = BossTier.values();
        DialogueOption[] options = new DialogueOption[tiers.length + 1];
        int ptr = 0;
        for (BossTier tier : tiers) {
            options[ptr++] = new DialogueOption(optionText(tier), p -> selectTier(tier));
        }
        options[ptr] = DialogueOption.nevermind();
        option(options);
    }

    private String optionText(BossTier tier) {
        return getPlayer().getUnlockedBossTiers().contains(tier) ?
                "Enter " + tier.getZoneName() :
                "Unlock " + tier.getZoneName();
    }

    /**
     * Handles unlocking and entering the chosen tier.
     */
    private void selectTier(BossTier tier) {
        Player player = getPlayer();
        if (!player.getUnlockedBossTiers().contains(tier)) {
            if (player.killcount < tier.getKillRequirement()) {
                player.sendMessage("You need " + tier.getKillRequirement() + " kills to unlock this tier.");
                player.getPA().closeAllWindows();
                return;
            }
            if (tier.getItemRequirement() > 0 && !player.getItems().playerHasItem(tier.getItemRequirement())) {
                player.sendMessage("You need a " + ItemDef.forId(tier.getItemRequirement()).getName() + " to unlock this tier.");
                player.getPA().closeAllWindows();
                return;
            }
            if (tier.getGpCost() > 0 && !player.getItems().playerHasItem(995, tier.getGpCost())) {
                player.sendMessage("You need " + Misc.formatCoins(tier.getGpCost()) + " coins to unlock this tier.");
                player.getPA().closeAllWindows();
                return;
            }

            if (tier.getItemRequirement() > 0) {
                player.getItems().deleteItem(tier.getItemRequirement(), 1);
            }
            if (tier.getGpCost() > 0) {
                player.getItems().deleteItem(995, tier.getGpCost());
            }
            player.getUnlockedBossTiers().add(tier);
            player.sendMessage("You unlock " + tier.getZoneName() + "!");
        }

        BossInstanceManager.enter(player, tier);
        player.getPA().closeAllWindows();
    }
}

