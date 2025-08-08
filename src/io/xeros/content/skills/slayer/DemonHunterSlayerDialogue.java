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
public class DemonHunterSlayerDialogue extends DialogueBuilder {

    private static final int NPC = Npcs.DEMON_HUNTER_MASTER;

    public DemonHunterSlayerDialogue(Player player) {
        super(player);
        setNpcId(NPC);
        npc("Greetings, slayer. How can I assist you on your Demon Hunter journey?");
        List<DialogueOption> options = new ArrayList<>();
        options.add(new DialogueOption("What's Demon Hunter Slayer?", this::explainSystem));
        options.add(new DialogueOption("What are the Demon Hunter Slayer tiers?", this::explainTiers));
        options.add(new DialogueOption("What are the rewards and XP like?", this::explainRewards));
        options.add(new DialogueOption("View Current Task", this::viewTask));
        options.add(new DialogueOption("Abandon Task", this::abandonTask));
        options.add(new DialogueOption("View Stats / Contract", this::viewStats));
        options.add(new DialogueOption("Open Reward Shop", this::openShop));
        if (player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            options.add(new DialogueOption("Admin Options", this::adminMenu));
        }
        option(options.toArray(new DialogueOption[0]));
    }

    private void explainSystem(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.npc("Demon Hunter Slayer is an elite slayer mode focused on defeating demon bosses.");
        builder.npc("You'll receive powerful assignments based on your Demon Hunter level.");
        builder.npc("Progress builds your streak, earns Slayer XP, and rewards Demon Marks.");
        builder.npc("Milestones unlock perks and elite tasks, and contracts offer extra rewards.");
        builder.npc("Track your progress, climb the leaderboard, and claim your glory.");
        builder.option(new DialogueOption("Got it. Back to menu.", p -> p.start(new DemonHunterSlayerDialogue(p))));
        player.start(builder);
    }

    private void viewTask(Player player) {
        if (player.getDemonHunterTask().isEmpty()) {
            player.sendMessage("You don't currently have a Demon Hunter task.");
            return;
        }
        DemonHunterTaskOverlayManager.schedule(player);
        player.sendMessage("Your task overlay has been refreshed.");
    }

    private void abandonTask(Player player) {
        if (player.getDemonHunterTask().isEmpty()) {
            player.sendMessage("You don't have a task to abandon.");
            return;
        }
        player.sendMessage("You have abandoned your current task.");
        player.setDemonHunterTask(null);
        player.setDemonHunterTaskProgress(0);
    }

    private void viewStats(Player player) {
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

    private void openShop(Player player) {
        DemonMarkRewardHandler.openShop(player);
    }

    private void adminMenu(Player player) {
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
                new DialogueOption("View boss XP logs", p -> {
                    for (DemonSlayerMaster.BossTier bt : DemonSlayerMaster.BossTier.values()) {
                        int xp = DemonHunterXPTable.getXPFor(bt.getBaseXp(), bt.getTier());
                        p.sendMessage(bt.getNpcName() + ": base " + bt.getBaseXp() + " -> " + xp);
                    }
                }),
                new DialogueOption("Open Reward Shop", DemonMarkRewardHandler::openShop),
                new DialogueOption("Back", p -> p.start(new DemonHunterSlayerDialogue(p))));
        player.start(builder);
    }

    private void explainTiers(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        for (DemonSlayerMaster.Tier tier : DemonSlayerMaster.Tier.values()) {
            String bosses = Arrays.stream(DemonSlayerMaster.BossTier.values())
                    .filter(b -> b.getTier() == tier)
                    .map(DemonSlayerMaster.BossTier::getNpcName)
                    .collect(Collectors.joining(", "));
            builder.npc("Tier " + tier.getId() + " (lvl " + tier.getLevelRequirement() + "): " + bosses);
        }
        builder.option(new DialogueOption("Back", p -> p.start(new DemonHunterSlayerDialogue(p))));
        player.start(builder);
    }

    private void explainRewards(Player player) {
        DialogueBuilder builder = new DialogueBuilder(player).setNpcId(NPC);
        builder.npc("On-task kills grant full Demon Hunter XP and Marks.");
        builder.npc("Off-task kills only award 25% of the base XP and no marks.");
        builder.npc("Higher tiers have larger XP multipliers from the configuration file.");
        builder.option(new DialogueOption("Back", p -> p.start(new DemonHunterSlayerDialogue(p))));
        player.start(builder);
    }

    @PostInit
    public static void init() {
        NPCSpawning.spawnNpc(Npcs.DEMON_HUNTER_MASTER, 3095, 3500, 0, 0, 0);
        NPCAction.register(Npcs.DEMON_HUNTER_MASTER, 1,
                (player, npc) -> player.start(new DemonHunterSlayerDialogue(player)));
    }
}

