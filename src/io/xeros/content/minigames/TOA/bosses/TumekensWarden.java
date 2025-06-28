package io.xeros.content.minigames.TOA.bosses;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.content.combat.npc.NPCCombatAttack;
import io.xeros.content.commands.owner.Object;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.TOA.TombsOfAmascutBoss;
import io.xeros.content.minigames.TOA.TombsOfAmascutConstants;
import io.xeros.content.minigames.TOA.instance.TombsOfAmascutInstance;
import io.xeros.model.*;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

import java.util.List;

public class TumekensWarden extends TombsOfAmascutBoss {

    private static final int TUMEKENS_WARDEN_ID = Npcs.TUMEKENS_WARDEN;
    private static final int ELIDINIS_WARDEN_ID = Npcs.ELIDINIS_WARDEN;
    private static final Boundary ROOM_BOUNDARY = new Boundary(3787, 5133, 3827, 5174,2);

    private boolean transforming = false;
    private boolean exitOpen = false;

    public TumekensWarden(InstancedArea instancedArea) {
        super(TUMEKENS_WARDEN_ID, new Position(3820, 5152, 2), instancedArea);
        setAttacks();
    }
    public void spawnPyramids(Player player, NPC npc) {
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
        npc.startGraphic(new Graphic(1950));
        npc.getCombatDefinition();
        System.out.println("baboon");

    }
    public void SpawnPillar(Player player, NPC npc) {
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
        npc.startGraphic(new Graphic(1950));
        npc.getCombatDefinition();
    }
    @Override
    public int getDeathAnimation() {
        return 8128;
    }

    @Override
    public void setDead(boolean isDead) {
        if (isDead) {
            if (getNpcId() == TUMEKENS_WARDEN_ID) {
                transform();

            } else {
                super.setDead(isDead);
            }
        }
    }

    @Override
    public void process() {
        if (isDead() && !exitOpen) {
            openExit();
            setAttacks(); // For testing
            super.process();
        }
        if (!transforming) {
            super.process();
        }
    }

    //3805, 5154, 3803 3801
    private void openExit() {
        exitOpen = true;
        Server.getGlobalObjects().add(new GlobalObject(TombsOfAmascutConstants.TREASURE_ROOM_ENTRANCE_OBJECT_ID, new Position(3814, 5148, getHeight()), 0, 10).setInstance(getInstance()));

        // Teleport dead players to exit
        getInstance().getPlayers().forEach(plr -> {
            if (plr.getAttributes().getBoolean(TombsOfAmascutInstance.TOMBS_OF_AMASCUT_DEAD_ATTR_KEY)) {
                plr.moveTo(new Position(3816, 5154,2));
            }

            plr.getBossTimers().death(TombsOfAmascutConstants.TOMBS_OF_AMASCUT);
            plr.tobCompletions++;
            plr.sendMessage("You've completed the Tombs Of Amascut@red@" + plr.tobCompletions + "@bla@ times.");
        });
    }

    private void transform() {
        if (!transforming) {
            transforming = true;
            CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (container.getTotalExecutions() == 0) {
                        startAnimation(8116);
                    } else if (container.getTotalExecutions() == 2) {
                        requestTransform(ELIDINIS_WARDEN_ID);
                        startAnimation(8119);
                        setAttacks();
                    } else if (container.getTotalExecutions() == 4) {
                        getHealth().reset();
                        startAnimation(Animation.RESET_ANIMATION);
                        forceChat("Behold my true nature!");
                        container.stop();
                        transforming = false;
                    }
                }
            }, 1);
        }
    }

    private void setAttacks() {
        if (getNpcId() == TUMEKENS_WARDEN_ID) {
            setNpcAutoAttacks(Lists.newArrayList(
                    new NPCAutoAttackBuilder()
                            .setAnimation(new Animation(8114))
                            .setCombatType(CombatType.RANGE)
                            .setAttackDamagesPlayer(false)
                            .setOnAttack(npcCombatAttack -> launchRangeAttack())
                            .setDistanceRequiredForAttack(14)
                            .setAttackDelay(5)
                            .createNPCAutoAttack(),

                    new NPCAutoAttackBuilder()
                            .setSelectAutoAttack(attack -> Misc.trueRand(4) == 0)
                            .setAnimation(new Animation(9518))
                            .setCombatType(CombatType.MAGE)
                            .setDistanceRequiredForAttack(17)
                            .setProjectile(new ProjectileBaseBuilder()
                                    .setProjectileId(1950)
                                    .createProjectileBase())
                            .setOnAttack(attack -> {
                                if (attack.getVictim().getInstance().equals(getInstance())) {
                                }
                            })
                            .setHitDelay(3)
                            .setMaxHit(1)
                            .setAttackDelay(2)
                            .createNPCAutoAttack(),
                    new NPCAutoAttackBuilder()
                            .setSelectAutoAttack(attack -> Misc.trueRand(4) == 0)
                            .setAnimation(new Animation(9518))
                            .setCombatType(CombatType.RANGE)
                            .setDistanceRequiredForAttack(17)
                            .setProjectile(new ProjectileBaseBuilder()
                                    .setProjectileId(1950)
                                    .createProjectileBase())
                            .setOnAttack(attack -> {
                                if (Misc.trueRand(5) == 0) {
                                    if (attack.getVictim().getInstance().equals(getInstance()))
                                        startAnimation(808);
                                    System.out.println("homieeeeee");
                                    spawnPyramids(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11655, new Position(3812, 5149,2), getInstance()));
                                    spawnPyramids(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11655, new Position(3816, 5154,2), getInstance()));
                                    spawnPyramids(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11655, new Position(3815, 5147,2), getInstance()));
                                    spawnPyramids(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11655, new Position(3810, 5147,2), getInstance()));
                                    spawnPyramids(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11655, new Position(3806, 5149,2), getInstance()));
                                    spawnPyramids(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11655, new Position(3803, 5146,2), getInstance()));
                                    spawnPyramids(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11655, new Position(3805, 5157,2), getInstance()));
                                    spawnPyramids(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11655, new Position(3809, 5157,2), getInstance()));
                                    spawnPyramids(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11655, new Position(3813, 5157,2), getInstance()));
                                    spawnPyramids(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11655, new Position(3816, 5159,2), getInstance()));
                                    spawnPyramids(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11655, new Position(3816, 5154,2), getInstance()));
                                }
                            })
                            .setHitDelay(3)
                            .setMaxHit(1)
                            .setAttackDelay(2)
                            .createNPCAutoAttack(),

                    new NPCAutoAttackBuilder()
                            .setSelectAutoAttack(attack -> Misc.trueRand(4) == 0)
                            .setAnimation(new Animation(9518))
                            .setCombatType(CombatType.MAGE)
                            .setDistanceRequiredForAttack(17)
                            .setProjectile(new ProjectileBaseBuilder()
                                    .setProjectileId(1950)
                                    .createProjectileBase())
                            .setOnAttack(attack -> {
                                if (attack.getVictim().getInstance().equals(getInstance())) {
                                }
                            })
                            .setHitDelay(3)
                            .setMaxHit(1)
                            .setAttackDelay(2)
                            .createNPCAutoAttack(),

                    new NPCAutoAttackBuilder()
                            .setSelectAutoAttack(attack -> Misc.trueRand(4) == 0)
                            .setAnimation(new Animation(9518))
                            .setCombatType(CombatType.RANGE)
                            .setDistanceRequiredForAttack(17)
                            .setProjectile(new ProjectileBaseBuilder()
                                    .setProjectileId(1950)
                                    .createProjectileBase())
                            .setOnAttack(attack -> {
                                if (Misc.trueRand(10) == 0) {
                                    if (attack.getVictim().getInstance().equals(getInstance()))
                                        startAnimation(9518);
                                    System.out.println("dick hats");
                                    SpawnPillar(attack.getVictim().asPlayer(), new TombsOfAmascutBoss(11751, new Position(3807, 5153,2), getInstance()));
                                }
                            })
                            .setHitDelay(3)
                            .setMaxHit(1)
                            .setAttackDelay(2)
                            .createNPCAutoAttack(),

                    new NPCAutoAttackBuilder()
                            .setAnimation(new Animation(8116))
                            .setAttackDamagesPlayer(false)
                            .setCombatType(CombatType.MELEE)
                            .setDistanceRequiredForAttack(18)
                            .setOnAttack(this::smash)
                            .setAttackDelay(3)
                            .setSelectAutoAttack(npcCombatAttack -> getInstance().getPlayers().stream().anyMatch(plr -> insideOf(plr.getPosition())))
                            .createNPCAutoAttack()
            ));

        } else {
            setNpcAutoAttacks(Lists.newArrayList(
                    new NPCAutoAttackBuilder()
                            .setAnimation(new Animation(8123))
                            .setCombatType(CombatType.MAGE)
                            .setDistanceRequiredForAttack(14)
                            .setEndGraphic(new Graphic(1950))
                            .setAttackDelay(5)
                            .setHitDelay(3)
                            .setMaxHit(40)
                            .setPrayerProtectionPercentage(npcCombatAttack -> 0.5)
                            .setProjectile(new ProjectileBaseBuilder().setProjectileId(1594).setSendDelay(1).createProjectileBase())
                            .createNPCAutoAttack(),
                    new NPCAutoAttackBuilder()
                            .setAttackDamagesPlayer(false)
                            .setSelectAutoAttack(npcCombatAttack -> Misc.trueRand(5) == 0)
                            .setAnimation(new Animation(8126))
                            .setOnAttack(npcCombatAttack -> flowerAttack())
                            .setAttackDelay(18)
                            .createNPCAutoAttack()
            ));
        }
    }



    private void flowerAttack() {
        List<Position> safeSpotList = Lists.newArrayList();
        for (int count = 0; count < 4; count++) {
            int x = ROOM_BOUNDARY.getMinimumX();
            int y = ROOM_BOUNDARY.getMinimumY();
            int xLength = ROOM_BOUNDARY.getMaximumX() - x;
            int yLength = ROOM_BOUNDARY.getMaximumY() - y;

            Position position;
            do {
                position = new Position(x + Misc.trueRand(xLength), y + Misc.trueRand(yLength));
            } while(safeSpotList.contains(position));

            safeSpotList.add(getInstance().resolve(position));
        }
        safeSpotList.forEach(position -> Server.playerHandler.sendStillGfx(new StillGraphic(1950, 2, position), getInstance()));

        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (isDead || isUnregister()) {
                    container.stop();
                    return;
                }
                if (container.getTotalExecutions() == 9) {
                    safeSpotList.forEach(pos -> new ProjectileBaseBuilder().setProjectileId(1900).createProjectileBase()
                            .createTargetedProjectile(TumekensWarden.this, pos).send(getInstance()));
                } else if (container.getTotalExecutions() == 11) {
                    getInstance().getPlayers().forEach(plr -> {
                        if (!Boundary.TOMBS_OF_AMASCUT_TUMEKENS_WARDEN.in(plr))
                            return;
                        if (safeSpotList.stream().noneMatch(pos -> plr.getPosition().equals(pos))) {
                            plr.appendDamage(40 + Misc.trueRand(40), Hitmark.HIT);
                        } else {
                            plr.appendDamage(0, Hitmark.MISS);
                        }
                    });
                    container.stop();
                }
            }
        }, 1);
    }

    private void smash(NPCCombatAttack npcCombatAttack) {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                getInstance().getPlayers().forEach(plr2 -> {
                    if (insideOf(plr2.getPosition())) {
                        int damage = Misc.trueRand(80);
                        plr2.appendDamage(damage, damage > 0 ? Hitmark.MISS : Hitmark.HIT);
                    }
                });

                container.stop();
            }
        }, 2);
    }

    private void launchRangeAttack() {
        getInstance().getPlayers().forEach(plr -> {
            if (plr.getAttributes().containsBoolean(TombsOfAmascutInstance.TOMBS_OF_AMASCUT_DEAD_ATTR_KEY))
                return;
            Position position = plr.getPosition();
            new ProjectileBaseBuilder().setProjectileId(1900).setSendDelay(1).createProjectileBase()
                    .createTargetedProjectile(this, position).send(getInstance());
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    getInstance().getPlayers().forEach(plr2 -> {
                        if (!Boundary.TOMBS_OF_AMASCUT_TUMEKENS_WARDEN.in(plr))
                            return;
                        if (plr2.getAttributes().containsBoolean(TombsOfAmascutInstance.TOMBS_OF_AMASCUT_DEAD_ATTR_KEY))
                            return;
                        if (plr2.getPosition().equals(position)) {
                            int damage = Misc.trueRand(70);
                            if (plr2.protectingRange()) {
                                damage /= 2;
                            }
                            plr2.appendDamage(damage, damage > 0 ? Hitmark.MISS : Hitmark.HIT);
                        }
                    });
                    Server.playerHandler.sendStillGfx(new StillGraphic(1950, position), getInstance());
                    container.stop();
                }
            }, 2);
        });
    }

    @Override
    public void onDeath() {
        super.onDeath();
        ((TombsOfAmascutInstance) this.getInstance()).setFinalBossComplete(true);
        String message = "Elidinis Warden has died. If you are stuck inside a cage use the command ::uncage";
        this.getInstance().getPlayers().forEach(p -> p.sendMessage(message));
    }


}