package io.xeros.content.commands.owner;

import io.xeros.content.bosses.Cerberus;
import io.xeros.content.commands.Command;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.item.lootable.impl.TheatreOfBloodChest;
import io.xeros.model.Npcs;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;

/**
 * Update the shops.
 * 
 * @author Emiel
 *
 */
public class Tobchest extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		TheatreOfBloodChest.getRandomItems(player, 5);
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
	}
}
