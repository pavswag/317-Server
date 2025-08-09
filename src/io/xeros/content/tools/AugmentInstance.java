package io.xeros.content.tools;

/**
 * Represents an applied augment on a specific tool, tracking rarity and durability.
 */
public class AugmentInstance {
    private final ToolAugment augment;
    private AugmentTier tier;
    private int durability;

    public AugmentInstance(ToolAugment augment, AugmentTier tier, int durability) {
        this.augment = augment;
        this.tier = tier;
        this.durability = durability;
    }

    public ToolAugment getAugment() {
        return augment;
    }

    public AugmentTier getTier() {
        return tier;
    }

    public void setTier(AugmentTier tier) {
        this.tier = tier;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    /** Reduce durability by one action. */
    public void degrade() {
        if (durability > 0) {
            durability--;
        }
    }

    /** Whether this augment is active (has durability remaining). */
    public boolean isActive() {
        return durability != 0; // 0 or negative = inactive
    }
}
