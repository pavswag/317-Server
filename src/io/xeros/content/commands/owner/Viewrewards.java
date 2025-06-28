package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.util.offlinestorage.ItemCollection;

import java.util.Optional;

public class Viewrewards extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
            try {
                String[] args = input.split("-");
                String playerName = args[0];
                ItemCollection.adminView(player, playerName);
            } catch (Exception e) {
                player.sendMessage("Error. Correct syntax: ::viewrewards-playername");
            }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
