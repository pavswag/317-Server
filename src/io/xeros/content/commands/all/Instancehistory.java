package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.content.instances.BossInstanceManager;
import io.xeros.model.entity.player.Player;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Displays the player's best instance times and scores.
 */
public class Instancehistory extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.sendMessage("@blu@Instance history:");
        for (BossInstanceManager.BossTier tier : BossInstanceManager.BossTier.values()) {
            Integer score = player.getBestInstanceScores().get(tier);
            Long time = player.getBestInstanceTimes().get(tier);
            if (score != null && time != null) {
                long seconds = TimeUnit.MILLISECONDS.toSeconds(time);
                player.sendMessage(tier.getZoneName() + " - Score: " + score + ", Time: " + seconds + "s");
            }
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Shows instance performance history.");
    }
}
