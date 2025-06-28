package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class comp extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.getCompletionistCapeRe().sendColours();
        player.getPA().showInterface(59960);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
