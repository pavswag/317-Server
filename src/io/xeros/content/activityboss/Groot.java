package io.xeros.content.activityboss;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.battlepass.Pass;
import io.xeros.content.collection_log.CollectionLog;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.death.NPCDeath;
import io.xeros.content.combat.npc.NPCAutoAttack;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.content.commands.admin.dboss;
import io.xeros.content.commands.owner.Npc;
import io.xeros.content.seasons.Halloween;
import io.xeros.model.*;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import io.xeros.sql.dailytracker.TrackerType;
import io.xeros.util.Location3D;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.xeros.model.Npcs.*;

public class Groot {

    public static int ActivityPoints = 6000;

    public static boolean spawned;
    public static boolean alive;

    public static void handlePointIncrease(NPC npc, Player player) {
        if (alive || spawned) {
            return;
        }
        boolean thursday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY;
        int times = 1;

        if (thursday && Halloween.DoubleGroot) {
            times = 4;
        } else if (thursday || Halloween.DoubleGroot) {
            times = 2;
        }

        //Having lil' groot removes 2x activity requirement.
        if (CollectionLog.collectionNPCS.get(CollectionLog.CollectionTabType.WILDERNESS).contains(npc.getNpcId())) {
            ActivityPoints -= 5 * times;
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 10 * times;
            }
        } else if (npc.getNpcId() == NEX || npc.getNpcId() == PHOSANIS_NIGHTMARE || npc.getNpcId() == 9425) {
            ActivityPoints -= 3* times;
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 6* times;
            }
        } else if (npc.getNpcId() == 1101) {
            ActivityPoints -= 15* times; // arbograve
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 30* times;
            }
        } else if (npc.getNpcId() == 5169) {
            ActivityPoints -= 20* times; // durial
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 40* times;
            }
        } else if (npc.getNpcId() == 5126) {
            ActivityPoints -= 30* times; // vote
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 60* times;
            }
        } else if (npc.getNpcId() == 8096) {
            ActivityPoints -= 100 * times; // donor
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 200 * times;
            }
        } else if (CollectionLog.collectionNPCS.get(CollectionLog.CollectionTabType.BOSSES).contains(npc.getNpcId())) {
            ActivityPoints -= times; // all bosses in "bosses collectiong log"
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= times;
            }
        } else if (npc.getNpcId() == 3601) {
            ActivityPoints -= times; // Unicow's
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 2*times;
            }
        } else if (npc.getNpcId() == 8781 || npc.getNpcId() == 10531 || npc.getNpcId() == 10532) {
            ActivityPoints -= 10*times; // Unicow's
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= 20*times;
            }
        } else {
            ActivityPoints -= times;
            if (player.hasFollower && player.petSummonId == 33208 || player.getItems().playerHasItem(30122) || player.getItems().playerHasItem(33208)) {
                ActivityPoints -= times;
            }
        }

        if (ActivityPoints <= 0) {
            ActivityPoints = 6000;
            spawnGroot();
        }

    }

    public static List<Player> targets = new ArrayList<>();
    public static HashMap<Player, Integer> damageCount = new HashMap<>();
    public static final Boundary BOUNDARY = Boundary.GROOT_BOSS;
    private static NPC Groot;

    public static void spawnGroot() {
        /*if (alive || spawned) {*/
        /*    return;*/
        /*}*/
        if (!targets.isEmpty()) {
            targets.clear();
        }
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
        Groot = NPCSpawning.spawn(4923, 2537, 3167, 0, 1, 30, true);
        Groot.getBehaviour().setRespawn(false);
        Groot.getBehaviour().setAggressive(true);
        Groot.getBehaviour().setRunnable(true);
        Groot.getHealth().setMaximumHealth(30000);
        Groot.getHealth().reset();
        alive = true;
        spawned = true;
        announce();
        Discord.writeBugMessage("[Groot] has spawned!, use ;;groot in-game to access him! @News-Event");
        TrackerType.GROOT.addTrackerData(1);
    }

    public static void announce() {
        new Broadcast("[Groot]@red@ has spawned!, use ;;groot to access him!").addTeleport(new Position(2523, 3167, 0)).copyMessageToChatbox().submit();
    }

    public static void updateTargets() {
        if (Groot == null) {
            return;
        }
        if (Groot.isDead) {
            return;
        }
        if (!targets.isEmpty()) {
            targets.clear();
        }

        targets = PlayerHandler.getPlayers().stream().filter(plr ->
                !plr.isDead && BOUNDARY.in(plr) && plr.getHeight() == 0).collect(Collectors.toList());
    }

    public static void handleDeath(NPC npc) {
        if (npc.getNpcId() == 4923) {
            spawned = false;
            alive = false;
            PlayerHandler.executeGlobalMessage("@cr22@[@gre@Groot@bla@] @red@Groot has been burnt down!@cr22@");
            HashMap<String, Integer> map = new HashMap<>();
            damageCount.forEach((p, i) -> {
                if (map.containsKey(p.getUUID())) {
                    map.put(p.getUUID(), map.get(p.getUUID()) + 1);
                } else {
                    map.put(p.getUUID(), 1);
                }
            });

            for (String s : map.keySet()) {
                if (map.containsKey(s) && map.get(s) > 1) {
                    for (Player player : PlayerHandler.getPlayers()) {
                        if (player.getUUID().equalsIgnoreCase(s)) {
                            Discord.writeServerSyncMessage("[@gre@Groot@bla@] " + player.getDisplayName() + " has tried to take more than 2 account's there!");
                        }
                    }
                }
            }

            map.values().removeIf(integer -> integer > 1);

            damageCount.forEach((player, integer) -> {
                if (integer > 100 && map.containsKey(player.getUUID())) {
                    int amountOfDrops = 1;
                    if (NPCDeath.isDoubleDrops()) {
                        amountOfDrops++;
                    }
                    Pass.addExperience(player, 5);
                    Server.getDropManager().create(player, npc, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 4923);
                    player.getNpcDeathTracker().add(npc.getName(), npc.getDefinition().getCombatLevel(), 1);
                }

                PetHandler.rollOnNpcDeath(player, npc);
            });
            reset();
        }
    }

    private static void reset() {
        if (Groot != null) {
            if (Groot.getIndex() > 0) {
                Groot.unregister();
            }
            Groot = null;
        }
        if (!targets.isEmpty()) {
            targets.clear();
        }
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
    }

    public static void attack(NPC npc) {
        int rng = Misc.random(0, 100);
                updateTargets();
        if (rng < 85) {
            npc.startAnimation(245);
            npc.setAttackType(CombatType.MELEE);
            npc.hitDelayTimer = 2;
            npc.attackTimer = 4;
            npc.forceChat("I am groot!");
            for (Player target : targets) {
                int dmg = Misc.random(0,15);

                if (target.protectingMelee()) {
                    dmg /= 2;
                }

                target.appendDamage(Groot, dmg, Hitmark.HIT);
            }
        } else {
            npc.startAnimation(8979);
            npc.setAttackType(CombatType.RANGE);
            npc.hitDelayTimer = 2;
            npc.attackTimer = 4;
            npc.forceChat("I AM GROOT!");
            for (Player target : targets) {
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (container.getTotalExecutions() == 0); {
                            target.startGraphic(new Graphic(2145));
                        }

                        if (container.getTotalExecutions() == 1) {
                            target.sendMessage("[@gre@Groot@bla@] @red@You've just been covered in shit!");
                        }

                        if (container.getTotalExecutions() % 3 == 0) {
                            if (target.protectingRange()) {
                                target.appendDamage(Groot, 1, Hitmark.HIT);
                            } else {
                                target.appendDamage(Groot, 3, Hitmark.HIT);
                            }
                        }

                        if (container.getTotalExecutions() == 10) {
                            container.stop();
                        }
                    }
                },1);
            }
        }
    }


}
