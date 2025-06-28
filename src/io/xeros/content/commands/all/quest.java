package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.content.questing.QuestInterfaceV2;
import io.xeros.model.entity.player.Player;

public class quest extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        QuestInterfaceV2.openInterface(player);
        player.getQuesting().handleHelpTabActionButton(666);
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }
}
