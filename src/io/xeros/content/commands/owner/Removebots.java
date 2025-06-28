package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.util.discord.Discord;

import java.util.Optional;

public class Removebots extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        if (!player.getRights().contains(Right.STAFF_MANAGER)) {
            player.sendMessage("Only owners can use this command.");
            return;
        }

        player.sendMessage("Logging out bots.");
        PlayerHandler.nonNullStream().forEach(plr -> {
            if (plr.isBot()) {

                Discord.writeSuggestionMessage(plr.getDisplayName() + " is a bot!");
                plr.forceLogout();
            }
        });
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

    public Optional<String> getDescription() {
        return Optional.of("Create a new setup, needs a name too!");
    }
}
