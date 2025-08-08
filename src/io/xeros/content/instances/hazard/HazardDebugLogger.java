package io.xeros.content.instances.hazard;

import io.xeros.content.instances.BossInstanceManager.BossInstanceArea;
import io.xeros.model.entity.npc.NPC;
import java.text.SimpleDateFormat;
import java.util.*;

/** Utility class to track hazard reaction debug information per instance. */
public class HazardDebugLogger {
    private static final Map<BossInstanceArea, Deque<String>> NPC_LOGS = new HashMap<>();
    private static final Map<BossInstanceArea, List<String>> AUDIT_LOGS = new HashMap<>();

    public static void logNpcReaction(BossInstanceArea area, NPC npc, HazardContext ctx) {
        Deque<String> q = NPC_LOGS.computeIfAbsent(area, k -> new ArrayDeque<>());
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String line = "[" + time + "] " + npc.getName() + " -> " + ctx.getType();
        q.add(line);
        log(area, line);
        if (q.size() > 25) {
            q.removeFirst();
        }
    }

    public static void log(BossInstanceArea area, String message) {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        List<String> logs = AUDIT_LOGS.computeIfAbsent(area, k -> new ArrayList<>());
        logs.add("[" + time + "] " + message);
        if (logs.size() > 200) {
            logs.remove(0);
        }
    }

    public static List<String> getNpcLogs(BossInstanceArea area) {
        Deque<String> q = NPC_LOGS.get(area);
        if (q == null) {
            return List.of();
        }
        return new ArrayList<>(q);
    }

    public static List<String> getAuditLogs(BossInstanceArea area) {
        return AUDIT_LOGS.getOrDefault(area, List.of());
    }

    public static void dump(BossInstanceArea area, java.nio.file.Path path) throws java.io.IOException {
        java.nio.file.Files.write(path, getAuditLogs(area));
    }

    public static void clear(BossInstanceArea area) {
        NPC_LOGS.remove(area);
        AUDIT_LOGS.remove(area);
    }
}
