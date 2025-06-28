package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class Post extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		c.getTradePost().openMyOffers();
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
	}
}
