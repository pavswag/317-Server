package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.net.login.LoginRequestLimit;

public class GetLoginLimit extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
       player.sendMessage("Login rate limit is set to {}", "" + LoginRequestLimit.MAX_LOGINS_PER_TICK);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
