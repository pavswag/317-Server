package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.specials.Special;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;

public class SoulReaper extends Special {

    public SoulReaper() {
        super(2.5, 1.0, 1.25, new int[] { 28338 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.startAnimation(6147);
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}
