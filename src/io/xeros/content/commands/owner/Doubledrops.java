package io.xeros.content.commands.owner;

import io.xeros.Configuration;
import io.xeros.content.commands.Command;
import io.xeros.content.wogw.Wogw;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.util.Misc;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class Doubledrops extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        int minutes = Integer.parseInt(input);
        if (minutes == 0) {
            Configuration.DOUBLE_DROPS_TIMER = 0;
            player.sendMessage("Double drops have ended.");
            return;
        }

        Configuration.DOUBLE_DROPS_TIMER = Misc.toCycles(minutes, TimeUnit.MINUTES);
        Wogw.sendActivateMessage("double drops");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Turn on double drops for x minutes, ::doubledrops 60 (60 minutes).");
    }
}
