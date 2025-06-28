package io.xeros.content.commands.owner;

import io.xeros.content.combat.Hitmark;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;

/**
 * Kill a player.
 *
 * @author Emiel
 */
public class Kill extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		Player player = PlayerHandler.getPlayerByDisplayName(input);
		if (player == null) {
			c.sendMessage("Player is null.");
			return;
		}
		if (player.invincible || player.inGodmode()) {
			player.invincible = false;
			player.setGodmode(false);
		}
		player.appendDamage(player.getHealth().getMaximumHealth(), Hitmark.HIT);
		player.sendMessage("You have been merked");
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
	}
}
