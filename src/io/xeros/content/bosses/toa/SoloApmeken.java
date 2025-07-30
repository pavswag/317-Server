package io.xeros.content.bosses.toa;

import io.xeros.content.minigames.TOA.bosses.Apmeken;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.TOA.TombsOfAmascutConstants;
import io.xeros.model.entity.player.Player;

public class SoloApmeken extends Apmeken {
    public SoloApmeken(InstancedArea area) {
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
