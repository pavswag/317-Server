package io.xeros.content.activityboss;

/**
 * Types of tracked activities that can trigger global boss spawns.
 */
public enum ActivityType {
    /** Item upgrades done through the upgrade interface. */
    UPGRADE_ITEM,
    /** Completion of clue caskets. */
    CLUE_CASKET,
    /** GP burnt in the Fire of Exchange. */
    FOE_BURN,
    /** Player achieves a 10+ killstreak. */
    KILLSTREAK_10,
    /** Players claiming vote rewards. */
    VOTE_CLAIM
}
