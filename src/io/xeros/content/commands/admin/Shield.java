package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * LOOK MOM! I'M A SHIELD!
 * 
 * @author Emiel
 */
public class Shield extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.isNpc && c.npcId2 == 336) {
			c.isNpc = false;
		} else {
			c.npcId2 = 336;
			c.isNpc = true;
		}
		c.setUpdateRequired(true);
		c.appearanceUpdateRequired = true;
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.ADMINISTRATOR.isOrInherits(player);
	}
}
