package io.xeros.content.commands.all;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;

import java.util.Optional;

/**
 * Teleport the player to home.
 *
 * @author Emiel
 */
public class Toa extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
			c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
			return;
		}

		if (c.jailEnd > 1) {
			c.forcedChat("I'm trying to teleport away!");
			c.sendMessage("You are still jailed!");
			return;
		}
		if (c.getPosition().inWild() && c.wildLevel > 20) {
			if (c.petSummonId != 10533) {
				c.sendMessage("You can't use this command in the wilderness.");
				return;
			}
		}

		if (c.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
			c.getPA().spellTeleport(3357, 9120, 0, false);
		} else {
			c.getPA().spellTeleport(3357, 9120, 0, false);
		}
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to home area");
	}


	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}
}
