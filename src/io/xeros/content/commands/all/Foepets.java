package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

/**
 * Open the forums in the default web browser.
 * 
 * @author Emiel
 */
public class Foepets extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		  /*c.getPA()
          .sendFrame126(
                  "https://arkcane.net/threads/foundry-basic-information-pet-information.25/", 12000);*/
}


	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens a web page with foe pet benefits.");
	}

}
