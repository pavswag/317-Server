package io.xeros.content.activityboss;

import io.xeros.model.entity.player.Position;
import java.util.Arrays;

/**
 * Enumeration of all global bosses that can be spawned by server wide activities.
 */
public enum GlobalBossType {
    CORRUPTED_HYDRA(9021, "Corrupted Hydra", ActivityType.UPGRADES, 500,
            new Position(1310, 3615, 0),
            "The Corrupted Hydra emerges from the toxic swamp!", SpawnZoneType.SAFE),

    TREASURE_MIMIC(8633, "Treasure Mimic", ActivityType.CLUES, 250,
            new Position(3087, 3495, 0),
            "A Treasure Mimic materialises, guarding its hoard!", SpawnZoneType.SAFE),

    ASHEN_BEAST(9022, "Ashen Beast", ActivityType.FIRE_OF_EXCHANGE, 100_000_000,
            new Position(3039, 3432, 0),
            "The Ashen Beast rises from the exchange ashes!", SpawnZoneType.SAFE),

    BLOOD_REAPER(9023, "Blood Reaper", ActivityType.KILLSTREAK, 1,
            new Position(3245, 3945, 0),
            "The Blood Reaper descends upon the Wilderness!", SpawnZoneType.WILDY);

    private final int npcId;
    private final String name;
    private final ActivityType activityType;
    private final int threshold;
    private final Position spawnPosition;
    private final String spawnMessage;
    private final SpawnZoneType zoneType;

    GlobalBossType(int npcId, String name, ActivityType activityType, int threshold,
                   Position spawnPosition, String spawnMessage, SpawnZoneType zoneType) {
        this.npcId = npcId;
        this.name = name;
        this.activityType = activityType;
        this.threshold = threshold;
        this.spawnPosition = spawnPosition;
        this.spawnMessage = spawnMessage;
        this.zoneType = zoneType;
    }

    public int getNpcId() {
        return npcId;
    }

    public String getName() {
        return name;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public int getThreshold() {
        return threshold;
    }

    public Position getSpawnPosition() {
        return spawnPosition;
    }

    public String getSpawnMessage() {
        return spawnMessage;
    }

    public SpawnZoneType getZoneType() {
        return zoneType;
    }

    public static GlobalBossType forActivity(ActivityType type) {
        return Arrays.stream(values()).filter(it -> it.activityType == type).findFirst().orElse(null);
    }

    public static GlobalBossType forNpcId(int npcId) {
        return Arrays.stream(values()).filter(it -> it.npcId == npcId).findFirst().orElse(null);
    }

    public enum SpawnZoneType {
        SAFE,
        WILDY,
        INSTANCED,
        RANDOM_AREA
    }
}
