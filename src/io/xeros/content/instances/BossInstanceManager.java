package io.xeros.content.instances;

import io.xeros.content.instances.impl.LegacySoloPlayerInstance;
import io.xeros.model.Npcs;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.definitions.NpcStats;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple manager for personal boss instances. Each player that enters a zone
 * receives their own height level so spawned NPCs are only visible to them.
 */
public class BossInstanceManager {

    /** Mapping of players to their active instance. */
    private static final Map<Player, BossInstanceArea> INSTANCES = new ConcurrentHashMap<>();

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
        }

        @Override
        public void onDispose() {
            INSTANCES.remove(owner);
        }

        public BossTier getTier() {
            return tier;
        }
    }

    /** Description for each NPC spawned in a tier. */
    public static class BossMob {
        private final int npcId;
        private final int hitpoints;
        private final int attack;
        private final int defence;

        public BossMob(int npcId, int hitpoints, int attack, int defence) {
            this.npcId = npcId;
            this.hitpoints = hitpoints;
            this.attack = attack;
            this.defence = defence;
        }

        public int getNpcId() {
            return npcId;
        }

        public int getHitpoints() {
            return hitpoints;
        }

        public int getAttack() {
            return attack;
        }

        public int getDefence() {
            return defence;
        }
    }

    /**
     * Difficulty tiers for bosses. Each tier specifies a kill requirement based
     * on the player's {@link io.xeros.content.combat.stats.NPCDeathTracker}
     * count for a particular NPC. When unlocking a tier, the player's kill count
     * for {@link #getKillNpcId()} must meet {@link #getKillRequirement()}.
     */
    public enum BossTier {
        TIER1("Training Grounds", 0, 0, -1, 5, Npcs.COW,
                new BossMob[]{new BossMob(Npcs.COW, 10, 1, 1)}),
        TIER2("Goblin Camp", 25, 10_000, -1, 10, Npcs.GOBLIN,
                new BossMob[]{new BossMob(Npcs.GOBLIN, 15, 5, 5)}),
        TIER3("Giants' Den", 75, 100_000, -1, 20, Npcs.HILL_GIANT,
                new BossMob[]{new BossMob(Npcs.HILL_GIANT, 35, 20, 20)}),
        TIER4("Moss Cave", 150, 250_000, -1, 25, Npcs.MOSS_GIANT,
                new BossMob[]{new BossMob(Npcs.MOSS_GIANT, 60, 40, 40)}),
        TIER5("Fire Pit", 250, 500_000, -1, 30, Npcs.FIRE_GIANT,
                new BossMob[]{new BossMob(Npcs.FIRE_GIANT, 80, 60, 60)}),
        TIER6("Green Dragons", 350, 750_000, -1, 35, Npcs.GREEN_DRAGON,
                new BossMob[]{new BossMob(Npcs.GREEN_DRAGON, 120, 90, 90)}),
        TIER7("Red Dragons", 500, 1_000_000, -1, 40, Npcs.RED_DRAGON,
                new BossMob[]{new BossMob(Npcs.RED_DRAGON, 150, 110, 110)}),
        TIER8("Black Dragons", 650, 2_000_000, -1, 45, Npcs.BLACK_DRAGON,
                new BossMob[]{new BossMob(Npcs.BLACK_DRAGON, 180, 130, 130)}),
        TIER9("Demon Domain", 800, 3_000_000, -1, 50, Npcs.BLACK_DEMON,
                new BossMob[]{new BossMob(Npcs.BLACK_DEMON, 200, 150, 150)}),
        TIER10("Dragon King", 1000, 5_000_000, 11286, 60, Npcs.KING_BLACK_DRAGON,
                new BossMob[]{new BossMob(Npcs.KING_BLACK_DRAGON, 250, 180, 180)});

        static {
            TIER1.requiredKillCountToUnlockNext = 25;
            TIER1.nextTier = TIER2;
            TIER2.requiredKillCountToUnlockNext = 50;
            TIER2.nextTier = TIER3;
            TIER3.requiredKillCountToUnlockNext = 75;
            TIER3.nextTier = TIER4;
            TIER4.requiredKillCountToUnlockNext = 100;
            TIER4.nextTier = TIER5;
            TIER5.requiredKillCountToUnlockNext = 150;
            TIER5.nextTier = TIER6;
            TIER6.requiredKillCountToUnlockNext = 200;
            TIER6.nextTier = TIER7;
            TIER7.requiredKillCountToUnlockNext = 250;
            TIER7.nextTier = TIER8;
            TIER8.requiredKillCountToUnlockNext = 300;
            TIER8.nextTier = TIER9;
            TIER9.requiredKillCountToUnlockNext = 400;
            TIER9.nextTier = TIER10;
            TIER10.requiredKillCountToUnlockNext = 0;
            TIER10.nextTier = null;
        }

        private final String zoneName;
        /** Kill requirement to unlock this tier. */
        private final int killRequirement;
        /** NPC id whose kill count is checked for this tier. */
        private final int killNpcId;
        /** GP cost to unlock the tier. */
        private final int gpCost;
        /** Optional item requirement (-1 if none). */
        private final int itemRequirement;
        private final int respawnTime;
        private final BossMob[] mobs;
        /** Kill count required within this tier to unlock the next one. */
        private int requiredKillCountToUnlockNext;
        /** The next tier unlocked after meeting the kill requirement. */
        private BossTier nextTier;

        BossTier(String zoneName, int killRequirement, int gpCost, int itemRequirement, int respawnTime, int killNpcId, BossMob[] mobs) {
            this.zoneName = zoneName;
            this.killRequirement = killRequirement;
            this.gpCost = gpCost;
            this.itemRequirement = itemRequirement;
            this.respawnTime = respawnTime;
            this.killNpcId = killNpcId;
            this.mobs = mobs;
        }

        public String getZoneName() {
            return zoneName;
        }

        public int getKillRequirement() {
            return killRequirement;
        }

        public int getKillNpcId() {
            return killNpcId;
        }

        /**
         * Returns the player's kill count for the NPC tied to this tier.
         */
        public int getKillCount(Player player) {
            String name = NpcDef.forId(killNpcId).getName();
            return player.getNpcDeathTracker().getKc(name);
        }

        public int getGpCost() {
            return gpCost;
        }

        public int getItemRequirement() {
            return itemRequirement;
        }

        public int getRespawnTime() {
            return respawnTime;
        }

        public BossMob[] getMobs() {
            return mobs;
        }

        public BossTier getNextTier() {
            return nextTier;
        }

        public int getRequiredKillCountToUnlockNext() {
            return requiredKillCountToUnlockNext;
        }

    }

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
