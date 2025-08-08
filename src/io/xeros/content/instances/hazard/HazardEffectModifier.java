package io.xeros.content.instances.hazard;

/**
 * Describes how a mutator modifies a particular hazard effect.
 */
public class HazardEffectModifier {
    private final EnvironmentalHazardType target;
    private final double damageMultiplier;
    private final String message;

    public HazardEffectModifier(EnvironmentalHazardType target, double damageMultiplier, String message) {
        this.target = target;
        this.damageMultiplier = damageMultiplier;
        this.message = message;
    }

    public EnvironmentalHazardType getTarget() {
        return target;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public String getMessage() {
        return message;
    }
}
