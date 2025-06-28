package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

/**
 * Opens the store page in the default web browser.
 * 
 * @author Emiel
 */
public class Donate extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.getPA().sendFrame126("https://Turmoilrsps.everythingrs.com/services/store", 12000);
	}
	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens store");
	}


	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}
}
