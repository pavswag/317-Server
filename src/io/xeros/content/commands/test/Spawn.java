package io.xeros.content.commands.test;

import java.util.Optional;

import io.xeros.content.ItemSpawner;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class Spawn extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        ItemSpawner.open(player);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.isOrInherits(player);
    }

    public Optional<String> getDescription() {
        return Optional.of("Opens an interface to spawn items.");
    }
}
