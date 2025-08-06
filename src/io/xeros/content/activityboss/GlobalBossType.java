package io.xeros.content.activityboss;

import io.xeros.model.CombatType;
import io.xeros.model.entity.player.Position;
import java.util.Arrays;

/**
 * Enumeration of all global bosses that can be spawned by server wide activities.
 */
public enum GlobalBossType {
    AHRIM(1672, "Ahrim", ActivityType.CLUE_CASKET, 250,
            new Position(3222, 9800, 0), CombatType.MAGE),
    DHAROK(1673, "Dharok", ActivityType.UPGRADE_ITEM, 500,
            new Position(3225, 9802, 0), CombatType.MELEE),
    KARIL(1675, "Karil", ActivityType.VOTE_CLAIM, 100,
            new Position(3228, 9805, 0), CombatType.RANGE),
    GUTHAN(1674, "Guthan", ActivityType.FOE_BURN, 100_000_000,
            new Position(3231, 9803, 0), CombatType.MELEE),
    VERAC(1677, "Verac", ActivityType.KILLSTREAK_10, 1,
            new Position(3229, 9797, 0), CombatType.MELEE);

    private final int npcId;
    private final String name;
    private final ActivityType activityType;
    private final int threshold;
    private final Position spawnPosition;
    private final CombatType combatType;

    GlobalBossType(int npcId, String name, ActivityType activityType, int threshold,
                   Position spawnPosition, CombatType combatType) {
        this.npcId = npcId;
        this.name = name;
        this.activityType = activityType;
        this.threshold = threshold;
        this.spawnPosition = spawnPosition;
        this.combatType = combatType;
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

    public CombatType getCombatType() {
        return combatType;
    }

    public static GlobalBossType forActivity(ActivityType type) {
        return Arrays.stream(values()).filter(it -> it.activityType == type).findFirst().orElse(null);
    }

    public static GlobalBossType forNpcId(int npcId) {
        return Arrays.stream(values()).filter(it -> it.npcId == npcId).findFirst().orElse(null);
    }
}
