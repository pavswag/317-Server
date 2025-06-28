package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.specials.Special;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.EntityReference;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.ClientGameTimer;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.concurrent.TimeUnit;

public class AxeOfAraphel extends Special {

    public AxeOfAraphel() {
        super(5.5, 1.5, 1, new int[] { 33175 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        if (damage.getAmount() > 0) {
            damage.setAmount(1);
            player.startAnimation(3299);
            if (target instanceof Player) {
                ((Player) target).gfx0(1290);
                if (((Player) target).playerLevel[1] > 0) {
                    ((Player) target).playerLevel[1] -= ((Player) target).playerLevel[1] / 3;
                    ((Player) target).getPA().refreshSkill(1);
                }
                if (((Player) target).getRunEnergy() > 0) {
                    ((Player) target).setRunEnergy(((Player) target).getRunEnergy() - 10, true);
                }
                if (Misc.random(100) < 25) {
                    ((Player) target).frozenBy = EntityReference.getReference(player);
                    ((Player) target).freezeDelay = 5;
                    ((Player) target).freezeTimer = 5;
                    ((Player) target).resetWalkingQueue();
                    ((Player) target).sendMessage("You have been frozen.");
                    ((Player) target).getPA().sendGameTimer(ClientGameTimer.FREEZE, TimeUnit.MILLISECONDS, 600 * 5);
                }
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        target.appendDamage(player, 5, Hitmark.HIT);
                        ((Player) target).gfx0(1290);
                        if (container.getTotalTicks() == 10) {
                            container.stop();
                        }
                    }
                }, 2);
            }
            if (target instanceof NPC) {
                ((NPC) target).gfx0(1290);
                ((NPC) target).lowerDefence(.3);
                if (Misc.random(100) < 25) {
                    ((NPC) target).freezeTimer = 5;
                    ((NPC) target).resetWalkingQueue();
                    ((NPC) target).frozenBy = EntityReference.getReference(player);
                }
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        target.appendDamage(player, 5, Hitmark.HIT);
                        ((NPC) target).gfx0(1290);
                        if (container.getTotalTicks() == 10) {
                            container.stop();
                        }
                    }
                }, 2);
            }
        }
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }

}
