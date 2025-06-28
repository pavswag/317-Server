package io.xeros.content.commands.owner;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.util.Misc;

public class DefenceStats extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.setPrintDefenceStats(!player.isPrintDefenceStats());
        player.sendMessage("Combat defence messages are now " + Misc.booleanToString(player.isPrintDefenceStats()) + ".");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Prints out combat defence stats while in combat.");
    }
}
