package io.xeros.content.bosses.toa;

import io.xeros.content.minigames.TOA.bosses.Apmeken;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.entity.player.Player;

public class SoloApmeken extends Apmeken {
    public SoloApmeken(InstancedArea area) {
        super(area);
    }

    @Override
    public void onDeath() {
        // Allow normal drop handling and respawn in the instance.
    }
}
