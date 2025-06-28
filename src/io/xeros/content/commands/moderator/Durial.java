package io.xeros.content.commands.moderator;

import io.xeros.content.bosses.Durial321;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;

public class Durial extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (Durial321.spawned || Durial321.alive) {
            c.sendMessage("You cannot execute this more than once!");
            return;
        }

        Durial321.init();
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.MODERATOR.isOrInherits(player);
    }
}
