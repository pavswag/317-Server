package io.xeros.content.skills.slayer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility for calculating Demon Hunter experience rewards from base npc experience.
 */
public class DemonHunterXPTable {

    private static final String CONFIG = "demonhunter-xp.json";
    private static final Map<Integer, Double> multipliers = new HashMap<>();

    static {
        load();
    }

    private static class XpConfig {
        int tier;
        double xpMultiplier;
    }

    private static void load() {
        try (FileReader reader = new FileReader(CONFIG)) {
            List<XpConfig> list = new Gson().fromJson(reader, new TypeToken<List<XpConfig>>(){}.getType());
            multipliers.clear();
            for (XpConfig cfg : list) {
                multipliers.put(cfg.tier, cfg.xpMultiplier);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        load();
    }

    /**
     * Calculate experience for killing a boss on task.
     */
    public static int getXPFor(int baseXp, DemonSlayerMaster.Tier tier) {
        double mult = multipliers.getOrDefault(tier.getId(), 1.0);
        return (int) (baseXp * mult);
    }

    /**
     * Experience for killing a boss off task.
     */
    public static int getOffTaskXP(int baseXp) {
        return (int) (baseXp * 0.25);
    }
}
