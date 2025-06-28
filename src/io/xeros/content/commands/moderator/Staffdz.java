package io.xeros.content.commands.moderator;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

public class Staffdz extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (c.inTrade || c.inDuel || c.getPosition().inWild()) {
            return;
        }

        c.getPA().startTeleport(1903, 5750, 0, "modern", false);
    }
    @Override
    public boolean hasPrivilege(Player player) {
        return Right.HELPER.isOrInherits(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Teleports you to the staff Donor Zone.");
    }

}