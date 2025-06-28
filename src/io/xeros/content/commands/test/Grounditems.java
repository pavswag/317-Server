package io.xeros.content.commands.test;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

public class Grounditems extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
       int amount = Integer.parseInt(input);
       Server.itemHandler.createGroundItem(player, 4151, player.getX(), player.getY(), player.getHeight(), amount);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

    public Optional<String> getDescription() {
        return Optional.of("Add ground items under you [::grounditems 20]");
    }
}
