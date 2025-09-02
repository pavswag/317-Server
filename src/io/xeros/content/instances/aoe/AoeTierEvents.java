package io.xeros.content.instances.aoe;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

/**
 * Static hooks that tie the tier system into NPC death events. The actual game
 * engine should call {@link #onNpcDeath(Player, NPC)} whenever an NPC dies so
 * that kill counts and rewards can be tracked.
 */
public class AoeTierEvents {

    public static void onNpcDeath(Player player, NPC npc) {
        if (player == null || npc == null) {
            return;
        }
        int tier = AoeTierController.getActiveTier(player);
        if (tier <= 0) {
            return;
        }
        AoeBossTierDef def = AoeTierRepo.byTier(tier);
        if (def != null && def.boss != null && def.boss.npcId == npc.npcId) {
            AoeTierController.incrementKill(player, tier);
            AoeTierRewardsLoader.forTier(tier).ifPresent(r -> {
                if (r.getFortuneXpPerKill() > 0) {
                    player.addDemonHunterXP(r.getFortuneXpPerKill());
                }
            });
            AoeTierController.endTier(player, true);
        }
    }
}
