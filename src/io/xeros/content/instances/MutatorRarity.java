package io.xeros.content.instances;

/**
 * Rarity tiers for instance mutators used for weekly rotations.
 */
public enum MutatorRarity {
    COMMON(50),
    UNCOMMON(30),
    RARE(15),
    LEGENDARY(5);

    private final int weight;

    MutatorRarity(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
