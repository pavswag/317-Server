package io.xeros.content.commands.owner;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.util.Misc;

public class AttackStats extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.setPrintAttackStats(!player.isPrintAttackStats());
        player.sendMessage("Combat attack messages are now " + Misc.booleanToString(player.isPrintAttackStats()) + ".");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Prints out combat attack stats while in combat.");
    }
}
