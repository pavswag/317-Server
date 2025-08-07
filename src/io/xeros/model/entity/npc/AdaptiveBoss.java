package io.xeros.model.entity.npc;

/**
 * Hook callbacks for adaptive boss mechanics. Implement this in a boss class
 * to customize behaviour when the adaptive mechanics trigger.
 */
public interface AdaptiveBoss {

    /**
     * Called when the boss drops below 50% hitpoints and its special attack
     * rate is increased.
     */
    default void onSpecialBoost(NPC npc) {}

    /**
     * Called once when the boss drops below 30% hitpoints and default minions
     * are about to be spawned.
     */
    default void onSummonMinions(NPC npc) {}

    /**
     * Called when the boss enrages after the fight lasts over ninety seconds.
     */
    default void onEnrage(NPC npc) {}
}

