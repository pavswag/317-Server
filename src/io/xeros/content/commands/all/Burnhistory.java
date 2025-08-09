package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

/**
 * Shows last 10 Fire of Exchange burns.
 */
public class Burnhistory extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.getBurnHistory().isEmpty()) {
            player.sendMessage("No recent burns recorded.");
            return;
        }
        int i = 1;
        for (String log : player.getBurnHistory()) {
            player.sendMessage(i++ + ". " + log);
        }
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Shows last 10 Fire of Exchange burns.");
    }
}
