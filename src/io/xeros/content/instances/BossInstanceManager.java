package io.xeros.content.instances;

import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.impl.LegacySoloPlayerInstance;
import io.xeros.model.Npcs;
import io.xeros.model.definitions.NpcStats;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

import java.util.Map;

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
    private static class BossInstanceArea extends LegacySoloPlayerInstance {

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
     * Difficulty tiers for bosses.
     */
    public enum BossTier {
        TIER1("Training Grounds", 0, 0, -1, 5,
                new BossMob[]{new BossMob(Npcs.COW, 10, 1, 1)}),
        TIER2("Giants' Den", 10, 100_000, -1, 20,
                new BossMob[]{new BossMob(Npcs.HILL_GIANT, 35, 20, 20)}),
        TIER3("Dragon Lair", 50, 1_000_000, 11286, 40,
                new BossMob[]{new BossMob(Npcs.KING_BLACK_DRAGON, 250, 150, 150)});

        private final String zoneName;
        /** Kill requirement to unlock this tier. */
        private final int killRequirement;
        /** GP cost to unlock the tier. */
        private final int gpCost;
        /** Optional item requirement (-1 if none). */
        private final int itemRequirement;
        private final int respawnTime;
        private final BossMob[] mobs;

        BossTier(String zoneName, int killRequirement, int gpCost, int itemRequirement, int respawnTime, BossMob[] mobs) {
            this.zoneName = zoneName;
            this.killRequirement = killRequirement;
            this.gpCost = gpCost;
            this.itemRequirement = itemRequirement;
            this.respawnTime = respawnTime;
            this.mobs = mobs;
        }

        public String getZoneName() {
            return zoneName;
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

        public BossMob[] getMobs() {
            return mobs;
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

        spawnNpcs(player, tier, instance);
    }

    /**
     * Spawn all NPCs for a player's boss instance. NPCs are spaced out using a
     * simple grid so they don't overlap and are only visible to the owner.
     */
    private static void spawnNpcs(Player player, BossTier tier, BossInstanceArea instance) {
        int baseX = player.getX();
        int baseY = player.getY();

        BossMob[] mobs = tier.getMobs();
        for (int index = 0; index < mobs.length; index++) {
            BossMob mob = mobs[index];

            // Spread NPCs out using a 3xN grid with 2 tile spacing
            int offsetX = (index % 3) * 2;
            int offsetY = (index / 3) * 2;

            NPC npc = NPCSpawning.spawnNpc(player, mob.getNpcId(), baseX + offsetX, baseY + offsetY,
                    instance.getHeight(), 0, 0, false, false,
                    NpcStats.builder()
                            .setHitpoints(mob.getHitpoints())
                            .setAttackLevel(mob.getAttack())
                            .setDefenceLevel(mob.getDefence())
                            .createNpcStats());
            if (npc != null) {
                npc.getBehaviour().setRespawn(true);
                npc.getBehaviour().setRespawnWhenPlayerOwned(true);
                instance.add(npc);
            }
        }
    }

    /**
     * Leave the boss instance, disposing of any spawned NPCs and freeing the
     * height level.
     */
    public static void leave(Player player) {
        BossInstanceArea instance = INSTANCES.remove(player);
        if (instance != null) {
            instance.dispose();
            player.getPA().movePlayerUnconditionally(player.getX(), player.getY(), 0);
        }
    }

    public static BossInstanceArea get(Player player) {
        return INSTANCES.get(player);
    }
}
