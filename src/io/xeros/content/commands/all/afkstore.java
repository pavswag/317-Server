package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

public class afkstore  extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.getShops().openShop(195);
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    public Optional<String> getDescription() {
        return Optional.of("Opens the afk store.");
    }
}
