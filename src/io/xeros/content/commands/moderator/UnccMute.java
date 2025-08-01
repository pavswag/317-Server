package io.xeros.content.commands.moderator;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.content.commands.punishment.PunishmentCommand;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;

/**
 * Forces a given player to log out.
 * 
 * @author Emiel
 */
public class UnccMute extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		new PunishmentCommand(commandName, input).parse(c);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Unmutes a player from the help cc.");
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
