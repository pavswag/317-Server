package io.xeros.content.instances.aoe;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

/**
 * Static hooks that tie the tier system into NPC death events. The actual game
 * engine should call {@link #onNpcDeath(Player, NPC)} whenever an NPC dies so
 * that kill counts can be tracked.
 */
public class AoeTierEvents {

    public static void onNpcDeath(Player player, NPC npc) {
        if (player == null || npc == null) {
            return;
        }
        for (AoeBossTierDef def : AoeBossTierLoader.getTiers()) {
            if (def.boss != null && def.boss.npcId == npc.npcId) {
                AoeTierController.incrementKill(player, def.tier);
                break;
            }
        }
    }
}
