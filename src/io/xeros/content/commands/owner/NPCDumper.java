package io.xeros.content.commands.owner;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.collisionmap.RegionProvider;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NpcSpawnLoader;
import io.xeros.model.entity.npc.NpcWalkingType;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.GameItem;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class NPCDumper extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        for (int i = 0; i < NPCHandler.npcs.length; i++) {
            NPC npc = NPCHandler.getNpc(i);
            if (npc != null) {
                if (Boundary.getWildernessLevel(npc.getX(), npc.getY()) > 0) {
                    for (GameItem allNPCdrop : Server.getDropManager().getAllNPCdrops(npc.getNpcId())) {
                        System.out.println("NPC ID : " + allNPCdrop.getId()+ ", " +allNPCdrop.getDef().getName() +", " + allNPCdrop.rarity);
                    }
                }
            }
        }
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

}
