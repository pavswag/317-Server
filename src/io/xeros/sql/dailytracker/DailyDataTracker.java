package io.xeros.sql.dailytracker;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.sql.MainSql.SQLTable;
import io.xeros.sql.MainSql.SqlManager;
import io.xeros.util.DataStorage;
import io.xeros.util.Misc;
import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 18/03/2024
 */
public class DailyDataTracker {

    public static boolean ENABLED = false;

    public static void insertData() {
        if (!ENABLED) {
            return;
        }
        SqlManager.getGameSqlNetwork().submit(connection -> {
            String query = "INSERT INTO " + SQLTable.getGameSchemaTable(SQLTable.DAILY_TRACKER) + " (votes, donations, real_online, new_joins, time_dumped) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, TrackerType.VOTES.getTrackerData());
                statement.setInt(2, TrackerType.DONATIONS.getTrackerData());
                statement.setInt(3, PlayerHandler.getUniquePlayerCount());
                statement.setInt(4, TrackerType.NEW_JOINS.getTrackerData());
                statement.setInt(5, TrackerType.DONOR_BOSS.getTrackerData());
                statement.setInt(6, TrackerType.VOTE_BOSS.getTrackerData());
                statement.setInt(7, TrackerType.AFK_BOSS.getTrackerData());
                statement.setInt(8, TrackerType.DURIAL.getTrackerData());
                statement.setInt(9, TrackerType.GROOT.getTrackerData());
                statement.setString(10, Misc.getTime());

                statement.executeUpdate();

                for (TrackerType trackerType : TrackerType.values()) {
                    trackerType.setTrackerData(0);
                    trackerType.getUniqueData().clear();
                    trackerType.getUniqueData2().clear();
                    DataStorage.saveData(trackerType.name(), trackerType.getTrackerData());
                }
            }
        });
    }

    public static void addUniqueData(TrackerType trackerType, String uniqueData, String uniqueData2) {
        if (trackerType.getUniqueData().contains(uniqueData))
            return;
        if (trackerType.getUniqueData2().contains(uniqueData2))
            return;

        trackerType.getUniqueData().add(uniqueData);

        trackerType.getUniqueData2().add(uniqueData2);
    }

    public static LocalDate today = LocalDate.now();

    public static void newDay() {
        LocalDate now = LocalDate.now();
        if (today == null) {
            today = LocalDate.now();
            DataStorage.saveData("today", today.toString());
        }
        if (!today.equals(now) && ENABLED) {
            today = now;

            DataStorage.saveData("today", today);

            insertData();
        }
    }
}
