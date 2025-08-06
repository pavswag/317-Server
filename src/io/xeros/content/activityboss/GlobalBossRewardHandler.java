package io.xeros.content.activityboss;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.broadcasts.Broadcast;

import java.util.List;

/**
 * Handles rewards and broadcasts for global boss deaths.
 */
public class GlobalBossRewardHandler {

    private GlobalBossRewardHandler() {
    }

    public static void handleDeath(GlobalBossType type, NPC npc, Player killer) {
        List<GlobalBossContributionTracker.Contribution> rankings = GlobalBossContributionTracker.getTopContributors(npc);
        int rank = 0;
        for (GlobalBossContributionTracker.Contribution c : rankings) {
            rank++;
            giveScaledLoot(type, c.getPlayer(), rank);
            c.getPlayer().addBossContribution(type.getName(), c.getDamage(), rank);
            c.getPlayer().sendMessage("You dealt " + c.getDamage() + " damage to " + type.getName() +
                    " (" + formatRank(rank) + " place)");
        }
        if (killer != null) {
            new Broadcast(killer.getDisplayName() + " has slain the " + type.getName() + "!")
                    .addTeleport(killer.getPosition())
                    .copyMessageToChatbox()
                    .submit();
        }
    }

    private static void giveScaledLoot(GlobalBossType type, Player player, int rank) {
        int tokens = rank <= 3 ? 10 - (rank * 2) : 2;
        player.sendMessage("You receive " + tokens + " boss tokens.");
        // Placeholder for actual loot table rolls based on rank
        if (rank == 1) {
            player.sendMessage("You receive a roll on the rare loot table!");
        } else if (rank <= 3) {
            player.sendMessage("You receive a roll on the uncommon loot table.");
        }
    }

    private static String formatRank(int rank) {
        switch (rank) {
            case 1:
                return "1st";
            case 2:
                return "2nd";
            case 3:
                return "3rd";
            default:
                return rank + "th";
        }
    }
}

