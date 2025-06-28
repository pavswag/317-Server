package io.xeros.content.commands.donator;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
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
