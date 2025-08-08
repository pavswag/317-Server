package io.xeros.content.tools;

/**
 * Represents a passive perk that can be applied to skilling tools.
 */
public enum ToolAugment {
    /** General skilling augment providing tiered bonuses. */
    PROFICIENCY(0, "Skilling proficiency augment", true),
    /** Focus augment used for set bonuses. */
    FOCUS(995, "Focus augment", false);

    private final int unlockItemId;
    private final String description;
    private final boolean stackable;

    ToolAugment(int unlockItemId, String description, boolean stackable) {
        this.unlockItemId = unlockItemId;
        this.description = description;
        this.stackable = stackable;
    }

    /**
     * Item id that must be burnt in the Fire of Exchange to unlock this augment.
     */
    public int getUnlockItemId() {
        return unlockItemId;
    }

    /**
     * Textual description of the augment's effect.
     */
    public String getDescription() {
        return description;
    }

    /** Whether multiple copies of this augment may be stacked on a single tool. */
    public boolean isStackable() {
        return stackable;
    }

    // Effects are handled by the AugmentTier.
}
