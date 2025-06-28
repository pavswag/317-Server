package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import static io.xeros.model.entity.player.packets.Commands.isNotStaffTeam;

public class sp extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.getPA().showInterface(43214);
        player.sendMessage("You open the spawning menu...");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

}