package io.xeros.content.commands.owner;

import io.xeros.Configuration;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * Toggle the Cyber Monday sale on or off.
 * 
 * @author Emiel
 *
 */
public class Cyber extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		Configuration.CYBER_MONDAY = !Configuration.CYBER_MONDAY;
		String status = Configuration.CYBER_MONDAY ? "On" : "Off";
		c.sendMessage("Cyber monday: " + status);
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
	}
}
