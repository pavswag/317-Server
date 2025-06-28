package io.xeros.content.commands.test;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class TestDonationRewards extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        int amount = 25;
        try {
            if (input.length() > 0)
                amount = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            player.sendMessage("Enter a valid number.");
            return;
        }
        player.getDonationRewards().increaseDonationAmount(amount);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

    public Optional<String> getDescription() {
        return Optional.of("Adds $$$ to your donated amount.");
    }
}
