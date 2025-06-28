package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.CosmeticOverride;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 15/02/2024
 */
public class cosmetics extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        CosmeticOverride.openInterface(player);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
