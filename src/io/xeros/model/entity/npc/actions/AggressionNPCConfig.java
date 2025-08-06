package io.xeros.model.entity.npc.actions;

import io.xeros.model.entity.npc.NPC;

import java.util.Set;

/**
 * Configuration for npc exceptions when forcing aggression.
 */
public class AggressionNPCConfig {

    private static final Set<Integer> PASSIVE_NPCS = Set.of();
    private static final Set<Integer> WHITELIST = Set.of();
    private static final Set<Integer> BLACKLIST = Set.of();
    private static boolean whitelistMode = false;

    public static boolean canAggress(NPC npc) {
        int id = npc.getNpcId();
        if (npc == null || npc.isDead() || npc.isPet || npc.isThrall) {
            return false;
        }
        if (npc.getDefinition().getCombatLevel() <= 0) {
            return false;
        }
        if (PASSIVE_NPCS.contains(id)) {
            return false;
        }
        if (whitelistMode) {
            return WHITELIST.contains(id);
        } else {
            return !BLACKLIST.contains(id);
        }
    }
}
