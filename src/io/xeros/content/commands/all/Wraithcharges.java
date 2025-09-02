package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.content.wraith.WraithCharges;

/**
 * Displays the remaining charges on the player's Wraith weapons.
 */
public class Wraithcharges extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.sendMessage("Wraith Scythe: " + WraithCharges.getCharge(player, WraithCharges.WRAITH_SCYTHE)
                + " charges. Wraith Staff: " + WraithCharges.getCharge(player, WraithCharges.WRAITH_STAFF)
                + " charges. Wraith Bow: " + WraithCharges.getCharge(player, WraithCharges.WRAITH_BOW) + " charges.");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Displays remaining Wraith weapon charges.");
    }
}
