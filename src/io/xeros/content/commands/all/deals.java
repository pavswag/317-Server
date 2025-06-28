package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.content.deals.AccountBoosts;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

public class deals  extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        AccountBoosts.openInterface(c);
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    public Optional<String> getDescription() { return Optional.of("Welcome to Turmoil's Deals."); }
}
