package io.xeros.content.instances;

import io.xeros.content.instances.impl.LegacySoloPlayerInstance;
import io.xeros.model.Npcs;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.definitions.NpcStats;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages personal boss instances. Each player receives a unique height level
 * so NPCs spawned for them are not visible to others.
 */
public class BossInstanceManager {

    /** Tracks active instances keyed by owning player. */
    private static final Map<Player, BossInstanceArea> INSTANCES = new ConcurrentHashMap<>();

    /**
     * Instance wrapper that automatically removes itself from the instance map
     * when disposed so height levels are immediately reusable.
     */
    public static class BossInstanceArea extends LegacySoloPlayerInstance {
        private final Player owner;
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

        public boolean isWithinAoeZone(Position pos) {
            return tier.getZoneBoundary().inside(pos);
        }
    }

    /** Description for each mob that can spawn in a tier. */
    public static class BossMob {
        private final int npcId;
        private final int hitpoints;
        private final int attack;
        private final int defence;
        private final int count;

        public BossMob(int npcId, int hitpoints, int attack, int defence, int count) {
            this.npcId = npcId;
            this.hitpoints = hitpoints;
            this.attack = attack;
            this.defence = defence;
            this.count = count;
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

        public int getCount() {
            return count;
        }
    }

    /**
     * Boss instance difficulty tiers. Each tier defines kill requirements and
     * the NPCs that can spawn.
     */
    public enum BossTier {
        TIER1("Training Grounds", new Boundary(2270, 4758, 2295, 4785), new Position(2282, 4770), 0, 0, -1, 5, Npcs.COW,
                new BossMob[]{new BossMob(Npcs.COW, 10, 1, 1, 5)}),
        TIER2("Goblin Camp", new Boundary(2270, 4758, 2295, 4785), new Position(2282, 4770), 25, 10_000, -1, 10, Npcs.GOBLIN,
                new BossMob[]{new BossMob(Npcs.GOBLIN, 15, 5, 5, 5)}),
        TIER3("Giants' Den", new Boundary(2270, 4758, 2295, 4785), new Position(2282, 4770), 75, 100_000, -1, 20, Npcs.HILL_GIANT,
                new BossMob[]{new BossMob(Npcs.HILL_GIANT, 35, 20, 20, 6)}),
        TIER4("Moss Cave", new Boundary(2270, 4758, 2295, 4785), new Position(2282, 4770), 150, 250_000, -1, 25, Npcs.MOSS_GIANT,
                new BossMob[]{new BossMob(Npcs.MOSS_GIANT, 60, 40, 40, 6)}),
        TIER5("Fire Pit", new Boundary(2270, 4758, 2295, 4785), new Position(2282, 4770), 250, 500_000, -1, 30, Npcs.FIRE_GIANT,
                new BossMob[]{new BossMob(Npcs.FIRE_GIANT, 80, 60, 60, 7)}),
        TIER6("Green Dragons", new Boundary(2270, 4758, 2295, 4785), new Position(2282, 4770), 350, 750_000, -1, 35, Npcs.GREEN_DRAGON,
                new BossMob[]{new BossMob(Npcs.GREEN_DRAGON, 120, 90, 90, 7)}),
        TIER7("Red Dragons", new Boundary(2270, 4758, 2295, 4785), new Position(2282, 4770), 500, 1_000_000, -1, 40, Npcs.RED_DRAGON,
                new BossMob[]{new BossMob(Npcs.RED_DRAGON, 150, 110, 110, 8)}),
        TIER8("Black Dragons", new Boundary(2270, 4758, 2295, 4785), new Position(2282, 4770), 650, 2_000_000, -1, 45, Npcs.BLACK_DRAGON,
                new BossMob[]{new BossMob(Npcs.BLACK_DRAGON, 180, 130, 130, 8)}),
        TIER9("Demon Domain", new Boundary(2270, 4758, 2295, 4785), new Position(2282, 4770), 800, 3_000_000, -1, 50, Npcs.BLACK_DEMON,
                new BossMob[]{new BossMob(Npcs.BLACK_DEMON, 200, 150, 150, 9)}),
        TIER10("Dragon King", new Boundary(2270, 4758, 2295, 4785), new Position(2282, 4770), 1_000, 5_000_000, 11286, 60, Npcs.KING_BLACK_DRAGON,
                new BossMob[]{new BossMob(Npcs.KING_BLACK_DRAGON, 250, 180, 180, 1)});

        static {
            TIER1.requiredKillCountToUnlockNext = 25;  TIER1.nextTier = TIER2;
            TIER2.requiredKillCountToUnlockNext = 50;  TIER2.nextTier = TIER3;
            TIER3.requiredKillCountToUnlockNext = 75;  TIER3.nextTier = TIER4;
            TIER4.requiredKillCountToUnlockNext = 100; TIER4.nextTier = TIER5;
            TIER5.requiredKillCountToUnlockNext = 150; TIER5.nextTier = TIER6;
            TIER6.requiredKillCountToUnlockNext = 200; TIER6.nextTier = TIER7;
            TIER7.requiredKillCountToUnlockNext = 250; TIER7.nextTier = TIER8;
            TIER8.requiredKillCountToUnlockNext = 300; TIER8.nextTier = TIER9;
            TIER9.requiredKillCountToUnlockNext = 400; TIER9.nextTier = TIER10;
            TIER10.requiredKillCountToUnlockNext = 0;  TIER10.nextTier = null;
        }

        private final String zoneName;
        private final Boundary zoneBoundary;
        private final Position spawnTile;
        private final int killRequirement;
        private final int gpCost;
        private final int itemRequirement;
        private final int respawnTime;
        private final int bossNpcId;
        private final BossMob[] mobs;
        private int requiredKillCountToUnlockNext;
        private BossTier nextTier;

        BossTier(String zoneName, Boundary zoneBoundary, Position spawnTile, int killRequirement, int gpCost, int itemRequirement,
                 int respawnTime, int bossNpcId, BossMob[] mobs) {
            this.zoneName = zoneName;
            this.zoneBoundary = zoneBoundary;
            this.spawnTile = spawnTile;
            this.killRequirement = killRequirement;
            this.gpCost = gpCost;
            this.itemRequirement = itemRequirement;
            this.respawnTime = respawnTime;
            this.bossNpcId = bossNpcId;
            this.mobs = mobs;
        }

        public String getZoneName() {
            return zoneName;
        }

        public Boundary getZoneBoundary() {
            return zoneBoundary;
        }

        public Position getSpawnTile() {
            return spawnTile;
        }

        public int getKillRequirement() {
            return killRequirement;
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

        public int getBossNpcId() {
            return bossNpcId;
        }

        public BossMob[] getMobs() {
            return mobs;
        }

        public int getRequiredKillCountToUnlockNext() {
            return requiredKillCountToUnlockNext;
        }

        public BossTier getNextTier() {
            return nextTier;
        }

        /** Returns the player's kill count for the NPC associated with this tier. */
        public int getKillCount(Player player) {
            String name = NpcDef.forId(bossNpcId).getName();
            return player.getNpcDeathTracker().getKc(name);
        }
    }

    /**
     * Enters a boss instance for the given tier and spawns NPCs for that player
     * only.
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

        Boundary bounds = tier.getZoneBoundary();
        BossInstanceArea area = new BossInstanceArea(player, tier, bounds);
        INSTANCES.put(player, area);

        area.add(player);
        Position spawn = tier.getSpawnTile();
        player.getPA().movePlayerUnconditionally(spawn.getX(), spawn.getY(), area.getHeight());

        spawnInstanceGrid(player, tier, area, false);
        BossInstanceOverlayManager.sendKillOverlay(player);
    }

    /**
     * Allows a player to preview a tier with harmless NPCs that do not award
     * progress or drops.
     */
    public static void preview(Player player, BossTier tier) {
        if (INSTANCES.containsKey(player)) {
            player.sendMessage("You are already inside an instance.");
            return;
        }

        Boundary bounds = tier.getZoneBoundary();
        BossInstanceArea area = new BossInstanceArea(player, tier, bounds);
        INSTANCES.put(player, area);

        area.add(player);
        Position spawn = tier.getSpawnTile();
        player.getPA().movePlayerUnconditionally(spawn.getX(), spawn.getY(), area.getHeight());

        spawnInstanceGrid(player, tier, area, true);
        player.setPreviewingBossInstance(true);
        BossInstanceOverlayManager.sendKillOverlay(player);
    }

    /**
     * Spawns NPCs randomly inside the tier's boundary so the instance feels populated.
     * Each {@link BossMob} defines how many creatures should appear for the tier.
     */
    private static void spawnInstanceGrid(Player player, BossTier tier, BossInstanceArea area, boolean preview) {
        Boundary bounds = tier.getZoneBoundary();
        int height = area.getHeight();

        BossMob[] mobs = tier.getMobs();
        if (mobs.length == 0) {
            return;
        }

        for (BossMob mob : mobs) {
            for (int i = 0; i < mob.getCount(); i++) {
                int x = Misc.random(bounds.getMinimumX() + 1, bounds.getMaximumX() - 1);
                int y = Misc.random(bounds.getMinimumY() + 1, bounds.getMaximumY() - 1);
                NPC npc = NPCSpawning.spawnNpc(player, mob.getNpcId(), x, y,
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
                    area.add(npc);
                }
            }
        }
    }

    /** Adds the tier to the player's unlocked set and returns {@code true} if it was newly unlocked. */
    public static boolean isFirstTierUnlock(Player player, BossTier tier) {
        return player.getUnlockedBossTiers().add(tier);
    }

    /** Removes the player from their boss instance and clears overlay text. */
    public static void leave(Player player) {
        BossInstanceOverlayManager.clear(player);
        BossInstanceArea area = INSTANCES.remove(player);
        if (area != null) {
            area.dispose();
        }
        player.setPreviewingBossInstance(false);
    }

    /**
     * Returns a non-null display name for the tier. When a player is supplied the
     * string is colour coded based on unlock state and shows progress.
     */
    public static String getTierDisplayNameSafe(BossTier tier) {
        return getTierDisplayNameSafe(tier, null);
    }

    public static String getTierDisplayNameSafe(BossTier tier, Player player) {
        if (tier == null) {
            return "@red@Unavailable";
        }
        String zone = tier.getZoneName();
        if (zone == null || zone.trim().isEmpty()) {
            Misc.println("BossInstanceManager warning: missing zone name for " + tier);
            zone = "Unknown";
        }

        // Strip non ASCII characters and normalise special dashes so the
        // dialogue renders consistently across clients.
        zone = zone.replaceAll("[^\\p{ASCII}]", "");
        zone = zone.replaceAll("[–—•]", "-");

        String base = "Tier " + (tier.ordinal() + 1) + " - " + zone;

        // Hard cap the length to avoid invisible options.
        if (base.length() > 50) {
            base = base.substring(0, 47) + "...";
        }

        Misc.println("AOE Dialogue: " + base);

        if (player == null) {
            return base;
        }
        boolean unlocked = player.getUnlockedBossTiers().contains(tier);
        BossTier prev = Arrays.stream(BossTier.values())
                .filter(t -> t.getNextTier() == tier)
                .findFirst()
                .orElse(null);
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

    /** Returns the active instance for a player or {@code null} if none. */
    public static BossInstanceArea get(Player player) {
        return INSTANCES.get(player);
    }
}