package io.xeros.content.commands.moderator;

import io.xeros.content.commands.Command;
import io.xeros.content.commands.punishment.PunishmentCommand;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

/**
 * Forces a given player to log out.
 * 
 * @author Emiel
 */
public class UnGambleBan extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		new PunishmentCommand(commandName, input).parse(c);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Unban a player from gambling.");
	}

	@Override
	public String getFormat() {
		return PunishmentCommand.getFormat(getCommand());
	}


	@Override
	public boolean hasPrivilege(Player player) {
		return Right.HELPER.isOrInherits(player);
	}
}
