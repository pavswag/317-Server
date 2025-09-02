package io.xeros.content.instances.aoe;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Loads {@link AoeBossTierDef} definitions from disk. The file is
 * expected at {@code data/aoe/aoe_boss_tiers.json} relative to the
 * working directory.
 */
public class AoeBossTierLoader {

    private static final Logger logger = LoggerFactory.getLogger(AoeBossTierLoader.class);
    private static final Path FILE = Paths.get("data/aoe/aoe_boss_tiers.json");
    private static final Gson GSON = new Gson();

    private static List<AoeBossTierDef> tiers = Collections.emptyList();

    public static void load() {
        try {
            if (!Files.exists(FILE)) {
                logger.warn("AOE boss tier file not found: {}", FILE.toAbsolutePath());
                tiers = Collections.emptyList();
                return;
            }
            String json = Files.readString(FILE);
            tiers = GSON.fromJson(json, new TypeToken<List<AoeBossTierDef>>(){}.getType());
            logger.info("Loaded {} AOE boss tiers.", tiers.size());
        } catch (IOException e) {
            logger.error("Error loading AOE boss tiers", e);
            tiers = Collections.emptyList();
        }
    }

    public static void reload() {
        load();
    }

    public static List<AoeBossTierDef> getTiers() {
        return tiers;
    }

    public static AoeBossTierDef getTier(int tier) {
        for (AoeBossTierDef def : tiers) {
            if (def.tier == tier) {
                return def;
            }
        }
        return null;
    }
}
