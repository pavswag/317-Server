package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

/**
 * Show the current position.
 * 
 * @author Noah
 *
 */
public class Announce extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		if (!player.announce) {
			player.announce = true;
			player.sendMessage("@blu@Global rare announcements are now @red@enabled.");
			return;
		} else {
			player.announce = false;
			player.sendMessage("@blu@Global rare announcements are now @gre@disabled.");
			return;
		}
	}


	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Toggle drop announcements.");
	}
}
