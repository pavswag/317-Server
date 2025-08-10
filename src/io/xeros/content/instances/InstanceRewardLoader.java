package io.xeros.content.instances;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Loads reward crate definitions for instance performance scores.
 */
public class InstanceRewardLoader {

    private static final Map<PerformanceRank, List<GameItem>> REWARDS = new EnumMap<>(PerformanceRank.class);

    static {
        try {
            Type type = new TypeToken<Map<String, List<GameItem>>>(){}.getType();
            InputStreamReader reader = new InputStreamReader(InstanceRewardLoader.class.getResourceAsStream("/instance_rewards.json"));
            Map<String, List<GameItem>> map = new Gson().fromJson(reader, type);
            for (Map.Entry<String, List<GameItem>> e : map.entrySet()) {
                PerformanceRank rank = PerformanceRank.valueOf(e.getKey().toUpperCase());
                REWARDS.put(rank, e.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Rolls a reward crate based on the player's final score. */
    public static void giveRewards(io.xeros.model.entity.player.Player player, int score) {
        PerformanceRank rank = PerformanceRank.forScore(score);
        List<GameItem> items = REWARDS.get(rank);
        if (items != null) {
            for (GameItem item : items) {
                player.getItems().addItem(item.getId(), item.getAmount());
            }
        }
        // Ultra rare roll scales with score.
        double chance = Math.max(1, 500 - (score / 100));
        if (Misc.random((int)chance) == 0) {
            player.getItems().addItem(12588, 1); // example ultra loot
            player.sendMessage("@dre@You have received an ultra rare item!");
        }
    }
}
