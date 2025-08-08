package io.xeros.content.commands.owner;

import io.xeros.content.activityboss.ActivityType;
import io.xeros.content.activityboss.GlobalBossActivityManager;
import io.xeros.content.activityboss.GlobalBossType;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

public class Bossdebug extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        for (ActivityType type : ActivityType.values()) {
            GlobalBossType boss = GlobalBossType.forActivity(type);
            int total = GlobalBossActivityManager.getTotal(type);
            player.sendMessage(type.name() + ": " + total + "/" + boss.getThreshold());
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Shows boss activity progress for debugging");
    }
}
