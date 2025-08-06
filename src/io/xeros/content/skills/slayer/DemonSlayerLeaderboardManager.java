package io.xeros.content.skills.slayer;

import io.xeros.model.entity.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks weekly Demon Hunter statistics.
 */
public class DemonSlayerLeaderboardManager {

    private static final Map<String, Integer> XP = new ConcurrentHashMap<>();
    private static final Map<String, Integer> TASKS = new ConcurrentHashMap<>();
    private static final Map<String, Integer> STREAKS = new ConcurrentHashMap<>();

    public static void addXp(Player player, int amount) {
        XP.merge(player.getLoginName(), amount, Integer::sum);
    }

    public static void taskCompleted(Player player) {
        TASKS.merge(player.getLoginName(), 1, Integer::sum);
        STREAKS.put(player.getLoginName(), player.getDemonTaskStreak());
    }
}
