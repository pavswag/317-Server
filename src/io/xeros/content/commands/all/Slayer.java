package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;

import java.util.Optional;

/**
 * Changes the password of the player.
 *
 * @author Emiel
 *
 */
public class Slayer extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.getPosition().inWild()) {
			c.sendMessage("You can only use this command outside the wilderness.");
			return;
		}
		if (c.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
			c.getPA().startTeleport(3135, 3628, 0, "modern", false);
			return;
		}
		c.getPA().startTeleport(3100, 3507, 0, "modern", false);
	}


	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to slayer area.");
	}
}
