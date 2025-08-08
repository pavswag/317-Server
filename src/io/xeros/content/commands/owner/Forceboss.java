package io.xeros.content.commands.owner;

import io.xeros.content.activityboss.GlobalBossActivityManager;
import io.xeros.content.activityboss.GlobalBossType;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

public class Forceboss extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            GlobalBossType type = GlobalBossType.valueOf(input.toUpperCase());
            GlobalBossActivityManager.forceSpawn(type);
            player.sendMessage("Forced spawn of " + type.getName());
        } catch (Exception e) {
            player.sendMessage("Unknown boss: " + input);
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Force spawn a global boss");
    }
}
