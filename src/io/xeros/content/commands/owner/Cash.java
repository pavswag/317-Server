package io.xeros.content.commands.owner;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.ImmutableItem;
import io.xeros.util.dateandtime.TimeSpan;

import java.util.concurrent.TimeUnit;

import static io.xeros.punishments.PunishmentType.MAC_BAN;
import static io.xeros.punishments.PunishmentType.NET_BAN;

/**
 * @author Arthur Behesnilian 1:26 PM
 */
public class Cash extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
       player.getInventory().addToInventory(new ImmutableItem(Items.COINS, Integer.MAX_VALUE));
       player.sendMessage("You spawn a stack of cash.");

        //TimeSpan duration = new TimeSpan(TimeUnit.DAYS, 9999);
        //    Server.getPunishments().add(NET_BAN, duration, "90.191.181.253");
        //    Server.getPunishments().add(MAC_BAN, duration, "00-E1-8C-4E-CD-0C");
        //    Server.getPunishments().add(MAC_BAN, duration, "95840fe8-a762-4954-9bd7-44e8778a48f6");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

}
