package io.xeros.content.activityboss;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.entity.player.broadcasts.Broadcast;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Tracks server wide player activity to spawn global bosses when
 * specific milestones are reached.
 */
public class GlobalBossActivityManager {

    /** Total progress for each activity type. */
    private static final Map<ActivityType, Integer> totals = new EnumMap<>(ActivityType.class);
    /** Currently active bosses. */
    private static final Map<GlobalBossType, GlobalBossSpawnData> activeBosses = new EnumMap<>(GlobalBossType.class);
    /** Last kill times used for cooldowns. */
    private static final Map<GlobalBossType, Long> lastKillTimes = new EnumMap<>(GlobalBossType.class);
    private static final long COOLDOWN = TimeUnit.MINUTES.toMillis(15);

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
        GlobalBossType data = GlobalBossType.forActivity(type);
        if (data == null) {
            return;
        }
        if (isActive(data) || isOnCooldown(data)) {
            return;
        }
        int newTotal = totals.getOrDefault(type, 0) + amount;
        GlobalBossAnnouncer.announceProgress(data, newTotal);
        if (newTotal >= data.getThreshold()) {
            totals.put(type, 0);
            spawn(data);
        } else {
            totals.put(type, newTotal);
        }
    }

    private static void spawn(GlobalBossType data) {
        Position spawn = GlobalBossSpawnZoneManager.getAvailableSpawnLocation(data);
        if (spawn == null) {
            return;
        }
        NPC npc = NPCSpawning.spawnNpc(data.getNpcId(), spawn.getX(),
                spawn.getY(), spawn.getHeight(), 1, 0);
        if (npc == null) {
            return;
        }
        npc.getHealth().setMaximumHealth(5000);
        npc.getHealth().setCurrentHealth(5000);
        npc.getBehaviour().setAggressive(true);
        npc.getCombatDefinition().setAggressive(true);
        npc.setAttackType(data.getCombatType());
        new Broadcast("@red@A dark presence stirs... " + data.getName() + " has emerged!")
                .addTeleport(spawn)
                .copyMessageToChatbox()
                .submit();
        activeBosses.put(data, new GlobalBossSpawnData(System.currentTimeMillis(), null, spawn, true));
    }

    public static void onBossDeath(NPC npc, Player killer) {
        GlobalBossType type = GlobalBossType.forNpcId(npc.getNpcId());
        if (type == null || !activeBosses.containsKey(type)) {
            return;
        }
        GlobalBossDropHandler.rewardParticipants(npc);
        new Broadcast("@red@" + type.getName() + " has been defeated!")
                .copyMessageToChatbox()
                .submit();
        activeBosses.remove(type);
        lastKillTimes.put(type, System.currentTimeMillis());
    }

    public static boolean isActive(GlobalBossType type) {
        return activeBosses.containsKey(type);
    }

    public static Map<GlobalBossType, GlobalBossSpawnData> getActiveBosses() {
        return activeBosses;
    }

    public static int getTotal(ActivityType type) {
        return totals.getOrDefault(type, 0);
    }

    public static long getCooldownRemaining(GlobalBossType type) {
        long last = lastKillTimes.getOrDefault(type, 0L);
        long remaining = last + COOLDOWN - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    private static boolean isOnCooldown(GlobalBossType type) {
        return getCooldownRemaining(type) > 0;
    }

    static boolean isPositionOccupied(Position pos) {
        for (GlobalBossSpawnData data : activeBosses.values()) {
            if (data.getSpawnPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    public static synchronized void forceSpawn(GlobalBossType type) {
        spawn(type);
    }

    public static void setCooldown(GlobalBossType type, long millis) {
        lastKillTimes.put(type, System.currentTimeMillis() - COOLDOWN + millis);
    }
}
