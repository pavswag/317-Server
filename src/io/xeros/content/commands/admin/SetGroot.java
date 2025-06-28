package io.xeros.content.commands.admin;

import io.xeros.content.activityboss.Groot;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

public class SetGroot extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            String[] args = input.split("-");
            if (args.length != 1) {
                throw new IllegalArgumentException();
            }
            int points = Integer.parseInt(args[0]);

            if ((Groot.ActivityPoints - points) <= 0) {
                player.sendMessage("You can't do that amount due to it being negative or zero!");
                return;
            }

            Groot.ActivityPoints -= points;
        } catch (Exception e) {
            player.sendMessage("Error. Correct syntax: ::setgroot-amount");
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Prints out combat defence stats while in combat.");
    }
}
