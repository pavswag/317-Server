package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.bots.FreakazoidBot;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

/**
 * Prints the status of all active Freakazoid bots.
 */
public class Freakazoidstatus extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        FreakazoidBot.getBots().forEach(bot ->
                player.sendMessage(bot.getStatus())
        );
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Shows Freakazoid bot statuses");
    }
}
