package io.xeros.model.entity.npc;

import java.util.Map;

/**
 * Hook callbacks for adaptive boss mechanics. Implement this in a boss class
 * to customize behaviour when the adaptive mechanics trigger.
 */
public interface AdaptiveBoss {

    /**
     * Phases for this boss. Up to four phases can be defined and are triggered
     * by hitpoint percentage or time thresholds.
     */
    default AdaptivePhase[] getPhases() {
        return new AdaptivePhase[0];
    }

    /**
     * Called when transitioning into a new phase.
     */
    default void onPhaseStart(NPC npc, AdaptivePhase phase) { }

    /**
     * Called when the boss drops below 50% hitpoints and its special attack
     * rate is increased.
     */
    default void onSpecialBoost(NPC npc) {}

    /**
     * Called once when the boss drops below 30% hitpoints and default minions
     * are about to be spawned. Use {@link NPC#registerMinion(NPC)} after
     * spawning custom minions so they can be cleaned up automatically.
     */
    default void onSummonMinions(NPC npc) {}

    /**
     * Called when the boss enrages after the fight lasts over ninety seconds.
     */
    default void onEnrage(NPC npc) {}

    /**
     * Allow bosses to tweak threat values before prioritisation occurs.
     */
    default void updateThreat(NPC npc, Map<Integer, Integer> threat) {}
}
