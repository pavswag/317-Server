package io.xeros.content.bosses.toa;

import io.xeros.content.minigames.TOA.bosses.Akkha;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;

public class SoloAkkha extends Akkha {
    public SoloAkkha(InstancedArea area) {
        super(area);
    }

    @Override
    public void onDeath() {
        // Allow normal drop handling and respawn in the instance.
    }
}
