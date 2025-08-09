package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.content.skills.slayer.DemonHunterTaskOverlayManager;
import io.xeros.model.entity.player.Player;

/** Displays the Demon Hunter Slayer information overlay. */
public class Dhs extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        DemonHunterTaskOverlayManager.send(player);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }
}
