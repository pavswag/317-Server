package io.xeros.content.commands.all;

import io.xeros.Configuration;
import io.xeros.content.commands.Command;
import io.xeros.content.instances.aoe.AoeTierController;
import io.xeros.content.instances.aoe.AoeTierRepo;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

public class Leaveaoe extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (AoeTierRepo.instanceForPlayer(player).isEmpty()) {
            player.sendMessage("You are not inside an AOE instance.");
            return;
        }
        AoeTierController.endTier(player, true);
        player.getPA().movePlayer(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0);
        player.sendMessage("You leave the AOE instance.");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("exit the current aoe instance");
    }
}

