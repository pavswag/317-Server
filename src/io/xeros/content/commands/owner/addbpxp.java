package io.xeros.content.commands.owner;

import io.xeros.content.battlepass.Pass;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

public class addbpxp extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split("-");
        String playerName = args[0];
        Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);
        if (optionalPlayer.isPresent()) {
            Player p = optionalPlayer.get();
            Pass.addExperience(p, Integer.parseInt(args[1]));
        } else {
            player.sendMessage(playerName + " is not online.");
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
