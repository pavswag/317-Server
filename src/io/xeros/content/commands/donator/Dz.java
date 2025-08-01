package io.xeros.content.commands.donator;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * Teleports the player to the donator zone.
 *
 * @author Emiel
 */
public class Dz extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.inTrade || c.inDuel || c.getPosition().inWild()) {
			return;
		}
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
			c.sendMessage("@cr10@This player is currently at the pk district.");
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
		c.start(new DialogueBuilder(c).option("Select the donor zone you wish to enter!",
				new DialogueOption("Apex Donator Zone (5)", p -> {
					if (c.amDonated >= 1500) {
						c.getPA().startTeleport(2406, 3803, 0, "modern", false);
					}
				}),
				new DialogueOption("Gilded Donator Zone (4)", p -> {
					if (c.amDonated >= 500) {
						c.getPA().startTeleport(1422, 2972, 0, "modern", false);
					}
				}),
				new DialogueOption("Major Donator Zone (3)", p -> {
					if (c.amDonated >= 250) {
						c.getPA().startTeleport(2110, 2720, 0, "modern", false);
					}
				}),
				new DialogueOption("Greater Donator Zone (2)", p -> {
					if (c.amDonated >= 100) {
						c.getPA().startTeleport(2406, 3803, 0, "modern", false);
					}
				}),
				new DialogueOption("Donator Zone (1)", p -> {
					if (c.amDonated >= 20) {
						c.getPA().startTeleport(1967, 5365, 0, "modern", false);
					}
				})));
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return player.amDonated >= 20;
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you to donator zone.");
	}

}
