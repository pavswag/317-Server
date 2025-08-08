package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

/**
 * Toggles global boss progress alerts for the player.
 */
public class Togglebossalerts extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
//        player.setBossAlerts(!player.isBossAlerts());
//        player.sendMessage("Global boss alerts " + (player.isBossAlerts() ? "enabled" : "disabled"));
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Toggles global boss alert messages");
    }
}
