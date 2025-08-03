package io.xeros.content.dialogue.impl;

import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.impl.FremennikDiaryEntry;
import io.xeros.content.achievement_diary.impl.MorytaniaDiaryEntry;
import io.xeros.content.battlepass.Pass;
import io.xeros.content.bosses.Hunllef;
import io.xeros.content.bosses.Kraken;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.bosses.wildypursuit.TheUnbearable;
import io.xeros.content.bosspoints.BossPoints;
import io.xeros.content.instances.BossInstanceManager;
import io.xeros.content.instances.BossInstanceOverlayManager;
import io.xeros.content.instances.TierRewardManager;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.event.eventcalendar.EventChallenge;
import io.xeros.content.events.monsterhunt.MonsterHunt;
import io.xeros.content.minigames.warriors_guild.AnimatedArmour;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.hunter.impling.ItemRarity;
import io.xeros.model.Graphic;
import io.xeros.model.Npcs;
import io.xeros.model.collisionmap.RegionProvider;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
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

        if (npc.getInstance() instanceof BossInstanceManager.BossInstanceArea area) {
            if (player.isPreviewingBossInstance()) {
                BossInstanceOverlayManager.sendKillOverlay(player);
                return;
            }
        }

        if (npc.getNpcId() == 5862) {
            Achievements.increase(player, AchievementType.SLAY_CERB, 1);
        } else if (npc.getNpcId() == 319) {
            Achievements.increase(player, AchievementType.SLAY_CORP, 1);
        } else if (npc.getNpcId() == 239) {
            Achievements.increase(player, AchievementType.SLAY_KBD, 1);
        } else if (npc.getNpcId() >= 5886 && npc.getNpcId() <= 5891) {
            Achievements.increase(player, AchievementType.SLAY_SIRE, 1);
        } else if (npc.getNpcId() == 494) {
            Achievements.increase(player, AchievementType.SLAY_KRAKEN, 1);
        } else if (npc.getNpcId() == 6503) {
            Achievements.increase(player, AchievementType.SLAY_CALLISTO, 1);
        } else if (npc.getNpcId() == 6615) {
            Achievements.increase(player, AchievementType.SLAY_SCORPIA, 1);
        } else if (npc.getNpcId() == 6610) {
            Achievements.increase(player, AchievementType.SLAY_VENENATIS, 1);
        } else if (npc.getNpcId() == 2054) {
            Achievements.increase(player, AchievementType.SLAY_CHAOSELE, 1);
        } else if (npc.getNpcId() == 6619) {
            Achievements.increase(player, AchievementType.SLAY_CHAOSFANATIC, 1);
        } else if (npc.getNpcId() == 6618) {
            Achievements.increase(player, AchievementType.SLAY_ARCHAEOLOGIST, 1);
        } else if (npc.getNpcId() == 8164) {
            Achievements.increase(player, AchievementType.SLAY_SHADOWARAPHAEL, 1);
        } else if (npc.getNpcId() == 8172) {
            Achievements.increase(player, AchievementType.SLAY_ARAPHAEL, 1);
        }

        Location3D location = new Location3D(dropX, dropY, dropHeight);
        int amountOfDrops = 1;
        if (isDoubleDrops()) {
            amountOfDrops++;
        }

        int bossPoints = BossPoints.getPointsOnDeath(npc);
        BossPoints.addPoints(player, bossPoints, false);

        if (npc.getInstance() instanceof BossInstanceManager.BossInstanceArea area) {
            if (player.isPreviewingBossInstance()) {
                BossInstanceOverlayManager.sendKillOverlay(player);
                return;
            }
            BossInstanceManager.BossTier tier = area.getTier();
            int newCount = player.getTierKillCounts().merge(tier, 1, Integer::sum);
            if (newCount == tier.getRequiredKillCountToUnlockNext()) {
                TierRewardManager.reward(player, tier);
                BossInstanceManager.BossTier next = tier.getNextTier();
                if (next != null && BossInstanceManager.isFirstTierUnlock(player, next)) {
                    player.gfx0(199);
                    player.startAnimation(862);
                    int number = next.ordinal() + 1;
                    String name = next.getZoneName();
                    player.sendMessage("<col=ff00ff><shad=000000>\uD83C\uDF89 You’ve unlocked Tier " + number + ": " + name + "!</col>");
                    PlayerHandler.executeGlobalMessage(player.getDisplayName() + " has unlocked Tier " + number + " – " + name + "! \uD83D\uDD25");
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
            BossInstanceOverlayManager.sendKillOverlay(player);
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
