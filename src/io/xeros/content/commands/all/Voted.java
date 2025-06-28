package io.xeros.content.commands.all;

import io.xeros.Configuration;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.afkzone.AfkBoss;
import io.xeros.content.bonus.DoubleExperience;
import io.xeros.content.commands.Command;
import io.xeros.content.commands.moderator.vboss;
import io.xeros.content.vote_panel.VotePanelManager;
import io.xeros.content.vote_panel.VoteUser;
import io.xeros.content.wogw.Wogw;
import io.xeros.model.Items;
import io.xeros.model.entity.player.ClientGameTimer;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.DateUtils;
import io.xeros.util.Misc;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Changes the password of the player.
 *
 * @author Emiel
 *
 */
public class Voted extends Command {

	public static final long XP_SCROLL_TICKS = TimeUnit.MINUTES.toMillis(30) / 600;
	private static final int GP_REWARD = 1_000_000;
	public static int globalVotes = 10;
	public static int totalVotes = 0;

	public static void claimVotes(Player player) {
		if (Configuration.VOTE_PANEL_ACTIVE) {
			votePanel(player);
		}
		int amt = 1;

		if (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33115)) {
			amt = 2;
		}

		player.bonusDmgTicks += (TimeUnit.MINUTES.toMillis(10) / 600);
		player.bonusDmg = true;
		player.getPA().sendGameTimer(ClientGameTimer.BONUS_DAMAGE, TimeUnit.MINUTES, (int) ((player.bonusDmgTicks / 100)));
		player.sendMessage("@gre@You've earned your self 10minutes of bonus damage!");
		rewards(player, amt);
		incrementGlobalVote(amt);

		if (!AfkBoss.IPAddress.contains(player.getIpAddress())) {
			AfkBoss.IPAddress.add(player.getIpAddress());
		}
		if (!AfkBoss.MACAddress.contains(player.getMacAddress())) {
			AfkBoss.MACAddress.add(player.getMacAddress());
		}
	}

	static void votePanel(Player player) {
		VotePanelManager.addVote(player.getLoginName());
		VoteUser user = VotePanelManager.getUser(player);
		if (user != null) {
			if (player.getLastVotePanelPoint().isBefore(LocalDate.now())) { // Gain one point per day
				player.setLastVotePanelPoint(LocalDate.now());
				boolean oldStreakOverflow = user.getDayStreak() >= VoteUser.MAX_DAY_STREAK;
				user.incrementDayStreak();
				if (user.getDayStreak() > VoteUser.MAX_DAY_STREAK && !oldStreakOverflow) {
					user.resetDayStreak();
					user.incrementDayStreak();
				}

				if (user.getDayStreak() == VoteUser.MAX_DAY_STREAK || oldStreakOverflow) { //They just hit a 5 day streak (after incrementing) so reward them!
					player.getItems().addItemToBankOrDrop(22093, 1);
					player.getItems().addItemToBankOrDrop(6199, 1);
					player.sendMessage("@pur@One @gre@vote key @pur@has been added to your bank for a 5 vote streak!");
					player.sendMessage("@red@You just completed a 5 day voting streak!");
					user.resetDayStreak();
					if (oldStreakOverflow) {
						user.incrementDayStreak();
					}
				}

				player.debug("Gained one ::vpanel point, streak: {}.", "" + user.getDayStreak());
				VotePanelManager.saveToJSON();
			}
		}
	}

	static void rewards(Player player, final int voteCount) {
		Achievements.increase(player, AchievementType.VOTER, voteCount);
		player.votePoints += voteCount;
		player.voteKeyPoints += voteCount;
		player.xpScroll = true;
		player.xpScrollTicks += XP_SCROLL_TICKS * voteCount;
		if (!DoubleExperience.isDoubleExperience()) player.getPA().sendGameTimer(ClientGameTimer.BONUS_XP, TimeUnit.MINUTES, (int) ((player.xpScrollTicks / 100)));
		boolean firstWeekOfMonth = DateUtils.isFirstWeekOfMonth();
		int amount = GP_REWARD * voteCount;
		if (firstWeekOfMonth) {
			amount *= 2;
			player.sendMessage("@red@You have gained " + voteCount + " voting point and extra gp for the first week of month!");
		} else {
			player.sendMessage("You have gained " +  voteCount + " voting point and gp!");
		}
		player.getItems().addItemUnderAnyCircumstance(Items.COINS, amount);
	}

	public static void incrementGlobalVote(final int voteCount) {
		final boolean firstWeekOfMonth = DateUtils.isFirstWeekOfMonth();
		final var header = firstWeekOfMonth ? "Double Vote Week" : "Vote";
		globalVotes += voteCount;
		totalVotes += voteCount;

		if (totalVotes >= 20) {
			vboss.spawnBoss();
			totalVotes = 0;
		}

		// Dividing by two because it counts both votes, don't have a better way atm
		if (globalVotes/2 >= 50) {
			Wogw.votingBonus();
			globalVotes = 0;
		} else if (globalVotes/2 == 40) {
			PlayerHandler.executeGlobalMessage("@cr10@[@pur@" + header + "@bla@] Global votes are at 40, reach 50 for a server boost!");
		} else if (globalVotes/2 == 20) {
			PlayerHandler.executeGlobalMessage("@cr10@[@pur@" + header + "@bla@] Global votes are at 20, reach 50 for a server boost!");
		} else if (globalVotes/2 == 10) {
			PlayerHandler.executeGlobalMessage("@cr10@[@pur@" + header + "@bla@] Global votes are at 10, reach 50 for a server boost!");
		}
	}

	@Override
	public void execute(Player c, String commandName, String input) {
		String[] args = input.split(" ");
		final String playerName = c.getLoginName();
		final String id = "1";
		final String amount = "all";

		com.everythingrs.vote.Vote.service.execute(new Runnable() {
			@Override
			public void run() {
				try {
					com.everythingrs.vote.Vote[] reward = com.everythingrs.vote.Vote.reward("D5xMvBs24EOx7N41AEYsdOoWiIJXdUstwtQH2941jsDZ8LV9Ia4zN2ttNe7rpWfTL7F6UJzc",
							playerName, id, amount);
					if (reward[0].message != null) {
						c.sendMessage(reward[0].message);
						return;
					}
					if (Misc.random(2) == 1) {
						c.getItems().addItemUnderAnyCircumstance(22093, 1);
						PlayerHandler.executeGlobalMessage("@red@" + c.getLoginName()
								+ " just got lucky and received a Vote Key!");
						c.sendMessage(
								"@cr10@We sent you a @or1@Vote Key@bla@for voting!");
					}
					if (Misc.random(5) == 1) {
						c.getItems().addItemUnderAnyCircumstance(2528, 1);
						PlayerHandler.executeGlobalMessage("@red@" + c.getLoginName()
								+ " has just voted and received a XP Lamp!");
						c.sendMessage(
								"@cr10@We sent you a @or1@XP Lamp @bla@for voting!");
					}
					//Votes.voteCount++;

					Achievements.increase(c, AchievementType.VOTER, 1);
					c.getItems().addItemUnderAnyCircumstance(reward[0].reward_id, reward[0].give_amount);
					c.getItems().addItemUnderAnyCircumstance(23933, reward[0].give_amount);
					c.sendMessage(
							"Thank you for voting! Open your vote boxes for vote points.");
				} catch (Exception e) {
					c.sendMessage("Api Services are currently offline. Please check back shortly");
					e.printStackTrace();
				}
			}

		});
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Claim your voted reward.");
	}

	private static void sendNextVoteTime(Player player, String name, Timestamp timestamp) {
		if (timestamp == null) return;
		var lastVoteInstant = timestamp.toInstant();
		var nextVoteInstant = lastVoteInstant.plus(12, ChronoUnit.HOURS);
		var waitDuration = Duration.between(Instant.now(), nextVoteInstant);
		if (waitDuration.isNegative()) {
			player.sendMessage("You can vote at " + name + ".");
			return;
		}
		var formatted = format(waitDuration);
		player.sendMessage("You can vote at " + name + " in " + formatted + ".");
	}

	private static String format(final Duration duration) {
		var hours = duration.toHours();
		var minutes = duration.toMinutes();
		var seconds = duration.toSeconds();
		// Round everything up by 1.
		if (hours > 0) {
			var adjustedHours = hours + 1;
			return adjustedHours + " hours";
		} else if (minutes > 0) {
			var adjustedMinutes = minutes + 1;
			return adjustedMinutes + " minutes";
		} else {
			var adjustedSeconds = seconds + 1;
			return adjustedSeconds + " seconds";
		}
	}


	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}
}
