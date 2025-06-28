package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * 
 * @author Grant_ | www.rune-server.ee/members/grant_ | 10/15/19
 *
 */
public class Broadcast extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            new io.xeros.model.entity.player.broadcasts.Broadcast(input).submit();
        } catch (Exception e) {
            player.sendMessage("Error.. executing command.. invalid input! try again!");
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }
}