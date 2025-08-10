package io.xeros.content.instances.hazard;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.content.instances.BossInstanceManager.BossTier;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Loads arena wide hazard patterns per tier from json.
 */
public class EnvironmentalHazardPatternLoader {

    public static class PatternConfig {
        private int frequency;
        private List<EnvironmentalHazardPattern> patterns;

        public int getFrequency() { return frequency; }
        public List<EnvironmentalHazardPattern> getPatterns() { return patterns; }
    }

    private static final Map<BossTier, PatternConfig> CONFIGS = new EnumMap<>(BossTier.class);

    static {
        try {
            Type type = new TypeToken<Map<String, PatternConfig>>(){}.getType();
            InputStreamReader reader = new InputStreamReader(EnvironmentalHazardPatternLoader.class.getResourceAsStream("/aoe_environmental_patterns.json"));
            Map<String, PatternConfig> map = new Gson().fromJson(reader, type);
            for (Map.Entry<String, PatternConfig> e : map.entrySet()) {
                // Pattern resources historically used keys like "TIER_1" whereas the enum
                // constants are defined without the underscore ("TIER1").  To remain backwards
                // compatible with existing JSON we normalise the key by stripping underscores
                // before resolving the {@link BossTier}.
                String normalised = e.getKey().replace("_", "").toUpperCase();
                BossTier tier = BossTier.valueOf(normalised);
                CONFIGS.put(tier, e.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PatternConfig get(BossTier tier) {
        return CONFIGS.get(tier);
    }
}
