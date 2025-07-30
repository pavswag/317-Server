package io.xeros.content.bosses.toa;

import io.xeros.content.minigames.TOA.bosses.Akkha;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;
import io.xeros.content.minigames.TOA.TombsOfAmascutConstants;

public class SoloAkkha extends Akkha {
    public SoloAkkha(InstancedArea area) {
        super(area);
    }

    @Override
    public void onDeath() {
        InstancedArea inst = getInstance();
        if (inst != null) {
            for (Player plr : inst.getPlayers()) {
                plr.moveTo(TombsOfAmascutConstants.FINISHED_TOMBS_OF_AMASCUT_POSITION);
            }
            inst.dispose();
        }
    }
}
