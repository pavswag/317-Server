package io.xeros.content.commands.all;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.content.dialogue.impl.ScrollConverterDialogue;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

/**
 * Shortcut command to open the scroll converter dialogue.
 */
public class ConvertScrolls extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (Boundary.isIn(player, Boundary.DUEL_ARENA) || Server.getMultiplayerSessionListener().inAnySession(player)) {
            player.sendMessage("You cannot convert scrolls right now.");
            return;
        }
        player.start(new ScrollConverterDialogue(player));
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }
}
