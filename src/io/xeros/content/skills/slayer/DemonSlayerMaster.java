package io.xeros.content.skills.slayer;

import io.xeros.content.skills.Skill;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.List;
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
        TIER1(1, 1), TIER2(2, 20), TIER3(3, 50), TIER4(4, 90), ELITE(5, 99);
        private final int multiplier;
        private final int levelReq;
        Tier(int multiplier, int levelReq) {
            this.multiplier = multiplier;
            this.levelReq = levelReq;
        }
        public int getMultiplier() { return multiplier; }
        public int getLevelRequirement() { return levelReq; }
    }

    /**
     * Boss pool for Demon Hunter tasks. Each boss belongs to a tier and has a
     * base experience reward.
     */
    public enum BossTier {
        ICE_DEMON("Ice Demon", Tier.TIER1, 200),
        CRAZY_ARCHAEOLOGIST("Crazy Archaeologist", Tier.TIER1, 220),
        BARRELCHEST("Barrelchest", Tier.TIER2, 350),
        CORPOREAL_BEAST("Corporeal Beast", Tier.TIER2, 400),
        CHAOS_FANATIC("Chaos Fanatic", Tier.TIER2, 360),
        NEX("Nex", Tier.TIER3, 600),
        GHOST_OF_DARKNESS("Ghost of Darkness", Tier.TIER3, 650),
        CRYOMANCER_RANGER("Cryomancer Ranger", Tier.TIER3, 700),
        DEMONIC_REVENANT_LORD("Demonic Revenant Lord", Tier.ELITE, 900),
        NIGHTSTALKER("Nightstalker", Tier.ELITE, 950);

        private final String npcName;
        private final Tier tier;
        private final int xpReward;
        BossTier(String npcName, Tier tier, int xpReward) {
            this.npcName = npcName;
            this.tier = tier;
            this.xpReward = xpReward;
        }
        public String getNpcName() { return npcName; }
        public Tier getTier() { return tier; }
        public int getXpReward() { return xpReward; }
        public boolean matches(String other) { return npcName.equalsIgnoreCase(other); }
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
            if (level >= t.getLevelRequirement() && t.ordinal() + 1 > player.getDemonHunterTierUnlocked()) {
                player.setDemonHunterTierUnlocked(t.ordinal() + 1);
                String unlocked = Arrays.stream(BossTier.values())
                        .filter(b -> b.getTier() == t)
                        .map(BossTier::getNpcName)
                        .findFirst().orElse("new foes");
                player.sendMessage("\uD83D\uDD13 New Tier Unlocked! You can now be assigned " + unlocked + " (Tier " + (t.ordinal()+1) + ")");
            }
        }

        if (level >= Tier.ELITE.getLevelRequirement() && player.getDemonTaskStreak() >= 100) {
            List<BossTier> elitePool = Arrays.asList(
                    BossTier.CRYOMANCER_RANGER,
                    BossTier.GHOST_OF_DARKNESS,
                    BossTier.DEMONIC_REVENANT_LORD,
                    BossTier.NIGHTSTALKER);
            BossTier boss = elitePool.get(Misc.random(elitePool.size() - 1));
            int amount = 15 + Misc.random(20);
            DemonSlayerTask task = new DemonSlayerTask(boss, amount);
            player.setDemonHunterTask(task);
            player.setDemonHunterTaskProgress(amount);
            DemonHunterTaskOverlayManager.send(player);
            DemonHunterTaskOverlayManager.schedule(player);
            PlayerHandler.executeGlobalMessage("[Demon Slayer] " + player.getDisplayName() + " received an elite task: " + boss.getNpcName() + ".");
            return task;
        }

        List<BossTier> pool = Arrays.stream(BossTier.values())
                .filter(b -> level >= b.getTier().getLevelRequirement() && b.getTier() != Tier.ELITE)
                .collect(Collectors.toList());
        if (pool.isEmpty()) {
            pool = Arrays.asList(BossTier.ICE_DEMON, BossTier.CRAZY_ARCHAEOLOGIST);
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
