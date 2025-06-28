package io.xeros.content.commands.moderator;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * Toggles whether the player will be visible or not.
 * 
 * @author Emiel
 */
public class Visible extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.isInvisible()) {
			c.setInvisible(false);
			c.sendMessage("You are no longer invisible.");
		} else {
			c.setInvisible(true);
			c.sendMessage("You are now invisible.");
		}
		c.getPA().requestUpdates();
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.HELPER.isOrInherits(player);
	}
}
