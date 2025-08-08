package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.instances.InstanceMutatorManager;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

/** Displays current local and global danger meters. */
public class Dangerlevel extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        if (!hasPrivilege(c)) {
            c.sendMessage("This command is for admins only.");
            return;
        }
        // zone id currently unused but kept for compatibility
        c.sendMessage("Danger: " + InstanceMutatorManager.getDangerLevel() + "/100, global: " + InstanceMutatorManager.getGlobalDanger() + "/100");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("show danger meter");
    }
}
