package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.skills.slayer.DemonHunterXPTable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/** Reloads Demon Hunter XP multipliers from configuration. */
public class Reloaddhxps extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        DemonHunterXPTable.reload();
        player.sendMessage("Demon Hunter XP multipliers reloaded.");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }
}
