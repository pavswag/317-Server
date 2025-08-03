package io.xeros.content.dialogue.impl;

import io.xeros.Server;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.instances.BossInstanceManager;
import io.xeros.content.instances.BossInstanceManager.BossTier;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dialogue allowing players to select and enter boss instance tiers.
 *
 * <p>This implementation avoids blank options, colour codes locked/unlocked tiers
 * and supports pagination when more than five tiers exist.</p>
 */
public class BossInstanceDialogue extends DialogueBuilder {

    /** NPC used for the dialogue head. */
    private static final int NPC_ID = io.xeros.model.Npcs.INSTANCE_MASTER;

    /** Maximum number of options shown per page. */
    private static final int OPTIONS_PER_PAGE = 5;

    /** Current page being viewed. */
    private final int page;

    /** Tiers displayed on the current page mapped by slot. */
    private final List<BossTier> displayed = new ArrayList<>(OPTIONS_PER_PAGE);

    public BossInstanceDialogue(Player player) {
        this(player, 0);
    }

    private BossInstanceDialogue(Player player, int page) {
        super(player);
        this.page = page;
        setNpcId(NPC_ID);
        start();
    }

    /**
     * Builds and sends the tier options for the current page. Any unused slots
     * are filled with a red "Unavailable" placeholder to prevent blank lines.
     */
    public void start() {
        Player player = getPlayer();
        BossTier[] tiers = BossTier.values();

        int startIndex = page * OPTIONS_PER_PAGE;

        displayed.clear();

        Misc.println("BossInstanceDialogue open page=" + page + " unlocked=" + player.getUnlockedBossTiers());

        List<DialogueOption> opts = new ArrayList<>();

        boolean addBack = page > 0;
        boolean addMore = startIndex + OPTIONS_PER_PAGE < tiers.length;

        int capacity = OPTIONS_PER_PAGE - (addBack ? 1 : 0) - (addMore ? 1 : 0);

        for (int i = 0; i < capacity && startIndex + i < tiers.length; i++) {
            BossTier tier = tiers[startIndex + i];
            displayed.add(tier);
            String label = buildTierLabel(player, tier);
            Misc.println("BossInstanceDialogue option " + (startIndex + i) + ": " + label);
            final int slot = opts.size();
            opts.add(new DialogueOption(label, p -> run(0, slot)));
        }

        if (addBack) {
            displayed.add(null);
            opts.add(new DialogueOption("Back", p -> p.start(new BossInstanceDialogue(p, page - 1))));
        }
        if (addMore) {
            displayed.add(null);
            opts.add(new DialogueOption("More", p -> p.start(new BossInstanceDialogue(p, page + 1))));
        }

        String[] labels = opts.stream().map(DialogueOption::getTitle).toArray(String[]::new);
        player.getDH().sendOptionDialogue("Choose a Boss Tier", labels);
        option("Choose a Boss Tier", opts.toArray(new DialogueOption[0]));
    }

    /**
     * Builds a safe, colour-coded label for a tier showing progress and key drops.
     */
    private String buildTierLabel(Player player, BossTier tier) {
        if (tier == null) {
            Misc.println("BossInstanceDialogue warning: null tier label");
            return "@red@Unavailable";
        }

        StringBuilder label = new StringBuilder(BossInstanceManager.getTierDisplayNameSafe(tier, player));

        // Append up to three key drop names
        List<GameItem> drops = Server.getDropManager().getNPCdrops(tier.getKillNpcId());
        if (drops != null && !drops.isEmpty()) {
            String dropNames = drops.stream()
                    .limit(3)
                    .map(drop -> {
                        ItemDef def = ItemDef.forId(drop.getId());
                        return def != null ? def.getName() : "?";
                    })
                    .collect(Collectors.joining(", "));
            if (!dropNames.isEmpty()) {
                label.append(" - ").append(dropNames);
            }
        }

        return label.toString();
    }

    /**
     * Handles option selection. Slots map to the tiers displayed on the current page.
     */
    public void run(int interfaceId, int slot) {
        Player player = getPlayer();
        if (slot < 0 || slot >= displayed.size()) {
            return;
        }

        BossTier tier = displayed.get(slot);
        Misc.println("BossInstanceDialogue click slot=" + slot + " tier=" + (tier == null ? "null" : tier.name()));
        if (tier == null) {
            return; // placeholder option
        }

        if (player.getUnlockedBossTiers().contains(tier)) {
            BossInstanceManager.enter(player, tier);
            player.getPA().closeAllWindows();
        } else {
            BossTier prev = Arrays.stream(BossTier.values()).filter(t -> t.getNextTier() == tier).findFirst().orElse(null);
            int progress = prev != null ? player.getTierKillCounts().getOrDefault(prev, 0) : 0;
            int required = prev != null ? prev.getRequiredKillCountToUnlockNext() : tier.getKillRequirement();
            int remaining = Math.max(0, required - progress);
            player.sendMessage("You must kill " + remaining + " more to unlock this tier.");
        }
    }
}

