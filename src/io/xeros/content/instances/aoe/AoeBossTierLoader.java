package io.xeros.content.instances.aoe;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.model.definitions.NpcDef;
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
            if (tiers == null) {
                tiers = Collections.emptyList();
            }
            tiers.forEach(AoeBossTierLoader::resolveNpcIds);
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

    private static void resolveNpcIds(AoeBossTierDef def) {
        if (def == null) {
            return;
        }
        resolve(def.boss);
        if (def.minions != null) {
            for (AoeBossTierDef.Npc n : def.minions) {
                resolve(n);
            }
        }
    }

    private static void resolve(AoeBossTierDef.Npc n) {
        if (n == null || n.npcId > 0) {
            return;
        }
        int id = resolveNpcIdByName(n.name);
        if (id > 0) {
            n.npcId = id;
        }
    }

    private static int resolveNpcIdByName(String name) {
        if (name == null) {
            return -1;
        }
        for (var entry : NpcDef.getDefinitions().entrySet()) {
            if (name.equalsIgnoreCase(entry.getValue().getName())) {
                return entry.getKey();
            }
        }
        return -1;
    }
}
