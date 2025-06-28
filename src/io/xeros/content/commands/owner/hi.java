package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class hi extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split(" ");
        int id = Integer.parseInt(args[0]);
        player.headIcon = id;
        player.getPA().requestUpdates();
        player.sendMessage("HeadIcon: " + id);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
//						c.headIcon = PRAYER_HEAD_ICONS[i];
//						c.getPA().requestUpdates();