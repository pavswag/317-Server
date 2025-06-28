package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.content.deals.TimeOffers;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 08/03/2024
 */
public class ReloadOffers extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        TimeOffers.forceReloadOffers(player);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
