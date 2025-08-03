package io.xeros.content.activityboss;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.broadcasts.Broadcast;

/**
 * Handles rewards and broadcasts for global boss deaths.
 */
public class GlobalBossRewardHandler {

    private GlobalBossRewardHandler() {
    }

    public static void handleDeath(GlobalBossType type, NPC npc, Player killer) {
        if (killer != null) {
            new Broadcast(killer.getDisplayName() + " has slain the " + type.getName() + "!")
                    .addTeleport(killer.getPosition())
                    .copyMessageToChatbox()
                    .submit();
        }
    }
}

