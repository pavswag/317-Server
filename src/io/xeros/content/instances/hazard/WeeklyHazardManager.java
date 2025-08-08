package io.xeros.content.instances.hazard;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.content.instances.InstanceMutator;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and stores the weekly rotating hazard set and mutator synergies.
 */
public class WeeklyHazardManager {

    public static class WeeklyHazardConfig {
        public List<EnvironmentalHazardType> staticHazards = new ArrayList<>();
        public List<InstanceMutator> synergyMutators = new ArrayList<>();
        public EnvironmentalHazardType eliteHazard;
    }

    private static WeeklyHazardConfig config = new WeeklyHazardConfig();

    public static WeeklyHazardConfig get() {
        return config;
    }

    public static void load() {
        try {
            Type type = new TypeToken<WeeklyHazardConfig>(){}.getType();
            InputStreamReader reader = new InputStreamReader(WeeklyHazardManager.class.getResourceAsStream("/aoe_weekly_hazards.json"));
            WeeklyHazardConfig loaded = new Gson().fromJson(reader, type);
            if (loaded != null) {
                config = loaded;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reload() {
        load();
    }

    static {
        load();
    }
}
