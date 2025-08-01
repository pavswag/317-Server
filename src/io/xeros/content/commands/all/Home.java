package io.xeros.content.commands.all;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;

import java.util.Optional;

/**
 * Teleport the player to home.
 *
 * @author Emiel
 */
public class Home extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()|| Boundary.isIn(c, Boundary.OUTLAST_AREA)
				|| Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA)
				|| Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_LOBBY)
				|| Boundary.isIn(c, Boundary.FOREST_OUTLAST)
				|| Boundary.isIn(c, Boundary.SNOW_OUTLAST)
				|| Boundary.isIn(c, Boundary.BOUNTY_HUNTER_OUTLAST)
				|| Boundary.isIn(c, Boundary.ROCK_OUTLAST)
				|| Boundary.isIn(c, Boundary.FALLY_OUTLAST)
				|| Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST)
				|| Boundary.isIn(c, new Boundary(3117, 3640, 3137, 3644))
				|| Boundary.isIn(c, new Boundary(3114, 3611, 3122, 3639))
				|| Boundary.isIn(c, new Boundary(3122, 3633, 3124, 3639))
				|| Boundary.isIn(c, new Boundary(3122, 3605, 3149, 3617))
				|| Boundary.isIn(c, new Boundary(3122, 3617, 3125, 3621))
				|| Boundary.isIn(c, new Boundary(3144, 3618, 3156, 3626))
				|| Boundary.isIn(c, new Boundary(3155, 3633, 3165, 3646))
				|| Boundary.isIn(c, new Boundary(3157, 3626, 3165, 3632))
				|| Boundary.isIn(c, Boundary.SWAMP_OUTLAST)
				|| Boundary.isIn(c, Boundary.WG_Boundary)) {
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
			c.getPA().spellTeleport(3135, 3628, 0, true);
		} else {
			c.getPA().spellTeleport(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0, true);
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
