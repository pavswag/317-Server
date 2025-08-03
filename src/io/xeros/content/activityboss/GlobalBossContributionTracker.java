package io.xeros.content.activityboss;

import io.xeros.content.combat.Damage;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility that calculates contribution rankings for global bosses.
 */
public class GlobalBossContributionTracker {

    private GlobalBossContributionTracker() {
    }

    public static List<Contribution> getTopContributors(NPC npc) {
        Map<Entity, List<Damage>> map = npc.getDamageTaken();
        if (map.isEmpty()) {
            return List.of();
        }
        return map.entrySet().stream()
                .filter(e -> e.getKey() != null && e.getKey().isPlayer())
                .map(e -> new Contribution((Player) e.getKey(),
                        e.getValue().stream().mapToInt(Damage::getAmount).sum()))
                .sorted(Comparator.comparingInt(Contribution::getDamage).reversed())
                .collect(Collectors.toList());
    }

    public static class Contribution {
        private final Player player;
        private final int damage;

        public Contribution(Player player, int damage) {
            this.player = player;
            this.damage = damage;
        }

        public Player getPlayer() {
            return player;
        }

        public int getDamage() {
            return damage;
        }
    }
}
