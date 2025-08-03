package io.xeros.content.activityboss;

import io.xeros.model.entity.player.Position;
import java.util.EnumMap;
import java.util.Map;

/**
 * Handles available spawn zones for global bosses and prevents overlap.
 */
public class GlobalBossSpawnZoneManager {

    private static final Map<GlobalBossType, Position[]> ZONES = new EnumMap<>(GlobalBossType.class);

    static {
        ZONES.put(GlobalBossType.CORRUPTED_HYDRA, new Position[]{new Position(1310, 3615, 0)});
        ZONES.put(GlobalBossType.TREASURE_MIMIC, new Position[]{new Position(3087, 3495, 0)});
        ZONES.put(GlobalBossType.ASHEN_BEAST, new Position[]{new Position(3039, 3432, 0)});
        ZONES.put(GlobalBossType.BLOOD_REAPER, new Position[]{new Position(3245, 3945, 0)});
    }

    private GlobalBossSpawnZoneManager() {
    }

    public static Position getAvailableSpawnLocation(GlobalBossType type) {
        Position[] positions = ZONES.get(type);
        if (positions == null) {
            return null;
        }
        for (Position pos : positions) {
            if (!GlobalBossActivityManager.isPositionOccupied(pos)) {
                return pos;
            }
        }
        return null;
    }
}

