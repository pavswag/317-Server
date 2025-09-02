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
import java.util.Optional;

/**
 * Loads tier reward definitions from {@code data/aoe/aoe_tier_rewards.json}.
 */
public class AoeTierRewardsLoader {

    private static final Logger logger = LoggerFactory.getLogger(AoeTierRewardsLoader.class);
    private static final Path FILE = Paths.get("data/aoe/aoe_tier_rewards.json");
    private static final Gson GSON = new Gson();

    private static List<AoeTierRewardsDef> defs = Collections.emptyList();

    static { load(); }

    public static void load() {
        try {
            if (!Files.exists(FILE)) {
                logger.warn("AOE tier rewards file not found: {}", FILE.toAbsolutePath());
                defs = Collections.emptyList();
                return;
            }
            String json = Files.readString(FILE);
            defs = GSON.fromJson(json, new TypeToken<List<AoeTierRewardsDef>>(){}.getType());
            if (defs == null) defs = Collections.emptyList();
            logger.info("Loaded {} AOE tier reward defs.", defs.size());
        } catch (IOException e) {
            logger.error("Error loading AOE tier rewards", e);
            defs = Collections.emptyList();
        }
    }

    public static void reload() { load(); }

    public static Optional<AoeTierRewardsDef> forTier(int tier) {
        return defs.stream().filter(d -> d.tier == tier).findFirst();
    }
}
