package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

public class Instance extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        //player.getAoeInstanceHandler().open();
        player.sendMessage("New system coming soon! <3");
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }
}
