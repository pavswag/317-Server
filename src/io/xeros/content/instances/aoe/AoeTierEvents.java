package io.xeros.content.instances.aoe;

import io.xeros.Server;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.List;

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
        int tier = AoeTierController.getActiveTier(player);
        if (tier > 0) {
            List<GameItem> drops = Server.getDropManager().getDropSample(player, npc.npcId);
            if (drops != null) {
                for (GameItem gi : drops) {
                    if (!AoeDropInterceptor.awardInsideAoe(player, gi)) {
                        Server.itemHandler.createGroundItem(player, gi.getId(), npc.getX(), npc.getY(), player.heightLevel, gi.getAmount(), player.getIndex(), false);
                    }
                }
            }
            AoeBossTierDef def = AoeBossTierLoader.getTier(tier);
            if (def != null && def.boss != null && def.boss.npcId == npc.npcId) {
                AoeTierController.incrementKill(player, tier);
                AoeTierRewardsLoader.forTier(tier).ifPresent(r -> {
                    if (r.getFortuneXpPerKill() > 0) {
                        player.addDemonHunterXP(r.getFortuneXpPerKill());
                    }
                });
            }
        }
    }
}
