package io.xeros.content.bosses.dukesucellus;

import io.xeros.Server;
import io.xeros.annotate.PostInit;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.range.RangeData;
import io.xeros.content.commands.owner.Pos;
import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.impl.LegacySoloPlayerInstance;
import io.xeros.model.Graphic;
import io.xeros.model.Projectile;
import io.xeros.model.ProjectileBaseBuilder;
import io.xeros.model.StillGraphic;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.ImmutableItem;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.objects.ObjectAction;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 23/03/2024
 */
public class DukeSucellus extends LegacySoloPlayerInstance {
    public DukeSucellus(Player player, Boundary boundary) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_RESPAWN, player, boundary);
    }

    public static void enter(Player player, DukeSucellus instance) {
        try {
            instance.add(player);

            NPC duke = NPCSpawning.spawnNpc(player, 12191, 3036, 6452, instance.getHeight(), 0, 35, false, false);
                duke.revokeWalkingPrivilege = true;
                duke.applyDead = false;
                duke.needRespawn = false;

            instance.add(duke);

            player.getPA().movePlayer(3039, 6434, instance.getHeight());

            randomGasFlares(duke);

            GlobalObject vat_1 = new GlobalObject(47536, 3034, 6438, instance.getHeight(), 3, 10, -1);
            GlobalObject vat_2 = new GlobalObject(47536, 3043, 6438, instance.getHeight(), 1, 10, -1);
            GlobalObject pestle_mortar = new GlobalObject(47560, 3034, 6434, instance.getHeight(), 1, 10, -1);
            GlobalObject pickaxe = new GlobalObject(47561, 3044, 6434, instance.getHeight(), 1, 10, -1);
            GlobalObject mushroom_1 = new GlobalObject(47524, 3048, 6453, instance.getHeight(), 0, 10, -1);
            GlobalObject mushroom_2 = new GlobalObject(47528, 3030, 6453, instance.getHeight(), 0, 10, -1);
            GlobalObject salt_1 = new GlobalObject(47522, 3033, 6448, instance.getHeight(), 2, 10, -1);
            GlobalObject salt_2 = new GlobalObject(47522, 3043, 6448, instance.getHeight(), 0, 10, -1);
            GlobalObject Lower_eye_East = new GlobalObject(47544, 3049, 6441, instance.getHeight(),1,10,-1);
            GlobalObject Middle_eye_East = new GlobalObject(47546, 3049, 6445, instance.getHeight(),1,10,-1);
            GlobalObject Upper_eye_East = new GlobalObject(47545, 3049, 6449, instance.getHeight(),1,10,-1);

            GlobalObject Lower_eye_West = new GlobalObject(47545, 3028, 6441, instance.getHeight(),3,10,-1);
            GlobalObject Middle_eye_West = new GlobalObject(47544, 3028, 6445, instance.getHeight(),3,10,-1);
            GlobalObject Upper_eye_West = new GlobalObject(47546, 3028, 6449, instance.getHeight(),3,10,-1);

            Server.getGlobalObjects().add(vat_1);
            Server.getGlobalObjects().add(vat_2);
            Server.getGlobalObjects().add(pestle_mortar);
            Server.getGlobalObjects().add(pickaxe);
            Server.getGlobalObjects().add(mushroom_1);
            Server.getGlobalObjects().add(mushroom_2);
            Server.getGlobalObjects().add(salt_1);
            Server.getGlobalObjects().add(salt_2);

            player.getPA().object(vat_1);
            player.getPA().object(vat_2);
            player.getPA().object(pestle_mortar);
            player.getPA().object(pickaxe);
            player.getPA().object(mushroom_1);
            player.getPA().object(mushroom_2);
            player.getPA().object(salt_1);
            player.getPA().object(salt_2);

            Server.getGlobalObjects().add(Lower_eye_East);
            Server.getGlobalObjects().add(Middle_eye_East);
            Server.getGlobalObjects().add(Upper_eye_East);
            Server.getGlobalObjects().add(Lower_eye_West);
            Server.getGlobalObjects().add(Middle_eye_West);
            Server.getGlobalObjects().add(Upper_eye_West);

            redEye(instance);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public static void changeVats(GlobalObject object, Player p) {
        if (object.getObjectId() == 47536) object.setId(47539);
        else if (object.getObjectId() == 47539) object.setId(47536);
        Server.getGlobalObjects().add(object);
        p.getPA().object(object);
    }

    private static void randomGasFlares(NPC duke) {
        CycleEventHandler.getSingleton().addEvent(duke, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                for (int i = 0; i < 3; i++) {
                    Position rando = gasFlares[Misc.random(gasFlares.length-1)];
                    ignitePosition(rando, duke);
                }
            }
        }, 8);
    }

    public static void handleDukeAttacks(NPC duke) {
        int rng = Misc.random(0, 10);

        if (rng <= 5) slapFloorAttack(duke);
        else if (rng > 5 && rng < 8) {
            duke.startAnimation(10178);
            gasFlare(duke);
        } else basicAttackHandler(duke);
    }

    private static void basicAttackHandler(NPC duke) {
        duke.startAnimation(10178);
        for (Player player : duke.getInstance().getPlayers())
            if (player != null) {
                RangeData.fireProjectileNPCtoPLAYER(duke, player, 50, 70, 2435, 35, 0, 37, 10);
                if (player.protectingMagic()) player.appendDamage(duke, Misc.random(1, 25), Hitmark.HIT);
                else player.appendDamage(duke, Misc.random(1, 50), Hitmark.HIT);
            }
    }


    private static void slapFloorAttack(NPC duke) {
        duke.startAnimation(10176);
        duke.startGraphic(new Graphic(2439));
        int[] xxx = {3037, 3038, 3039, 3040, 3041};
        int[] yyy = {6451, 6451, 6451, 6451, 6451};
        for (int i = 0; i < xxx.length; i++)
            Server.playerHandler.sendStillGfx(new StillGraphic(2440, new Position(xxx[i], yyy[i], duke.getHeight())), duke.getInstance());
        CycleEventHandler.getSingleton().addEvent(duke, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (container.getTotalExecutions() == 2) {
                    int[] xx = {3036, 3037, 3038, 3039, 3040, 3041, 3042, 3042, 3035, 3043};
                    int[] yy = {6452, 6451};

                    List<Integer> x = Arrays.stream(xx).boxed().collect(Collectors.toList());
                    List<Integer> y = Arrays.stream(yy).boxed().collect(Collectors.toList());

                    for (Player player : duke.getInstance().getPlayers())
                        if (player != null && x.contains(player.getPosition().getX()) && y.contains(player.getPosition().getY()))
                            player.appendDamage(duke, 15, Hitmark.HIT);
                    container.stop();
                }
            }
        }, 1);
    }

    private static final Position[] gasFlares = {
            new Position(3036, 6442, 0),
            new Position(3039, 6442, 0),
            new Position(3042, 6442, 0),
            new Position(3042, 6446, 0),
            new Position(3039, 6446, 0),
            new Position(3036, 6446, 0),
            new Position(3036, 6450, 0),
            new Position(3039, 6450, 0),
            new Position(3042, 6450, 0)};
    private static void gasFlare(NPC duke) {
        int duke_hp = duke.getHealth().getCurrentHealth();
        double healthPercentage = (double) duke_hp / duke.getHealth().getMaximumHealth() * 100;
        Position dukePosition = duke.getPosition();

        if (healthPercentage < 50) {
            List<Position> closestPositions = findClosestPositions(dukePosition);
            for (Position closestPosition : closestPositions)
                Projectile.createTargeted(duke, closestPosition, new ProjectileBaseBuilder().setProjectileId(2436).setCurve(0).setSpeed(30).createProjectileBase()).send(null);
            ignitePositions(closestPositions, duke);
        } else {
            Position closestPosition = findClosestPosition(dukePosition);
            Projectile.createTargeted(duke, closestPosition, new ProjectileBaseBuilder().setProjectileId(2436).setCurve(0).setSpeed(30).createProjectileBase()).send(null);
            ignitePosition(closestPosition, duke);
        }
    }
    private static Position findClosestPosition(Position targetPosition) {
        Position closestPosition = null;
        double minDistance = Double.MAX_VALUE;

        for (Position position : DukeSucellus.gasFlares) {
            double distance = position.distanceTo(targetPosition);
            if (distance < minDistance) {
                minDistance = distance;
                closestPosition = position;
            }
        }

        return closestPosition;
    }
    private static List<Position> findClosestPositions(Position targetPosition) {
        Comparator<Position> comparator = Comparator.comparingDouble(position -> position.distanceTo(targetPosition));
        Arrays.sort(DukeSucellus.gasFlares, comparator);
        return new ArrayList<>(Arrays.asList(DukeSucellus.gasFlares).subList(0, 2));
    }
    private static void ignitePositions(List<Position> positions, NPC duke) {
        for (Position position : positions) ignitePosition(position, duke);
    }
    private static void ignitePosition(Position position, NPC duke) {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                dealDamageToPlayers(position, duke);
                if (container.getTotalExecutions() == 2) sendStillGraphic(2431, position);
                if (container.getTotalExecutions() == 4) sendStillGraphic(2432, position);
                if (container.getTotalExecutions() == 6) {
                    sendStillGraphic(2433, position);
                    container.stop();
                }
            }
        }, 2);
    }

    private static void sendStillGraphic(int graphicId, Position position) {
        Server.playerHandler.sendStillGfx(new StillGraphic(graphicId, position), position);
    }

    private static void dealDamageToPlayers(Position position, NPC duke) {
        if (duke.getInstance() == null) return;
        if (duke.getInstance().getPlayers().isEmpty()) return;
        for (Player player : duke.getInstance().getPlayers())
            if (player != null && player.getPosition().withinDistance(position, 1))
                player.appendDamage(duke, Misc.random(5, 10), Hitmark.HIT);
    }
    private static void redEye(DukeSucellus duke) {
        GlobalObject Lower_eye_East = Server.getGlobalObjects().get(47544, 3049, 6441, duke.getHeight());
        GlobalObject Middle_eye_East = Server.getGlobalObjects().get(47546, 3049, 6445, duke.getHeight());
        GlobalObject Upper_eye_East = Server.getGlobalObjects().get(47545, 3049, 6449, duke.getHeight());

        GlobalObject Lower_eye_West = Server.getGlobalObjects().get(47545, 3028, 6441, duke.getHeight());
        GlobalObject Middle_eye_West = Server.getGlobalObjects().get(47544, 3028, 6445, duke.getHeight());
        GlobalObject Upper_eye_West = Server.getGlobalObjects().get(47546, 3028, 6449, duke.getHeight());

        AtomicInteger animationIndex = new AtomicInteger(0);
        int[] animationOrder = {1, 2, 3, 2}; // 1: Lower, 2: Middle, 3: Upper

        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (duke.getNpcs().stream().anyMatch(npc -> npc.getNpcId() == 12191)) {
                    container.stop();
                    return;
                }

                int index = animationIndex.getAndIncrement() % animationOrder.length;
                switch (animationOrder[index]) {
                    case 1:
                        duke.getPlayers().forEach(player -> {
                            if (player != null) {
                                player.getPA().sendPlayerObjectAnimation(Lower_eye_West, 10188);
                                player.getPA().sendPlayerObjectAnimation(Lower_eye_East, 10187);
                            }
                        });
                        break;
                    case 2:
                        duke.getPlayers().forEach(player -> {
                            if (player != null) {
                                player.getPA().sendPlayerObjectAnimation(Middle_eye_East, 10189);
                                player.getPA().sendPlayerObjectAnimation(Middle_eye_West, 10187);
                            }
                        });
                        break;
                    case 3:
                        duke.getPlayers().forEach(player -> {
                            if (player != null) {
                                player.getPA().sendPlayerObjectAnimation(Upper_eye_West, 10188);
                                player.getPA().sendPlayerObjectAnimation(Upper_eye_East, 10189);
                            }
                        });
                        break;
                }
            }
        }, 3); // Change the delay here if needed (in ticks)

        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                int idx = animationIndex.get() % animationOrder.length;
                switch (animationOrder[idx]) {
                    case 1:
                        for (Player player : duke.getPlayers())
                            if (player != null)
                                if (player.getPosition().equals(new Position(3030, 6442, player.getHeight())) ||
                                        player.getPosition().equals(new Position(3031, 6442, player.getHeight())) ||
                                        player.getPosition().equals(new Position(3032, 6442, player.getHeight())) ||
                                        player.getPosition().equals(new Position(3046, 6442, player.getHeight())) ||
                                        player.getPosition().equals(new Position(3047, 6442, player.getHeight())) ||
                                        player.getPosition().equals(new Position(3048, 6442, player.getHeight())))
                                    player.appendDamage(Misc.random(40, 69), Hitmark.HIT);
                        break;
                    case 2:
                        for (Player player : duke.getPlayers())
                            if (player != null)
                                if (player.getPosition().equals(new Position(3030, 6446, player.getHeight())) ||
                                        player.getPosition().equals(new Position(3031, 6446, player.getHeight())) ||
                                        player.getPosition().equals(new Position(3032, 6446, player.getHeight())) ||
                                        player.getPosition().equals(new Position(3046, 6446, player.getHeight())) ||
                                        player.getPosition().equals(new Position(3047, 6446, player.getHeight())) ||
                                        player.getPosition().equals(new Position(3048, 6446, player.getHeight())))
                                    player.appendDamage(Misc.random(40, 69), Hitmark.HIT);
                        break;
                    case 3:
                        for (Player player : duke.getPlayers())
                            if (player != null)
                                if (player.getPosition().equals(new Position(3030, 6450, player.getHeight())) ||
                                        player.getPosition().equals(new Position(3031, 6450, player.getHeight())) ||
                                        player.getPosition().equals(new Position(3032, 6450, player.getHeight())) ||
                                        player.getPosition().equals(new Position(3046, 6450, player.getHeight())) ||
                                        player.getPosition().equals(new Position(3047, 6450, player.getHeight())) ||
                                        player.getPosition().equals(new Position(3048, 6450, player.getHeight())))
                                    player.appendDamage(Misc.random(40, 69), Hitmark.HIT);
                        break;
                }
            }
        }, 1);
    }
}
