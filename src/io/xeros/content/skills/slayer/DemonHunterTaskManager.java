package io.xeros.content.skills.slayer;

import io.xeros.content.skills.Skill;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

/**
 * Handles Demon Hunter task progress and completion.
 */
public class DemonHunterTaskManager {

    public static void handleKill(Player player, NPC npc) {
        String name = npc.getDefinition().getName().replace("_", " ");
        Optional<DemonSlayerMaster.BossTier> bossOpt = DemonSlayerMaster.BossTier.forName(name);
        if (bossOpt.isEmpty()) {
            return;
        }
        DemonSlayerMaster.BossTier boss = bossOpt.get();
        int baseXp = boss.getBaseXp();

        if (player.getDemonHunterTask().isPresent() && player.getDemonHunterTask().get().getBoss() == boss) {
            int remaining = player.getDemonHunterTaskProgress() - 1;
            player.setDemonHunterTaskProgress(Math.max(0, remaining));

            int xp = DemonHunterXPTable.getXPFor(baseXp, boss.getTier());
            if (player.getDemonHunterMilestones().contains(10)) {
                xp = (int) (xp * 1.05);
            }
            if (player.getDemonContract().isPresent() && !player.getDemonContract().get().isCompleted()
                    && player.getDemonContract().get().matches(boss)) {
                xp *= 2;
                player.getDemonContract().get().complete();
                player.sendMessage("\uD83C\uDFAF Bonus contract complete!");
            }
            player.addDemonHunterXP(xp);
            player.getPA().addSkillXPMultiplied(baseXp, Skill.SLAYER.getId(), true);
            DemonSlayerLeaderboardManager.addXp(player, xp);

            if (remaining <= 0) {
                player.sendMessage("\uD83C\uDFAF Task Complete: You've slain " + boss.getNpcName() + "! +" + (xp * player.getDemonHunterTask().get().getAmount()) + " Demon Hunter XP");
                player.incrementDemonTaskStreak();
                DemonSlayerMilestoneManager.check(player);
                DemonMarkRewardHandler.reward(player);
                DemonSlayerLeaderboardManager.taskCompleted(player);
                player.setDemonHunterTask(null);
                player.setDemonHunterTaskProgress(0);
            } else {
                DemonHunterTaskOverlayManager.send(player);
            }
        } else {
            int xp = DemonHunterXPTable.getOffTaskXP(baseXp);
            player.addDemonHunterXP(xp);
            player.getPA().addSkillXPMultiplied(xp, Skill.SLAYER.getId(), true);
        }
    }
}
