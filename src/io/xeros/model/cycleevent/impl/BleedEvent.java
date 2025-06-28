package io.xeros.model.cycleevent.impl;

import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.Health;
import io.xeros.model.entity.HealthStatus;

import java.util.Optional;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 23/03/2024
 */
public class BleedEvent extends Event<Entity> {


    private int damage;

    private final Optional<Entity> inflictor;

    public BleedEvent(Entity attachment, int damage, Optional<Entity> inflictor) {
        super("health_status", attachment, 1);
        this.damage = damage;
        this.inflictor = inflictor;
    }

    @Override
    public void execute() {
        if (attachment == null) {
            super.stop();
            return;
        }

        Health health = attachment.getHealth();

        if (health.isNotSusceptibleTo(HealthStatus.BLEED)) {
            super.stop();
            return;
        }

        if (attachment.getHealth().getCurrentHealth() <= 0) {
            super.stop();
            return;
        }

        attachment.appendDamage(null, damage, Hitmark.DAWNBRINGER);
        inflictor.ifPresent(inf -> attachment.addDamageTaken(inf, damage));

        damage += 5;

        if (damage > 20) {
            Server.getEventHandler().stop(attachment, "health_status");
            super.stop();
        }
    }
}
