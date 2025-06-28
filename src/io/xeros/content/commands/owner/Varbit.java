package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 19/02/2024
 */
public class Varbit extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split("-");
        try {
        if (args.length != 2) {
            throw new IllegalArgumentException();
        }
        int varbit = Integer.parseInt(args[0]);
        int state = Integer.parseInt(args[1]);
        player.getPA().sendConfig(varbit, state);
        } catch (Exception e) {
            player.sendMessage("Error. Correct syntax: ::varbit-id-state");
        }

    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
