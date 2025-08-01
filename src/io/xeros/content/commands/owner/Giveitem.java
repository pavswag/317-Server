package io.xeros.content.commands.owner;

import java.util.Optional;
import io.xeros.content.ItemSpawner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

/**
 * Give a certain amount of an item to a player.
 * 
 * @author Emiel
 */
public class Giveitem extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		if (input.isEmpty()) {
			player.sendMessage("Error. Correct syntax: ::giveitem-player");
			return;
		}

		Optional<Player> optionalTarget = PlayerHandler.getOptionalPlayerByDisplayName(input);
		if (!optionalTarget.isPresent()) {
			player.sendMessage(input + " is not online.");
			return;
		}

		Player target = optionalTarget.get();
		if (target.getMode().isIronmanType() && !player.getRights().isOrInherits(Right.STAFF_MANAGER)) {
			player.sendMessage("You cannot give items to these players because of their respective game modes.");
			return;
		}

		player.getAttributes().set(ItemSpawner.TARGET_ATTRIBUTE_KEY, target);
		ItemSpawner.open(player);
		player.sendMessage("Items spawned will be sent to " + target.getDisplayName() + ".");
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.GAME_DEVELOPER.isOrInherits(player);
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Opens spawn interface and sends items to a chosen player.");
	}

}
