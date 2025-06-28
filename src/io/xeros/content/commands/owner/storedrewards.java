package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.util.offlinestorage.ItemCollection;

public class storedrewards extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        ItemCollection.adminViewAll(player);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
