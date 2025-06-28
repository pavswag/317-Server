package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.net.ChannelHandler;

public class Connections extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
       player.sendMessage("There are currently {} active connections.", "" + ChannelHandler.getActiveConnections());
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
