package io.xeros.content.instances;

/**
 * Ranks based on instance score which drive reward crates and achievements.
 */
public enum PerformanceRank {
    BRONZE(0),
    SILVER(1000),
    GOLD(2000),
    PLATINUM(3000),
    DIAMOND(4000);

    private final int threshold;

    PerformanceRank(int threshold) {
        this.threshold = threshold;
    }

    public int getThreshold() {
        return threshold;
    }

    public static PerformanceRank forScore(int score) {
        PerformanceRank result = BRONZE;
        for (PerformanceRank r : values()) {
            if (score >= r.threshold) {
                result = r;
            }
        }
        return result;
    }
}
