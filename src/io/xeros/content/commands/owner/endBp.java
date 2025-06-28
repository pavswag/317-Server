package io.xeros.content.commands.owner;

import io.xeros.content.battlepass.Pass;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class endBp extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Pass.setSeasonEnded(true);
        Pass.setDaysUntilStart(1L);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
