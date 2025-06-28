package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * Open a specific shop.
 * 
 * @author Emiel
 *
 */
public class Shop extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		try {
			c.getShops().openShop(Integer.parseInt(input));
			c.sendMessage("You successfully opened shop #" + input + ".");
		} catch (IndexOutOfBoundsException ioobe) {
			c.sendMessage("Error. Correct syntax: ::shop shopid");
		}
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
	}
}
