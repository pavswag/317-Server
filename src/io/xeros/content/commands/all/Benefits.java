package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Open the forums in the default web browser.
 * 
 * @author Emiel
 */
public class Benefits extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
    }


	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens a web page with donator benefits");
	}

}
