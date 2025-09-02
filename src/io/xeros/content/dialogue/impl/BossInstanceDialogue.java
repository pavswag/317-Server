package io.xeros.content.dialogue.impl;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.instances.aoe.AoeBossTierDef;
import io.xeros.content.instances.aoe.AoeBossTierLoader;
import io.xeros.content.instances.aoe.AoeTierController;
import io.xeros.model.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialogue for selecting AOE boss tiers. Builds a fresh instance each time to
 * avoid stale state and guarantees visible option lines.
 */
public class BossInstanceDialogue extends DialogueBuilder {

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
        List<AoeBossTierDef> tiers = AoeBossTierLoader.getTiers();
        Player player = getPlayer();
        if (tiers.isEmpty()) {
            player.start(new DialogueBuilder(player).statement("No tiers configured"));
            return;
        }
        int totalPages = (int) Math.ceil(tiers.size() / (double) OPTIONS_PER_PAGE);
        int startIndex = page * OPTIONS_PER_PAGE;
        List<DialogueOption> options = new ArrayList<>();
        for (int i = 0; i < OPTIONS_PER_PAGE && startIndex + i < tiers.size(); i++) {
            AoeBossTierDef def = tiers.get(startIndex + i);
            final int tierNumber = def.tier;
            String label = optionLabel(player, def);
            options.add(new DialogueOption(label, p -> handleSelect(p, tierNumber)));
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

    private void handleSelect(Player player, int tier) {
        if (tier <= AoeTierController.getUnlockedTier(player)) {
            AoeTierController.startTier(player, tier);
            player.getPA().closeAllWindows();
        } else {
            int kc = AoeTierController.getKillCount(player, tier - 1);
            AoeBossTierDef prev = AoeBossTierLoader.getTier(tier - 1);
            int req = prev != null ? prev.getUnlockKills() : 0;
            int remaining = Math.max(0, req - kc);
            player.sendMessage("You must kill " + remaining + " more to unlock this tier.");
        }
    }

    private String optionLabel(Player player, AoeBossTierDef def) {
        String zone = safeDisplayName(def);
        boolean unlocked = def.tier <= AoeTierController.getUnlockedTier(player);
        if (unlocked) {
            return "T" + def.tier + " - " + zone + " [Unlocked]";
        }
        return "T" + def.tier + " - " + zone + " [Locked " + def.unlockKills + "]";
    }

    public static String safeDisplayName(AoeBossTierDef def) {
        if (def == null) {
            return "Unknown";
        }
        String name = def.zoneName;
        if (name == null || name.isBlank()) {
            return "Tier " + def.tier;
        }
        return name.replaceAll("[^\\p{ASCII}]", "");
    }
}
