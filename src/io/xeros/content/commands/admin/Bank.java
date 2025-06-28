package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.commands.punishment.PunishmentCommand;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

/**
 * Open the banking interface.
 * 
 * @author Emiel
 */
public class Bank extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.getPA().player.itemAssistant.openUpBank();
		c.inBank = true;
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.ADMINISTRATOR.isOrInherits(player);
	}
}
