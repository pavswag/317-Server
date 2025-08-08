package io.xeros.content.skills.slayer;

import io.xeros.content.skills.Skill;
import io.xeros.model.Npcs;
import io.xeros.model.definitions.NpcStats;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Slayer master dedicated to the Demon Hunter skill. This master only assigns
 * boss tier tasks and rewards Demon Hunter experience on completion.
 */
public class DemonSlayerMaster extends SlayerMaster {

    public DemonSlayerMaster() {
        // No direct interaction through NPC id for now, so use -1.
        super(-1, 5, new int[] {0,0,0,0,0,0}, new Task[0]);
    }

    /** Difficulty tiers used to scale experience rewards. */
    public enum Tier {
        TIER1(1, 5),
        TIER2(2, 15),
        TIER3(3, 30),
        TIER4(4, 45),
        TIER5(5, 60),
        TIER6(6, 75),
        TIER7(7, 85),
        TIER8(8, 90),
        TIER9(9, 95),
        TIER10(10, 99);

        private final int id;
        private final int levelReq;

        Tier(int id, int levelReq) {
            this.id = id;
            this.levelReq = levelReq;
        }

        public int getId() {
            return id;
        }

        public int getLevelRequirement() {
            return levelReq;
        }

        public static Optional<Tier> forId(int id) {
            return Arrays.stream(values()).filter(t -> t.id == id).findFirst();
        }
    }

    /**
     * Boss pool for Demon Hunter tasks. Each boss belongs to a tier and has a
     * base experience reward.
     */
    public enum BossTier {
        GENERAL_GRAARDOR("General Graardor", Npcs.GENERAL_GRAARDOR, Tier.TIER1),
        KRIL_TSUTSAROTH("K'ril Tsutsaroth", Npcs.KRIL_TSUTSAROTH, Tier.TIER2),
        COMMANDER_ZILYANA("Commander Zilyana", Npcs.COMMANDER_ZILYANA, Tier.TIER3),
        KREE_ARRA("Kree'Arra", Npcs.KREEARRA, Tier.TIER3),
        CHAOS_FANATIC("Chaos Fanatic", Npcs.CHAOS_FANATIC, Tier.TIER4),
        VENENATIS("Venenatis", Npcs.VENENATIS, Tier.TIER5),
        CALLISTO("Callisto", Npcs.CALLISTO, Tier.TIER6),
        VETION("Vet'ion", Npcs.VETION, Tier.TIER6),
        SKOTIZO("Skotizo", Npcs.SKOTIZO, Tier.TIER7),
        GHOST_OF_DARKNESS("Ghost of Darkness", 1429, Tier.TIER8),
        CRYOMANCER_RANGER("Cryomancer Ranger", 1656, Tier.TIER9),
        THE_NIGHTMARE("The Nightmare", Npcs.THE_NIGHTMARE, Tier.TIER10);

        private final String npcName;
        private final int npcId;
        private final Tier tier;

        BossTier(String npcName, int npcId, Tier tier) {
            this.npcName = npcName;
            this.npcId = npcId;
            this.tier = tier;
        }

        public String getNpcName() { return npcName; }

        public int getNpcId() { return npcId; }

        public Tier getTier() { return tier; }

        public boolean matches(String other) { return npcName.equalsIgnoreCase(other); }

        public int getBaseXp() {
            return NpcStats.forId(npcId).getHitpoints();
        }

        public static Optional<BossTier> forName(String name) {
            return Arrays.stream(values()).filter(b -> b.matches(name)).findFirst();
        }

        public static Optional<BossTier> forId(int id) {
            return Arrays.stream(values()).filter(b -> b.npcId == id).findFirst();
        }
    }

    /**
     * Representation of a demon hunter task.
     */
    public static class DemonSlayerTask {
        private final BossTier boss;
        private final int amount;

        public DemonSlayerTask(BossTier boss, int amount) {
            this.boss = boss;
            this.amount = amount;
        }

        public BossTier getBoss() { return boss; }
        public int getAmount() { return amount; }
    }

    /**
     * Assigns a new demon hunter task based on the player's Demon Hunter level.
     */
    public DemonSlayerTask assign(Player player) {
        int level = player.playerLevel[Skill.DEMON_HUNTER.getId()];

        for (Tier t : Tier.values()) {
            if (level >= t.getLevelRequirement() && t.getId() > player.getDemonHunterTierUnlocked()) {
                player.setDemonHunterTierUnlocked(t.getId());
                String unlocked = Arrays.stream(BossTier.values())
                        .filter(b -> b.getTier() == t)
                        .map(BossTier::getNpcName)
                        .collect(Collectors.joining(", "));
                player.sendMessage("\uD83D\uDD13 New Tier Unlocked! You can now be assigned " + unlocked + " (Tier " + t.getId() + ")");
            }
        }

        List<BossTier> pool = Arrays.stream(BossTier.values())
                .filter(b -> level >= b.getTier().getLevelRequirement())
                .collect(Collectors.toList());
        if (pool.isEmpty()) {
            pool = Arrays.asList(BossTier.GENERAL_GRAARDOR);
        }
        BossTier boss = pool.get(Misc.random(pool.size() - 1));
        int amount = 5 + Misc.random(30); // 5-35
        DemonSlayerTask task = new DemonSlayerTask(boss, amount);
        player.setDemonHunterTask(task);
        player.setDemonHunterTaskProgress(amount);
        DemonHunterTaskOverlayManager.send(player);
        DemonHunterTaskOverlayManager.schedule(player);
        return task;
    }
}
