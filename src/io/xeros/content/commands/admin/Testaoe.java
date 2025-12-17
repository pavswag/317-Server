package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.instances.aoe.AoeTierController;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

public class Testaoe extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (!hasPrivilege(player)) {
            player.sendMessage("This command is for admins only.");
            return;
        }
        if (input == null || input.isBlank()) {
            player.sendMessage("Usage: ::testaoe <tier>");
            return;
        }
        try {
            int tier = Integer.parseInt(input.trim());
            AoeTierController.startTier(player, tier);
        } catch (NumberFormatException e) {
            player.sendMessage("Usage: ::testaoe <tier>");
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("builds an aoe test instance");
    }
}

