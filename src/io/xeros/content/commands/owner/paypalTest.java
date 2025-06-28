package io.xeros.content.commands.owner;

import com.paypal.api.payments.Item;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.sql.ingamestore.Configuration;
import io.xeros.sql.ingamestore.PayPal;
import io.xeros.sql.ingamestore.StoreInterface;

/**
 * @author ArkCane
 * @project arkcane-server
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 12/12/2023
 */
public class paypalTest extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        StoreInterface.openInterface(player);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
