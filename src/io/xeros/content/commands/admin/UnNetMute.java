package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.commands.punishment.PunishmentCommand;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

public class UnNetMute extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		new PunishmentCommand(commandName, input).parse(c);
	}

	@Override
	public String getFormat() {
		return PunishmentCommand.getFormat(getCommand());
	}

	public Optional<String> getDescription() {
		return Optional.of("Remove net mute for online player");
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.ADMINISTRATOR.isOrInherits(player);
	}
}
