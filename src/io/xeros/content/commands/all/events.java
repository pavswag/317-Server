package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.content.worldevent.WorldEventInformation;
import io.xeros.model.entity.player.Player;

public class events extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        WorldEventInformation.openInformationInterface(player);
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }
}
