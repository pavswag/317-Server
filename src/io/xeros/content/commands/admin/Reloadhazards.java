package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.instances.hazard.WeeklyHazardManager;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

/** Reloads weekly hazard configuration from disk. */
public class Reloadhazards extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        WeeklyHazardManager.reload();
        c.sendMessage("Hazard configuration reloaded.");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("reload weekly hazards");
    }
}
