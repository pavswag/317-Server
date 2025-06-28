package io.xeros.content.commands.moderator;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.ConnectedFrom;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.util.discord.Discord;

import java.util.Optional;

/**
 * Forces a given player to log out.
 * 
 * @author Emiel
 */
public class Kick extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(input);
		if (optionalPlayer.isPresent()) {
			Player c2 = optionalPlayer.get();
			if (Server.getMultiplayerSessionListener().inAnySession(c)) {
				c.sendMessage("The player is in a trade, or duel. You cannot do this at this time.");
				return;
			}
			c2.outStream.createFrame(109);
			CycleEventHandler.getSingleton().stopEvents(c2);
			Discord.writeSuggestionMessage(c.getDisplayName() + " Kicked " + c2.getDisplayName());
			c2.forceLogout();
			ConnectedFrom.addConnectedFrom(c2, c2.connectedFrom);
			c.sendMessage("Kicked " + c2.getDisplayName());
		} else {
			c.sendMessage(input + " is not online. You can only kick online players.");
		}
	}


	@Override
	public boolean hasPrivilege(Player player) {
		return Right.HELPER.isOrInherits(player);
	}
}
