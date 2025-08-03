package io.xeros.content.commands.all;

import io.xeros.content.activityboss.GlobalBossActivityManager;
import io.xeros.content.activityboss.GlobalBossSpawnData;
import io.xeros.content.activityboss.GlobalBossType;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

import java.util.Optional;

/**
 * Displays global boss activity progress and active bosses.
 */
public class Bosses extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        player.sendMessage("@dre@Global Boss Status:");
        for (GlobalBossType type : GlobalBossType.values()) {
            if (GlobalBossActivityManager.isActive(type)) {
                GlobalBossSpawnData data = GlobalBossActivityManager.getActiveBosses().get(type);
                Position pos = data.getSpawnPosition();
                player.sendMessage(type.getName() + " - Alive at (" + pos.getX() + ", " + pos.getY() + ")");
            } else {
                int progress = GlobalBossActivityManager.getTotal(type.getActivityType());
                int threshold = type.getThreshold();
                long cd = GlobalBossActivityManager.getCooldownRemaining(type);
                String msg = type.getName() + ": " + progress + "/" + threshold;
                if (cd > 0) {
                    msg += " - cooldown " + formatDuration(cd);
                }
                player.sendMessage(msg);
            }
        }
    }

    private String formatDuration(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        seconds %= 60;
        return minutes + "m " + seconds + "s";
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Shows global boss spawn progress");
    }
}

