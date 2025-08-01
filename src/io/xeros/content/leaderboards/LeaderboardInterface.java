package io.xeros.content.leaderboards;

import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.List;
import java.util.Map;

import static io.xeros.content.leaderboards.LeaderboardUtils.rewards;

public class LeaderboardInterface {
    private static final int INTERFACE_ID = 18820;

    private static final int LEADERBOARD_TYPE_SELECT_BUTTON_START = 18_828;
    private static final int LEADERBOARD_TYPE_SELECT_BUTTON_END = 18_866;
    private static final int LEADERBOARD_TYPE_SELECT_TEXT_START = 18_829;

    private static final int LEADERBOARD_PERIOD_SELECT_BUTTON_START = 18_837;

    private static final int NAME_START_ID = 18_870;
    private static final int COUNT_START_ID = 18_871;
    private static final int REWARD_START_ID = 18_872;

    private static final String LAST_VIEWED_ATTRIBUTE = "last_viewed_leaderboard";

    public static final int LEADERBOARD_TYPE_CONFIG = 1376;
    public static final int LEADERBOARD_PERIOD_CONFIG = 1377;

    public static void openInterface(Player player) {
        int lastViewedLeaderboard = player.getAttributes().getInt(LAST_VIEWED_ATTRIBUTE, 0);

        if (!prepareData(player, LeaderboardType.values()[lastViewedLeaderboard], LeaderboardPeriodicity.TODAY))
            return;

        player.getPA().showInterface(INTERFACE_ID);
    }

    public static boolean handleButtons(Player player, int button) {
        if (button >= LEADERBOARD_TYPE_SELECT_BUTTON_START && button <= LEADERBOARD_TYPE_SELECT_BUTTON_END) {
            int index = (button - LEADERBOARD_TYPE_SELECT_BUTTON_START) / 2;
            if (index < LeaderboardType.values().length) {
                player.getAttributes().setInt(LAST_VIEWED_ATTRIBUTE, index);
                prepareData(player, LeaderboardType.values()[index], player.getCurrentLeaderboardPeriod());
            }
            return true;
        }


        return false;
    }

    private static boolean prepareData(Player player, LeaderboardType type, LeaderboardPeriodicity period) {
        player.getPA().sendConfig(LEADERBOARD_TYPE_CONFIG, type.ordinal());
        player.getPA().sendConfig(LEADERBOARD_PERIOD_CONFIG, period.ordinal());

        Map<LeaderboardType, List<LeaderboardEntry>> leaderboard = LeaderboardUtils.daily;
        List<LeaderboardEntry> entries = leaderboard.get(type);

        if (entries == null) {
            player.sendMessage("Leaderboards are loading, please wait.");
            return false;
        }

        int index = 0;
        for(; index < LeaderboardType.values().length; index++) {
            LeaderboardType board = LeaderboardType.values()[index];
            boolean selected = type == board;
            player.getPA().sendString(LEADERBOARD_TYPE_SELECT_TEXT_START + (index * 2), (selected ? "@whi@" : "") + board.getName());
        }
        for (; index < 20; index++) {
            player.getPA().sendString(LEADERBOARD_TYPE_SELECT_TEXT_START + (index * 2), "");
        }

        for (int i = 0; i < 10; i++) {
            boolean found = false;
            for(LeaderboardReward reward : rewards) {
                if (reward.getPeriod() == period &&
                        reward.getType() == type &&
                        reward.getPlace() == (i + 1)) {
                    player.getPA().itemOnInterface(reward.getItem(), REWARD_START_ID + (i * 4), 0);
                    found = true;
                    break;
                }
            }
            if (!found) {
                player.getPA().itemOnInterface(null, REWARD_START_ID + (i * 4), 0);
            }
        }

        index = 0;
        for (LeaderboardEntry entry : entries) {
            player.getPA().sendString(NAME_START_ID + (index * 4), entry.getDisplayName());
            player.getPA().sendString(COUNT_START_ID + (index * 4), Misc.insertCommas("" + entry.getAmount()));
            index++;
        }
        for (; index < 10; index++) {
            player.getPA().sendString(NAME_START_ID + (index * 4), "");
            player.getPA().sendString(COUNT_START_ID + (index * 4), "");
        }

        return true;
    }

}
