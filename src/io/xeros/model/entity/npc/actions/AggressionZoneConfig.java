package io.xeros.model.entity.npc.actions;

import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Position;

import java.util.Set;

/**
 * Configuration for zones where forced aggression is allowed.
 */
public class AggressionZoneConfig {

    private static final Set<Boundary> AGGRESSIVE_ZONES = Set.of(
            Boundary.PERK_ZONE,
            Boundary.LITHKREN_VAULT,
            NightmareConstants.BOUNDARY
    );

    private static final Set<Integer> DISABLED_REGIONS = Set.of();

    public static boolean isAggressionAllowed(Position pos) {
        if (DISABLED_REGIONS.contains(pos.getRegionId())) {
            return false;
        }
        return AGGRESSIVE_ZONES.stream().anyMatch(b -> Boundary.isIn(pos, b));
    }
}
