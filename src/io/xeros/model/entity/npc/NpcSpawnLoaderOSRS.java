package io.xeros.model.entity.npc;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.model.entity.npc.data.NpcMaxHit;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NpcSpawnLoaderOSRS {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(NpcSpawnLoaderOSRS.class.getName());
    private static final String DIRECTORY = "C:/Users/"+ Configuration.SAVES+"/Dropbox/etc/cfg/npc/osrsspawns/";

    public static void initOsrsSpawns() {
        File[] files = Objects.requireNonNull(new File(DIRECTORY).listFiles());
        int loaded = Arrays.stream(files).parallel().mapToInt(NpcSpawnLoaderOSRS::loadOsrsSpawns).sum();
        log.info("Spawned " + loaded + " OSRS npcs.");
    }

    public static int loadOsrsSpawns(File file) {
        try (FileReader fr = new FileReader(file.getPath())) {
            List<Spawn> list = new Gson().fromJson(fr, new TypeToken<List<Spawn>>() { }.getType());
            for (var spawn : list) {
                if (spawn.walkRange > 0) {
                    switch (spawn.direction) {
                        case "N":
                        case "W":
                        case "S":
                        case "E":
                            NPCHandler.newNPC(spawn.id, spawn.x, spawn.y, spawn.z, NpcWalkingType.WALK.ordinal(), NpcMaxHit.getMaxHit(spawn.id));
                            break;
                    }
                    continue;
                }

                switch (spawn.direction) {
                    case "N":
                        NPCHandler.newNPC(spawn.id, spawn.x, spawn.y, spawn.z, NpcWalkingType.FACE_NORTH.ordinal(), NpcMaxHit.getMaxHit(spawn.id));
                        break;
                    case "E":
                        NPCHandler.newNPC(spawn.id, spawn.x, spawn.y, spawn.z, NpcWalkingType.FACE_EAST.ordinal(), NpcMaxHit.getMaxHit(spawn.id));
                        break;
                    case "S":
                        NPCHandler.newNPC(spawn.id, spawn.x, spawn.y, spawn.z, NpcWalkingType.FACE_SOUTH.ordinal(), NpcMaxHit.getMaxHit(spawn.id));
                        break;
                    case "W":
                        NPCHandler.newNPC(spawn.id, spawn.x, spawn.y, spawn.z, NpcWalkingType.FACE_WEST.ordinal(), NpcMaxHit.getMaxHit(spawn.id));
                        break;
                }
            }
            return list.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static final class Spawn {
        @Expose
        public String name;
        @Expose
        public int id;
        @Expose
        public int x, y, z;
        @Expose
        public String direction = "S";
        @Expose
        public int walkRange;
        @Expose
        public String world;
    }
}
