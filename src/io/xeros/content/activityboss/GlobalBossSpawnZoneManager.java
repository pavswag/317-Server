package io.xeros.content.activityboss;

import io.xeros.model.entity.player.Position;
import java.util.EnumMap;
import java.util.Map;

/**
 * Handles available spawn zones for global bosses and prevents overlap.
 */
public class GlobalBossSpawnZoneManager {

    private static final Map<GlobalBossType, Position[]> ZONES = new EnumMap<>(GlobalBossType.class);
    private static final Map<GlobalBossType, String> ZONE_NAMES = new EnumMap<>(GlobalBossType.class);

    static {
        ZONES.put(GlobalBossType.AHRIM, new Position[]{new Position(3222, 9800, 0)});
        ZONE_NAMES.put(GlobalBossType.AHRIM, "Ahrim's Crypt");

        ZONES.put(GlobalBossType.DHAROK, new Position[]{new Position(3225, 9802, 0)});
        ZONE_NAMES.put(GlobalBossType.DHAROK, "Dharok's Crypt");

        ZONES.put(GlobalBossType.KARIL, new Position[]{new Position(3228, 9805, 0)});
        ZONE_NAMES.put(GlobalBossType.KARIL, "Karil's Crypt");

        ZONES.put(GlobalBossType.GUTHAN, new Position[]{new Position(3231, 9803, 0)});
        ZONE_NAMES.put(GlobalBossType.GUTHAN, "Guthan's Crypt");

        ZONES.put(GlobalBossType.VERAC, new Position[]{new Position(3229, 9797, 0)});
        ZONE_NAMES.put(GlobalBossType.VERAC, "Verac's Crypt");
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

    public static String getZoneName(GlobalBossType type) {
        return ZONE_NAMES.getOrDefault(type, "Unknown");
    }
}

