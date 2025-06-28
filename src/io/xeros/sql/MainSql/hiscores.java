package io.xeros.sql.MainSql;

import io.xeros.Configuration;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.mode.ModeType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class hiscores implements Runnable {

    public static final String HOST = ""; // website ip address
    public static final String USER = "";
    public static final String PASS = "";
    public static final String DATABASE = "";

    public static final String TABLE = "";

    private Player player;
    private Connection conn;
    private Statement stmt;

    public hiscores(Player player) {
        this.player = player;
    }

    public boolean connect(String host, String database, String user, String pass) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            return false;
        }
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+database, user, pass);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void run() {
        try {
            if (!connect("localhost", "hiscores", "ark", "neK6R2p5")) {
                destroy();
                return;
            }

            if (player.getRights().isOrInherits(Right.STAFF_MANAGER) || player.getRights().isOrInherits(Right.ADMINISTRATOR) || player.getRights().isOrInherits(Right.COMMUNITY_MANAGER)) {
               destroy();
                return;
            }

            String name = player.getLoginName().toLowerCase();

            System.out.println("Saving " + name + ", to the hiscores.");

            PreparedStatement stmt1 = prepare("DELETE FROM "+TABLE+" WHERE username=?");
            stmt1.setString(1, name);
            stmt1.execute();

            PreparedStatement stmt2 = prepare(generateQuery());
            stmt2.setString(1, name);
            stmt2.setInt(2, getRights(player));

            stmt2.setInt(3, getModeString(player)); // game mode number
            stmt2.setInt(4, player.getPA().calculateTotalLevel()); // total level

            stmt2.setLong(5, player.getTotalXp());

            for (int i = 0; i < 24; i++)
                stmt2.setInt(6 + i, player.playerXP[i]);
            //System.out.println(stmt2);
            stmt2.execute();

            destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PreparedStatement prepare(String query) throws SQLException {
        return conn.prepareStatement(query);
    }

    public void destroy() {
        try {
            conn.close();
            conn = null;
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static String generateQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO "+TABLE+" (");
        sb.append("username, ");
        sb.append("rights, ");
        sb.append("mode, ");
        sb.append("total_level, ");
        sb.append("overall_xp, ");
        sb.append("attack_xp, ");
        sb.append("defence_xp, ");
        sb.append("strength_xp, ");
        sb.append("constitution_xp, ");
        sb.append("ranged_xp, ");
        sb.append("prayer_xp, ");
        sb.append("magic_xp, ");
        sb.append("cooking_xp, ");
        sb.append("woodcutting_xp, ");
        sb.append("fletching_xp, ");
        sb.append("fishing_xp, ");
        sb.append("firemaking_xp, ");
        sb.append("crafting_xp, ");
        sb.append("smithing_xp, ");
        sb.append("mining_xp, ");
        sb.append("herblore_xp, ");
        sb.append("agility_xp, ");
        sb.append("thieving_xp, ");
        sb.append("slayer_xp, ");
        sb.append("farming_xp, ");
        sb.append("runecrafting_xp, ");
        sb.append("hunter_xp, ");
        sb.append("demonhunter_xp, ");
        sb.append("fortune_xp) ");
        sb.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return sb.toString();
    }

    private int getModeString(Player player) {
        int mode = 0;
        if (player.getMode().getType() == ModeType.STANDARD) {
            mode = 0;
        } else if (player.getMode().getType() == ModeType.IRON_MAN) {
            mode = 21;
        } else if (player.getMode().getType() == ModeType.ULTIMATE_IRON_MAN) {
            mode = 22;
        } else if (player.getMode().getType() == ModeType.HC_IRON_MAN) {
            mode = 23;
        } else if (player.getMode().getType() == ModeType.OSRS) {
            mode = 24;
        } else if (player.getMode().getType() == ModeType.ROGUE) {
            mode = 25;
        } else if (player.getMode().getType() == ModeType.ROGUE_IRONMAN) {
            mode = 26;
        }  else if (player.getMode().getType() == ModeType.ROGUE_HARDCORE_IRONMAN) {
            mode = 27;
        } else if (player.getMode().getType() == ModeType.GROUP_IRONMAN) {
            mode = 28;
        } else if (player.getMode().getType() == ModeType.WILDYMAN) {
            mode = 29;
        } else if (player.getMode().getType() == ModeType.HARDCORE_WILDYMAN) {
            mode = 30;
        }
        return mode;
    }

    private int getRights(Player player) {
        int rights = 0;
        if (player.getRights().contains(Right.GAME_DEVELOPER)) {
            return Right.GAME_DEVELOPER.getValue();
        }
        if (player.getRights().contains(Right.STAFF_MANAGER)) {
            return Right.STAFF_MANAGER.getValue();
        }
        if (player.getRights().contains(Right.ADMINISTRATOR)) {
            return Right.ADMINISTRATOR.getValue();
        }
        if (player.getRights().contains(Right.MODERATOR)) {
            return Right.MODERATOR.getValue();
        }
        if (player.getRights().contains(Right.HELPER)) {
            return Right.HELPER.getValue();
        }
        if (player.getRights().contains(Right.ALMIGHTY_DONATOR)) {
            return Right.ALMIGHTY_DONATOR.getValue();
        }
        if (player.getRights().contains(Right.APEX_DONATOR)) {
            return Right.APEX_DONATOR.getValue();
        }
        if (player.getRights().contains(Right.PLATINUM_DONATOR)) {
            return Right.PLATINUM_DONATOR.getValue();
        }
        if (player.getRights().contains(Right.GILDED_DONATOR)) {
            return Right.GILDED_DONATOR.getValue();
        }
        if (player.getRights().contains(Right.SUPREME_DONATOR)) {
            return Right.SUPREME_DONATOR.getValue();
        }
        if (player.getRights().contains(Right.MAJOR_DONATOR)) {
            return Right.MAJOR_DONATOR.getValue();
        }
        if (player.getRights().contains(Right.EXTREME_DONATOR)) {
            return Right.EXTREME_DONATOR.getValue();
        }
        if (player.getRights().contains(Right.GREAT_DONATOR)) {
            return Right.GREAT_DONATOR.getValue();
        }
        if (player.getRights().contains(Right.SUPER_DONATOR)) {
            return Right.SUPER_DONATOR.getValue();
        }
        if (player.getRights().contains(Right.DONATOR)) {
            return Right.DONATOR.getValue();
        }

        return rights;
    }
}