package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

public class TaskManager extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.getTaskMaster().showInterface();
        player.getQuesting().handleHelpTabActionButton(667);
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }
}
