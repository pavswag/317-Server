package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class Getclip extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		String[] args = input.split(" ");
		int x = Integer.parseInt(args[0]);
		int y = Integer.parseInt(args[1]);
		int z = Integer.parseInt(args[2]);
		System.out.println("Clip: " + c.getRegionProvider().getClipping(x, y, z));
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
	}

}
