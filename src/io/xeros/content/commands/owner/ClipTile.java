package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.collisionmap.RegionProvider;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * @author Arthur Behesnilian 4:07 PM
 */
public class ClipTile extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        int flag = input.isEmpty() ? RegionProvider.FULL_NPC_TILE_FLAG : Integer.parseInt(input);
        player.getRegionProvider().addClipping(flag, player.getX(), player.getY(), player.getHeight());
        player.sendMessage("You add flag=" + flag + " to " + player.getPosition());
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
