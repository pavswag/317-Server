package io.xeros.content.bosses.whisperer;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.bosses.hydra.CombatProjectile;
import io.xeros.content.combat.Damage;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.content.combat.npc.NPCCombatAttack;
import io.xeros.content.combat.range.RangeData;
import io.xeros.model.*;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 23/03/2024
 */
public class TheWhisperer {
    public static void handleAttacks(NPC npc) {
        handleBasicAttacks(npc);
    }


    private static final CombatProjectile RANGED_PROJECTILE = new CombatProjectile(2445, 50, 25, 0, 100, 0, 50);
    private static final CombatProjectile MAGIC_PROJECTILE = new CombatProjectile(2444, 50, 25, 0, 100, 0, 50);

    private static void handleBasicAttacks(NPC npc) {
        for (Player target : getTargets()) {
            for (int i = 0; i < 3; i++) {
                CombatType combatType = (Misc.random(1) == 0 ? CombatType.MAGE : CombatType.RANGE);

                if (combatType.equals(CombatType.MAGE)) {
                    MAGIC_PROJECTILE.delay = 4+i;
                    sendProjectile(MAGIC_PROJECTILE, target, npc);
                } else if (combatType.equals(CombatType.RANGE)) {
                    RANGED_PROJECTILE.delay = 4+i;
                    sendProjectile(RANGED_PROJECTILE, target, npc);
                }


                if (combatType == CombatType.RANGE) {
                    if (!target.protectingRange()) {
                        target.getDamageQueue().add(new Damage(target, Misc.random(1,5), 5+i, target.playerEquipment, Hitmark.HIT, combatType));
                    }
                } else {
                    if (combatType == CombatType.MAGE) {
                        if (target.protectingMagic()) {
                            target.getDamageQueue().add(new Damage(target, 0, 5+i, target.playerEquipment, Hitmark.MISS, combatType));
                        } else {
                            target.getDamageQueue().add(new Damage(target, Misc.random(1,5), 5+i, target.playerEquipment, Hitmark.HIT, combatType));
                        }
                    }
                }
            }
        }
        if (Misc.isLucky(85)) {
            handleSplashAttack();
        }
    }

    private static void sendProjectile(CombatProjectile projectile, Player player, NPC npc) {
        int size = (int) Math.ceil((double) npc.getSize() / 2.0);

        int centerX = npc.getX() + size;
        int centerY = npc.getY() + size;
        int offsetX = (centerY - player.getY()) * -1;
        int offsetY = (centerX - player.getX()) * -1;
        player.getPA().createPlayersProjectile(centerX, centerY, offsetX, offsetY, projectile.getAngle(), projectile.getSpeed(), projectile.getGfx(), projectile.getStartHeight(), projectile.getEndHeight(), -player.getIndex() - 1, 65, projectile.getDelay());
    }

    public static void handleSplashAttack() {
        for (Player player : getTargets()) {
            int playerX = player.getPosition().getX();
            int playerY = player.getPosition().getY();

            int[][] diagonalOffsets = {
                    {-4, -4}, {4, -4}, {-4, 4}, {4, 4}
            };
            for (int[] offset : diagonalOffsets) {
                int offsetX = offset[0];
                int offsetY = offset[1];

                int tentacleX = playerX + offsetX;
                int tentacleY = playerY + offsetY;

                int diffX = playerX - tentacleX;
                int diffY = playerY - tentacleY;
                int steps = Math.max(Math.abs(diffX), Math.abs(diffY)) + 1;

                for (int step = 0; step <= steps; step++) {
                    int delay = step;
                    int currentGfxId = 2447 + step;

                    int gfxX = tentacleX + (diffX * step / steps);
                    int gfxY = tentacleY + (diffY * step / steps);

                    sendGFXWithDelay(player, gfxX, gfxY, currentGfxId, delay);
                }
            }
        }
    }

    private static void sendGFXWithDelay(Player player, int spawnX, int spawnY, int gfxId, int delay) {
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                Server.playerHandler.sendStillGfx(new StillGraphic(gfxId, new Position(spawnX, spawnY, 0)), player.getPosition());
                if (spawnX == player.getPosition().getX() && spawnY == player.getPosition().getY()) {
                    player.appendDamage(Misc.random(5, 15), Hitmark.HIT);
                    player.startGraphic(new Graphic(2450));
                }
                container.stop();
            }
        }, delay);
    }

    private static List<Player> getTargets() {
        ArrayList<Player> list = new ArrayList<Player>();

        for (Player player : PlayerHandler.getPlayers()) {
            if (player != null && Boundary.isIn(player, Boundary.WHISPERER_BOUNDARY)) {
                if (!player.isDead() && player.getHealth().getCurrentHealth() > 0) {
                    list.add(player);
                }
            }
        }

        return list;
    }

}
