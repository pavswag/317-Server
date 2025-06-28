package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;

/**
 * Changes the password of the player.
 * 
 * @author Emiel
 *
 */
public class Train extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.getPosition().inWild()) {
			c.sendMessage("You can only use this command outside the wilderness.");
			return;
		}
		if (c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
			c.sendMessage("You cannot access this area.");
			return;
		}
		c.getPA().startTeleport(2673, 3710, 0, "modern", false);
	}


	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Takes you to rock crabs.");
	}
}
