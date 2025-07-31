package io.xeros.content.bosses.toa;

import io.xeros.content.minigames.TOA.bosses.Crondis;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.entity.player.Player;

public class SoloCrondis extends Crondis {
    public SoloCrondis(InstancedArea area) {
        super(area);
    }

    @Override
    public void onDeath() {
        // Allow normal drop handling and respawn in the instance.
    }
}
