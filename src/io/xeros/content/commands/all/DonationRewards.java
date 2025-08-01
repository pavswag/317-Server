package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

public class DonationRewards extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.getDonationRewards().openInterface();
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    public Optional<String> getDescription() {
        return Optional.of("Opens the Donation rewards interface.");
    }
}
