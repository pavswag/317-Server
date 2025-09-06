package io.xeros.content.instances.aoe;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.model.definitions.NpcDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

/**
 * Loads {@link AoeBossTierDef} definitions from disk and populates
 * {@link AoeTierRepo}. The loader never returns null and provides
 * diagnostics when data is missing.
 */
public class AoeBossTierLoader {

    private static final Logger logger = LoggerFactory.getLogger(AoeBossTierLoader.class);
    private static final Path FILE = Paths.get("data/aoe_tiers.json");
    private static final Gson GSON = new Gson();

    static {
        loadAllOrWarn();
    }

    /** Returns the default tier definition file path. */
    public static Path defaultFile() {
        return FILE;
    }

    /** Load tiers from a file. Never returns null. */
    public static List<AoeBossTierDef> load(File file) {
        try {
            if (file == null || !file.exists()) {
                logger.warn("[AOE] Tier file missing: {}", file != null ? file.getAbsolutePath() : "null");
                return Collections.emptyList();
            }
            String json = Files.readString(file.toPath());
            List<AoeBossTierDef> tiers = GSON.fromJson(json, new TypeToken<List<AoeBossTierDef>>(){}.getType());
            if (tiers == null) {
                return Collections.emptyList();
            }
            tiers.forEach(AoeBossTierLoader::resolveNpcIds);
            return tiers;
        } catch (IOException e) {
            logger.error("[AOE] Error loading tiers from {}", file.getAbsolutePath(), e);
            return Collections.emptyList();
        }
    }

    /** Load tiers from disk and set repository, logging warnings when empty. */
    public static void loadAllOrWarn() {
        loadAllOrWarn("startup");
    }

    public static void loadAllOrWarn(String sourceTag) {
        File file = FILE.toFile();
        if (!file.exists()) {
            logger.warn("[AOE] Tier file not found at {}. Creating placeholder.", file.getAbsolutePath());
            createPlaceholder(file);
        }
        List<AoeBossTierDef> tiers = load(file);
        if (tiers.isEmpty()) {
            logger.warn("[AOE] No tiers found. Checked: {}", file.getAbsolutePath());
        }
        AoeTierRepo.set(tiers, sourceTag);
    }

    private static void resolveNpcIds(AoeBossTierDef def) {
        if (def == null) {
            return;
        }
        resolve(def.boss, def);
        if (def.minions != null) {
            def.minions.forEach(n -> resolve(n, def));
        }
    }

    private static void resolve(AoeBossTierDef.Npc npc, AoeBossTierDef tier) {
        if (npc == null) {
            return;
        }
        if (npc.npcId <= 0) {
            int id = resolveNpcIdByName(npc.name);
            if (id > 0) {
                npc.npcId = id;
            } else {
                tier.disabled = true;
                tier.disabledReason = "npcId unresolved: " + npc.name;
            }
        }
    }

    private static int resolveNpcIdByName(String name) {
        if (name == null) return -1;
        for (var entry : NpcDef.getDefinitions().entrySet()) {
            if (name.equalsIgnoreCase(entry.getValue().getName())) {
                return entry.getKey();
            }
        }
        return -1;
    }

    private static void createPlaceholder(File file) {
        file.getParentFile().mkdirs();
        AoeBossTierDef placeholder = new AoeBossTierDef();
        placeholder.tier = 1;
        placeholder.zoneName = "Unicow Pasture";
        placeholder.unlockKills = 10;
        AoeBossTierDef.Npc boss = new AoeBossTierDef.Npc();
        boss.name = "Unicow";
        boss.npcId = resolveNpcIdByName("Unicow");
        placeholder.boss = boss;
        placeholder.minions = Collections.emptyList();
        placeholder.templateRegionId = 12889;
        placeholder.spawnOffsetX = 5;
        placeholder.spawnOffsetY = 5;
        placeholder.aggroRange = 10;
        placeholder.respawnSeconds = 25;
        placeholder.rewards = new AoeBossTierDef.Rewards();
        List<AoeBossTierDef> list = List.of(placeholder);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(GSON.toJson(list));
        } catch (IOException e) {
            logger.error("[AOE] Failed to write placeholder tier file", e);
        }
    }
}

