package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.content.seasons.ChristmasBoss;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
* @project arkcane-server
* @author ArkCane
* @social Discord: ArkCane
* Website: www.arkcane.net
 * @since 26/11/2023
 */
public class xmasb extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        ChristmasBoss.initBoss();
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
