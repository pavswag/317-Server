package io.xeros.content.commands.admin;

import io.xeros.content.activityboss.Groot;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class gboss extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Groot.spawnGroot();
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }
}
