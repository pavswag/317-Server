package io.xeros.content.instances.aoe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * In-memory repository for AOE boss tier definitions. Acts as the single
 * source of truth that other components query.
 */
public class AoeTierRepo {

    private static final Logger logger = LoggerFactory.getLogger(AoeTierRepo.class);
    private static final AtomicReference<List<AoeBossTierDef>> TIERS =
            new AtomicReference<>(Collections.emptyList());

    public static List<AoeBossTierDef> get() {
        return Collections.unmodifiableList(TIERS.get());
    }

    public static int size() {
        return TIERS.get().size();
    }

    public static void set(List<AoeBossTierDef> tiers, String sourceTag) {
        List<AoeBossTierDef> copy = tiers == null ? Collections.emptyList() : List.copyOf(tiers);
        TIERS.set(copy);
        String first = copy.isEmpty() ? "none" : ("T" + copy.get(0).tier + " " + copy.get(0).zoneName);
        logger.info("[AOE] Tiers loaded: {} from {} (first: {})", copy.size(), sourceTag, first);
    }

    public static AoeBossTierDef byTier(int tier) {
        for (AoeBossTierDef def : TIERS.get()) {
            if (def.tier == tier) {
                return def;
            }
        }
        return null;
    }
}

