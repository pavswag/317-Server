package io.xeros.content.combat.specials.impl;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.specials.Special;
import io.xeros.content.skills.Skill;
import io.xeros.model.CombatType;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerAssistant;
import io.xeros.util.Misc;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static io.xeros.content.WeaponGames.WGModes.max;

public class WebWeaver extends Special {
    public WebWeaver() {
        super(5, 4.0, 1.0, new int[]{27655});
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.startAnimation(9964);
        player.gfx0(2354);

        // Create a ScheduledExecutorService with a single thread
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        // Schedule a task to apply damage to the target after 2 seconds
        executor.schedule(() -> {
            if (target.isPlayer()) {
                target.asPlayer().gfx0(2355);
            } else if (target.isNPC()) {
                target.asNPC().gfx0(2355);
            }
            // Apply damage here
        }, 1000, TimeUnit.MILLISECONDS);

        // Shutdown the executor to release resources after the task completes
        executor.shutdown();

        if (damage.getAmount() == 0) {
            int second = Misc.random(0, max);
            if (second == 0) {
                doHit(player, target, 0, 0);
                doHit(player, target, (int) (max * 0.75d), 1);
                doHit(player, target, (int) (max * 0.75d), 1);
            } else {
                doHit(player, target, second, 0);
                doHit(player, target, second / 2, 1);
                doHit(player, target, second / 2, 1);
            }
        } else {
            int halvedHit = damage.getAmount() == 0 ? 0 : damage.getAmount() / 2;
            int finalHit = halvedHit == 0 ? 0 : halvedHit / 2;
            doHit(player, target, halvedHit, 0);
            doHit(player, target, finalHit, 1);
            doHit(player, target, finalHit, 1);
        }
    }

    private void doHit(Player player, Entity target, int damage, int delay) {
        player.getDamageQueue().add(new Damage(target, damage, player.hitDelay + delay, player.playerEquipment, damage > 0 ? Hitmark.HIT : Hitmark.MISS, CombatType.RANGE));
        player.getPA().addXpDrop(new PlayerAssistant.XpDrop(damage, Skill.ATTACK.getId()));
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {
    }
}