package io.xeros.content.commands.admin;

import io.xeros.Configuration;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * Toggles whether the Duel Arena is enabled or not.
 * 
 * @author Emiel
 */
public class Duelarena extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		Configuration.NEW_DUEL_ARENA_ACTIVE = !Configuration.NEW_DUEL_ARENA_ACTIVE;
		c.sendMessage("The duel arena is currently " + (Configuration.NEW_DUEL_ARENA_ACTIVE ? "Enabled" : "Disabled") + ".");
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.ADMINISTRATOR.isOrInherits(player);
	}
}
