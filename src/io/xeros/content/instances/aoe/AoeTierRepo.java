package io.xeros.content.instances.aoe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import io.xeros.model.entity.player.Player;

/**
 * In-memory repository for AOE boss tier definitions. Acts as the single
 * source of truth that other components query.
 */
public class AoeTierRepo {

    private static final Logger logger = LoggerFactory.getLogger(AoeTierRepo.class);
    private static final AtomicReference<List<AoeBossTierDef>> TIERS =
            new AtomicReference<>(Collections.emptyList());
    private static final Map<UUID, AoeInstance> INSTANCES = new ConcurrentHashMap<>();
    private static final Map<Integer, UUID> PLAYER_TO_INSTANCE = new ConcurrentHashMap<>();

    public static List<AoeBossTierDef> get() {
        return Collections.unmodifiableList(TIERS.get());
    }

    public static int size() {
        return TIERS.get().size();
    }

    public static void set(List<AoeBossTierDef> tiers, String sourceTag) {
        List<AoeBossTierDef> copy;
        if (tiers == null || tiers.isEmpty()) {
            copy = Collections.emptyList();
        } else {
            copy = Collections.unmodifiableList(new ArrayList<>(tiers));
        }
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

    public static void registerInstance(Player player, AoeInstance instance) {
        if (player == null || instance == null) {
            return;
        }
        INSTANCES.put(instance.id(), instance);
        PLAYER_TO_INSTANCE.put(player.getIndex(), instance.id());
        logger.info("[AOE] Instance registered id={} tier={} player={}", instance.id(), instance.tier().getTier(), player.getLoginName());
    }

    public static Optional<AoeInstance> instanceForPlayer(Player player) {
        if (player == null) {
            return Optional.empty();
        }
        UUID id = PLAYER_TO_INSTANCE.get(player.getIndex());
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(INSTANCES.get(id));
    }

    public static Optional<AoeInstance> instanceById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(INSTANCES.get(id));
    }

    public static void clearInstance(Player player) {
        clearInstance(player, "player_context");
    }

    public static void clearInstance(Player player, String reason) {
        if (player == null) {
            return;
        }
        UUID id = PLAYER_TO_INSTANCE.remove(player.getIndex());
        if (id != null) {
            INSTANCES.remove(id);
            logger.info("[AOE] Cleared instance {} for player {} reason={} stack={}", id, player.getLoginName(), reason,
                    Arrays.toString(Thread.currentThread().getStackTrace()));
        }
    }

    public static void clearInstance(UUID id) {
        clearInstance(id, "no_player_context");
    }

    public static void clearInstance(UUID id, String reason) {
        if (id == null) {
            return;
        }
        INSTANCES.remove(id);
        PLAYER_TO_INSTANCE.values().removeIf(uuid -> uuid.equals(id));
        logger.info("[AOE] Cleared instance {} reason={} stack={}", id, reason, Arrays.toString(Thread.currentThread().getStackTrace()));
    }
}
