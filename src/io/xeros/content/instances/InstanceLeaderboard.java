package io.xeros.content.instances;

import io.xeros.content.instances.BossInstanceManager.BossTier;
import java.util.*;

/**
 * Simple in-memory leaderboard for fastest times and highest scores per tier.
 * This does not persist across server restarts.
 */
public class InstanceLeaderboard {

    public static class Entry implements Comparable<Entry> {
        public final String player;
        public final long time;
        public final int score;
        public Entry(String player, long time, int score) {
            this.player = player;
            this.time = time;
            this.score = score;
        }
        @Override
        public int compareTo(Entry o) {
            int diff = Integer.compare(o.score, score);
            if (diff == 0) diff = Long.compare(time, o.time);
            if (diff == 0) diff = player.compareTo(o.player);
            return diff;
        }
    }

    private static final Map<BossTier, NavigableSet<Entry>> LEADERBOARDS = new EnumMap<>(BossTier.class);

    public static void record(String player, BossTier tier, long time, int score) {
        NavigableSet<Entry> set = LEADERBOARDS.computeIfAbsent(tier, t -> new TreeSet<>());
        set.add(new Entry(player, time, score));
        while (set.size() > 10) {
            set.pollLast();
        }
    }

    public static List<Entry> top(BossTier tier) {
        return new ArrayList<>(LEADERBOARDS.getOrDefault(tier, Collections.emptyNavigableSet()));
    }
}
