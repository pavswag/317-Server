package io.xeros.content.activityboss;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.Map;

/**
 * Distributes rewards to all participants of a global boss kill.
 */
public class GlobalBossDropHandler {

    private GlobalBossDropHandler() {
    }

    public static void rewardParticipants(NPC boss) {
        Map<Player, Integer> contributions = GlobalBossContributionTracker.getContributors(boss);
        for (Player player : contributions.keySet()) {
            GameItem reward = GlobalBossLootTable.rollRewardFor(player);
            if (reward != null) {
                player.getItems().addItemUnderAnyCircumstance(reward.getId(), reward.getAmount());
                player.sendMessage("@blu@You received a reward for helping defeat " + boss.getName() + "!");
            }
        }
    }
}
