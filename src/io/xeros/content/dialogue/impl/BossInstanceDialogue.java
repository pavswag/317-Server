package io.xeros.content.dialogue.impl;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.instances.BossInstanceManager;
import io.xeros.content.instances.BossInstanceManager.BossTier;
import java.util.Arrays;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

/**
 * Dialogue allowing players to enter or unlock personal boss tiers.
 */
public class BossInstanceDialogue extends DialogueBuilder {

    private static final int NPC_ID = io.xeros.model.Npcs.INSTANCE_MASTER;

    public BossInstanceDialogue(Player player) {
        this(player, 0);
    }

    private BossInstanceDialogue(Player player, int page) {
        super(player);
        setNpcId(NPC_ID);
        tierMenu(page);
    }

    /**
     * Displays the tier selection menu.
     */
    private static final int TIERS_PER_PAGE = 2;

    private void tierMenu(int page) {
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
            int prev = page - 1;
            options[ptr++] = new DialogueOption("Back", p -> p.start(new BossInstanceDialogue(p, prev)));
        }
        if (end < tiers.length) {
            int next = page + 1;
            options[ptr++] = new DialogueOption("More", p -> p.start(new BossInstanceDialogue(p, next)));
        }
        options[ptr++] = DialogueOption.nevermind();
        option(java.util.Arrays.copyOf(options, ptr));
    }

    private String optionText(BossTier tier) {
        Player player = getPlayer();
        boolean unlocked = player.getUnlockedBossTiers().contains(tier);
        String prefix = unlocked ? "\u2705 " : "\uD83D\uDD12 ";

        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append("Tier ").append(tier.ordinal() + 1);

        if (unlocked) {
            sb.append(" - Unlocked");
        } else {
            // find the tier that unlocks this one
            BossTier prev = Arrays.stream(BossTier.values())
                    .filter(t -> t.getNextTier() == tier)
                    .findFirst()
                    .orElse(null);
            if (prev != null) {
                int current = player.getTierKillCounts().getOrDefault(prev, 0);
                int required = prev.getRequiredKillCountToUnlockNext();
                sb.append(" - ").append(current).append("/").append(required).append(" kills");
                if (current >= required) {
                    sb.append(" (Ready!)");
                } else {
                    sb.append(" (Progressing...)");
                }
            }
        }
        return sb.toString();
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

