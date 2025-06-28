package io.xeros.content.commands.all;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.sql.MainSql.StoreDonation;
import io.xeros.sql.donation.query.ClaimDonationsQuery;
import io.xeros.sql.donation.model.DonationItem;
import io.xeros.sql.donation.model.DonationItemList;
import io.xeros.sql.donation.query.GetDonationsQuery;
import io.xeros.util.logging.player.DonatedLog;

/**
 * Changes the password of the player.
 *
 * @author Emiel
 *
 */
public class Claim extends Command {

	public static void claimDonations(Player player) {
		Server.getDatabaseManager().exec(Server.getConfiguration().getStoreDatabase(), (context, connection) -> {
			DonationItemList donationItemList = new GetDonationsQuery(player.getLoginName()).execute(context, connection);

			player.addQueuedAction(plr -> {
				List<DonationItem> claimed = new ArrayList<>();

				try {
					donationItemList.newDonations().forEach(item -> {
						if (giveDonationItem(player, item)) {
							claimed.add(item);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (!claimed.isEmpty()) {
						Server.getDatabaseManager().exec(Server.getConfiguration().getStoreDatabase(), new ClaimDonationsQuery(player, claimed));
					}
				}
			});

			return null;
		});
	}

	public static boolean giveDonationItem(Player plr, DonationItem item) {
		int itemId = item.getItemId();
		int itemQuantity = item.getItemAmount();
		if (plr.getItems().hasRoomInInventory(itemId, itemQuantity)) {
			plr.getItems().addItem(itemId, itemQuantity);
			Server.getLogging().write(new DonatedLog(plr, item));
			plr.getDonationRewards().increaseDonationAmount(item.getItemCost() * itemQuantity);
			plr.sendMessage("You've received x" + item.getItemAmount() + " " + item.getItemName());
			plr.start(new DialogueBuilder(plr).option("Would you like to announce your donation?",
					new DialogueOption("Yes, show my support!", p -> {
				PlayerHandler.message(Right.STAFF_MANAGER, "@blu@[" + p.getDisplayName() + "]@pur@ has just donated for " + itemQuantity + " " + item.getItemName() + "!");
			}), new DialogueOption("No thank you, keep me out the loop.", p -> p.getPA().closeAllWindows())));
			return true;
		} else {
			plr.sendMessage("Not enough room in inventory to claim " + item.getItemName() + ", make space and try again.");
			return false;
		}
	}


	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}

	@Override
	public void execute(Player c, String commandName, String input) {
		new java.lang.Thread() {
			public void run() {
				try {
					com.everythingrs.donate.Donation[] donations = com.everythingrs.donate.Donation
							.donations("D5xMvBs24EOx7N41AEYsdOoWiIJXdUstwtQH2941jsDZ8LV9Ia4zN2ttNe7rpWfTL7F6UJzc", c.getLoginName());
					if (donations.length == 0) {
						c.sendMessage("You currently don't have any items waiting. You must donate first!");
						return;
					}
					if (donations[0].message != null) {
						c.sendMessage(donations[0].message);
						return;
					}
					for (com.everythingrs.donate.Donation donate: donations) {
						String messaged = "Donation System:" + c.getDisplayName() + " has just Donated for " + ItemDef.forId(donate.product_id).getName();
						c.getItems().addItem(donate.product_id, donate.product_amount);
					}
					c.sendMessage("Thank you for donating!");
					PlayerHandler.executeGlobalMessage("@GRE@ Donation System:@GRE@ @RED@" + c.getDisplayName() + "@BLA@ has just Donated Thank you For the Support!!@BLA@");
				} catch (Exception e) {
					c.sendMessage("Api Services are currently offline. Please check back shortly");
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Claim your donated item.");
	}
}
