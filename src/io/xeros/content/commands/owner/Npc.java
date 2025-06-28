package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * Spawn a specific Npc.
 * 
 * @author Emiel
 *
 */
public class Npc extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		int newNPC = Integer.parseInt(input);
		if (newNPC > 0) {
//			NPCHandler.despawn(newNPC, c.heightLevel);
			NPC npc = NPCSpawning.spawnNpc(newNPC, c.absX, c.absY, c.heightLevel, 1, 10);
			npc.getBehaviour().setRespawn(true);
/*			NPCHandler.newNPC(spawn.id, spawn.position.getX(), spawn.position.getY(), spawn.position.getHeight(), walkingTypeOrdinal, NpcMaxHit.getMaxHit(spawn.id));
			NPCSpawning.spawnNpc(c, newNPC, c.absX, c.absY, c.heightLevel, 0, 7, false, false);*/
			c.sendMessage("You spawn npc " + NpcDef.forId(newNPC).getName() + ", "+ newNPC);
		} else {
			c.sendMessage("No such NPC.");
		}
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
	}
}
