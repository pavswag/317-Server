package io.xeros.content.commands.all;

import io.xeros.Configuration;
import io.xeros.content.commands.Command;
import io.xeros.content.itemskeptondeath.ItemsKeptOnDeathInterface;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;

import java.util.Optional;

/**
 * Changes the password of the player.
 * 
 * @author Emiel
 *
 */
public class Skull extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.getPosition().inWild()) {
			c.sendMessage("You cannot use this command in the wilderness.");
			return;
		}
		if (c.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
			return;
		}
		if (c.isSkulled) {
			c.isSkulled = false;
			c.skullTimer = 0;
			c.headIconPk = -1;
			c.getPA().requestUpdates();
			c.sendMessage("You are no longer skulled.");
			ItemsKeptOnDeathInterface.refreshIfOpen(c);
			return;
		}
		c.isSkulled = true;
		c.skullTimer = Configuration.SKULL_TIMER;
		c.headIconPk = 0;
		c.getPA().requestUpdates();
		c.sendMessage("You are now skulled.");
		ItemsKeptOnDeathInterface.refreshIfOpen(c);
	}


	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Puts a skull above your head..");
	}
}
