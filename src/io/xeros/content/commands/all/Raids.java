package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;

public class Raids extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.wildLevel > 20) {
			c.sendMessage("@red@You cannot teleport above 20 wilderness.");
			return;
		}
		if (c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
			return;
		}
		c.getPA().startTeleport(1234, 3567, 0, "modern", false);
		c.sendMessage("@red@You have been teleported to the raids lobby.");
	}


	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to raids.");
	}

}
