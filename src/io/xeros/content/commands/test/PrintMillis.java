package io.xeros.content.commands.test;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class PrintMillis extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.sendMessage("" + System.currentTimeMillis());
        System.out.println(System.currentTimeMillis());
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

    public Optional<String> getDescription() {
        return Optional.of("Prints milliseconds to chat and console.");
    }
}
