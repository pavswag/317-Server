package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.content.questing.QuestInterfaceV2;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class qzz extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        QuestInterfaceV2.openInterface(player);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
