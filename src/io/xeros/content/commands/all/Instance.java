package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.content.instances.BossInstanceUIManager;
import io.xeros.model.entity.player.Player;

import java.util.Optional;

/**
 * Opens the boss instance overview panel.
 */
public class Instance extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        BossInstanceUIManager.openOverview(player);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Shows boss instance progress.");
    }
}