package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

/**
 * Lists recent tinker table actions for debugging.
 */
public class Debugtinker extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.getTinkerLogs().isEmpty()) {
            player.sendMessage("No tinker activity logged.");
            return;
        }
        int i = 1;
        for (String log : player.getTinkerLogs()) {
            player.sendMessage(i++ + ". " + log);
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Show recent tinker table logs");
    }
}
