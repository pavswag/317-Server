package io.xeros.content.pet;

import com.google.gson.reflect.TypeToken;
import io.xeros.util.JsonUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Helper methods for the simplified pet system.
 */
public class PetUtility {

    private static final String PERK_FILE = "etc/cfg/pet_perks.json";

    private static List<PetPerk> PERKS = Collections.emptyList();

    /** Load perk definitions from JSON file. */
    public static void init() {
        try {
            File file = new File(PERK_FILE);
            if (file.exists()) {
                PERKS = JsonUtil.fromJson(PERK_FILE, new TypeToken<List<PetPerk>>(){});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<PetPerk> getPerks() {
        return PERKS;
    }

    public static int getLevelForXP(int xp) {
        int level = 1;
        int requirement = 100;
        int accumulated = 0;
        while (xp >= accumulated + requirement) {
            accumulated += requirement;
            level++;
            requirement += 100;
        }
        return level;
    }

    public static int getXPForLevel(int level) {
        int xp = 0;
        int requirement = 100;
        for (int i = 1; i < level; i++) {
            xp += requirement;
            requirement += 100;
        }
        return xp;
    }
}