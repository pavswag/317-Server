package io.xeros.content.instances.aoe;

import com.fasterxml.jackson.core.type.TypeReference;
import io.xeros.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads {@link AoeZoneMapDef} definitions from json and exposes lookup helpers.
 */
public final class AoeZoneMaps {

    private static final Logger logger = LoggerFactory.getLogger(AoeZoneMaps.class);
    private static final Path FILE = Paths.get("data/aoe/AoeZoneMapConfig.json");
    private static final Map<String, AoeZoneMapDef> MAPS = new ConcurrentHashMap<>();

    private AoeZoneMaps() {}

    private static class Config {
        public List<AoeZoneMapDef> tiers = Collections.emptyList();
    }

    static {
        reload();
    }

    public static void reload() {
        MAPS.clear();
        if (!Files.exists(FILE)) {
            logger.warn("[AOE] Map config missing: {}", FILE.toAbsolutePath());
            return;
        }
        try {
            Config config = JsonUtil.fromJacksonJson(FILE.toFile(), new TypeReference<Config>(){});
            List<AoeZoneMapDef> defs = config != null && config.tiers != null ? config.tiers : Collections.emptyList();
            for (AoeZoneMapDef def : defs) {
                if (def == null || def.getId() == null) {
                    continue;
                }
                MAPS.put(normalize(def.getId()), def);
            }
            logger.info("[AOE] Loaded {} zone map defs (file={})", MAPS.size(), FILE.toAbsolutePath());
        } catch (IOException e) {
            logger.error("[AOE] Failed loading map defs from {}", FILE.toAbsolutePath(), e);
        }
    }

    private static String normalize(String id) {
        return id == null ? "" : id.trim().toUpperCase();
    }

    public static Optional<AoeZoneMapDef> forId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(MAPS.get(normalize(id)));
    }

    public static Map<String, AoeZoneMapDef> snapshot() {
        return Collections.unmodifiableMap(new ConcurrentHashMap<>(MAPS));
    }
}

