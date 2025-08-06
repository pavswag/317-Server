package io.xeros.model.entity.npc.actions;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Utility class for forcing nearby npcs to become aggressive towards a player.
 */
public class AggressionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AggressionHandler.class);

    private static final long CACHE_LIFETIME_MS = 5_000L;

    private static final Map<Player, CachedTargets> CACHE = new WeakHashMap<>();

    private static class CachedTargets {
        final List<NPC> npcs;
        final Position lastPosition;
        final int region;
        final long time;

        CachedTargets(List<NPC> npcs, Position lastPosition, int region, long time) {
            this.npcs = npcs;
            this.lastPosition = lastPosition;
            this.region = region;
            this.time = time;
        }
    }

    /**
     * Forces all attackable npcs in the given range to target the player.
     *
     * @param player the player receiving aggression
     * @param range the radius in which npcs should be forced to attack
     */
    public static void forceAggro(Player player, int range) {
        if (!AggressionZoneConfig.isAggressionAllowed(player.getPosition())) {
            return;
        }

        CachedTargets cached = CACHE.get(player);
        int region = player.getPosition().getRegionId();
        if (cached == null || !player.getPosition().equals(cached.lastPosition)
                || cached.region != region || System.currentTimeMillis() - cached.time > CACHE_LIFETIME_MS) {
            List<NPC> nearby = new ArrayList<>();
            for (NPC npc : NPCHandler.npcs) {
                if (npc == null || !AggressionNPCConfig.canAggress(npc)) {
                    continue;
                }
                if (npc.heightLevel != player.heightLevel || npc.getInstance() != player.getInstance()) {
                    continue;
                }
                if (!AggressionZoneConfig.isAggressionAllowed(npc.getPosition())) {
                    continue;
                }
                if (npc.getDistance(player.getX(), player.getY()) <= range) {
                    nearby.add(npc);
                }
            }
            cached = new CachedTargets(nearby, player.getPosition(), region, System.currentTimeMillis());
            CACHE.put(player, cached);
        }

        for (NPC npc : cached.npcs) {
            if (!AggressionNPCConfig.canAggress(npc)) {
                continue;
            }
            if (npc.getPlayerAttackingIndex() > 0) {
                continue;
            }
            if (npc.getDistance(player.getX(), player.getY()) > range) {
                continue;
            }
            npc.setPlayerAttackingIndex(player.getIndex());
            player.underAttackByNpc = npc.getIndex();
            logger.debug("Forced aggression of npc {} at ({}, {}) toward {}", npc.getNpcId(), npc.absX, npc.absY, player.getLoginName());
        }
    }
}
