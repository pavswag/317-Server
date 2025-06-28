package io.xeros.content.commands.owner;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.commands.Command;
import io.xeros.content.customs.CustomItemHandler;
import io.xeros.content.dailyrewards.DailyRewardContainer;
import io.xeros.content.donor.DonorVault;
import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.content.minigames.coinflip.CoinFlip;
import io.xeros.content.minigames.coinflip.CoinFlipJson;
import io.xeros.content.referral.ReferralCode;
import io.xeros.content.vote_panel.VotePanelManager;
import io.xeros.content.wogw.Wogw;
import io.xeros.model.collisionmap.RegionProvider;
import io.xeros.model.collisionmap.doors.DoorDefinition;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.ItemStats;
import io.xeros.model.definitions.ShopDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.ContainerUpdate;
import io.xeros.model.world.ShopHandler;
import io.xeros.sql.refsystem.RefManager;

import java.io.IOException;

/**
 * Reloading certain objects by {String input}
 * 
 * @author Matt
 */

public class Reload extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		switch (input) {

			case "scan":
				for (NPC npc : NPCHandler.npcs) {
					if (npc == null) {
						continue;
					}
					if (npc.getPosition().withinDistance(player.getPosition(), 15)) {
						player.sendErrorMessage("Null npc ? " + npc.getNpcId());
						npc.appendDamage(npc.getHealth().getMaximumHealth(), Hitmark.HIT);
						npc.unregisterInstant();
					}
				}
				break;

			case "":
				player.sendMessage("@red@Usage: ::reload doors, drops, items, objects, shops or npcs");
				break;

			case "coinflip":
				try {
					CoinFlipJson.loadJson();
				} catch (IOException e) {
					player.sendMessage("Error loading coinflip data, check the server output!");
					e.printStackTrace(System.err);
				}
				break;

			case "dailyrewards":
				try {
					DailyRewardContainer.load();
					player.sendMessage("Loaded daily rewards.");
				} catch (Exception e) {
					player.sendMessage("Error loading daily rewards, check the server output!");
					e.printStackTrace(System.err);
				}
				break;

			case "referralcodes":
				try {
//					ReferralCode.load();
					RefManager.loadReferralRewards();
					player.sendMessage("Loaded referral codes.");
				} catch (Exception e) {
					player.sendMessage("Error loading referrals, check the server output!");
					e.printStackTrace(System.err);
				}
				break;

			case "store":
				io.xeros.sql.ingamestore.Configuration.loadConfiguration();
				break;

			case "doors":
				try {
					DoorDefinition.load();
					player.sendMessage("@blu@Reloaded Doors.");
				} catch (IOException e) {
					e.printStackTrace(System.err);
					player.sendMessage("@blu@Unable to reload doors, check console.");
				}
				break;

			case "drops":
				try {
					Server.getDropManager().read();
					player.sendMessage("@blu@Reloaded Drops.");
				} catch (Exception e) {
					player.sendMessage("@red@Error reloading drops!");
					e.printStackTrace(System.err);
				}

				break;

			case "items":
				try {
					ItemDef.load();
					ItemStats.load();
					CustomItemHandler.handleCustomItem();
					DonorVault.handleStatics();
					CoinFlip.itemActionHandler();
					player.sendMessage("@blu@Reloaded Items.");
				} catch (Exception e) {
					player.sendMessage("@blu@Unable to reload items, check console.");
					e.printStackTrace(System.err);
				}
				break;

			case "wogw":
				Wogw.init();
				break;
			case "objects":
				try {
					Server.getGlobalObjects().reloadObjectFile(player);
					player.sendMessage("@blu@Reloaded Objects.");
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
				break;

			case "shops":
				try {
					FireOfExchangeBurnPrice.createBurnPriceShop();
					Server.shopHandler = new ShopHandler();
					ShopDef.load();
					ShopHandler.load();
					player.sendMessage("@blu@Reloaded Shops");
				} catch (Exception e) {
					player.sendMessage("Error occurred, check console.");
					e.printStackTrace(System.err);
				}
				break;

			case "npcs":
				Server.npcHandler = null;
				Server.npcHandler = new NPCHandler();
				player.sendMessage("@blu@Reloaded NPCs");
				break;

			case "votes" :
				VotePanelManager.init();
				player.sendMessage("@blu@Reloaded Votes");
				break;

			case "punishments":
				try {
					Server.getPunishments().initialize();
					player.sendMessage("@blu@Reloaded Punishments.");
				} catch (IOException e) {
					e.printStackTrace(System.err);
				}
				break;

			case "looting":
				Configuration.BAG_AND_POUCH_PERMITTED = !Configuration.BAG_AND_POUCH_PERMITTED;
				player.sendMessage((Configuration.BAG_AND_POUCH_PERMITTED ? "Enabled" : "Disabled") + " bag and pouch.");
				break;
		}
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
	}

}
