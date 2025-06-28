package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.formula.MagicMaxHit;
import io.xeros.content.combat.formula.rework.MeleeCombatFormula;
import io.xeros.content.combat.specials.Special;
import io.xeros.content.skills.Skill;
import io.xeros.model.CombatType;
import io.xeros.model.Graphic;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerAssistant;
import io.xeros.util.Misc;

public class Voidwaker extends Special {
    public Voidwaker() {
        super(5.0, 1.0, 1.0, new int[] { 27690,28531  });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.startAnimation(1378);
        if (target.isPlayer()) {
            target.asPlayer().gfx0(2363);
        } else if (target.isNPC()) {
            target.asNPC().gfx0(2363);
        }
        int maximumDamage = MeleeCombatFormula.get().getMaxHit(player, player, 1.0, 1.0);
        int damage2 = Misc.random((maximumDamage/2), (int) (maximumDamage*1.5));
        damage.setAmount(damage2);
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}
