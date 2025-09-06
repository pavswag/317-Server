package io.xeros.content.instances.aoe;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import java.util.Optional;

/**
 * Prints the player's absolute coordinates for quick verification.
 */
public class Whereami extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        int region = (player.getX() >> 6 << 8) + (player.getY() >> 6);
        player.sendMessage("x=" + player.getX() + " y=" + player.getY() + " h=" + player.getHeight() + " region=" + region);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Print current coordinates");
    }
}
