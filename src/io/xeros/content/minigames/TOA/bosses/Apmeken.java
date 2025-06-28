package io.xeros.content.minigames.TOA.bosses;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.content.combat.npc.NPCCombatAttack;
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

public class Apmeken extends TombsOfAmascutBoss {

    private static final int[] GREEN_SPLAT_STILL_GFX = {1654, 1655, 1656, 1657, 1658, 1659, 1660, 1661};
    public Apmeken(InstancedArea instancedArea) {
        super(Npcs.MUMMY, new Position(3808, 5282, instancedArea.getHeight()), instancedArea);
        setAttacks();

    }

    public void spawnBaboons1(Player player, NPC npc) {
        npc.face = 3; //
        npc.setPlayerAttackingIndex(-1);
        npc.startAnimation(808);
        getNpcAutoAttacks();
        npc.setPlayerAttackingIndex(player.getIndex());
        npc.facePlayer(player.getIndex());
        npc.underAttack = true;
        npc.walkingHome = false;
        npc.setPlayerAttackingIndex(0);
        npc.facePlayer(0);
        npc.underAttack = false;
        npc.setMovement(Direction.EAST);
        npc.lastRandomlySelectedPlayer = 0;
        npc.maxHit = 15;
        npc.startGraphic(new Graphic(2156));
        npc.getCombatDefinition();
        System.out.println("baboon");
        Position[] startPosition = {new Position(3815, 5283), new Position(3800, 5285), new Position(3799, 5284)};
        Position[] endPosition = {new Position(3804, 5280), new Position(3806, 5282), new Position(3807, 5278)};
        npc.startAnimation(9518);
        ForceMovement forceMovement = new ForceMovement(npc, 6, startPosition[0], endPosition[0], 200, 500);
        forceMovement.startForceMovement();


        if (npc.getAttributes().getBoolean("force_movement")
                && player.getPosition().withinDistance(npc.getPosition(), 2)) {
            npc.startAnimation(9518);
            System.out.println("deal damage to player");
        }

    }

    @Override
    public void process() {

        setAttacks(); // For testing
        super.process();
    }
    private void setAttacks() {  // best place to put it ? top call method or in the npc autoattack ?

        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(7421))
                        .setCombatType(CombatType.RANGE)
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
                            if (Misc.trueRand(5) == 0) {
                                if (attack.getVictim().getInstance().equals(getInstance()))
                                    startAnimation(9518);
                                System.out.println("BAAABOOMS");
                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11709, new Position(3817, 5283, getInstance().getHeight()), getInstance()));
                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11710, new Position(3817, 5285, getInstance().getHeight()), getInstance()));
                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11711, new Position(3815, 5285, getInstance().getHeight()), getInstance()));
                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11712, new Position(3815, 5283, getInstance().getHeight()), getInstance()));


                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11713, new Position(3817, 5277, getInstance().getHeight()), getInstance()));
                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11714, new Position(3815, 5277, getInstance().getHeight()), getInstance()));
                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11715, new Position(3816, 5275, getInstance().getHeight()), getInstance()));
                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11716, new Position(3817, 5276, getInstance().getHeight()), getInstance()));

                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11717, new Position(3801, 5277, getInstance().getHeight()), getInstance()));
                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11718, new Position(3799, 5277, getInstance().getHeight()), getInstance()));
                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11709, new Position(3799, 5275, getInstance().getHeight()), getInstance()));
                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11710, new Position(3801, 5275, getInstance().getHeight()), getInstance()));


                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11711, new Position(3801, 5283, getInstance().getHeight()), getInstance()));
                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11713, new Position(3799, 5283, getInstance().getHeight()), getInstance()));
                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11715, new Position(3799, 5285, getInstance().getHeight()), getInstance()));
                                spawnBaboons1(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11717, new Position(3801, 5285, getInstance().getHeight()), getInstance()));

                            }
                        })
                        .setHitDelay(3)
                        .setMaxHit(1)
                        .setAttackDelay(2)
                        .createNPCAutoAttack(),



                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(8059))
                        .setMaxHit(0)
                        .setAttackDamagesPlayer(false)
                        .setDistanceRequiredForAttack(18)
                        .setOnAttack(this::sendSplatProjectile)
                        .createNPCAutoAttack()
        ));
    }
    private void sendSplatProjectile(NPCCombatAttack npcCombatAttack) {
        Position position = npcCombatAttack.getVictim().getPosition();
        new ProjectileBaseBuilder().setProjectileId(1644).setSendDelay(1).createProjectileBase()
                .createTargetedProjectile(npcCombatAttack.getNpc(), position).send(getInstance());

        // Cycle event to handle pool damage
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (isDead() || !isRegistered()) {
                    container.stop();
                    return;
                }

                if (container.getTotalExecutions() == 2) {
                    Server.playerHandler.sendStillGfx(new StillGraphic(GREEN_SPLAT_STILL_GFX[Misc.trueRand(GREEN_SPLAT_STILL_GFX.length)], 0, position), getInstance());
                }

                if (container.getTotalExecutions() == 18) {
                    container.stop();
                } else if (container.getTotalExecutions() >= 2) {
                    getInstance().getPlayers().stream().filter(plr -> plr.getPosition().equals(position)).forEach(plr ->
                            plr.appendDamage(6 + Misc.random(10), Hitmark.POISON));
                }
            }
        }, 1);
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
