package io.xeros.content.skills.slayer;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

import java.util.Arrays;

/**
 * Handles milestone perks for demon slayer tasks.
 */
public class DemonSlayerMilestoneManager {

    private static final int[] MILESTONES = {5, 10, 25, 50, 100, 250, 500};

    public static void check(Player player) {
        int streak = player.getDemonTaskStreak();
        Arrays.stream(MILESTONES).forEach(milestone -> {
            if (streak >= milestone && !player.getDemonHunterMilestones().contains(milestone)) {
                player.getDemonHunterMilestones().add(milestone);
                switch (milestone) {
                    case 10:
                        player.sendMessage("Streak 10: +5% Demon XP");
                        break;
                    case 25:
                        player.sendMessage("Streak 25: +1 task skip per day");
                        break;
                    case 50:
                        player.sendMessage("Streak 50: Elite demons unlocked");
                        break;
                    case 100:
                        player.sendMessage("Streak 100: Bonus loot table rolls");
                        break;
                    default:
                        player.sendMessage("Streak " + milestone + " reached!");
                }
                PlayerHandler.executeGlobalMessage("[Demon Slayer] " + player.getDisplayName() + " reached a streak of " + milestone + "!");
            }
        });
    }
}
