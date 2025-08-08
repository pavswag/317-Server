package io.xeros.content.instances;

import java.util.List;

/**
 * Tier wide combat settings and multipliers applied to mobs when spawned.
 */
public class TierCombatProfile {
    private final double hpMultiplier;
    private final double attackMultiplier;
    private final double defenceMultiplier;
    private final double attackSpeedMultiplier;
    private final double specialFrequencyMultiplier;
    private final List<String> allowedSpecials;

    public TierCombatProfile(double hpMultiplier, double attackMultiplier, double defenceMultiplier,
                             double attackSpeedMultiplier, double specialFrequencyMultiplier, List<String> allowedSpecials) {
        this.hpMultiplier = hpMultiplier;
        this.attackMultiplier = attackMultiplier;
        this.defenceMultiplier = defenceMultiplier;
        this.attackSpeedMultiplier = attackSpeedMultiplier;
        this.specialFrequencyMultiplier = specialFrequencyMultiplier;
        this.allowedSpecials = allowedSpecials;
    }

    public double getHpMultiplier() { return hpMultiplier; }
    public double getAttackMultiplier() { return attackMultiplier; }
    public double getDefenceMultiplier() { return defenceMultiplier; }
    public double getAttackSpeedMultiplier() { return attackSpeedMultiplier; }
    public double getSpecialFrequencyMultiplier() { return specialFrequencyMultiplier; }
    public List<String> getAllowedSpecials() { return allowedSpecials; }
}
