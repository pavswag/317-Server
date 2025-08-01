package io.xeros.model.entity.npc;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.model.entity.npc.data.NpcMaxHit;
import io.xeros.model.entity.player.Position;

import static io.xeros.model.entity.npc.NpcSpawnLoaderOSRS.initOsrsSpawns;

public class NpcSpawnLoader {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(NpcSpawnLoader.class.getName());
    private static final String DIRECTORY = "C:/Users/"+ Configuration.SAVES+"/Dropbox/etc/cfg/npc/spawns/";

    public static void load() {
        File[] files = Objects.requireNonNull(new File(DIRECTORY).listFiles());
        int loaded = 0;
        loaded += load(new File("C:/Users/"+Configuration.SAVES+"/Dropbox/etc/cfg/npc/npc_spawns.json"));
        for (File file : files) {
            loaded += load(file);
        }
        log.info("Spawned " + loaded + " npcs.");
    }

    private static int load(File file) {
        try (FileReader fr = new FileReader(file.getPath())) {
            List<NpcSpawn> list = new Gson().fromJson(fr, new TypeToken<List<NpcSpawn>>() {}.getType());
            for (var spawn : list) {
                int walkingTypeOrdinal = spawn.walkingType == null ? NpcWalkingType.WALK.ordinal() : spawn.walkingType.ordinal();
                NPCHandler.newNPC(spawn.id, spawn.position.getX(), spawn.position.getY(), spawn.position.getHeight(), walkingTypeOrdinal, NpcMaxHit.getMaxHit(spawn.id));
            }
            return list.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static class NpcSpawn {
        private final int id;
        private final Position position;
        private final NpcWalkingType walkingType;
        public NpcSpawn(int id, Position position, NpcWalkingType walkingType) {
            this.id = id;
            this.position = position;
            this.walkingType = walkingType;
        }

        @Override
        public String toString() {
            return "NpcSpawn{" + "id=" + id + ", position=" + position + ", walkingType=" + walkingType + '}';
        }
    }
}
