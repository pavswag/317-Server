package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Teleport the player to the mage bank.
 * 
 * @author Emiel
 */
public class Crystal extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
//    	c.getPA().sendFrame126("https://ArkCane.net", 12000);
	}


	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens up the crystal guide.");
	}

}
