package io.xeros.content.minigames.TOA;

import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.TOA.instance.TombsOfAmascutInstance;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public class TombsOfAmascutBoss extends NPC {

    public TombsOfAmascutBoss(int npcId, Position position, InstancedArea instancedArea) {
        super(npcId, position);
        instancedArea.add(this);
        getBehaviour().setRespawn(false);
        getBehaviour().setAggressive(true);
    }

    public void onDeath() {
        Entity killer = calculateKiller();
        if (getInstance() != null) {
            getInstance().getPlayers().forEach(plr -> {
                int points = 4;
                if (killer != null && killer.equals(plr)) {
                    points += 2;
                }
                ((TombsOfAmascutInstance) plr.getInstance()).getMvpPoints().award(plr, points);
                ((TombsOfAmascutInstance) plr.getInstance()).getFoodRewards().award(plr, points);
                ((TombsOfAmascutInstance) plr.getInstance()).moveToNextRoom(plr);
            });
        }
    }

}
