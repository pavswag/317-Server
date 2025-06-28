package io.xeros.content.commands.test;

import java.io.IOException;
import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class EquipmentSetup extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            io.xeros.model.EquipmentSetup.equip(player, input);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage("Could not equip equipment setup.");
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

    public Optional<String> getDescription() {
        return Optional.of("Spawn a specific equipment setup.");
    }
}
