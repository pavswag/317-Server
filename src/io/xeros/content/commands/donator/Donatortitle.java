package io.xeros.content.commands.donator;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * Changes the title of the player to their default donator title.
 * 
 * @author Emiel
 */
public class Donatortitle extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.sendMessage("You will now get your donator title instead. Relog for changes to take effect.");
		c.keepTitle = false;
		c.killTitle = false;
	}
	@Override
	public boolean hasPrivilege(Player player) {
		return player.amDonated >= 20;
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Changes your player title");
	}

	@Override
	public Optional<String> getParameter() {
		return Optional.of("title");
	}

}
