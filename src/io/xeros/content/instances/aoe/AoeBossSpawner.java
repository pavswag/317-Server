package io.xeros.content.instances.aoe;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility used by {@link AoeTierController} for spawning a boss and its minions
 * into an instance. This implementation focuses on simplicity and assumes the
 * caller manages lifecycle and timers.
 */
public class AoeBossSpawner {

    /**
     * Spawns the boss and minions defined by {@code def} centred on
     * {@code centre}. Returns the spawned NPCs for tracking.
     */
    public static List<NPC> spawn(Player owner, AoeBossTierDef def, Position centre) {
        List<NPC> spawned = new ArrayList<>();
        if (def == null || centre == null) {
            return spawned;
        }
        int spacing = def.getAoeGrid() != null && def.getAoeGrid().spacing > 0 ? def.getAoeGrid().spacing : 1;
        int totalMinions = 0;
        if (def.minions != null) {
            for (AoeBossTierDef.Npc npc : def.minions) {
                totalMinions += Math.max(1, npc.count);
            }
        }

        System.out.println("[AOE-BOSS] spawn start centre=" + centre.getX() + "," + centre.getY() + "," + centre.getHeight()
                + " boss=" + (def.boss != null ? def.boss.npcId : "null")
                + " minions=" + (def.minions == null ? "null" : def.minions.size()));
        System.out.println("[AOE-BOSS] totals totalMinions=" + totalMinions + " spacing=" + spacing);
        // Spawn boss in centre
        NPC boss = def.boss == null ? null : NPCSpawning.spawnNpc(owner, def.boss.npcId, centre.getX(), centre.getY(), centre.getHeight(), 1, 0, false, false, null);
        System.out.println("[AOE-BOSS] bossSpawned=" + (boss != null) + " pos=" + centre.getX() + "," + centre.getY() + "," + centre.getHeight()
                + " npcIndex=" + (boss != null ? boss.getIndex() : -1));
        if (boss != null) {
            spawned.add(boss);
        }
        // Offsets for up to three minions around the boss
        int[][] offsets = new int[][]{{1,0}, {-1,0}, {0,1}, {0,-1}};
        if (def.minions != null) {
            for (int i = 0; i < def.minions.size() && i < offsets.length; i++) {
                AoeBossTierDef.Npc m = def.minions.get(i);
                int x = centre.getX() + offsets[i][0] * spacing;
                int y = centre.getY() + offsets[i][1] * spacing;
                for (int count = 0; count < Math.max(1, m.count); count++) {
                    NPC npc = NPCSpawning.spawnNpc(owner, m.npcId, x, y, centre.getHeight(), 1, 0, false, false, null);
                    System.out.println("[AOE-BOSS] minion npcId=" + m.npcId + " tile=" + x + "," + y + "," + centre.getHeight()
                            + " ok=" + (npc != null) + " idx=" + (npc != null ? npc.getIndex() : -1));
                    if (npc != null) {
                        spawned.add(npc);
                    }
                }
            }
        }
        System.out.println("[AOE-BOSS] spawn complete spawnedSize=" + spawned.size());
        return spawned;
    }
}
