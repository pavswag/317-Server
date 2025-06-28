package io.xeros.content.commands.donator;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * Teleports the player to the donator zone.
 * 
 * @author Emiel
 */
public class Donatorzone extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.inTrade || c.inDuel || c.getPosition().inWild()) {
			return;
		}
		c.getPA().startTeleport(1967, 5365, 0, "modern", false);
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return player.amDonated >= 20;
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to the donator zone");
	}

}
