package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class progress extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split(" ");

        int ID = Integer.parseInt(args[0]);
        int percent = Integer.parseInt(args[1]);

        player.getPA().sendProgressBar(ID, percent);
        player.getPA().showInterface(59951);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
