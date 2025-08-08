package io.xeros.content.instances.hazard;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.content.instances.BossInstanceManager;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class EnvironmentalHazardLoader {

    public static class HazardConfig {
        private int frequency;
        private List<EnvironmentalHazardDefinition> hazards;
        public int getFrequency() { return frequency; }
        public List<EnvironmentalHazardDefinition> getHazards() { return hazards; }
    }

    private static final Map<BossInstanceManager.BossTier, HazardConfig> CONFIGS = new EnumMap<>(BossInstanceManager.BossTier.class);

    static {
        try {
            Type type = new TypeToken<Map<String, HazardConfig>>(){}.getType();
            InputStreamReader reader = new InputStreamReader(EnvironmentalHazardLoader.class.getResourceAsStream("/aoe_environmental_hazards.json"));
            Map<String, HazardConfig> map = new Gson().fromJson(reader, type);
            for (Map.Entry<String, HazardConfig> e : map.entrySet()) {
                BossInstanceManager.BossTier tier = BossInstanceManager.BossTier.valueOf(e.getKey().toUpperCase());
                CONFIGS.put(tier, e.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HazardConfig get(BossInstanceManager.BossTier tier) {
        return CONFIGS.get(tier);
    }
}
