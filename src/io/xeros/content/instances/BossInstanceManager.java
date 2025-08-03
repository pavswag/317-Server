package io.xeros.content.instances;

import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.impl.LegacySoloPlayerInstance;
import io.xeros.model.Npcs;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.definitions.NpcStats;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

import java.util.Arrays;
import java.util.Map;
import io.xeros.util.Misc;

/**
 * Simple manager for personal boss instances. Each player that enters a zone
 * receives their own height level so spawned NPCs are only visible to them.
 */
public class BossInstanceManager {

    /** Mapping of players to their active instance. */
    private static final Map<Player, BossInstanceArea> INSTANCES = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * Simple instance type that cleans up the instance map when disposed so
     * height levels can be reused immediately.
     */
    public static class BossInstanceArea extends LegacySoloPlayerInstance {

        /** Player that owns this instance. */
        private final Player owner;
        /** Tier of this instance used for respawn times. */
        private final BossTier tier;

        BossInstanceArea(Player owner, BossTier tier, Boundary boundary) {
            super(InstanceConfiguration.CLOSE_ON_EMPTY, owner, boundary);
            this.owner = owner;
            this.tier = tier;
@@ -205,75 +207,132 @@ public class BossInstanceManager {
    /**
     * Enter a boss instance for the given tier. A new height level is reserved
     * for the player and the appropriate NPCs are spawned for them only.
     */
    public static void enter(Player player, BossTier tier) {
        if (INSTANCES.containsKey(player)) {
            player.sendMessage("You are already inside a boss instance.");
            return;
        }

        if (!player.getUnlockedBossTiers().contains(tier)) {
            player.sendMessage("You haven't unlocked this tier yet.");
            return;
        }

        // Small boundary around the player so instance cleanup works.
        Boundary bounds = new Boundary(player.getX() - 10, player.getY() - 10,
                player.getX() + 10, player.getY() + 10);

        BossInstanceArea instance = new BossInstanceArea(player, tier, bounds);
        INSTANCES.put(player, instance);

        instance.add(player);
        player.getPA().movePlayerUnconditionally(player.getX(), player.getY(), instance.getHeight());

        spawnInstanceGrid(player, tier, instance, false);
        BossInstanceOverlayManager.sendKillOverlay(player);
    }

    /**
     * Allows players to preview a boss tier without earning rewards or progress.
     */
    public static void preview(Player player, BossTier tier) {
        if (INSTANCES.containsKey(player)) {
            player.sendMessage("You are already inside an instance.");
            return;
        }

        Boundary bounds = new Boundary(player.getX() - 10, player.getY() - 10,
                player.getX() + 10, player.getY() + 10);

        BossInstanceArea instance = new BossInstanceArea(player, tier, bounds);
        INSTANCES.put(player, instance);

        instance.add(player);
        player.getPA().movePlayerUnconditionally(player.getX(), player.getY(), instance.getHeight());

        spawnInstanceGrid(player, tier, instance, true);
        player.setPreviewingBossInstance(true);
        BossInstanceOverlayManager.sendKillOverlay(player);
    }

    /**
     * Spawn NPCs in a grid around the player so the instance feels populated. NPCs are spaced
     * two tiles apart and up to twenty are spawned using the tier's NPC pool.
     */
    private static void spawnInstanceGrid(Player player, BossTier tier, BossInstanceArea instance, boolean preview) {
        int baseX = player.getX();
        int baseY = player.getY();
        int height = instance.getHeight();

        BossMob[] mobs = tier.getMobs();
        if (mobs.length == 0) {
            return;
        }

        int spawned = 0;
        for (int dx = -5; dx <= 5 && spawned < 20; dx += 2) {
            for (int dy = -5; dy <= 5 && spawned < 20; dy += 2) {
                BossMob mob = mobs[Misc.random(mobs.length - 1)];
                NPC npc = NPCSpawning.spawnNpc(player, mob.getNpcId(), baseX + dx, baseY + dy,
                        height, 0, 0, false, false,
                        NpcStats.builder()
                                .setHitpoints(mob.getHitpoints())
                                .setAttackLevel(mob.getAttack())
                                .setDefenceLevel(mob.getDefence())
                                .createNpcStats());
                if (npc != null) {
                    if (preview) {
                        npc.getBehaviour().setAggressive(false);
                        npc.getCombatDefinition().setAggressive(false);
                        npc.getBehaviour().setRespawn(false);
                    } else {
                        npc.getBehaviour().setRespawn(true);
                        npc.getBehaviour().setRespawnWhenPlayerOwned(true);
                    }
                    instance.add(npc);
                    spawned++;
                }
            }
        }
    }

    /**
     * Adds the given tier to the player's unlocked set if it hasn't been added before.
     *
     * @return {@code true} if this is the first time the player has unlocked the tier
     */
    public static boolean isFirstTierUnlock(Player player, BossTier tier) {
        return player.getUnlockedBossTiers().add(tier);
    }

    /**
     * Removes the player from their boss instance, clearing overlays and
     * freeing the height level so it can be reused.
     */
    public static void leave(Player player) {
        BossInstanceOverlayManager.clear(player);
        BossInstanceArea area = INSTANCES.remove(player);
        if (area != null) {
            area.dispose();
        }
        player.setPreviewingBossInstance(false);
    }

    /**
     * Returns a safe, non-null display name for the given tier. The format is
     * "Tier X – Zone Name". If any information is missing, a fallback label is
     * used so that option strings are never empty.
     */
    public static String getTierDisplayNameSafe(BossTier tier) {
        return getTierDisplayNameSafe(tier, null);
    }

    /**
     * Returns a safe label for the tier, colour coded based on whether the player has it
     * unlocked. When a player is provided, their kill progress is included to show lock state.
     */
    public static String getTierDisplayNameSafe(BossTier tier, Player player) {
        if (tier == null) {
            return "@red@Unavailable";
        }
        String zone = tier.getZoneName();
        if (zone == null || zone.trim().isEmpty()) {
            Misc.println("BossInstanceManager warning: missing zone name for " + tier);
            zone = "Unknown";
        }
        String base = "Tier " + (tier.ordinal() + 1) + " – " + zone;

        if (player == null) {
            return base;
        }

        boolean unlocked = player.getUnlockedBossTiers().contains(tier);
        BossTier prev = Arrays.stream(BossTier.values()).filter(t -> t.getNextTier() == tier).findFirst().orElse(null);
        int progress = prev != null ? player.getTierKillCounts().getOrDefault(prev, 0) : 0;
        int required = prev != null ? prev.getRequiredKillCountToUnlockNext() : tier.getKillRequirement();

        if (unlocked) {
            return "@gre@" + base;
        }
        if (required > 0 && progress >= required) {
            return "@yel@" + base + " - Preview";
        }
        return "@red@Locked: " + base + " (" + progress + "/" + required + ")";
    }


    public static BossInstanceArea get(Player player) {
        return INSTANCES.get(player);
    }
}