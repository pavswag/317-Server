package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class curses extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.setSidebarInterface(5, 24633);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
