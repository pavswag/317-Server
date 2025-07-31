package io.xeros.content.bosses.toa;

import io.xeros.content.minigames.TOA.bosses.Baba;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.entity.player.Player;

public class SoloBaba extends Baba {
    public SoloBaba(InstancedArea area) {
        super(area);
    }

    @Override
    public void onDeath() {
        // Allow normal drop handling and respawn in the instance.
    }
}
