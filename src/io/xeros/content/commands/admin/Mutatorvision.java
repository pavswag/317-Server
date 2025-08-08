package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

/** Toggles developer mutator overlays. */
public class Mutatorvision extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        if (!hasPrivilege(c)) {
            c.sendMessage("This command is for admins only.");
            return;
        }
        boolean enabled = !c.getAttributes().getBoolean("mutatorvision");
        c.getAttributes().set("mutatorvision", enabled);
        c.sendMessage("Mutator vision " + (enabled ? "enabled" : "disabled") + ".");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("toggle mutator overlays");
    }
}
