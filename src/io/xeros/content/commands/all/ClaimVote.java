package io.xeros.content.commands.all;

import com.everythingrs.vote.Vote;
import io.xeros.Configuration;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.ClientGameTimer;
import io.xeros.model.entity.player.Player;

import java.util.concurrent.TimeUnit;

import static io.xeros.content.commands.all.Voted.*;

public class ClaimVote extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        final String name = player.getLoginName().toLowerCase();
        final String key = "ls4JehGVUJYI0xszuHS2X8u0UhS4wZI80gEnUj5OEWMJlKCd7Y6BPQkfyCIqDcFFuzPTP9wY";
        try {
            final Vote[] reward = Vote.reward(key, name, "1", "all");
            if (reward[0].message != null) {
                player.sendErrorMessage("You do not have any votes to claim.");
                return;
            }
            if (Configuration.VOTE_PANEL_ACTIVE) votePanel(player);
            for (Vote vote : reward) {
                addRewards(player, vote);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void addRewards(Player player, Vote reward) {
        applyBonusDamage(player);
        System.out.println("amount=" + reward.give_amount);
        rewards(player, reward.give_amount);
        incrementGlobalVote(reward.give_amount);
        player.getItems().addItemUnderAnyCircumstance(reward.reward_id, reward.give_amount);
    }

    private void applyBonusDamage(Player player) {
        player.bonusDmgTicks += (TimeUnit.MINUTES.toMillis(10) / 600);
        player.bonusDmg = true;
        player.getPA().sendGameTimer(ClientGameTimer.BONUS_DAMAGE, TimeUnit.MINUTES, (int) ((player.bonusDmgTicks / 100)));
        player.sendMessage("@gre@You've earned your self 10minutes of bonus damage!");
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }
}
