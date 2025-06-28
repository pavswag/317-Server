package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.concurrent.TimeUnit;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 31/03/2024
 */
public class eliteboost extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.EliteCentCooldown < System.currentTimeMillis()) {
           // player.EliteCentBoost = 6000;

            player.EliteCentCooldown = (System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));
        } else {
            player.sendMessage("This boost is on cooldown!");
        }
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }
}
