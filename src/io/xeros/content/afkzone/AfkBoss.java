package io.xeros.content.afkzone;

import io.xeros.Server;
import io.xeros.annotate.Init;
import io.xeros.annotate.PostInit;
import io.xeros.content.battlepass.Pass;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.death.NPCDeath;
import io.xeros.content.commands.owner.Pos;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.*;
import io.xeros.model.entity.player.broadcasts.Broadcast;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAction;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.objects.ObjectAction;
import io.xeros.sql.dailytracker.TrackerType;
import io.xeros.util.Location3D;
import io.xeros.util.discord.Discord;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 30/01/2024
 */

public class AfkBoss {

    private static int Goblin = 655;

    public static int GoblinSpawnAmount = 0;

    public static boolean GoblinActive = false;
    private static NPC goblinBoss;

    public static ArrayList<String> IPAddress = new ArrayList<>();
    public static ArrayList<String> MACAddress = new ArrayList<>();
    private static LocalDateTime checkDate = LocalDateTime.now();
    public static HashMap<Player, Integer> damageCount = new HashMap<>();

    public static void spawnGoblin() {
        if (GoblinActive) {
            return;
        }
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }

        goblinBoss = NPCSpawning.spawnNpc(Goblin, 2118, 5510, 0, 0, 0);
        goblinBoss.getBehaviour().setRespawn(false);
        goblinBoss.getBehaviour().setAggressive(true);
        goblinBoss.getBehaviour().setRunnable(true);
        goblinBoss.getHealth().setMaximumHealth(30000);
        goblinBoss.getHealth().reset();

        GoblinActive = true;

        announce();
        Discord.writeBugMessage("[Goblin] has spawned!, ::afk in-game to access him! @News-Event");
        TrackerType.AFK_BOSS.addTrackerData(1);
    }

    public static void announce() {
        new Broadcast("[Goblin] has spawned!, ::afk to access him!").addTeleport(new Position(2120, 5519, 0)).copyMessageToChatbox().submit();
    }

    public static void tick() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Duration duration = Duration.between(checkDate, currentDateTime);

        // Check if the duration is more than 24 hours
        if (duration.toHours() >= 24) {
            // Reset data and update checkDate
            IPAddress.clear();
            MACAddress.clear();
            checkDate = currentDateTime;
        }
    }

    public static void handleGoblinTick() {
        tick();
        GoblinSpawnAmount += 1;

        if (GoblinSpawnAmount >= (2500 + (500 * PlayerHandler.getPlayers().size()))) { // 2500 was originally 5000
            spawnGoblin();
            GoblinSpawnAmount = 0;
            handleAfkAccounts(false);
        }
    }

    public static void handleRewards() {
        damageCount.forEach((player, integer) -> {
            if (player != null) {
                if (integer > 1) {
                    int amountOfDrops = 2;
                    if (NPCDeath.isDoubleDrops()) {
                        amountOfDrops++;
                    }
                    Pass.addExperience(player, 10);
                    Server.getDropManager().create(player, goblinBoss, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 655);
                }
            }
        });
        despawn();
    }

    public static void despawn() {
        GoblinActive = false;
        if (goblinBoss != null) {
            if (goblinBoss.getIndex() > 0) {
                goblinBoss.unregister();
            }
            goblinBoss = null;
        }
        handleAfkAccounts(true);
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
    }

    public static int getGoblinSpawnAmount() {
        return (((2500 + (500 * PlayerHandler.getPlayers().size())) - AfkBoss.GoblinSpawnAmount)); // 2500 was originally 5000
    }

    public static boolean hasVoted(Player player) {
        if (true) {
            return true;
        }
        return IPAddress.contains(player.getIpAddress()) || MACAddress.contains(player.getMacAddress()) || player.getRights().isOrInherits(Right.STAFF_MANAGER);
    }

    private static void handleAfkAccounts(boolean death) {
        if (!death) {
            for (Player player : PlayerHandler.getPlayers()) {
                if (player == null)
                    continue;
                if (!Boundary.isIn(player, Boundary.AFK_ZONE))
                    continue;
                if (!hasVoted(player))
                    continue;
                if (player.afk_position == null)
                    continue;

                CycleEventHandler.getSingleton().stopEvents(player);
                player.afk_position = player.getPosition();
                player.moveTo(new Position(2120, 5514, 0));
                player.attackEntity(goblinBoss);
            }
        } else {
            for (Player player : PlayerHandler.getPlayers()) {
                if (player == null)
                    continue;
                if (!Boundary.isIn(player, Boundary.AFK_ZONE_BOSS))
                    continue;
                if (!hasVoted(player))
                    continue;
                if (player.afk_position == null)
                    continue;

                player.moveTo(player.afk_position);
                Afk.Start(player, new Location3D(player.afk_obj_position.getX(), player.afk_obj_position.getY(), 0), player.afk_object);
            }
        }
    }
}
