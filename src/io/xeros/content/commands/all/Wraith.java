package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.content.wraith.WraithCharges;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

/**
 * Handles ::wraith charge <amount> to charge Wraith weapons.
 */
public class Wraith extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        String[] args = input.split(" ");
        if (args.length == 0 || args[0].isEmpty()) {
            player.sendMessage("Usage: ::wraith charge <amount>");
            return;
        }
        if (args[0].equalsIgnoreCase("charge")) {
            int requested = Integer.MAX_VALUE;
            if (args.length > 1) {
                try {
                    requested = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    player.sendMessage("Invalid amount.");
                    return;
                }
            }
            int slot = -1;
            int weaponId = player.playerEquipment[Player.playerWeapon];
            if (WraithCharges.isWraithWeapon(weaponId)) {
                slot = Player.playerWeapon;
            } else {
                for (int i = 0; i < player.playerItems.length; i++) {
                    if (WraithCharges.isWraithWeapon(player.playerItems[i] - 1)) {
                        slot = i;
                        break;
                    }
                }
            }
            if (slot == -1) {
                player.sendMessage("You don't have a Wraith weapon to charge.");
                return;
            }
            WraithCharges.addChargesFromEssence(player, slot, WraithCharges.WRAITH_ESSENCE, requested);
        } else if (args[0].equalsIgnoreCase("charges")) {
            player.sendMessage("Wraith Scythe: " + WraithCharges.getCharge(player, WraithCharges.WRAITH_SCYTHE)
                    + " charges. Wraith Staff: " + WraithCharges.getCharge(player, WraithCharges.WRAITH_STAFF)
                    + " charges. Wraith Bow: " + WraithCharges.getCharge(player, WraithCharges.WRAITH_BOW) + " charges.");
        } else {
            player.sendMessage("Usage: ::wraith charge <amount>");
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Manage Wraith weapon charges.");
    }
}
