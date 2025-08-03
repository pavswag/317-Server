package io.xeros.content.pet;

/**
 * Simplified pet perk definition used by the pet manager.
 */
public class PetPerk {

    private String perkKey;
    private double baseValue;
    private double levelModifier;
    private String description;
    private int maxLevel;
    private int level;

    public PetPerk() {}

    public String getPerkKey() {
        return perkKey;
    }

    public double getBaseValue() {
        return baseValue;
    }

    public double getLevelModifier() {
        return levelModifier;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getValue() {
        return baseValue + level * levelModifier;
    }
}