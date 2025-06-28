package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class Max extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		for (int i = 0; i < 24; i++) {
			player.playerLevel[i] = 99;
			player.playerXP[i] = player.getPA().getXPForLevel(99) + 1;
			player.getPA().refreshSkill(i);
			player.getPA().setSkillLevel(i, player.playerLevel[i], player.playerXP[i]);
			player.getPA().levelUp(i);
		}
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
	}

}
