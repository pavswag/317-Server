package io.xeros.content.instances;

import io.xeros.content.instances.impl.LegacySoloPlayerInstance;
import io.xeros.model.Npcs;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.definitions.NpcStats;
import io.xeros.model.definitions.NpcStatsBuilder;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages personal boss instances. Each player receives a unique height level
 * so NPCs spawned for them are not visible to others.
 */
public class BossInstanceManager {

    /** Tracks active instances keyed by owning player. */
    private static final Map<Player, BossInstanceArea> INSTANCES = new ConcurrentHashMap<>();

    /** Minimum distance an NPC must be from the player spawn to still
     *  automatically aggro. This can be tweaked for balancing. */
    private static final int MINIMUM_AGGRO_RANGE = 8;

    /**
     * Instance wrapper that automatically removes itself from the instance map
     * when disposed so height levels are immediately reusable.
     */
    public static class BossInstanceArea extends LegacySoloPlayerInstance {
        private final Player owner;
        private final BossTier tier;
        private final io.xeros.content.instances.hazard.EnvironmentalHazardScheduler hazards;
        private final boolean dynamicWaveScaling;
        /** Tracks consecutive kills per player for killstreak rewards. */
        private final Map<Player, Integer> killStreaks = new HashMap<>();
        /** Tracks consecutive AoE kills for bonus rewards. */
        private final Map<Player, Integer> aoeStreaks = new HashMap<>();

        BossInstanceArea(Player owner, BossTier tier, Boundary boundary) {
            super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, owner, boundary);
            this.owner = owner;
            this.tier = tier;
            this.hazards = new io.xeros.content.instances.hazard.EnvironmentalHazardScheduler(this);
            this.dynamicWaveScaling = tier.isDynamicWaveScaling();
        }

        @Override
        public void onDispose() {
            hazards.stop();
            INSTANCES.remove(owner);
        }

        public void startHazards() {
            hazards.start();
        }

        public void startDynamicSpawns() {
            if (!dynamicWaveScaling) {
                return;
            }
            CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
                int ticks;
                @Override
                public void execute(CycleEventContainer container) {
                    ticks++;
                    if (ticks % 50 == 0) {
                        spawnInstanceGrid(owner, tier, BossInstanceArea.this, false);
                    }
                    if (InstanceMutatorManager.getGlobalDanger() > 90) {
                        spawnInstanceGrid(owner, tier, BossInstanceArea.this, false);
                    }
                }
            }, 1);
        }

        public BossTier getTier() {
            return tier;
        }

        public boolean isWithinAoeZone(Position pos) {
            Boundary boundary = tier.getZoneBoundary();
            return pos.getHeight() == getHeight()
                    && pos.getX() >= boundary.getMinimumX() && pos.getX() <= boundary.getMaximumX()
                    && pos.getY() >= boundary.getMinimumY() && pos.getY() <= boundary.getMaximumY();
        }

        public io.xeros.content.instances.hazard.EnvironmentalHazardScheduler getHazardScheduler() {
            return hazards;
        }

        /** Records a kill for the player and distributes killstreak rewards. */
        public void recordKill(Player player) {
            int streak = killStreaks.merge(player, 1, Integer::sum);
            if (streak % 10 == 0) {
                TierRewardManager.rewardKillstreak(player, tier, streak);
            }
        }

        /** Records an AoE kill for bonus streak rewards. */
        public void recordAoeKill(Player player) {
            int streak = aoeStreaks.merge(player, 1, Integer::sum);
            if (streak % 5 == 0) {
                TierRewardManager.rewardAoeKillstreak(player, tier, streak);
            }
        }

        /** Clears any tracked streaks for the supplied player. */
        public void resetStreak(Player player) {
            killStreaks.remove(player);
            aoeStreaks.remove(player);
        }
        /** Clears only the AoE streak for cases where a normal kill occurs. */
        public void resetAoeStreak(Player player) {
            aoeStreaks.remove(player);
        }
    }

    /** Description for each mob that can spawn in a tier. */
    public static class BossMob {
        private final int npcId;
        private final int hitpoints;
        private final int attack;
        private final int defence;
        private final int count;
        private final List<String> specialAttacks;

        public BossMob(int npcId, int hitpoints, int attack, int defence, int count, List<String> specialAttacks) {
            this.npcId = npcId;
            this.hitpoints = hitpoints;
            this.attack = attack;
            this.defence = defence;
            this.count = count;
            this.specialAttacks = specialAttacks;
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

        public List<String> getSpecialAttacks() {
            return specialAttacks;
        }
    }

    /**
     * Boss instance difficulty tiers. Each tier defines kill requirements and
     * the NPCs that can spawn.
     */
    public enum BossTier {
        // drops/unicow.yml and drops/imp.yml supply beginner hides and beads.
        TIER1("Unicow Pasture", new Boundary(2276, 4764, 2288, 4776), new Position(2282, 4770), 0, 0, -1, 5, Npcs.UNICOW, 8,
                new BossMob[]{
                        new BossMob(Npcs.UNICOW, 25, 10, 5, 8, List.of()),
                        new BossMob(Npcs.IMP, 10, 5, 3, 6, List.of())
                },
                new TierCombatProfile(1.0,1.0,1.0,1.0,1.0,List.of()), 1.1, 1.0),
        // drops/aberrant_spectre.yml and drops/banshee.yml add herb and seed drops for early profit.
        TIER2("Spectre Crypt", new Boundary(2274, 4762, 2290, 4778), new Position(2282, 4770), 25, 10_000, -1, 10, Npcs.ABERRANT_SPECTRE, 10,
                new BossMob[]{
                        new BossMob(Npcs.ABERRANT_SPECTRE, 60, 45, 30, 6, List.of()),
                        new BossMob(Npcs.BANSHEE, 20, 15, 10, 4, List.of()),
                        new BossMob(Npcs.TWISTED_BANSHEE, 25, 20, 15, 2, List.of())
                },
                new TierCombatProfile(1.1,1.05,1.05,1.05,1.0,List.of()), 1.1, 1.0),
        // drops/abyssal_demon.yml and drops/bloodveld.yml showcase whip and blood rune loot.
        TIER3("Abyssal Pit", new Boundary(2272, 4760, 2292, 4780), new Position(2282, 4770), 75, 100_000, -1, 20, Npcs.ABYSSAL_DEMON, 8,
                new BossMob[]{
                        new BossMob(Npcs.ABYSSAL_DEMON, 150, 115, 110, 4, List.of()),
                        new BossMob(Npcs.BLOODVELD, 120, 90, 85, 4, List.of()),
                        new BossMob(Npcs.NECHRYAEL, 170, 120, 110, 2, List.of())
                },
                new TierCombatProfile(1.2,1.1,1.1,1.1,1.05,List.of()), 1.1, 1.0),
        // drops/cerberus.yml pairs the hound of hell with its lesser cousins for a fiery challenge.
        TIER4("Cerberus Pits", new Boundary(2270, 4758, 2294, 4784), new Position(2282, 4770), 150, 250_000, -1, 25, Npcs.CERBERUS, 6,
                new BossMob[]{
                        new BossMob(Npcs.CERBERUS, 600, 300, 250, 2, List.of()),
                        new BossMob(Npcs.HELLHOUND, 150, 120, 110, 4, List.of())
                },
                new TierCombatProfile(1.3,1.15,1.15,1.15,1.1,List.of()), 1.1, 1.0),
        // drops/callisto.yml, drops/vetion.yml and drops/venenatis.yml bring wilderness bosses together for dangerous rewards.
        TIER5("Wilderness Lairs", new Boundary(2268, 4756, 2296, 4786), new Position(2282, 4770), 250, 500_000, -1, 30, Npcs.CALLISTO, 4,
                new BossMob[]{
                        new BossMob(Npcs.CALLISTO, 400, 280, 260, 1, List.of()),
                        new BossMob(Npcs.VETION, 450, 300, 280, 1, List.of()),
                        new BossMob(Npcs.VENENATIS, 330, 250, 240, 1, List.of())
                },
                new TierCombatProfile(1.4,1.2,1.2,1.2,1.15,List.of()), 1.0, 1.0),
        // drops/abyssal_sire.yml leverages abyssal demons and gorillas for mid-tier bossing.
        TIER6("Sire Den", new Boundary(2266, 4754, 2298, 4788), new Position(2282, 4770), 350, 750_000, -1, 35, Npcs.ABYSSAL_SIRE, 3,
                new BossMob[]{
                        new BossMob(Npcs.ABYSSAL_SIRE, 400, 300, 250, 1, List.of()),
                        new BossMob(Npcs.ABYSSAL_DEMON, 220, 160, 160, 2, List.of()),
                        new BossMob(Npcs.DEMONIC_GORILLA, 210, 170, 170, 1, List.of())
                },
                new TierCombatProfile(1.5,1.25,1.25,1.25,1.2,List.of()), 1.0, 1.0),
        // drops/commander_zilyana.yml and drops/kril_tsutsaroth.yml pit rival gods in a cramped antechamber.
        TIER7("Godwars Antechamber", new Boundary(2264, 4752, 2300, 4790), new Position(2282, 4770), 500, 1_000_000, -1, 40, Npcs.COMMANDER_ZILYANA, 3,
                new BossMob[]{
                        new BossMob(Npcs.COMMANDER_ZILYANA, 300, 250, 220, 1, List.of()),
                        new BossMob(Npcs.KRIL_TSUTSAROTH, 350, 270, 250, 1, List.of())
                },
                new TierCombatProfile(1.6,1.3,1.3,1.3,1.25,List.of()), 0.9, 1.2),
        // drops/general_graardor.yml and drops/skotizo.yml supply high-end uniques like Bandos armour and dark totems.
        TIER8("Bandos Stronghold", new Boundary(2262, 4750, 2302, 4792), new Position(2282, 4770), 650, 2_000_000, -1, 45, Npcs.GENERAL_GRAARDOR, 2,
                new BossMob[]{
                        new BossMob(Npcs.GENERAL_GRAARDOR, 350, 300, 300, 1, List.of()),
                        new BossMob(Npcs.SKOTIZO, 400, 320, 300, 1, List.of())
                },
                new TierCombatProfile(1.7,1.35,1.35,1.35,1.3,List.of()), 0.9, 1.2),
        // drops/alchemical_hydra.yml and drops/vorkath.yml combine two lucrative dragon bosses.
        TIER9("Dragon's Apex", new Boundary(2260, 4748, 2304, 4794), new Position(2282, 4770), 800, 3_000_000, -1, 50, Npcs.ALCHEMICAL_HYDRA, 2,
                new BossMob[]{
                        new BossMob(Npcs.ALCHEMICAL_HYDRA, 450, 300, 300, 1, List.of()),
                        new BossMob(Npcs.VORKATH, 750, 350, 350, 1, List.of())
                },
                new TierCombatProfile(1.8,1.4,1.4,1.4,1.35,List.of()), 0.9, 1.2),
        // drops/the_nightmare.yml and drops/zulrah.yml headline the final tier with coveted uniques.
        TIER10("Nightmare Lair", new Boundary(2258, 4746, 2306, 4796), new Position(2282, 4770), 1_000, 5_000_000, 11286, 60, Npcs.THE_NIGHTMARE, 2,
                new BossMob[]{
                        new BossMob(Npcs.THE_NIGHTMARE, 1_000, 350, 350, 1, List.of("infernal_slam")),
                        new BossMob(Npcs.ZULRAH, 500, 300, 300, 1, List.of())
                },
                new TierCombatProfile(2.0,1.5,1.5,1.45,1.4,List.of("infernal_slam")), 0.9, 1.2);

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
        private final int desiredNpcDensity;
        private final BossMob[] mobs;
        private final TierCombatProfile combatProfile;
        private final double aoeDamageMultiplier;
        private final double aoeCooldownMultiplier;
        private final boolean dynamicWaveScaling;
        private int requiredKillCountToUnlockNext;
        private BossTier nextTier;

        BossTier(String zoneName, Boundary zoneBoundary, Position spawnTile, int killRequirement, int gpCost, int itemRequirement,
                 int respawnTime, int bossNpcId, int desiredNpcDensity, BossMob[] mobs, TierCombatProfile combatProfile,
                 double aoeDamageMultiplier, double aoeCooldownMultiplier) {
            this(zoneName, zoneBoundary, spawnTile, killRequirement, gpCost, itemRequirement, respawnTime, bossNpcId,
                    desiredNpcDensity, mobs, combatProfile, aoeDamageMultiplier, aoeCooldownMultiplier, false);
        }

        BossTier(String zoneName, Boundary zoneBoundary, Position spawnTile, int killRequirement, int gpCost, int itemRequirement,
                 int respawnTime, int bossNpcId, int desiredNpcDensity, BossMob[] mobs, TierCombatProfile combatProfile,
                 double aoeDamageMultiplier, double aoeCooldownMultiplier, boolean dynamicWaveScaling) {
            this.zoneName = zoneName;
            this.zoneBoundary = zoneBoundary;
            this.spawnTile = spawnTile;
            this.killRequirement = killRequirement;
            this.gpCost = gpCost;
            this.itemRequirement = itemRequirement;
            this.respawnTime = respawnTime;
            this.bossNpcId = bossNpcId;
            this.desiredNpcDensity = desiredNpcDensity;
            this.mobs = mobs;
            this.combatProfile = combatProfile;
            this.aoeDamageMultiplier = aoeDamageMultiplier;
            this.aoeCooldownMultiplier = aoeCooldownMultiplier;
            this.dynamicWaveScaling = dynamicWaveScaling;
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

        public int getDesiredNpcDensity() {
            return desiredNpcDensity;
        }

        public BossMob[] getMobs() {
            return mobs;
        }

        public TierCombatProfile getCombatProfile() { return combatProfile; }

        /** Multiplier applied to damage dealt by hazards and NPCs in this tier. */
        public double getDamageMultiplier() { return 1.0 + (ordinal() * 0.1); }

        public double getAoeDamageMultiplier() { return aoeDamageMultiplier; }

        public double getAoeCooldownMultiplier() { return aoeCooldownMultiplier; }

        public boolean isDynamicWaveScaling() { return dynamicWaveScaling; }

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
        player.getInstancePerformanceTracker().start(tier);
        BossInstanceOverlayManager.sendKillOverlay(player);
        area.startHazards();
        area.startDynamicSpawns();
        InstanceMutatorManager.resetDanger();
        player.sendMessage("Active mutators: " + InstanceMutatorManager.getActiveDisplay());
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
        area.startDynamicSpawns();
        InstanceMutatorManager.resetDanger();
        player.sendMessage("Active mutators: " + InstanceMutatorManager.getActiveDisplay());
    }

    /**
     * Spawns NPCs randomly inside the tier's boundary so the instance feels populated.
     * Each {@link BossMob} defines how many creatures should appear for the tier.
     */
    private static void spawnInstanceGrid(Player player, BossTier tier, BossInstanceArea area, boolean preview) {
        Boundary bounds = tier.getZoneBoundary();
        int height = area.getHeight();

        TierCombatProfile profile = tier.getCombatProfile();
        BossMob[] mobs = tier.getMobs();
        if (mobs.length == 0) {
            return;
        }

        // scale counts based on desired density
        int areaTiles = (bounds.getMaximumX() - bounds.getMinimumX() + 1) * (bounds.getMaximumY() - bounds.getMinimumY() + 1);
        int desiredTotal = Math.max(1, areaTiles / Math.max(1, tier.getDesiredNpcDensity()));
        int baseTotal = Arrays.stream(mobs).mapToInt(BossMob::getCount).sum();
        double ratio = baseTotal > 0 ? Math.max(1.0, (double) desiredTotal / baseTotal) : 1.0;
        if (tier.ordinal() <= BossTier.TIER4.ordinal()) {
            ratio *= 1.2;
        } else if (tier.ordinal() >= BossTier.TIER7.ordinal()) {
            ratio *= 0.8;
        }

        Position spawnTile = tier.getSpawnTile();
        for (BossMob mob : mobs) {
            int targetCount = (int) Math.ceil(mob.getCount() * ratio);
            int spawned = 0;
            int attempts = 0;
            int delay = 0;
            while (spawned < targetCount && attempts++ < targetCount * 5) {
                int x = Misc.random(bounds.getMinimumX() + 1, bounds.getMaximumX() - 1);
                int y = Misc.random(bounds.getMinimumY() + 1, bounds.getMaximumY() - 1);
                if (Misc.distance(spawnTile.getX(), spawnTile.getY(), x, y) > MINIMUM_AGGRO_RANGE) {
                    continue; // ensure within aggro range
                }
                final int fx = x;
                final int fy = y;
                final BossMob fmob = mob;
                final boolean fpreview = preview;
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        NpcStats base = NpcStats.forId(fmob.getNpcId());
                        NpcStatsBuilder builder = NpcStats.builder();
                        builder.from(base);
                        builder.setHitpoints((int) (fmob.getHitpoints() * profile.getHpMultiplier()));
                        builder.setAttackLevel((int) (fmob.getAttack() * profile.getAttackMultiplier()));
                        builder.setDefenceLevel((int) (fmob.getDefence() * profile.getDefenceMultiplier()));
                        builder.setAttackSpeed((int) Math.max(1, base.getAttackSpeed() / profile.getAttackSpeedMultiplier()));
                        NPC npc = NPCSpawning.spawnNpc(player, fmob.getNpcId(), fx, fy,
                                height, 0, 0, false, false, builder.createNpcStats());
                        if (npc != null) {
                            if (fpreview) {
                                npc.getBehaviour().setAggressive(false);
                                npc.getCombatDefinition().setAggressive(false);
                                npc.getBehaviour().setRespawn(false);
                            } else {
                                npc.getBehaviour().setAggressive(true);
                                npc.getCombatDefinition().setAggressive(true);
                                npc.getBehaviour().setRespawn(true);
                                npc.getBehaviour().setRespawnWhenPlayerOwned(true);
                            }
                            List<NpcSpecialAttack> specials = NpcSpecialAttackLoader.getAll(fmob.getSpecialAttacks());
                            if (!specials.isEmpty()) {
                                List<NpcSpecialAttack> scaled = new ArrayList<>();
                                for (NpcSpecialAttack sa : specials) {
                                    scaled.add(sa.withAdjustedChance(sa.getActivationChance() * profile.getSpecialFrequencyMultiplier()));
                                }
                                npc.getAttributes().set("tier_special_attacks", scaled);
                            }
                            area.add(npc);
                        }
                        container.stop();
                    }
                }, delay);
                delay += 2;
                spawned++;
            }
            if (spawned < targetCount) {
                Misc.println("BossInstanceManager warning: spawned " + spawned + "/" + targetCount
                        + " NPCs for id " + mob.getNpcId());
            }
        }
    }

    /** Adds the tier to the player's unlocked set and returns {@code true} if it was newly unlocked. */
    public static boolean isFirstTierUnlock(Player player, BossTier tier) {
        return player.getUnlockedBossTiers().add(tier);
    }

    /** Reloads NPCs and hazards for an existing instance area. */
    public static void reloadArea(BossInstanceArea area) {
        if (area == null) return;
        area.getHazardScheduler().stop();
        for (NPC n : new ArrayList<>(area.getNpcs())) {
            n.unregister();
        }
        spawnInstanceGrid(area.owner, area.getTier(), area, false);
        area.startHazards();
        InstanceMutatorManager.resetDanger();
    }

    /** Removes the player from their boss instance and clears overlay text. */
    public static void leave(Player player) {
        BossInstanceOverlayManager.clear(player);
        BossInstanceArea area = INSTANCES.remove(player);
        if (area != null) {
            area.resetStreak(player);
            area.dispose();
        }
        InstancePerformanceTracker.InstanceResult result = player.getInstancePerformanceTracker().finish();
        if (result != null) {
            long seconds = java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(result.timeMs);
            player.sendMessage("@blu@Instance complete in " + seconds + "s, score: " + result.score);
            // Update personal bests
            player.getBestInstanceScores().merge(result.tier, result.score, Math::max);
            player.getBestInstanceTimes().merge(result.tier, result.timeMs, Math::min);
            InstanceRewardLoader.giveRewards(player, result.score);
            InstanceLeaderboard.record(player.getLoginName(), result.tier, result.timeMs, result.score);
            PerformanceRank rank = PerformanceRank.forScore(result.score);
            if (rank.ordinal() >= PerformanceRank.GOLD.ordinal()) {
                player.sendMessage("@gre@Achievement unlocked: " + rank.name() + " performer!");
            }
            int kills = player.getTierKillCounts().getOrDefault(result.tier, 0);
            if (kills >= result.tier.getRequiredKillCountToUnlockNext()) {
                String msg = player.getDisplayName() + " has completed " + getTierDisplayNameSafe(result.tier) + "!";
                PlayerHandler.nonNullStream().forEach(p -> p.sendMessage(msg));
            }
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

    /** Returns an arbitrary active area for the given tier, or {@code null} if none exist. */
    public static BossInstanceArea getAny(BossTier tier) {
        return INSTANCES.values().stream().filter(a -> a.getTier() == tier).findFirst().orElse(null);
    }

    public static int getMinimumAggroRange() {
        return MINIMUM_AGGRO_RANGE;
    }
}