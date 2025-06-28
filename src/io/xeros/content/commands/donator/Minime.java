package io.xeros.content.commands.donator;

import io.xeros.content.commands.Command;
import io.xeros.content.minime.MiniMeFunctions;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class Minime extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        MiniMeFunctions.create(player);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return player.amDonated >= 20;
    }
}
