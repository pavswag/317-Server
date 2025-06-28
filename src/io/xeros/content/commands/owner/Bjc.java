package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.content.games.blackjack.BJManager;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 10/04/2024
 */
public class Bjc extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.queue(() -> {
            player.setBjManager(new BJManager(player));
            player.getBjManager().open();
        });//just to test
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
