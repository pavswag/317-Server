package io.xeros.content.commands.owner;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.npc.NpcSpawnLoader;
import io.xeros.model.entity.npc.NpcWalkingType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.entity.player.Right;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class buildnpc extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        int newNPC = Integer.parseInt(input);
        Path path = Paths.get(Server.getDataDirectory() + "/dump/new_npcs.json");
        File file = path.toFile();
        if (!file.exists()) {
            Preconditions.checkState(file.mkdirs());
        }

        try  {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileReader fr = new FileReader(path.toString());
            List<NpcSpawnLoader.NpcSpawn> listx = new Gson().fromJson(fr, new TypeToken<List<NpcSpawnLoader.NpcSpawn>>() {}.getType());
            NpcSpawnLoader.NpcSpawn newnpc = new NpcSpawnLoader.NpcSpawn(newNPC, player.getPosition(), NpcWalkingType.WALK);
            ArrayList<NpcSpawnLoader.NpcSpawn> list = new ArrayList<>();
            if (listx != null && !listx.isEmpty()) {
                list.addAll(listx);
            }
            list.add(newnpc);
            System.out.println("Added new NPC "+ newNPC +", to the new_npcs.json file.");
            player.sendMessage("Written NPC " + newNPC + ", to file.");
            FileWriter writer = new FileWriter(file.getPath());

            writer.write(gson.toJson(list));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

}
