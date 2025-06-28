package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class Anim extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		int id = Integer.parseInt(input);
		player.startAnimation(id);
		player.sendMessage("Playing animation: " + id);
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
	}

}
