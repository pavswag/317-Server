package io.xeros.content.commands.moderator;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * Changes the title of the player to their default staff title.
 * 
 * @author Emiel
 */
public class Stafftitle extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.sendMessage("You will now get your staff title instead. Relog for changes to take effect.");
		c.keepTitle = false;
		c.killTitle = false;
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.MODERATOR.isOrInherits(player);
	}
}
