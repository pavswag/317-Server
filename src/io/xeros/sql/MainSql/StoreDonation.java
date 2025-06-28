package io.xeros.sql.MainSql;

import io.xeros.content.commands.admin.dboss;
import io.xeros.content.deals.*;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.ImmutableItem;
import io.xeros.sql.dailytracker.TrackerType;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class StoreDonation implements Runnable {

    private Player player;
    private Connection conn;
    private Statement stmt;
    private int totalAmount = 0;

    /**
     * The constructor
     * @param player
     */
    public StoreDonation(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try {
            if (!connect("51.222.84.54", "ebpayments", "OlympusNew", "5uL2yuf8B13e")) {
                return;
            }

            String name = player.getDisplayName().toLowerCase();
            ResultSet rs = executeQuery("SELECT * FROM payments WHERE status = 'completed' AND username = '" + name + "' AND store = 'Turmoil'");

            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                int quantity = rs.getInt("item_quantity");
                double productPrice = rs.getDouble("price");

                productPrice = (quantity * productPrice);

                String itemName = ItemDef.forId(itemId).getName();

                handleDonation(itemId, quantity, productPrice, itemName);

                rs.updateString("status", "claimed");
                rs.updateRow();
            }
            destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDonation(int itemId, int quantity, double productPrice, String itemName) {

        player.queue(() -> {
            player.getInventory().addOrDrop(new ImmutableItem(itemId, quantity));
            player.getDonationRewards().increaseDonationAmount((int) productPrice);
            player.sendMessage("You've received x" + quantity + " " + itemName);
            player.setStoreDonated((long) (player.getStoreDonated() + productPrice));
            player.amDonated += (int) productPrice;
            PlayerHandler.executeGlobalMessage( "@blu@[" + player.getDisplayName() + "]@pur@ just donated for " + quantity + "x " + itemName + "!");

            TrackerType.DONATIONS.addTrackerData((int) productPrice);

            BonusItems.handleDonation(player, itemId, quantity);
            AccountBoosts.addWeeklyDono(player, (int) productPrice);
            player.setCosmeticCredits((long) (player.getCosmeticCredits() + productPrice));
            TimeOffers.checkPurchase(player, itemId, quantity);
            totalAmount += (int) productPrice;

            player.start(new DialogueBuilder(player).statement("Thank you for donating!",
                    "Your items are in your bank."));

            if (totalAmount >= 1000) {
                dboss.spawnBoss();
                totalAmount = 0;
            }
        });

    }

    /**
     *
     * @param host the host ip address or url
     * @param database the name of the database
     * @param user the user attached to the database
     * @param pass the users password
     * @return true if connected
     */
    public boolean connect(String host, String database, String user, String pass) {
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+database, user, pass);
            return true;
        } catch (SQLException e) {
            System.out.println("Failing connecting to database!");
            return false;
        }
    }

    /**
     * Disconnects from the MySQL server and destroy the connection
     * and statement instances
     */
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

    /**
     * Executes an update query on the database
     * @param query
     * @see {@link Statement#executeUpdate}
     */
    public int executeUpdate(String query) {
        try {
            this.stmt = this.conn.createStatement(1005, 1008);
            int results = stmt.executeUpdate(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * Executres a query on the database
     * @param query
     * @see {@link Statement#executeQuery(String)}
     * @return the results, never null
     */
    public ResultSet executeQuery(String query) {
        try {
            this.stmt = this.conn.createStatement(1005, 1008);
            ResultSet results = stmt.executeQuery(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
