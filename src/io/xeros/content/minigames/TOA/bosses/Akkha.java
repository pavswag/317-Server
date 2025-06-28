package io.xeros.content.minigames.TOA.bosses;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.content.combat.npc.NPCCombatAttack;
import io.xeros.content.commands.owner.Getnpcid;
import io.xeros.content.commands.owner.Npc;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.TOA.TombsOfAmascutBoss;
import io.xeros.content.minigames.TOA.instance.TombsOfAmascutInstance;
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

public class Akkha extends TombsOfAmascutBoss {

    private static final int[] GREEN_SPLAT_STILL_GFX = {1950, 1950, 1950, 1950, 1950, 1950, 1950, 1950};

    private final Animation ATTACK_ANIMATION = new Animation(8139);

    public Akkha(InstancedArea instancedArea) {
        super(Npcs.AKKHA, new Position(3680, 5408, instancedArea.getHeight() + 1), instancedArea);
    }

    public void spawnMinons(Player player, NPC npc) {
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
    }

    @Override
    public void process() {

        setAttacks(); // For testing
        super.process();
    }
    private void setAttacks() {  // best place to put it ? top call method or in the npc autoattack ?

        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(808))
                        .setCombatType(CombatType.RANGE)
                        .setDistanceRequiredForAttack(17)
                        .setHitDelay(2)
                        .setMaxHit(36)
                        .setAttackDelay(8)
                        .setProjectile(new ProjectileBaseBuilder()
                                .setSendDelay(1)
                                .setProjectileId(1900)
                                .setCurve(0)
                                .setStartHeight(0)
                                .setEndHeight(0)
                                .createProjectileBase())
                        .createNPCAutoAttack(),

                // Blood attack
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(4) == 0)
                        .setAnimation(new Animation(808))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(17)
                        .setProjectile(new ProjectileBaseBuilder()
                                .setProjectileId(1900)
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
                        .setAnimation(new Animation(808))
                        .setCombatType(CombatType.RANGE)
                        .setDistanceRequiredForAttack(17)
                        .setProjectile(new ProjectileBaseBuilder()
                                .setProjectileId(1900)
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
                        .setAnimation(new Animation(808))
                        .setCombatType(CombatType.RANGE)
                        .setDistanceRequiredForAttack(17)
                        .setProjectile(new ProjectileBaseBuilder()
                                .setProjectileId(1900)
                                .createProjectileBase())
                        .setOnAttack(attack -> {
                            if (Misc.trueRand(5) == 0) {
                                if (attack.getVictim().getInstance().equals(getInstance()))
                                    System.out.println("BAAABOOMS");
                                spawnMinons(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11777, new Position(3674, 5402,1), getInstance()));
                                spawnMinons(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11797, new Position(3675, 5412,1), getInstance()));
                                spawnMinons(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11797, new Position(3683, 5412,1), getInstance()));
                                spawnMinons(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11777, new Position(3686, 5402,1), getInstance()));
                            }
                        })
                        .setHitDelay(3)
                        .setMaxHit(1)
                        .setAttackDelay(2)
                        .createNPCAutoAttack(),



                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(808))
                        .setMaxHit(0)
                        .setAttackDamagesPlayer(false)
                        .setDistanceRequiredForAttack(18)
                        .setOnAttack(this::sendSplatProjectile)
                        .createNPCAutoAttack()
        ));
    }
    private void sendSplatProjectile(NPCCombatAttack npcCombatAttack) {
        Position position = npcCombatAttack.getVictim().getPosition();
        new ProjectileBaseBuilder().setProjectileId(1900).setSendDelay(1).createProjectileBase()
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
            Server.playerHandler.sendStillGfx(new StillGraphic(1992, pos), player.getInstance());

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
