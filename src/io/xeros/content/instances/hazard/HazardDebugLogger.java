package io.xeros.content.instances.hazard;

import io.xeros.content.instances.BossInstanceManager.BossInstanceArea;
import io.xeros.model.entity.npc.NPC;
import java.text.SimpleDateFormat;
import java.util.*;

/** Utility class to track hazard reaction debug information per instance. */
public class HazardDebugLogger {
    private static final Map<BossInstanceArea, Deque<String>> NPC_LOGS = new HashMap<>();

    public static void logNpcReaction(BossInstanceArea area, NPC npc, HazardContext ctx) {
        Deque<String> q = NPC_LOGS.computeIfAbsent(area, k -> new ArrayDeque<>());
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        q.add("[" + time + "] " + npc.getName() + " -> " + ctx.getType());
        if (q.size() > 25) {
            q.removeFirst();
        }
    }

    public static List<String> getNpcLogs(BossInstanceArea area) {
        Deque<String> q = NPC_LOGS.get(area);
        if (q == null) {
            return List.of();
        }
        return new ArrayList<>(q);
    }

    public static void clear(BossInstanceArea area) {
        NPC_LOGS.remove(area);
    }
}
