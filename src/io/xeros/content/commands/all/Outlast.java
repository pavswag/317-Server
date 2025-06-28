package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;

import java.util.Optional;

/**
 * Open the forums in the default web browser.
 *
 * @author Emiel
 */
public class Outlast extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
			c.sendMessage("You cannot access this area.");
			return;
		}
		if (c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
			return;
		}
		c.sendMessage("@red@[OUTLAST]@blu@ Bank your items and enter the portal to join the tournament! Good Luck!");
		c.getPA().spellTeleport(3109, 3479, 0, false);
	}


	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Quick teleport to outlast teleport.");
	}

}
