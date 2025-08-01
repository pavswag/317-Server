package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.net.login.LoginThrottler;

public class Clearloginattempts extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		LoginThrottler.clear();
		c.sendMessage("Cleared all login attempts.");
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
	}

}
