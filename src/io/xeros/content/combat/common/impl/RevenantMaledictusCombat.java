package io.xeros.content.combat.common.impl;

import io.xeros.Server;
import io.xeros.content.combat.common.CommonCombatMethod;
import io.xeros.content.combat.common.hit.ProjectileEntity;
import io.xeros.model.Animation;
import io.xeros.model.CombatType;
import io.xeros.model.collisionmap.PathChecker;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.util.discord.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class RevenantMaledictusCombat extends CommonCombatMethod {

    int[] animations = new int[]{9278, 9279};

    @Override
    public boolean prepareAttack(Entity entity, Player target) {
        if (entity == null || entity.isDead) return false;
        if (!withinDistance()) return false;
        if (Server.random.inclusive(1, 5) == 1) windWave(entity, target);
        else iceOrBlood(entity, target);
        return true;
    }

    void windWave(Entity npc, Player target) {
        if (!PathChecker.hasLineOfSight(entity, target)) return;
        int bigWave = 134;
        int littleWave = 2034;
        npc.startAnimation(new Animation(9277));
        Position targetTile = target.getPosition().deepCopy();
        var tileDist = npc.getPosition().distanceTo(targetTile);
        int duration = (int) (58 + 3 + (5 * tileDist));
        List<Position> cornerTiles = new ArrayList<>();
        List<Position> outlineTiles = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dy = -1; dy <= 1; dy += 2) {
                cornerTiles.add(targetTile.translate(dx, dy));
            }
        }
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                if ((Math.abs(dx) == 2 || Math.abs(dy) == 2) && (dx != 2 || dy != 2) && (dx != 2 || dy != -2) && (dx != -2 || dy != 2) && (dx != -2 || dy != -2)) {
                    outlineTiles.add(targetTile.translate(dx, dy));
                }
            }
        }
        ProjectileEntity projectile = new ProjectileEntity(entity, target, 1456, 58, duration, 125, 8, 16, 5, 208, 2);
        projectile.sendProjectile();
        final int delay = projectile.getSpeed() / 30 + 2;
        var clientTimePerTick = 20;
        target.getPA().stillGfx(bigWave, targetTile, 0, (int) ((delay * clientTimePerTick) * 1.1));
        for (Position cornerTile : cornerTiles) {
            target.getPA().stillGfx(littleWave, cornerTile, 0, (int) (((delay + 1) * clientTimePerTick) * 1.1 * .9));
        }
        for (Position outlineTile : outlineTiles) {
            target.getPA().stillGfx(littleWave, outlineTile, 0, (int) (((delay + 2) * clientTimePerTick) * 1.1 * .9));
        }
        target.getPA().stillGfx(bigWave, targetTile, 0, (int) (((delay + 2) * clientTimePerTick) * 1.1 * .9));
        var damage = Server.random.inclusive(1, 25);
        BooleanSupplier condition = () -> target.getPosition().inSqRadius(targetTile, 2);
        this.build(damage, 9277, CombatType.MAGE).consume(h -> h.setDelay(delay - 1)).executeUnderCondition(condition);
        cornerTiles.clear();
        outlineTiles.clear();
    }

    void iceOrBlood(Entity npc, Entity target) {
        if (!PathChecker.hasLineOfSight(entity, target)) return;
        var randomAnimation = StringUtil.randomElement(animations);
        npc.startAnimation(new Animation(randomAnimation));
        int tileDist = (int) npc.getPosition().distanceTo(target.getCenterPosition());
        int duration = (41 + 11 + (5 * tileDist));
        ProjectileEntity p = new ProjectileEntity(entity, target, 2033, 41, duration, 40, 36, 15, 5, 5);
        p.sendProjectile();
        final int delay = p.getSpeed() / 30;
        var damage = Server.random.inclusive(1, 25);
        this.build(damage, 9277, CombatType.MAGE).consume(h -> h.setDelay(delay - 1)).execute();
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 5;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 10;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }
}