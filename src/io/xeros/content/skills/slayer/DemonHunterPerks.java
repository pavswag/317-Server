package io.xeros.content.skills.slayer;

import io.xeros.content.skills.Skill;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Perks unlocked by leveling the Demon Hunter skill.
 */
public class DemonHunterPerks {

    /** Perk definitions with unlock level and short description. */
    public enum Perk {
        DROP_RATE(20, "Drop Rate +5%"),
        FAST_TRACK(40, "Faster Tasks"),
        MARK_MASTER(60, "+1 Mark/kill");

        private final int level;
        private final String description;

        Perk(int level, String description) {
            this.level = level;
            this.description = description;
        }

        public int getLevel() {
            return level;
        }

        public String getDescription() {
            return description;
        }

        public boolean isUnlocked(int currentLevel) {
            return currentLevel >= level;
        }
    }

    /** Returns true if the player has unlocked the given perk. */
    public static boolean has(Player player, Perk perk) {
        int level = player.getLevel(Skill.DEMON_HUNTER);
        return perk.isUnlocked(level);
    }

    /** Lists descriptions of perks unlocked at the player's current level. */
    public static String unlockedDescriptions(Player player) {
        int level = player.getLevel(Skill.DEMON_HUNTER);
        List<String> list = Arrays.stream(Perk.values())
                .filter(p -> p.isUnlocked(level))
                .map(Perk::getDescription)
                .collect(Collectors.toList());
        return String.join(", ", list);
    }

    /**
     * Damage bonus based on Demon Hunter level when fighting the assigned boss.
     *
     * @return fractional bonus (0.10 = +10% damage)
     */
    public static double getDamageBonus(Player player, NPC npc) {
        Optional<DemonSlayerMaster.DemonSlayerTask> task = player.getDemonHunterTask();
        if (task.isPresent() && npc != null && npc.getNpcId() == task.get().getBoss().getNpcId()) {
            int level = player.getLevel(Skill.DEMON_HUNTER);
            return level * 0.01;
        }
        return 0.0;
    }
}
