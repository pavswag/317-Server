package io.xeros.content.skills.slayer;

import io.xeros.content.skills.Skill;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

/**
 * Handles Demon Hunter task progress and completion.
 */
public class DemonHunterTaskManager {

    public static void handleKill(Player player, NPC npc) {
        player.getDemonHunterTask().ifPresent(task -> {
            String name = npc.getDefinition().getName().replace("_", " ");
            if (!task.getBoss().matches(name)) {
                return;
            }
            int remaining = player.getDemonHunterTaskProgress() - 1;
            player.setDemonHunterTaskProgress(Math.max(0, remaining));

            int xp = DemonHunterXPTable.getXPFor(task.getBoss().getTier(), player.playerLevel[Skill.DEMON_HUNTER.getId()]);
            if (player.getDemonHunterMilestones().contains(10)) {
                xp = (int)(xp * 1.05);
            }
            if (player.getDemonContract().isPresent() && !player.getDemonContract().get().isCompleted()
                    && player.getDemonContract().get().matches(task.getBoss())) {
                xp *= 2;
                player.getDemonContract().get().complete();
                player.sendMessage("\uD83C\uDFAF Bonus contract complete!");
            }
            player.addDemonHunterXP(xp);
            player.getPA().addSkillXPMultiplied(task.getBoss().getXpReward(), Skill.SLAYER.getId(), true);
            DemonSlayerLeaderboardManager.addXp(player, xp);

            if (remaining <= 0) {
                player.sendMessage("\uD83C\uDFAF Task Complete: You've slain " + task.getBoss().getNpcName() + "! +" + (xp * task.getAmount()) + " Demon Hunter XP");
                player.incrementDemonTaskStreak();
                DemonSlayerMilestoneManager.check(player);
                DemonMarkRewardHandler.reward(player);
                DemonSlayerLeaderboardManager.taskCompleted(player);
                player.setDemonHunterTask(null);
                player.setDemonHunterTaskProgress(0);
            } else {
                DemonHunterTaskOverlayManager.send(player);
            }
        });
    }
}
