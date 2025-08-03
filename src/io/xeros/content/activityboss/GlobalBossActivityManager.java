package io.xeros.content.activityboss;

import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import java.util.EnumMap;
import java.util.Map;

/**
 * Tracks server wide player activity to spawn global bosses when
 * specific milestones are reached.
 */
public class GlobalBossActivityManager {

    /** Total progress for each activity type. */
    private static final Map<ActivityType, Integer> totals = new EnumMap<>(ActivityType.class);

    private GlobalBossActivityManager() {
    }

    /**
     * Records activity and spawns the corresponding boss if the
     * threshold is met.
     *
     * @param type   type of activity
     * @param amount amount to add toward the threshold
     */
    public static synchronized void record(ActivityType type, int amount) {
        ActivityBossData data = ActivityBossData.forType(type);
        if (data == null) {
            return;
        }
        int newTotal = totals.getOrDefault(type, 0) + amount;
        if (newTotal >= data.getThreshold()) {
            totals.put(type, 0);
            spawn(data);
        } else {
            totals.put(type, newTotal);
        }
    }

    private static void spawn(ActivityBossData data) {
        NPCSpawning.spawnNpc(data.getNpcId(), data.getSpawnPosition().getX(),
                data.getSpawnPosition().getY(), data.getSpawnPosition().getHeight(), 1, 0);
        new Broadcast(data.getSpawnMessage())
                .addTeleport(data.getSpawnPosition())
                .copyMessageToChatbox()
                .submit();
    }
}
