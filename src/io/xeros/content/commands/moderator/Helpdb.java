package io.xeros.content.commands.moderator;

import io.xeros.content.commands.Command;
import io.xeros.content.help.HelpDatabase;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * Opens an interface containing all help tickets.
 * 
 * @author Emiel
 */
public class Helpdb extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		HelpDatabase.getDatabase().openDatabase(c);
	}


	@Override
	public boolean hasPrivilege(Player player) {
		return Right.HELPER.isOrInherits(player);
	}
}
