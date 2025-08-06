package io.xeros.content.activityboss;

/**
 * Types of tracked activities that can trigger global boss spawns.
 */
public enum ActivityType {
    /** Item upgrades done through the upgrade interface. */
    UPGRADES,
    /** Completion of clue scrolls. */
    CLUES,
    /** Upgrade points burnt in the Fire of Exchange. */
    FIRE_OF_EXCHANGE,
    /** Player achieves a large PvP killstreak. */
    KILLSTREAK
}
