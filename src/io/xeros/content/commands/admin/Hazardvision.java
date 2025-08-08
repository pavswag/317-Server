package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

/** Toggles developer hazard tile overlays. */
public class Hazardvision extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        if (!hasPrivilege(c)) {
            c.sendMessage("This command is for admins only.");
            return;
        }
        boolean enabled = !c.getAttributes().getBoolean("hazardvision");
        c.getAttributes().set("hazardvision", enabled);
        c.sendMessage("Hazard vision " + (enabled ? "enabled" : "disabled") + ".");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("toggle hazard overlays");
    }
}
