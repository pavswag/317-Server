package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.content.fusion.FusionTypes;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class Fuse extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.getFusionSystem().openInterface(FusionTypes.WEAPON);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
