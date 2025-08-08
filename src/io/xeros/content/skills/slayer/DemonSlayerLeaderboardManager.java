package io.xeros.content.skills.slayer;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks weekly Demon Hunter statistics.
 */
public class DemonSlayerLeaderboardManager {

    private static final Map<String, Integer> XP = new ConcurrentHashMap<>();
    private static final Map<String, Integer> TASKS = new ConcurrentHashMap<>();
    private static final Map<String, Integer> STREAKS = new ConcurrentHashMap<>();

    private static String topXpPlayer = "";
    private static int topXp = 0;
    private static String topTaskPlayer = "";
    private static int topTasks = 0;
    private static String topStreakPlayer = "";
    private static int topStreak = 0;

    public static void addXp(Player player, int amount) {
        int total = XP.merge(player.getLoginName(), amount, Integer::sum);
        if (total > topXp) {
            topXp = total;
            topXpPlayer = player.getLoginName();
            PlayerHandler.executeGlobalMessage("[Demon Slayer] " + player.getDisplayName() + " now leads weekly Demon Hunter XP with " + total + ".");
        }
    }

    public static void taskCompleted(Player player) {
        int tasks = TASKS.merge(player.getLoginName(), 1, Integer::sum);
        int streak = player.getDemonTaskStreak();
        STREAKS.put(player.getLoginName(), streak);
        if (tasks > topTasks) {
            topTasks = tasks;
            topTaskPlayer = player.getLoginName();
            PlayerHandler.executeGlobalMessage("[Demon Slayer] " + player.getDisplayName() + " leads weekly Demon Hunter tasks with " + tasks + ".");
        }
        if (streak > topStreak) {
            topStreak = streak;
            topStreakPlayer = player.getLoginName();
            PlayerHandler.executeGlobalMessage("[Demon Slayer] " + player.getDisplayName() + " has the longest Demon Hunter streak at " + streak + ".");
        }
    }
}
