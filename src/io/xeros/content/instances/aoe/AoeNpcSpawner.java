package io.xeros.content.instances.aoe;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spawns and tracks NPCs for an {@link AoeInstance}.
 */
public final class AoeNpcSpawner {

    private static final Logger logger = LoggerFactory.getLogger(AoeNpcSpawner.class);
    private static final Map<UUID, List<Integer>> ACTIVE = new ConcurrentHashMap<>();

    private AoeNpcSpawner() {}

    public static void spawnForInstance(Player owner, AoeInstance inst, AoeZoneMapDef map) {
        List<Integer> spawned = new ArrayList<>();
        for (AoeZoneMapDef.Npc def : map.getNpcs()) {
            int z = inst.z() + def.getZ();
            NPC npc = NPCSpawning.spawnNpc(owner, def.getId(), def.getX(), def.getY(), z, 1, 0, false, false);
            if (npc != null) {
                npc.randomWalk = def.isWalk();
                spawned.add(npc.getIndex());
                logger.info("[AOE-MAP] Spawned npc id={} index={} at ({},{},{})", def.getId(), npc.getIndex(), def.getX(), def.getY(), z);
            } else {
                logger.warn("[AOE-MAP] Failed to spawn npc id={} at ({},{},{})", def.getId(), def.getX(), def.getY(), z);
            }
        }
        ACTIVE.put(inst.id(), spawned);
    }

    public static void despawnForInstance(AoeInstance inst) {
        List<Integer> indices = ACTIVE.remove(inst.id());
        if (indices == null) {
            return;
        }
        for (Integer idx : indices) {
            if (idx == null) continue;
            NPC npc = NPCHandler.npcs[idx];
            if (npc != null) {
                npc.unregister();
                logger.info("[AOE-MAP] Despawned npc index={} from instance={}", idx, inst.id());
            }
        }
    }
}

