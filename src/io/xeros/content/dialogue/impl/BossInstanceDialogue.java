package io.xeros.content.dialogue.impl;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.instances.aoe.AoeBossTierDef;
import io.xeros.content.instances.aoe.AoeBossTierLoader;
import io.xeros.content.instances.aoe.AoeTierController;
import io.xeros.content.instances.aoe.AoeTierRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.xeros.model.entity.player.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialogue for selecting AOE boss tiers. Reads from {@link AoeTierRepo}
 * each time it opens and paginates over the list.
 */
public class BossInstanceDialogue extends DialogueBuilder {

    private static final Logger logger = LoggerFactory.getLogger(BossInstanceDialogue.class);

    private static final int OPTIONS_PER_PAGE = 5;
    private final int page;

    public BossInstanceDialogue(Player player) {
        this(player, 0);
    }

    private BossInstanceDialogue(Player player, int page) {
        super(player);
        this.page = page;
        build();
    }

    private void build() {
        List<AoeBossTierDef> tiers = AoeTierRepo.get();
        Player player = getPlayer();
        if (tiers.isEmpty()) {
            String path = AoeBossTierLoader.defaultFile().toFile().getAbsolutePath();
            logger.info("[AOE-DLG] Open with empty tier list. Checked: {} exists={}", path, new File(path).exists());
            player.start(new DialogueBuilder(player).statement(
                    "No tiers configured (file: " + path + "). Use ::aoe tier reload."));
            return;
        }
        int totalPages = (int) Math.ceil(tiers.size() / (double) OPTIONS_PER_PAGE);
        logger.info("[AOE-DLG] Open: total={}, page={}/{}", tiers.size(), page + 1, totalPages);
        int startIndex = page * OPTIONS_PER_PAGE;
        List<DialogueOption> options = new ArrayList<>();
        for (int i = 0; i < OPTIONS_PER_PAGE && startIndex + i < tiers.size(); i++) {
            AoeBossTierDef def = tiers.get(startIndex + i);
            final int tierNumber = def.tier;
            String label = optionLabel(player, def);
            options.add(new DialogueOption(label, p -> handleSelect(p, tierNumber, def)));
        }
        if (page > 0) {
            options.add(new DialogueOption("Back", p -> p.start(new BossInstanceDialogue(p, page - 1))));
        }
        if (startIndex + OPTIONS_PER_PAGE < tiers.size()) {
            options.add(new DialogueOption("More", p -> p.start(new BossInstanceDialogue(p, page + 1))));
        }
        option("AOE Boss Tiers (Page " + (page + 1) + "/" + totalPages + ")",
                options.toArray(new DialogueOption[0]));
    }

    private void handleSelect(Player player, int tier, AoeBossTierDef def) {
        String state = def.disabled ? "Disabled" : (tier <= AoeTierController.getUnlockedTier(player) ? "Unlocked" : "Locked");
        logger.info("[AOE-DLG] Select tier={} state={}", tier, state);
        if (def.disabled) {
            player.sendMessage(def.getDisabledReason());
            return;
        }

        if (tier <= AoeTierController.getUnlockedTier(player)) {
            AoeTierController.startTier(player, tier);
            player.getPA().closeAllWindows();
        } else {
            int prevTier = tier - 1;
            int kc = AoeTierController.getKillCount(player, prevTier);
            int req = def.unlockKills;
            int remaining = Math.max(0, req - kc);
            player.sendMessage("You must kill " + remaining + " more to unlock this tier.");
        }
    }

    private String optionLabel(Player player, AoeBossTierDef def) {
        String zone = safe(def.zoneName);
        if (def.disabled) {
            return "T" + def.tier + " - " + zone + " [Disabled: " + def.getDisabledReason() + "]";
        }
        boolean unlocked = def.tier <= AoeTierController.getUnlockedTier(player);
        if (unlocked) {
            return "T" + def.tier + " - " + zone + " [Unlocked]";
        }
        return "T" + def.tier + " - " + zone + " [Locked " + def.unlockKills + "]";
    }

    private static String safe(String s) {
        if (s == null || s.isBlank()) {
            return "Unknown";
        }
        return s.replaceAll("[^\\p{ASCII}]", "");
    }
}

