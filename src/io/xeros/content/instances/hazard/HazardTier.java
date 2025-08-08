package io.xeros.content.instances.hazard;

/**
 * Severity tier for arena environmental hazards. Higher tiers
 * scale damage and other effects to make hazards more punishing.
 */
public enum HazardTier {
    BASIC(1.0),
    ADVANCED(1.5),
    EXTREME(2.0);

    private final double scale;
    HazardTier(double scale) {
        this.scale = scale;
    }

    public int scale(int base) {
        return (int) Math.max(1, Math.round(base * scale));
    }
}
