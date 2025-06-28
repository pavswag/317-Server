package io.xeros.content.combat.common.impl;

import io.xeros.Server;
import io.xeros.content.combat.common.CommonCombatMethod;
import io.xeros.content.combat.common.hit.HitBuilder;
import io.xeros.content.combat.common.hit.ProjectileEntity;
import io.xeros.content.combat.melee.CombatPrayer;
import io.xeros.model.Animation;
import io.xeros.model.CombatType;
import io.xeros.model.collisionmap.PathChecker;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;
import lombok.NonNull;

import javax.annotation.Nonnull;

public class VenenatisCombat extends CommonCombatMethod {

    int attackCount = 0;

    @Override
    public boolean prepareAttack(Entity entity, Player target) {
        if (isReachable()) {
            meleeAttack(entity, target);
        } else {
            attackCount++;
            if (attackCount <= 4) {
                rangeAttack(entity, target);
            } else if (attackCount <= 8) {
                if (attackCount == 8) attackCount = 0;
                magicAttack(entity, target);
            }
        }
        return true;
    }

    public void meleeAttack(@NonNull final Entity entity, @NonNull Entity target) {
        entity.startAnimation(new Animation(9991));
        var damage = Server.random.inclusive(0, 25);
        if (target instanceof Player player) {
            if (player.prayerActive[CombatPrayer.PROTECT_FROM_MELEE]) damage = 0;
            this.build(damage, 9991, CombatType.MELEE).execute();
        }
    }

    public void magicAttack(@Nonnull final Entity entity, @Nonnull Entity target) {
        var tile = entity.getCenterPosition().translate(4, 4);
        var tileDist = tile.getManhattanDistance(target.getCenterPosition());
        var duration = (tileDist * 2) + 25;
        var damage = Server.random.inclusive(0, 25);
        if (target instanceof Player player) {
            if (player.prayerActive[CombatPrayer.PROTECT_FROM_MAGIC]) damage = 0;
            this.build(damage, 9990, CombatType.MAGE).consume(builder -> {
                ProjectileEntity projectile = new ProjectileEntity(entity, target, 2358, 25, duration, 37, 22, 14, 4, 2);
                projectile.sendProjectile();
                int speed = projectile.getSpeed();
                builder.setDelay((int) (speed / 30D));
            }).execute();
        }
    }

    private void rangeAttack(@Nonnull final Entity entity, @Nonnull Entity target) {
        var tile = entity.getCenterPosition().translate(4, 4);
        var tileDist = tile.getManhattanDistance(target.getCenterPosition());
        var duration = (tileDist * 2) + 25;
        var damage = Server.random.inclusive(0, 25);
        if (target instanceof Player player) {
            if (player.prayerActive[CombatPrayer.PROTECT_FROM_RANGED]) damage = 0;
            this.build(damage, 9989, CombatType.RANGE).consume(builder -> {
                ProjectileEntity projectile = new ProjectileEntity(entity, target, 2356, 25, duration, 37, 22, 14, 4, 2);
                projectile.sendProjectile();
                int speed = projectile.getSpeed();
                builder.setDelay((int) (speed / 30D));
            }).execute();
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 6;
    }
}