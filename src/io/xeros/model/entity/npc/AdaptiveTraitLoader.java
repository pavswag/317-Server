package io.xeros.model.entity.npc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.util.Misc;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loads adaptive trait definitions from a json file and provides random
 * selections for bosses.
 */
public class AdaptiveTraitLoader {

    private static final List<AdaptiveTrait> TRAITS = new ArrayList<>();

    static {
        load();
    }

    private static void load() {
        try (FileReader reader = new FileReader("resources/adaptive_traits.json")) {
            Type list = new TypeToken<List<AdaptiveTrait>>(){}.getType();
            List<AdaptiveTrait> loaded = new Gson().fromJson(reader, list);
            if (loaded != null) {
                TRAITS.addAll(loaded);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a random selection of 1-3 traits.
     */
    public static List<AdaptiveTrait> randomTraits() {
        List<AdaptiveTrait> copy = new ArrayList<>(TRAITS);
        Collections.shuffle(copy);
        int count = Misc.random(1, Math.min(3, copy.size()));
        return new ArrayList<>(copy.subList(0, count));
    }

    public static List<AdaptiveTrait> getTraits() {
        return TRAITS;
    }
}
