package io.xeros.content.minigames.TOA.bosses;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.TOA.TombsOfAmascutBoss;
import io.xeros.content.minigames.tob.instance.TobInstance;
import io.xeros.model.*;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.PathFinder;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

import java.util.List;

public class Baba extends TombsOfAmascutBoss {


    public Baba(InstancedArea instancedArea) {
        super(Npcs.BABA, new Position(3808, 5406, instancedArea.getHeight()), instancedArea);
        setAttacks();

    }

    public void spawnBoulder(Player player, NPC npc) {
        npc.face = 3; //
        npc.setPlayerAttackingIndex(-1);
        npc.startAnimation(9518);

        Position[] startPosition = {new Position(3817, 5404),new Position(3817, 5408),new Position(3817, 5411)};
        Position[] endPosition = {new Position(3799, 5408),new Position(3799, 5408),new Position(3799, 5412)};
        npc.startAnimation(9518);
        ForceMovement forceMovement = new ForceMovement(npc, 6, startPosition[0], endPosition[0], 200, 500);
        forceMovement.startForceMovement();


        if (npc.getAttributes().getBoolean("force_movement")
                && player.getPosition().withinDistance(npc.getPosition(), 2)) {
            npc.startAnimation(9518);
            System.out.println("deal damage to player");
        }

    }
    public void spawnBaboons(Player player, NPC npc) {
        npc.startAnimation(808);
        getNpcAutoAttacks();
        npc.setPlayerAttackingIndex(player.getIndex());
        npc.facePlayer(player.getIndex());
        npc.underAttack = true;
        npc.walkingHome = false;
        npc.setPlayerAttackingIndex(0);
        npc.facePlayer(0);
        npc.underAttack = false;
        npc.lastRandomlySelectedPlayer = 0;
        npc.maxHit = 15;
        npc.startGraphic(new Graphic(2156));
        npc.getCombatDefinition();
    }
    private void setAttacks() {  // best place to put it ? top call method or in the npc autoattack ?

        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(9742))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(17)
                        .setHitDelay(2)
                        .setMaxHit(36)
                        .setAttackDelay(8)
                        .setProjectile(new ProjectileBaseBuilder()
                        .setSendDelay(1)
                        .setProjectileId(2245)
                        .setCurve(0)
                        .setStartHeight(0)
                        .setEndHeight(0)
                        .createProjectileBase())
                        .createNPCAutoAttack(),

                // Blood attack
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(4) == 0)
                        .setAnimation(new Animation(7421))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(17)
                        .setProjectile(new ProjectileBaseBuilder()
                        .setProjectileId(2245)
                        .createProjectileBase())
                        .setOnAttack(attack -> {
                        if (attack.getVictim().getInstance().equals(getInstance())) {
                        createBlood(attack.getVictim().asPlayer(), this);
                            }
                        })
                        .setHitDelay(3)
                        .setMaxHit(1)
                        .setAttackDelay(2)
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(4) == 0)
                        .setAnimation(new Animation(7421))
                        .setCombatType(CombatType.RANGE)
                        .setDistanceRequiredForAttack(17)
                        .setProjectile(new ProjectileBaseBuilder()
                                .setProjectileId(2139)
                                .createProjectileBase())
                        .setOnAttack(attack -> {
                            if (attack.getVictim().getInstance().equals(getInstance())) {
                                createBlood(attack.getVictim().asPlayer(), this);
                            }
                        })
                        .setHitDelay(3)
                        .setMaxHit(1)
                        .setAttackDelay(2)
                        .createNPCAutoAttack(),

                // boulder attack
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(4) == 0)
                        .setAnimation(new Animation(9518))
                        .setCombatType(CombatType.RANGE)
                        .setDistanceRequiredForAttack(17)
                        .setProjectile(new ProjectileBaseBuilder()
                                .setProjectileId(2245)
                                .createProjectileBase())
                        .setOnAttack(attack -> {
                            if (Misc.trueRand(10) == 0) {
                                if (attack.getVictim().getInstance().equals(getInstance()))
                                    startAnimation(9518);
                                spawnBoulder(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11783, new Position(3817, 5404, getInstance().getHeight()), getInstance()));
                                spawnBoulder(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11783, new Position(3817, 5408, getInstance().getHeight()), getInstance()));
                                spawnBoulder(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11783, new Position(3817, 5411, getInstance().getHeight()), getInstance()));
                            }
                        })
                        .setHitDelay(3)
                        .setMaxHit(1)
                        .setAttackDelay(2)
                        .createNPCAutoAttack(),


                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(7) == 0)
                        .setAnimation(new Animation(9518))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(17)
                        .setProjectile(new ProjectileBaseBuilder()
                                .setProjectileId(2245)
                                .createProjectileBase())
                        .setOnAttack(attack -> {
                            if (attack.getVictim().getInstance().equals(getInstance())) {
                                spawnBaboons(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11709, new Position(3807, 5408, getInstance().getHeight()), getInstance()));
                                spawnBaboons(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11709, new Position(3807, 5406, getInstance().getHeight()), getInstance()));
                                spawnBaboons(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11709, new Position(3805, 5411, getInstance().getHeight()), getInstance()));
                                spawnBaboons(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11709, new Position(3803, 5408, getInstance().getHeight()), getInstance()));
                            }
                        })
                        .setHitDelay(3)
                        .setMaxHit(10)
                        .setAttackDelay(2)
                        .createNPCAutoAttack()
        ));
    }

    private void createBlood(Player player, NPC maiden) {
        // Get accessible directions for blood
        List<Direction> accessibleDirectionList = Lists.newArrayList();
        for (Direction direction : Direction.values()) {
            Position delta = player.getPosition().translate(direction.getDelta()[0], direction.getDelta()[1]);
            if (PathFinder.getPathFinder().accessable(player, delta.getX(), delta.getY())) {
                accessibleDirectionList.add(direction);
            }
        }

        // Build random directions based on accessible list
        List<Direction> directionList = Lists.newArrayList();
        for (int count = 0; count < Math.min(2, accessibleDirectionList.size()); count++) {
            Direction direction;
            do {
                direction = accessibleDirectionList.get(Misc.trueRand(accessibleDirectionList.size()));
            } while (directionList.contains(direction) && accessibleDirectionList.size() > 1);
            directionList.add(direction);
        }

        // Create position list from directions and player positions
        List<Position> positionList = Lists.newArrayList();
        positionList.add(player.getPosition());
        for (Direction direction : directionList) {
            positionList.add(player.getPosition().translate(direction.getDelta()[0], direction.getDelta()[1]));
        }

        // Send the graphic and apply the damage event
        positionList.forEach(pos -> {
            Server.playerHandler.sendStillGfx(new StillGraphic(2139, pos), player.getInstance());

            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (maiden.isDead() || !maiden.isRegistered() || container.getTotalExecutions() == 10) {
                        container.stop();
                        return;
                    }

                    if (container.getTotalExecutions() > 1) {
                        maiden.getInstance().getPlayers().forEach(plr -> {
                            if (plr.getAttributes().containsBoolean(TobInstance.TOB_DEAD_ATTR_KEY))
                                return;
                            if (plr.getPosition().equals(pos)) {
                                plr.appendDamage(5 + Misc.trueRand(7), Hitmark.HIT);
                            }
                        });
                    }
                }
            }, 1);

        });
    }
}
