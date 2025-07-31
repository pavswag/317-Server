package io.xeros.content.bosses.toa;

import io.xeros.content.minigames.TOA.bosses.Kephri;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.entity.player.Player;

public class SoloKephri extends Kephri {
    public SoloKephri(InstancedArea area) {
        super(area);
    }

    @Override
    public void onDeath() {
        // Allow normal drop handling and respawn in the instance.
    }
}
