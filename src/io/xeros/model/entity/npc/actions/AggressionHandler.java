package io.xeros.model.entity.npc.actions;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for forcing nearby npcs to become aggressive towards a player.
 */
public class AggressionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AggressionHandler.class);

    /**
     * Forces all attackable npcs in the given range to target the player.
     *
     * @param player the player receiving aggression
     * @param range the radius in which npcs should be forced to attack
     */
    public static void forceAggro(Player player, int range) {
        for (NPC npc : NPCHandler.npcs) {
            if (npc == null) {
                continue;
            }
            if (npc.isDead() || npc.isPet || npc.isThrall) {
                continue;
            }
            if (npc.heightLevel != player.heightLevel) {
                continue;
            }
            if (npc.getInstance() != player.getInstance()) {
                continue;
            }
            if (npc.getPlayerAttackingIndex() > 0) {
                continue;
            }
            if (npc.getDefinition().getCombatLevel() <= 0) {
                continue;
            }
            if (npc.getDistance(player.getX(), player.getY()) > range) {
                continue;
            }
            npc.setPlayerAttackingIndex(player.getIndex());
            player.underAttackByNpc = npc.getIndex();
            logger.debug("Forced aggression of npc {} ({}) toward {}", npc.getNpcId(), npc.getIndex(), player.getLoginName());
        }
    }
}
