package io.xeros.content.tools;

/**
 * Tiers for skilling augments.
 */
public enum AugmentTier {
    BASIC(0.02, 0.05, 0.0, 1, 0),
    ENHANCED(0.04, 0.10, 0.01, 2, 10_000_000),
    MASTERWORK(0.06, 0.15, 0.03, 3, 50_000_000);

    private final double xpBoost;
    private final double saveChance;
    private final double doubleChance;
    private final int crystalCost;
    private final int xpRequired;

    AugmentTier(double xpBoost, double saveChance, double doubleChance, int crystalCost, int xpRequired) {
        this.xpBoost = xpBoost;
        this.saveChance = saveChance;
        this.doubleChance = doubleChance;
        this.crystalCost = crystalCost;
        this.xpRequired = xpRequired;
    }

    public double getXpBoost() {
        return xpBoost;
    }

    public double getSaveChance() {
        return saveChance;
    }

    public double getDoubleChance() {
        return doubleChance;
    }

    public int getCrystalCost() {
        return crystalCost;
    }

    public int getXpRequired() {
        return xpRequired;
    }

    /**
     * Get the next tier above this one, or this if masterwork.
     */
    public AugmentTier upgrade() {
        int next = ordinal() + 1;
        return next < values().length ? values()[next] : this;
    }
}
