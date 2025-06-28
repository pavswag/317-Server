package io.xeros.content.bosses.godwars.impl;

import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.impl.LegacySoloPlayerInstance;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public class SaradominInstance extends LegacySoloPlayerInstance {

    public SaradominInstance(Player player, Boundary boundary) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
    }

    public static void enter(Player player, SaradominInstance instance) {
        try {
            instance.add(player);

            for (NPC npc : NPCHandler.npcs) {
                if (npc != null && Boundary.isIn(npc, Boundary.SARADOMIN_GODWARS) && !instance.getNpcs().contains(npc) && npc.getHeight() == 0 && npc.getInstance() == null) {
                    int maxhit = new NPCHandler().getMaxHit(player, npc);
                    NPC sara = NPCSpawning.spawnNpc(instance, npc.getNpcId(), npc.getX(), npc.getY(), instance.getHeight(), 1, maxhit);
                instance.add(sara);
                }
            }

            player.moveTo(new Position(2907, 5265, instance.getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}