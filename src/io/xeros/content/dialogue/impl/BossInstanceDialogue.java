package io.xeros.content.dialogue.impl;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.instances.aoe.AoeBossTierDef;
import io.xeros.content.instances.aoe.AoeBossTierLoader;
import io.xeros.content.instances.aoe.AoeTierController;
import io.xeros.content.instances.aoe.AoeTierRepo;
import io.xeros.model.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            logger.info("[AOE-DLG] open total=0 page=0/0 file={} exists={}", path, new File(path).exists());
            player.start(new DialogueBuilder(player).statement(
                    "No tiers configured (file: " + path + "). Use ::aoe tier reload."));
            return;
        }
        int totalPages = computeTotalPages(tiers);
        int startIndex = computeStartIndex(page, tiers);
        boolean hasBack = page > 0;
        int slots = OPTIONS_PER_PAGE - (hasBack ? 1 : 0);
        boolean hasMore = startIndex + slots < tiers.size();
        if (hasMore) {
            slots--; // reserve slot for "More"
        }
        logger.info("[AOE-DLG] open total={} page={}/{}", tiers.size(), page + 1, totalPages);
        List<DialogueOption> options = new ArrayList<>();
        for (int i = 0; i < slots && startIndex + i < tiers.size(); i++) {
            AoeBossTierDef def = tiers.get(startIndex + i);
            final int idx = startIndex + i;
            options.add(new DialogueOption(optionLabel(player, def), p -> handleSelect(p, idx, def)));
        }
        if (hasBack) {
            options.add(new DialogueOption("Back", p -> p.start(new BossInstanceDialogue(p, page - 1))));
        }
        if (hasMore) {
            options.add(new DialogueOption("More", p -> p.start(new BossInstanceDialogue(p, page + 1))));
        }
        option("AOE Boss Tiers (Page " + (page + 1) + "/" + totalPages + ")",
                options.toArray(new DialogueOption[0]));
    }

    private void handleSelect(Player player, int index, AoeBossTierDef t) {
        String state = t.isDisabled() ? "Disabled" : (AoeTierController.isUnlocked(player, t.getTier()) ? "Unlocked" : "Locked");
        logger.info("[AOE-DLG] select idx={} -> tier={} state={}", index, t.getTier(), state);
        if (t.isDisabled()) {
            player.sendMessage(safe(t.getDisabledReason()));
            return;
        }
        if (!AoeTierController.isUnlocked(player, t.getTier())) {
            int need = Math.max(0, t.getUnlockKills() - AoeTierController.getKillcount(player, t.getTier()));
            player.sendMessage("You must kill " + need + " more to unlock this tier.");
            return;
        }
        AoeTierController.startTier(player, t.getTier());
        player.getPA().closeAllWindows();
    }

    private static int computeStartIndex(int page, List<AoeBossTierDef> tiers) {
        int index = 0;
        for (int p = 0; p < page && index < tiers.size(); p++) {
            int slots = OPTIONS_PER_PAGE;
            if (p > 0) {
                slots--; // Back
            }
            if (index + slots < tiers.size()) {
                slots--; // More
            }
            index += slots;
        }
        return index;
    }

    private static int computeTotalPages(List<AoeBossTierDef> tiers) {
        int pages = 0;
        int index = 0;
        while (index < tiers.size()) {
            int slots = OPTIONS_PER_PAGE;
            if (pages > 0) {
                slots--; // Back
            }
            if (index + slots < tiers.size()) {
                slots--; // More
            }
            index += slots;
            pages++;
        }
        return Math.max(pages, 1);
    }

    private static String optionLabel(Player p, AoeBossTierDef t) {
        final String zone = safe(t.getZoneName());
        final int tierNum = t.getTier();
        if (t.isDisabled()) {
            return "\u2716 T" + tierNum + " - " + zone + " [Disabled: " + safe(t.getDisabledReason()) + "]";
        }
        if (AoeTierController.isUnlocked(p, tierNum)) {
            return "T" + tierNum + " - " + zone + " [Unlocked]";
        } else {
            int need = Math.max(0, t.getUnlockKills() - AoeTierController.getKillcount(p, tierNum));
            return "T" + tierNum + " - " + zone + " [Locked " + need + "]";
        }
        return name.replaceAll("[^\\p{ASCII}]", "");
    }

    private static String safe(String s) {
        return (s == null || s.trim().isEmpty()) ? "Unknown" : s.replace('–', '-');
    }

    private static String safe(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Unknown";
        }
        return name.replaceAll("[^\\p{ASCII}]", "").replace('–', '-');
    }

    private static String safe(String name) {
        return (name == null || name.trim().isEmpty())
                ? "Unknown"
                : name.replace('–', '-');
    }

    private static String safe(String s) {
        return (s == null || s.trim().isEmpty())
                ? "Unknown"
                : s.replace('–', '-');
    }

    private static String safe(String s) {
        return (s == null || s.trim().isEmpty())
                ? "Unknown"
                : s.replace('–', '-');
    }
}

