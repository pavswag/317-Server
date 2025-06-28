package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.specials.Special;
import io.xeros.model.Items;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;

public class GraniteMaulHandleSpecialAttack extends Special {

    public GraniteMaulHandleSpecialAttack() {
        super(5.0, 1, 1, new int[]{24225, 24227});
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {

    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}