package io.xeros.content.bosses.vardorvis;

import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.melee.CombatPrayer;
import io.xeros.content.combat.range.RangeData;
import io.xeros.model.Direction;
import io.xeros.model.Graphic;
import io.xeros.model.StillGraphic;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.HealthStatus;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ArkCane
 * @project arkcane-server
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 17/01/2024
 */
public class Vardorvis {


    /**
     * The vardorvis npc
     */

    private static boolean SentAxes = false;

    private static long head_delay = 0;
    public static void handleAttacks(NPC npc) {
        if (!SentAxes && Misc.isLucky(50)) {
            handleSwingingAxes();
        }

        if (Misc.isLucky(25) && System.currentTimeMillis() > head_delay) {
            handleHeadGaze();
            head_delay = (System.currentTimeMillis() + TimeUnit.SECONDS.toDays(15));
        }

        if (Misc.isLucky(15)) {
            handleDartingSpikes(npc);
        }
        handleMelee(npc);
    }

    private static void handleMelee(NPC npc) {
        if (npc.isDeadOrDying() || !npc.isRegistered()  || !npc.isAutoRetaliate()) {
            return;
        }
        npc.startAnimation(10341);
        for (Player p : getTargets()) {
            if (p.protectingMelee()) {
                p.appendDamage(npc, Misc.random(1, 5), Hitmark.HIT);
            } else {
                p.appendDamage(npc, Misc.random(1, 30), Hitmark.HIT);
            }
        }
    }

    private static void handleSwingingAxes() {
        if (getTargets().isEmpty() || SentAxes)
            return;
        SentAxes = true;
        int count = 2;

            for (int i = 0; i < count; i++) {
                int dir = Misc.random(VardorvisAxePositions.values().length-1);
                Position start = VardorvisAxePositions.values()[dir].start;
                Position end = VardorvisAxePositions.values()[dir].finish;
                if (Misc.random(3) == 1) {
                    NPC axe = NPCSpawning.spawnNpc(12227, end.getX(), end.getY(), 0, 0, Misc.random(25));
                    CycleEventHandler.getSingleton().addEvent(axe, new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            handleAxeDamage(axe);
                            axe.setFacePlayer(false);
                            axe.setWalkDirection(Direction.fromDeltas(end, start));
                            axe.moveTowards(start.getX(), start.getY(), false, false);
                            if (container.getTotalExecutions() == 10) {
                                axe.setInvisible(true);
                                axe.unregister();
                                SentAxes = false;
                                CycleEventHandler.getSingleton().stopEvents(axe);
                                container.stop();
                            }
                        }
                    },1);
                }
                else {
                    NPC axe = NPCSpawning.spawnNpc(12227, start.getX(), start.getY(), 0, 0, Misc.random(25));
                    CycleEventHandler.getSingleton().addEvent(axe, new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            handleAxeDamage(axe);
                            axe.setFacePlayer(false);
                            axe.setWalkDirection(Direction.fromDeltas(start, end));
                            axe.moveTowards(end.getX(), end.getY(), false, false);
                            if (container.getTotalExecutions() >= 10) {
                                axe.setInvisible(true);
                                axe.unregister();
                                SentAxes = false;
                                CycleEventHandler.getSingleton().stopEvents(axe);
                                container.stop();
                            }
                        }
                    },1);
                }

            }
    }

    private static void handleAxeDamage(NPC axe) {
        for (Player player : getTargets()) {
            if (!axe.isInvisible() && axe.isRegistered()) {
                if (player.getPosition().withinDistance(axe.getPosition(), 1)) {
                    if (player.protectingMelee()) {
                        player.appendDamage(axe,5, Hitmark.HIT);
                    } else {
                        player.appendDamage(axe,35, Hitmark.HIT);
                    }
                    handleBleedDamage(player);
                }
            }
        }
    }

    private static void handleStrange() {

    }

    private static void handleHeadGaze() {
        if (getTargets().isEmpty())
            return;

        for (Player player : getTargets()) {
            Position gazePos = new Position(Misc.random(1125, 1133), Misc.random(3414, 3422));
            NPC gaze = NPCSpawning.spawnNpc(12226, gazePos.getX(), gazePos.getY(), player.getHeight(), 0, 0);

            CycleEventHandler.getSingleton().addEvent(new java.lang.Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (container.getTotalExecutions() == 1) {
                        gaze.facePlayer(player.getIndex());
                    }

                    if (container.getTotalExecutions() == 2) {
                        RangeData.fireProjectileNPCtoPLAYER(gaze, player, 50, 70, 1379, 35, 0, 37, 10);
                    }

                    if (container.getTotalExecutions() == 4) {
                        if (!player.protectingRange()) {
                            CombatPrayer.resetOverHeads(player);
                            player.playerLevel[5] = player.playerLevel[5] > 0 ? (int) (player.playerLevel[5] * 0.5) : 0;
                            CombatPrayer.resetPrayers(player);
                            player.prayerId = -1;
                            player.getPA().refreshSkill(5);
                        }
                        gaze.unregister();
                        container.stop();
                    }
                }
            }, 1);
        }
    }

    private static void handleDartingSpikes(NPC npc) {
        List<Player> targets = getTargets();
        if (targets.isEmpty()) {
            return;
        }
        for (Player target : targets) {
            handleSpike(npc, target);
        }
        /*
        npc.setAutoRetaliate(false);
        CycleEventHandler.getSingleton().addEvent(npc, new CycleEvent() {
            Iterator<Player> iterator = targets.iterator(); // Iterator to loop over targets

            @Override
            public void execute(CycleEventContainer container) {
                if (iterator.hasNext()) {
                    Player target = iterator.next();

                    npc.startAnimation(10341);
                    // Move towards the target player
                    if (npc.getPosition().getY() > target.getPosition().getY()) {
                        npc.moveTowards(target.getX(), target.getY() - 1, true, false);
                    } else {
                        npc.moveTowards(target.getX(), target.getY() + 1, true, false);
                    }

                    // Check if NPC has reached the target player's position
                    if (npc.getPosition().withinDistance(target.getPosition(), 1)) {
                        // Handle spike at the player's position

                    }
                } else {
                    npc.setAutoRetaliate(true);
                    container.stop(); // Stop the event if all targets have been visited
                }
            }
        }, 1); // Check every tick*/
    }

    private static void handleSpike(NPC npc, Player player) {
        Position initialPosition = player.getPosition(); // Store initial position

        Position[] graphicPositions = generateRandomPositionsAroundPlayer(player.getPosition());
        Position[] allGraphicPositions = new Position[graphicPositions.length + 1];
        allGraphicPositions[0] = initialPosition; // Add player's initial position
        System.arraycopy(graphicPositions, 0, allGraphicPositions, 1, graphicPositions.length); // Copy other positions

        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (container.getTotalExecutions() == 1) {
                    for (Position position : allGraphicPositions) {
                        if (position != null) {
                            Server.playerHandler.sendStillGfx(new StillGraphic(2510, position), npc.getPosition());
                        }
                    }
                }

                if (container.getTotalExecutions() == 2) {
                    for (Position position : allGraphicPositions) {
                        if (player.getPosition().equals(position)) {
                            Server.playerHandler.sendStillGfx(new StillGraphic(2512, position), npc.getPosition());
                            player.appendDamage(npc, Misc.random(1, 15), Hitmark.HIT);
                        }
                    }
                    container.stop();
                }
            }
        }, 1); // Wait for 2 ticks
    }

    private static final Random random = new Random();

    private static Position[] generateRandomPositionsAroundPlayer(Position playerPosition) {
        Position[] positions = new Position[4];

        int minX = Math.max(playerPosition.getX() - 2, Boundary.VARDORVIS.getMinimumX());
        int maxX = Math.min(playerPosition.getX() + 2, Boundary.VARDORVIS.getMaximumX());
        int minY = Math.max(playerPosition.getY() - 2, Boundary.VARDORVIS.getMinimumY());
        int maxY = Math.min(playerPosition.getY() + 2, Boundary.VARDORVIS.getMaximumY());

        for (int i = 0; i < positions.length; i += 2) {
            int x = (i == 0) ? playerPosition.getX() - 1 : playerPosition.getX() + 1;
            int y = random.nextInt(maxY - minY + 1) + minY;
            positions[i] = new Position(x, y);
        }

        for (int i = 1; i < positions.length; i += 2) {
            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = (i == 1) ? playerPosition.getY() - 1 : playerPosition.getY() + 1;
            positions[i] = new Position(x, y);
        }

        return positions;
    }

    private static void handleBleedDamage(Player player) {
        player.getHealth().proposeStatus(HealthStatus.BLEED, 2, Optional.empty());
    }

    private static List<Player> getTargets() {
        ArrayList<Player> list = new ArrayList<>();

        for (Player player : PlayerHandler.getPlayers()) {
            if (player != null && Boundary.isIn(player, Boundary.VARDORVIS)) {
                list.add(player);
            }
        }

        return list;
    }
}
