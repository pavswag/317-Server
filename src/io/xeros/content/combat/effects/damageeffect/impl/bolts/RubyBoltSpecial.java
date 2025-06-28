package io.xeros.content.combat.effects.damageeffect.impl.bolts;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.effects.damageeffect.DamageBoostingEffect;
import io.xeros.content.combat.range.RangeData;
import io.xeros.model.SoundType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

public class RubyBoltSpecial implements DamageBoostingEffect {

    @Override
    public void execute(Player attacker, Player defender, Damage damage) {
        int amount = (int)(defender.getHealth().getCurrentHealth()*(20/100.0f));
        int attackHP = (int) (attacker.getHealth().getCurrentHealth() * (10/100.0f));
        int reduced_amount = (attacker.getHealth().getCurrentHealth() - attackHP);
        if (reduced_amount <= 0) {
            return;
        }

        damage.setAmount(amount);
        RangeData.createCombatGraphic(defender, 754, false);
        defender.ignoreDefence = true;
        attacker.getHealth().reduce(attackHP);
        attacker.getPA().sendSound(2911, SoundType.AREA_SOUND);
    }

    @Override
    public void execute(Player attacker, NPC defender, Damage damage) {
        if (defender.getDefinition().getName() == null) {
            return;
        }
        int amount = (int)(defender.getHealth().getCurrentHealth()*(20/100.0f));
        int attackHP = (int) (attacker.getHealth().getCurrentHealth() * (10/100.0f));
        int reduced_amount = (attacker.getHealth().getCurrentHealth() - attackHP);
        if (reduced_amount <= 0) {
            return;
        }
        if (amount > 100) {
            amount = 100;
        }
        damage.setAmount(amount);
        RangeData.createCombatGraphic(defender, 754, false);
        attacker.getHealth().reduce(attackHP);
    }

    @Override
    public boolean isExecutable(Player p) {
        if (p.usingZaryteSpec && p.playerEquipment[Player.playerArrows] == 9242) {
            p.usingZaryteSpec = false;
            return true;
        }
        if (p.playerEquipment[Player.playerArrows] != 9242) {
            return false;
        }
        double chance = Math.random();
        if (p.npcAttackingIndex > 0) {
            if (chance <= 0.066)
                p.rubyBoltSpecial = true;
        }
        if (p.playerAttackingIndex > 0) {
            if (chance <= 0.121)
                p.rubyBoltSpecial = true;
        }


        return p.rubyBoltSpecial;
    }

    @Override
    public double getMaxHitBoost(Player attacker, Entity defender) {
        return 0;
    }

}
