package io.xeros.content.instances;

import io.xeros.content.instances.BossInstanceManager.BossTier;
import java.util.concurrent.TimeUnit;

/**
 * Tracks per-instance performance statistics such as time, damage dealt/taken
 * and special-attack dodges. Used for scoring and reward crates when the
 * player leaves the instance.
 */
public class InstancePerformanceTracker {

    private long startTime;
    private long damageDealt;
    private long damageTaken;
    private int specialDodges;
    private BossTier tier;

    /** Starts tracking for a new instance. */
    public void start(BossTier tier) {
        this.tier = tier;
        this.startTime = System.currentTimeMillis();
        this.damageDealt = 0;
        this.damageTaken = 0;
        this.specialDodges = 0;
    }

    /** Records final results and resets the tracker. */
    public InstanceResult finish() {
        if (startTime == 0 || tier == null) {
            return null;
        }
        long time = System.currentTimeMillis() - startTime;
        int score = calculateScore(tier, time, damageDealt, damageTaken, specialDodges);
        InstanceResult result = new InstanceResult(tier, time, score);
        startTime = 0;
        tier = null;
        damageDealt = 0;
        damageTaken = 0;
        specialDodges = 0;
        return result;
    }

    public void addDamageDealt(int amount) {
        damageDealt += Math.max(0, amount);
    }

    public void addDamageTaken(int amount) {
        damageTaken += Math.max(0, amount);
    }

    public void addSpecialDodge() {
        specialDodges++;
    }

    public long getStartTime() {
        return startTime;
    }

    public static int calculateScore(BossTier tier, long timeMs, long dealt, long taken, int dodges) {
        double timeScore = Math.max(1, TimeUnit.MILLISECONDS.toSeconds(timeMs));
        double efficiency = dealt > 0 ? (double)(dealt - taken) / dealt : 0;
        double tierMult = tier.ordinal() + 1;
        double base = tierMult * 1000;
        double score = base + (efficiency * 500) + (dodges * 50) - (timeScore * 10);
        return (int)Math.max(0, Math.round(score));
    }

    /** Container for a finished run. */
    public static class InstanceResult {
        public final BossTier tier;
        public final long timeMs;
        public final int score;
        public InstanceResult(BossTier tier, long timeMs, int score) {
            this.tier = tier;
            this.timeMs = timeMs;
            this.score = score;
        }
    }
}
