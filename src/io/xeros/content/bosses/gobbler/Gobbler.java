package io.xeros.content.bosses.gobbler;

import io.xeros.Server;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Location3D;
import io.xeros.util.Misc;

import java.util.concurrent.TimeUnit;

public class Gobbler {

    public static long last_spawn;
    public static boolean spawned;
    public static NPC gobbler;
    public static long delay = 0;
    public static String SpawnLocation;


    public static void ticker() {
        if (delay == 0) {
            delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1);
        }
        spawn();
    }

    public static void spawn() {
        if (!spawned && last_spawn < System.currentTimeMillis() && delay < System.currentTimeMillis()) {
            Spawns spawn = Spawns.values()[Misc.random(Spawns.values().length - 1)];

            gobbler = NPCSpawning.spawn(6306, spawn.x, spawn.y, 0, 1, 5,false);
            gobbler.getBehaviour().setRespawn(false);
            gobbler.getBehaviour().setAggressive(true);
            spawned = true;
            delay = 1;
            SpawnLocation = spawn.location;
            last_spawn = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
            PlayerHandler.executeGlobalMessage("@cr28@[@red@Evil Chicken@bla@] @red@has just spawned " + spawn.location);
        }
    }

    public static void handleDeath(int killerID) {
        Player target = PlayerHandler.players[killerID];
        if (target != null) {
            target.setSeasonalPoints(target.getSeasonalPoints() + 2);
            target.sendMessage("You where awarded 2 seasonal point's for killing Evil Chicken.");
            Server.getDropManager().create(target, gobbler, new Location3D(target.getX(), target.getY(), target.getHeight()), 1, 6306);
            target.getNpcDeathTracker().add(NpcDef.forId(6306).getName(), NpcDef.forId(6306).getCombatLevel(), 0);
        }
        spawned = false;
    }

}
