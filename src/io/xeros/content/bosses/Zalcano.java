package io.xeros.content.bosses;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

public class Zalcano {

    public int zalcanoID = 9049;
    public int stanceID = 8429;
    public int attackID = 8433;
    public int walkID = 8430;
    public int flinchID = 8436;
    public int deathID = 8437;

    public Boundary zolcanoBoundary = new Boundary(2240, 4288, 2303, 4351);

    public void spawn() {
        NPC zolcano = NPCSpawning.spawn(zalcanoID, 2275,4328,0,1,5,false);
        zolcano.getBehaviour().setAggressive(true);
        zolcano.getBehaviour().setRespawn(false);
    }

    public void attack() {

    }

    public void death() {

    }

    public void rewards() {
        for (Player player : PlayerHandler.getPlayers()) {
            if (Boundary.isIn(player, zolcanoBoundary) && player.getZalcanoDamage() > 199) {
                System.out.println("Reward has been earned!");
            }
        }
    }

    public void mine() {

    }

    public void useItemOnNPC() {

    }

}
