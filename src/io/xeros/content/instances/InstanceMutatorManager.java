package io.xeros.content.instances;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.util.Misc;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages weekly mutator rotations, active mutators and the global danger
 * meter for AOE instances.
 */
public class InstanceMutatorManager {

    private static class MutatorConfig {
        MutatorRarity rarity;
        public MutatorRarity getRarity() { return rarity; }
    }

    private static final Map<InstanceMutator, MutatorConfig> DEFINITIONS = new EnumMap<>(InstanceMutator.class);
    private static final List<MutatorSynergy> SYNERGIES = new ArrayList<>();
    private static final EnumSet<InstanceMutator> ACTIVE = EnumSet.noneOf(InstanceMutator.class);
    private static int dangerLevel;

    static {
        try {
            Type type = new TypeToken<Map<String, MutatorConfig>>(){}.getType();
            InputStreamReader reader = new InputStreamReader(InstanceMutatorManager.class.getResourceAsStream("/aoe_mutators.json"));
            Map<String, MutatorConfig> defs = new Gson().fromJson(reader, type);
            for (Map.Entry<String, MutatorConfig> e : defs.entrySet()) {
                InstanceMutator mutator = InstanceMutator.valueOf(e.getKey().toUpperCase());
                DEFINITIONS.put(mutator, e.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Type type = new TypeToken<List<MutatorSynergy>>(){}.getType();
            InputStreamReader reader = new InputStreamReader(InstanceMutatorManager.class.getResourceAsStream("/aoe_mutator_synergies.json"));
            List<MutatorSynergy> synergies = new Gson().fromJson(reader, type);
            if (synergies != null) {
                SYNERGIES.addAll(synergies);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Rolls a new weekly set of mutators based on rarity weights. */
    public static void rollWeeklyMutators() {
        ACTIVE.clear();
        List<InstanceMutator> pool = new ArrayList<>(DEFINITIONS.keySet());
        while (ACTIVE.size() < 2 && !pool.isEmpty()) {
            InstanceMutator pick = weightedRandom(pool);
            ACTIVE.add(pick);
            pool.remove(pick);
        }
        // roll anomaly slot
        if (Misc.random(99) == 0 && DEFINITIONS.containsKey(InstanceMutator.ANOMALY)) {
            ACTIVE.add(InstanceMutator.ANOMALY);
        }
        checkSynergies();
    }

    private static InstanceMutator weightedRandom(List<InstanceMutator> pool) {
        int total = pool.stream().mapToInt(p -> DEFINITIONS.get(p).getRarity().getWeight()).sum();
        int roll = Misc.random(total - 1);
        int cumulative = 0;
        for (InstanceMutator m : pool) {
            cumulative += DEFINITIONS.get(m).getRarity().getWeight();
            if (roll < cumulative) {
                return m;
            }
        }
        return pool.get(0);
    }

    private static void checkSynergies() {
        for (MutatorSynergy synergy : SYNERGIES) {
            if (ACTIVE.containsAll(synergy.getMutators())) {
                // For now simply raise danger as a placeholder effect
                increaseDanger(20);
            }
        }
    }

    public static boolean isActive(InstanceMutator mutator) {
        return ACTIVE.contains(mutator);
    }

    public static void toggle(InstanceMutator mutator) {
        if (ACTIVE.contains(mutator)) {
            ACTIVE.remove(mutator);
        } else {
            ACTIVE.add(mutator);
        }
        checkSynergies();
    }

    public static String getActiveDisplay() {
        if (ACTIVE.isEmpty()) {
            return "None";
        }
        return ACTIVE.stream().map(Enum::name).collect(Collectors.joining(", "));
    }

    public static void increaseDanger(int amount) {
        dangerLevel = Math.min(100, dangerLevel + amount);
        if (dangerLevel >= 100) {
            // placeholder: would spawn mega hazard or boss
        }
    }

    public static int getDangerLevel() {
        return dangerLevel;
    }

    public static void resetDanger() {
        dangerLevel = 0;
    }
}
