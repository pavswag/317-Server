package io.xeros.content.bosses.godwars.impl;

import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.impl.LegacySoloPlayerInstance;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

public class ArmadylInstance extends LegacySoloPlayerInstance {

    public ArmadylInstance(Player player, Boundary boundary) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
    }

    public static void enter(Player player, ArmadylInstance instance) {
        try {
            instance.add(player);

            for (NPC npc : NPCHandler.npcs) {
                if (npc != null && Boundary.isIn(npc, Boundary.ARMADYL_GODWARS) && !instance.getNpcs().contains(npc) && npc.getHeight() == 2 && npc.getInstance() == null) {
                    int maxhit = new NPCHandler().getMaxHit(player, npc);
                    NPC arma = NPCSpawning.spawnNpc(instance, npc.getNpcId(), npc.getX(), npc.getY(), instance.getHeight()+2, 1, maxhit);
                    instance.add(arma);
                }
            }

            player.getPA().movePlayer(2839, 5296, instance.getHeight()+2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}