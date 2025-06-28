package io.xeros.content.commands.owner;

import io.xeros.Configuration;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;

public class Bogof extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        if (Configuration.BOGOF) {
            Configuration.BOGOF = false;
            PlayerHandler.executeGlobalMessage("@cr1@ @blu@The BOGOF Offer has now expired <3");
        } else {
            Configuration.BOGOF = true;
            PlayerHandler.executeGlobalMessage("@cr1@ @blu@The BOGOF Offer is now active! all donations will be doubled!!");
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
