package io.xeros.content.skills.slayer;

import io.xeros.annotate.PostInit;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.skills.Skill;
import io.xeros.model.Npcs;
import io.xeros.model.entity.npc.NPCAction;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dialogue interface for the Demon Hunter Slayer Master NPC.
 */
public class DemonHunterSlayerDialogue {

    private static final int NPC = Npcs.DEMON_HUNTER_MASTER;

    /**
     * Opens the main Demon Hunter menu.
     */
    public static void showMainMenu(Player player) {
        showMainMenu(player, true);
    }

    private static void showMainMenu(Player player, boolean greet) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        if (greet) {
            builder.npc("Greetings, slayer. Need help with demon hunts?");
        }

        List<DialogueOption> options = new ArrayList<>();
        options.add(new DialogueOption("What's Demon Hunter Slayer?", DemonHunterSlayerDialogue::explainSystem));
        options.add(new DialogueOption("What are the Demon tiers?", DemonHunterSlayerDialogue::explainTiers));
        options.add(new DialogueOption("Rewards and XP?", DemonHunterSlayerDialogue::explainRewards));
        options.add(new DialogueOption("Task Menu", DemonHunterSlayerDialogue::showTaskMenu));
        if (player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            options.add(new DialogueOption("Admin Options", DemonHunterSlayerDialogue::showAdminMenu));
        }
        builder.option(options.toArray(new DialogueOption[0]));
        player.start(builder);
    }

    /**
     * Opens the task management submenu.
     */
    public static void showTaskMenu(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.option(
                new DialogueOption("View Current Task", DemonHunterSlayerDialogue::viewTask),
                new DialogueOption("Abandon Task", DemonHunterSlayerDialogue::abandonTask),
                new DialogueOption("View Stats / Contract", DemonHunterSlayerDialogue::viewStats),
                new DialogueOption("Open Reward Shop", DemonHunterSlayerDialogue::openShop),
                new DialogueOption("Back", p -> showMainMenu(p, false))
        );
        player.start(builder);
    }

    /**
     * Opens the administrator submenu (page 1).
     */
    public static void showAdminMenu(Player player) {
        if (!player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            player.sendMessage("Only admins can access this.");
            return;
        }
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.option(
                new DialogueOption("Assign Task", p -> new DemonSlayerMaster().assign(p)),
                new DialogueOption("Set Contract", p -> {
                    DemonSlayerMaster.BossTier[] pool = DemonSlayerMaster.BossTier.values();
                    DemonSlayerMaster.BossTier boss = pool[Misc.random(pool.length - 1)];
                    p.setDemonContract(new DemonSlayerContract(boss, 1));
                    p.sendMessage("Contract set: defeat " + boss.getNpcName());
                }),
                new DialogueOption("Add 10 Demon Marks", p -> {
                    p.addDemonMarks(10);
                    p.sendMessage("You were given 10 Demon Marks.");
                }),
                new DialogueOption("Force contract reset", p -> {
                    p.setDemonContract(null);
                    p.sendMessage("Contract cleared.");
                }),
                new DialogueOption("More Options", DemonHunterSlayerDialogue::showAdminMenuMore)
        );
        player.start(builder);
    }

    /**
     * Additional administrator options (page 2).
     */
    private static void showAdminMenuMore(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.option(
                new DialogueOption("View boss XP logs", p -> {
                    for (DemonSlayerMaster.BossTier bt : DemonSlayerMaster.BossTier.values()) {
                        int xp = DemonHunterXPTable.getXPFor(bt.getBaseXp(), bt.getTier());
                        p.sendMessage(bt.getNpcName() + ": base " + bt.getBaseXp() + " -> " + xp);
                    }
                }),
                new DialogueOption("Open Reward Shop", DemonMarkRewardHandler::openShop),
                new DialogueOption("Back", p -> showMainMenu(p, false)),
                new DialogueOption("Previous", DemonHunterSlayerDialogue::showAdminMenu)
        );
        player.start(builder);
    }

    private static void explainSystem(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.npc("Demon Hunter is an elite mode", "focused on demon bosses.");
        builder.npc("Assignments scale with your level", "and grant Demon Marks.");
        builder.npc("Milestones unlock perks and tasks", "while contracts add bonuses.");
        builder.option(
                new DialogueOption("Back to menu", p -> showMainMenu(p, false)),
                DialogueBuilder.EXIT_DIALOGUE_OPTION
        );
        player.start(builder);
    }

    private static void viewTask(Player player) {
        if (player.getDemonHunterTask().isEmpty()) {
            player.sendMessage("You don't currently have a Demon Hunter task.");
            return;
        }
        DemonHunterTaskOverlayManager.schedule(player);
        player.sendMessage("Your task overlay has been refreshed.");
    }

    private static void abandonTask(Player player) {
        if (player.getDemonHunterTask().isEmpty()) {
            player.sendMessage("You don't have a task to abandon.");
            return;
        }
        player.sendMessage("You have abandoned your current task.");
        player.setDemonHunterTask(null);
        player.setDemonHunterTaskProgress(0);
    }

    private static void viewStats(Player player) {
        int level = player.playerLevel[Skill.DEMON_HUNTER.getId()];
        int streak = player.getDemonTaskStreak();
        int marks = player.getDemonMarks();
        player.sendMessage("Demon Hunter Level: " + level);
        player.sendMessage("Task Streak: " + streak);
        player.sendMessage("Demon Marks: " + marks);
        player.getDemonContract().ifPresentOrElse(
                c -> player.sendMessage("Active Contract: Defeat " + c.getAmount() + " " + c.getTarget().getNpcName()),
                () -> player.sendMessage("Active Contract: none"));
    }

    private static void openShop(Player player) {
        DemonMarkRewardHandler.openShop(player);
    }

    private static void explainTiers(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        for (DemonSlayerMaster.Tier tier : DemonSlayerMaster.Tier.values()) {
            String bosses = Arrays.stream(DemonSlayerMaster.BossTier.values())
                    .filter(b -> b.getTier() == tier)
                    .map(DemonSlayerMaster.BossTier::getNpcName)
                    .collect(Collectors.joining(", "));
            builder.npc("Tier " + tier.getId() + " (lvl " + tier.getLevelRequirement() + "):", bosses);
        }
        builder.option(
                new DialogueOption("Back to menu", p -> showMainMenu(p, false)),
                DialogueBuilder.EXIT_DIALOGUE_OPTION
        );
        player.start(builder);
    }

    private static void explainRewards(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.npc("On-task kills give full XP", "and Demon Marks.");
        builder.npc("Each Demon Hunter level", "adds +1% task damage.");
        builder.npc("Lvl20: +5% drops, Lvl40:", "faster kills, Lvl60: +1 mark");
        builder.npc("Off-task kills give 25%", "base XP and no marks.");
        builder.option(
                new DialogueOption("Back to menu", p -> showMainMenu(p, false)),
                DialogueBuilder.EXIT_DIALOGUE_OPTION
        );
        player.start(builder);
    }

    @PostInit
    public static void init() {
        NPCSpawning.spawnNpc(Npcs.DEMON_HUNTER_MASTER, 3095, 3500, 0, 0, 0);
        NPCAction.register(Npcs.DEMON_HUNTER_MASTER, 1,
                (player, npc) -> DemonHunterSlayerDialogue.showMainMenu(player, true));
    }
}
