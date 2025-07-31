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
    private static final int TIERS_PER_PAGE = 5;
    private int page;

    private void tierMenu() {
        tierMenu(0);
    }

    private void tierMenu(int page) {
        this.page = page;
        BossTier[] tiers = BossTier.values();
        int start = page * TIERS_PER_PAGE;
        int end = Math.min(start + TIERS_PER_PAGE, tiers.length);

        DialogueOption[] options = new DialogueOption[TIERS_PER_PAGE + 2];
        int ptr = 0;
        for (int i = start; i < end; i++) {
            BossTier tier = tiers[i];
            options[ptr++] = new DialogueOption(optionText(tier), p -> selectTier(tier));
        }
        if (page > 0) {
            options[ptr++] = new DialogueOption("Back", p -> tierMenu(page - 1));
        }
        if (end < tiers.length) {
            options[ptr++] = new DialogueOption("More", p -> tierMenu(page + 1));
        }
        options[ptr++] = DialogueOption.nevermind();
        option(java.util.Arrays.copyOf(options, ptr));
    }

    private String optionText(BossTier tier) {
        String action = getPlayer().getUnlockedBossTiers().contains(tier) ? "Enter " : "Unlock ";
        return action + "Tier " + (tier.ordinal() + 1) + " - " + tier.getZoneName();
    }

    /**
     * Handles unlocking and entering the chosen tier.
     */
    private void selectTier(BossTier tier) {
        Player player = getPlayer();
        if (!player.getUnlockedBossTiers().contains(tier)) {
            if (tier.getKillCount(player) < tier.getKillRequirement()) {
                String name = io.xeros.model.definitions.NpcDef.forId(tier.getKillNpcId()).getName();
                player.sendMessage("You need " + tier.getKillRequirement() + " " + name + " kills to unlock this tier.");
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

