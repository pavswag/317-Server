package io.xeros.model.shops;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementTier;
import io.xeros.content.achievement.Achievements.Achievement;
import io.xeros.content.achievement_diary.impl.LumbridgeDraynorDiaryEntry;
import io.xeros.content.fireofexchange.FireOfExchange;
import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.content.lootbag.LootingBag;
import io.xeros.content.questing.hftd.HftdQuest;
import io.xeros.content.upgrade.UpgradeMaterials;
import io.xeros.content.wogw.Wogwitems;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.ShopDef;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.save.PlayerSave;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.model.world.ShopHandler;
import io.xeros.util.Misc;
import io.xeros.util.logging.player.ShopBuyLog;
import io.xeros.util.logging.player.ShopSellLog;

public class ShopAssistant {

	public static final int SHOP_INTERFACE_ID = 64000;
	public static final int SHOP_INTERFACE_ID2 = 3824;

	private final Player c;

	public ShopAssistant(Player client) {
		this.c = client;
	}

	public boolean shopSellsItem(int itemID) {
		for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
			if (itemID == (ShopHandler.ShopItems[c.myShopId][i] - 1)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Shops
	 **/

	public void openShop(int ShopID) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		if (Boundary.isIn(c, Boundary.TOURNAMENT_LOBBIES_AND_AREAS) && ShopID != 147) {
			c.sendMessage("You cannot do this right now.");
			return;
		}
		if (!c.getMode().isShopAccessible(ShopID)) {
			if (Server.isDebug()) {
				c.sendMessage("@red@You normally can't view this shop but debug mode");
			} else {
				c.sendMessage("Your game mode does not permit you to access this shop.");
				c.getPA().closeAllWindows();
				return;
			}
		}
		if (c.getLootingBag().isWithdrawInterfaceOpen() || c.getLootingBag().isDepositInterfaceOpen() || c.viewingRunePouch) {
			c.sendMessage("You should stop what you are doing before opening a shop.");
			return;
		}

		setScrollHeight(ShopID);
		c.getPA().resetScrollPosition(64015);
		c.nextChat = 0;
		c.dialogueOptions = 0;
		c.getItems().sendInventoryInterface(3823);
		resetShop(ShopID);
		c.isShopping = true;
		c.myShopId = ShopID;
		c.getPA().sendFrame248(SHOP_INTERFACE_ID, 3822);
		c.getPA().sendFrame126(ShopHandler.ShopName[ShopID], 64003);
	}

	private void setScrollHeight(int shopId) {
		int size = ShopHandler.getShopItems(shopId).size();
		int defaultHeight = 253;
		int rowHeight = (int) Math.ceil(size / 10.0) * 46;
		c.getPA().setScrollableMaxHeight(64015, Math.max(rowHeight, defaultHeight));
	}

	public void updatePlayerShop() {
		for (int i = 1; i < Configuration.MAX_PLAYERS; i++) {
			if (PlayerHandler.players[i] != null) {
				if (PlayerHandler.players[i].isShopping == true && PlayerHandler.players[i].myShopId == c.myShopId && i != c.getIndex()) {
					PlayerHandler.players[i].updateShop = true;
				}
			}
		}
	}

	public void resetShop(int ShopID) {
		// synchronized (c) {
		int TotalItems = 0;
		for (int i = 0; i < ShopHandler.MaxShopItems; i++) {
			if (ShopHandler.ShopItems[ShopID][i] > 0) {
				TotalItems++;
			}
		}
		if (TotalItems > ShopHandler.MaxShopItems) {
			TotalItems = ShopHandler.MaxShopItems;
		}
		if (ShopID == 80) {
			c.getPA().sendInterfaceHidden(0, 64017);
			c.getPA().sendFrame126("PKP: " + Misc.insertCommas(Integer.toString(c.pkp)), 64019);
		} else {
			c.getPA().sendInterfaceHidden(1, 64017);
		}

		if (c.getOutStream() != null) {
			c.getOutStream().createFrameVarSizeWord(53);
			c.getOutStream().writeUnsignedWord(64016);
			c.getOutStream().writeUnsignedWord(TotalItems);
			int TotalCount = 0;
			for (int i = 0; i < TotalItems; i++) {
				if (ShopHandler.ShopItems[ShopID][i] > 0 || i <= ShopHandler.ShopItemsStandard[ShopID]) {
					if (ShopHandler.ShopItemsN[ShopID][i] > 254) {
						c.getOutStream().writeByte(255);
						c.getOutStream().writeDWord_v2(ShopHandler.ShopItemsN[ShopID][i]);
					} else {
						c.getOutStream().writeByte(ShopHandler.ShopItemsN[ShopID][i]);
					}
					if (ShopHandler.ShopItems[ShopID][i] > Configuration.ITEM_LIMIT || ShopHandler.ShopItems[ShopID][i] < 0) {
						ShopHandler.ShopItems[ShopID][i] = Configuration.ITEM_LIMIT;
					}
					c.getOutStream().writeWordBigEndianA(ShopHandler.ShopItems[ShopID][i]);
					TotalCount++;
				}
				if (TotalCount > TotalItems) {
					break;
				}
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}
	}

	public static int getBuyFromShopPrice(int shopId, int itemId) {
		ShopDef def = ShopDef.get(shopId);
		if (def != null) {
			int definitionPrice = def.getPrice(itemId);
			if (definitionPrice != Integer.MAX_VALUE && definitionPrice != 0)
				return definitionPrice;
		}
		return getItemShopValue(itemId);
	}

	public static int getItemShopValue(int itemId) {
		return ItemDef.forId(itemId).getShopValue();
	}

	/**
	 * buy item from shop (Shop Price)
	 **/
	public void buyFromShopPrice(int removeId, int removeSlot) {
		if (c.myShopId == 0)
			return;
		String itemName = ItemAssistant.getItemName(removeId);
		boolean old = false;
		if (c.myShopId == FireOfExchangeBurnPrice.SHOP_ID) {
			String formattedPrice = Misc.formatCoins(FireOfExchangeBurnPrice.getBurnPrice(null, removeId, false));
			if (formattedPrice.equals("-1")) {
				for (UpgradeMaterials value : UpgradeMaterials.values()) {
					if (value.getReward().getId() == removeId) {
						formattedPrice = Misc.formatCoins((value.getCost() / 5));
						old = true;
					}
				}
			}
			if (formattedPrice.equals("-1") && !old) {
				c.sendMessage("You cannot exchange {} for exchange points.", itemName);
				return;
			}
			c.sendMessage("You can exchange {} for <col=255>{}</col> exchange points.", itemName, formattedPrice);
			return;
		}

		int ShopValue = (int) Math.floor(getBuyFromShopPrice(c.myShopId, removeId));

		ShopValue *= 1.00;
		ShopValue = c.getMode().getModifiedShopPrice(c.myShopId, removeId, ShopValue);
		String ShopAdd = "";

		if (c.myShopId == 179) {
			c.sendMessage(itemName + ": currently costs 10,000,000 coins.");
			return;
		}
		if (c.myShopId == 198) {
			c.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " molch pearls.");
			return;
		}
		if (c.myShopId == 40) {
			c.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " mage arena points.");
			return;
		}
		if (c.myShopId == 189) {
			c.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " WeaponGame points.");
			return;
		}
		if (c.myShopId == 83) {
			c.sendMessage("You cannot buy items from this shop.");
			return;
		}
		if (c.myShopId == 44) {
			if (ItemDef.forId(removeId).getName().contains("head")) {
				c.sendMessage("This product cannot be purchased.");
				return;
			}
		}
		if (c.myShopId == 82) {
			c.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Assault points.");
			return;
		}
		if (c.myShopId == 191) {
			c.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Star Dust.");
			return;
		}
		if (c.myShopId == 118) {
			c.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Discord points.");
			return;
		}
		if (c.myShopId == 44) {
			c.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " slayer points.");
			return;
		}
		if (c.myShopId == 13) {
			c.sendMessage("Jossik will switch " + itemName + " for " + getSpecialItemValue(removeId) + " rusty casket.");
			return;
		}
		if (c.myShopId == 10) {
			int price = getSpecialItemValue(removeId);
			if (price == 0) {
				c.sendMessage(itemName + " is free.");
				return;
			}
			c.sendMessage(itemName + ": currently costs [ <col=a30027>" +  Misc.insertCommas(price) + " </col>] Slayer Points.");
			return;
		}
		if (c.myShopId == 120) {
			c.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " prestige points.");
			return;
		}
		if (c.myShopId == 77) {
			c.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Vote points.");
			return;
		}
		if (c.myShopId == 80) {
			c.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] PKP Points.");
			return;
		}
		if (c.myShopId == 121) {
			c.sendMessage(itemName + ": currently costs [ @pur@" + Misc.insertCommas(getSpecialItemValue(removeId)) + " @bla@] Boss Points.");
			return;
		}
		if (c.myShopId == 15) {
			c.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Christmas Points.");
			return;
		}
		if (c.myShopId == 171) {
			if (removeId == 691 || removeId == 692 || removeId == 693 || removeId == 696) {
				c.sendMessage("@pur@[NOMAD]@bla@ You pay an extra 10% when buying back Upgrade Points as certificates.");
			}
			c.sendMessage(itemName + "@bla@: currently costs [ @pur@" + Misc.formatCoins(getSpecialItemValue(removeId)) + " @bla@] Upgrade Points.");
			return;
		}
		if (c.myShopId == 172 || c.myShopId == 173) {
			c.sendMessage(itemName + ": currently exchanges for  [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Exchange Points.");
			return;
		}
		if (c.myShopId == 131) {
			c.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Tournament Points.");
			return;
		}
		if (c.myShopId == 193) {
			c.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Arbograve Swamp Points.");
			return;
		}
		if (c.myShopId == 119) {
			c.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Blood Money points.");
			return;
		}
		if (c.myShopId == 192) {
			c.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] AOE Instance points.");
			return;
		}
		if (c.myShopId == 78) {
			c.sendMessage(itemName + ": currently costs [@lre@" + getSpecialItemValue(removeId) + " @bla@] Achievement Points.");
			return;
		}
		if (c.myShopId == 75) {
			c.sendMessage(itemName + ": currently costs [@gre@ " + getSpecialItemValue(removeId) + " @bla@] PC points.");
			return;
		}
		if (c.myShopId == 9 || c.myShopId == 112) {
			c.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Donator credits.");
			return;
		}
		if (c.myShopId == 18) {
			c.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Marks of grace.");
			return;
		}
		if (c.myShopId == 197) {
			c.sendMessage(itemName + ": currently costs " + Misc.insertCommas(ShopValue) + " Bloody points.");
			return;
		}
		if (c.myShopId == 600) {
			c.sendMessage(itemName + ": currently costs " + Misc.insertCommas(ShopValue) + " Scrap Paper.");
			return;
		}
		if (c.myShopId == 115) {
			c.sendMessage(itemName + ": is completeley free.");
			return;
		}
		if (c.myShopId == 116) {
			c.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Blood money.");
			return;
		}
		if (c.myShopId == 117) {
			c.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Blood money.");
			return;
		}
		if (c.myShopId == 195) {
			c.sendMessage(itemName + ": currently costs " + Misc.insertCommas(ShopValue) + " AFK points.");
			return;
		}
		if (c.myShopId == 196) {
			c.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Seasonal points.");
			return;
		}
		if (c.myShopId == 199) {
			c.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Donation Coins.");
			return;
		}
		if (c.myShopId == 123) {
			c.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Platinum token.");
			return;
		}

		if (c.myShopId == 29) {
			if (c.getRechargeItems().hasItem(11136)) {
				ShopValue = (int) (ShopValue * 0.95);
			}
			if (c.getRechargeItems().hasItem(11138)) {
				ShopValue = (int) (ShopValue * 0.9);
			}
			if (c.getRechargeItems().hasItem(11140)) {
				ShopValue = (int) (ShopValue * 0.85);
			}
			if (c.getRechargeItems().hasItem(13103)) {
				ShopValue = (int) (ShopValue * 0.75);
			}
			c.sendMessage(itemName + ": currently costs " + ShopValue + " tokkul" + ShopAdd);
			return;
		}
		if (c.myShopId == 79) {
			c.sendMessage("This item is free.");
			return;
		}
		c.sendMessage(itemName + ": currently costs " + Misc.insertCommas(ShopValue) + " coins (" + Misc.formatCoins(ShopValue) + ")");
	}

	public int getSpecialItemValue(int id) {
		ShopDef shopDef = ShopDef.getDefinitions().get(c.myShopId);
		switch (c.myShopId) {
			case 26: // Sigmond the merchant
				switch(id) {
					case 2007:
						return 1_000;
					case 19473:
						return 10_000;
					case 6814:
						return 2_500;
					case 950:
						return 750;
					case 1893:
						return 500;
					case 712:
						return 250;
				}
				break;
			case 13: // Jossik quest shop
				return 1;
			case 15:
				switch(id) {
					case 12887:
					case 12888:
					case 12889:
					case 12890:
					case 12891:
					case 12892:
					case 12893:
					case 12894:
					case 12895:
					case 12896:
						return 80;

				}
				break;
			case 82:
				switch (id) {
					case 10548:
						return 30;
					case 10551:
						return 100;
					case 11898:
					case 11897:
					case 11896:
						return 25;
					case 11899:
					case 11900:
						return 25;
					case 11937:
					case 11936:
						return 10;
				}
				break;
			case 147://outlast shop
				if (Boundary.isIn(c, Boundary.OUTLAST_AREA) || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_LOBBY)
						|| Boundary.isIn(c, Boundary.FOREST_OUTLAST)
						|| Boundary.isIn(c, Boundary.SNOW_OUTLAST)
						|| Boundary.isIn(c, Boundary.ROCK_OUTLAST)
						|| Boundary.isIn(c, Boundary.BOUNTY_HUNTER_OUTLAST)
						|| Boundary.isIn(c, Boundary.FALLY_OUTLAST)
						|| Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST)
						|| Boundary.isIn(c, Boundary.SWAMP_OUTLAST)
						|| Boundary.isIn(c, Boundary.WG_Boundary)) {
					switch (id) {
						case 385:
						case 3144:
						case 2301:
						case 3024:
						case 12695:
						case 2444:
						case 3040:
						case 10926:
							return 0;
					}
					return 5_999;
				}
				break;
			case 195:
			case 118:
			case 75:
			case 189:
			case 197:
			case 600:
			case 191:
			case 198:
			case 193:
			case 196:
			case 190:
			case 123:
			case 77: //Vote Shop
				int price = shopDef.getPrice(id);
				if (price != 0) {
					return price;
				}

				switch (id) {
					case 11936:
						return 3;
					case 11920:
						return 60;
					case 12797:
						return 55;
					case 6739:
						return 25;
					case 2577://ranger boots
						return 40;
					case 12526:
						return 25;
					case 20214:
					case 20217:
						return 20;
					case 20050:
						return 30;
					case 13221:
						return 100;
					case 20756:
						return 50;
					case 21028:
						return 60;
					case 13241:
					case 13242:
					case 13243:
						return 100;
					case 6666:
						return 15;
					case 12783:
						return 400;
					case 12639:
					case 12637:
					case 12638:
					case 24198:
					case 24201:
					case 24204:
					case 24195:
					case 24192:
						return 45;
					case 20836:
						return 85;
					case 5608:
						return 150;
					case 12600:
						return 30;
					case 19941:
						return 85;
					case 20056:
						return 150;
					case 10507:
						return 50;
					case 9920:
						return 120;
					case 21439:
						return 45;
					case 13148:
						return 15;
					case 23677:
						return 55;
					case 23294:
					case 23285:
					case 23288:
					case 23291:
					case 776:
						return 10;
					case 2841:
						return 15;
					case 22093:
						return 55;
					case 30023:
						return 160;
					case 12954:
						return 10;
					case 10551:
						return 10;

				}
				break;
			case 80:
			case 112:
			case 199:
			case 9: //Donator Point Shop
			case 119:
			case 131:
			case 10:
			case 120:
			case 192:
			case 121: // Boss points shop
				return shopDef.getPrice(id);

			case 171: //Exchange Points
				return FireOfExchange.getExchangeShopPrice(id);
			case 172: //Exchange shop showcase
				switch (id) {
					case 4722://barrows start
					case 4720:
					case 4716:
					case 4718:
					case 4714:
					case 4712:
					case 4708:
					case 4710:
					case 4736:
					case 4738:
					case 4732:
					case 4734:
					case 4753:
					case 4755:
					case 4757:
					case 4759:
					case 4745:
					case 4747:
					case 4749:
					case 4751:
					case 4724:
					case 4726:
					case 4728:
					case 4730:// all barrows complete
					case 11836://bandos boots
						return 400;
					case 2577://ranger boots
					case 6585://fury
						return 2000;
					case 4151://whip
						return 750;
					case 6737://b ring
					case 6733://archer ring

					case 6731://seers ring

						return 1850;
					case 11834://bcp
					case 11832://tassets
					case 11826://army helm
					case 11828://army plate
					case 11830://arma leg
					case 13239:// primordials ect +
					case 13237://pegasion
					case 13235://eternal
						return 7500;
					case 13576://d warhammer
						return 14000;
					case 11802://ags
						return 12000;
					case 20784://claws
						return 17000;
					case 13265://abby dagger
					case 11808://zgs
					case 11804://bgs
					case 11806://sgs
						return 5000;
					case 13263://bludgon
					case 19552://zenyte brace
					case 19547://anguish
					case 19553://torture

						return 8500;
					case 12002: //occult
						return 800;
					case 12809://sara blessed
					case 12006://tent whip
						return 5000;
					case 11284://dfs
					case 19478://light ballista
					case 12853://amulet of damned
						return 6500;
					case 19481://heavy ballista
						return 13000;
					case 12821://spectral
					case 12825://arcane
						return 25000;
					case 12817://ely
						return 45000;
					case 11785://arma crossbow
						return 6500;
					case 21902:
						return 8500;
					case 21012://dhcb
						return 9000;
					case 12924://blowpipe
					case 12926:
						return 20000;
					case 11770://seers i
					case 11771://archer i
					case 11772://warrior i
					case 11773://b ring i
						return 5000;
					case 20997://t bow
						return 100000;
					case 12806://malediction
					case 12807://odium
						return 6000;
				}
				break;
			case 173: //Exchange shop showcase 2
				switch (id) {
					case 22322:
					case 21015:
					case 21003:
					case 12902:
					case 13196:
					case 13198:
					case 12929:
						return 9000;
					case 20517:
					case 20520:
					case 20595:
						return 1200;
					case 20095://ankou start
					case 20098:
					case 20101:
					case 20104:
					case 20107://ankou end
					case 20080://mummy start
					case 20083:
					case 20086:
					case 20089:
					case 20092://mummy end
						return 4750;
					case 12603:
					case 12605:
						return 1850;
					case 21902:
						return 7000;
					case 21006:
						return 10000;
					case 21018://justiciar + ancestral
					case 21021:
					case 21024:
					case 22326:
					case 22327:
					case 22328:
						return 25000;

				}
				break;
			case 117:
				switch (id) {
					case 4716:
					case 4720:
					case 4722:
					case 4718: //Dharok
						return 120;
					case 4724:
					case 4726:
					case 4728:
					case 4730: //Guthan
					case 4745:
					case 4747:
					case 4749:
					case 4751: //Torag
					case 4753:
					case 4755:
					case 4757:
					case 4759: //Verac
						return 100;
					case 4708:
					case 4710:
					case 4712:
					case 4714: //Ahrim
					case 4732:
					case 4734:
					case 4736:
					case 4738: //Karil
						return 200;
					case 12006: //Abyssal tentacle
						return 400;
					case 13263: //Bludgeon
						return 3500;
					case 13271: //Abyssal dagger
						return 800;
					case 19481: //Ballista
						return 1500;
					case 12902: //Toxic staff
						return 1000;
					case 12924: //Blowpipe
						return 3000;
					case 11286: //Visage
						return 100;
					case 11785: //Armadyl crossbow
						return 2500;
					case 13227: //Crystals
					case 13229:
					case 13231:
					case 13233:
						return 1500;
					case 12695: //Super combat
						return 1;
					case 12929: //Serp helm
						return 1200;
					case 12831: //Blessed shield
						return 800;
					case 19529: //Zenyte shard
						return 1500;
					case 11832: //Bandos chest
					case 11834: //Bandos tassets
					case 11826: //Armadyl helm
					case 11828: //Armadyl chest
					case 11830: //Armadyl skirt
						return 700;
					case 6737: //Berseker ring
						return 500;
					case 6735: //Warrior ring
						return 50;
					case 6733: //Archers ring
						return 150;
					case 6731: //Seers ring
						return 150;
					case 12603: //Tyrannical ring
					case 12605: //Treasonous ring
						return 200;
					case 12853: //Amulet of the damned
						return 700;
					case 6585: //Fury
						return 150;
					case 11802: //Ags
						return 2000;
					case 11804: //Bgs
						return 200;
					case 11806: //Sgs
						return 1000;
					case 11808: //Zgs
						return 300;
					case 13576: //Dwh
						return 3000;
					case 11235: //Dbow
						return 150;
					case 11926: //Odium ward
					case 11924: //Malediction ward
						return 700;
					case 10551: //Torso
						return 150;
					case 10548: //Fighter hat
						return 100;
					case 11663: //Void mage helm
					case 11665: //Void melee helm
					case 11664: //Void ranger helm
					case 8842: //Void gloves
						return 50;
					case 8839: //Void top
					case 8840: //Void bottom
						return 75;
				}
				break;

			case 2:
				switch (id) {
					case 527:
						return 256;
				}
				break;

			case 116:
				if (ItemDef.forId(id).getName().contains("dharok")) {
					return 20;
				}
				if (ItemDef.forId(id).getName().contains("guthan") ||
						ItemDef.forId(id).getName().contains("torag") ||
						ItemDef.forId(id).getName().contains("verac") ||
						ItemDef.forId(id).getName().contains("karil")) {
					return 12;
				}
				if (ItemDef.forId(id).getName().contains("ahrim")) {
					return 14;
				}

				switch (id) {
					case 12695: //Super combat
						return 5;

					case 12831: //Blessed spirit shield
						return 25;

					case 11772: //Warriors ring
					case 12692: //Treasonous ring
					case 12691: //Tyrannical ring
						return 50;

					case 12924: //Blowpipe
					case 11770: //Rings
					case 11771:
					case 11773:
					case 12851: //Amulet of damned
					case 12853: //Amulet of damned
						return 75;

					case 11235: //Dbow
						return 150;

					case 12929: //Serp helmets
					case 13196:
					case 13198:
					case 13235: //Cerb boots
					case 13237:
					case 13239:
					case 19553: //Amulet of torture
					case 19547: //Anguish
						return 250;

					case 12807: //Wards
					case 12806:
						return 300;

					case 11804: //Godswords
					case 11806:
					case 11808:
						return 500;

					case 12902: //Tsotd
					case 13271: //Abyssal dagger
						return 800;

					case 11802: //Armadyl godsword
					case 13576: //D warhammer
					case 13263: //Abyssal bludgeon
						return 1000;

					case 19481: //Heavy ballista
						return 1500;

					case 12821: //Spectral
						return 2000;

					case 12825: //Arcane
						return 2500;

					case 12817: //Elysian
						return 3500;
				}
				break;
		}

		switch (id) {
			/*
			 * Graceful store
			 */
			case 11850:
				return 35;
			case 11852:
				return 40;
			case 11854:
				return 55;
			case 11856:
				return 60;
			case 11858:
				return 30;
			case 11860:
				return 40;
			case 12792:
				return 15;
			case 12641:
				return 10;
			/*
			 * PK STORE
			 */
			case 11900:
				return 60;
			case 11899:
				return 70;
			case 11898:
				return 50;
			case 11896:
				return 80;
			case 11897:
				return 70;
			case 12829:
				return 120;
			case 2379:
			case 13066:
			case 2289:
				return 1;
			case 12746:
				return 20;
			case 7582:
			case 3144:
			case 4153:
				return 50;
			case 1052:
				return 40;
			case 4224:
				return 25;
			case 1249:
				return 300;
			case 2839:
				return 350;
			case 4202:
				return 200;
			case 6720:
				return 300;
			case 4081:
				return 250;
			case 3481:
			case 3483:
			case 3485:
			case 3486:
			case 3488:
			case 6856:
			case 6857:
			case 6858:
			case 6859:
			case 6860:
			case 6861:
			case 6862:
			case 6863:
			case 989:
				return 10;
			case 4333:
			case 4353:
			case 4373:
			case 4393:
			case 4413:
			case 11212:
				return 2;
			case 2996:
			case 23933:
				return 1;
			case 4170:
				return 100;
			case 12020:
				return 200;

			case 10887:
				if (c.myShopId == 9 || c.myShopId == 112)
					return 20;
				else
					return 200;
			case 8849:
				return 20;
			case 8848:
				return 20;
			case 7398:
			case 7399:
			case 7400:
				return 40;
			case 8845:
				return 10;
			case 12751:
				return 500;
			case 7462:
				if (c.myShopId == 10)
					return 0;

			case 2437:
			case 2441:
			case 2443:
				return 100;
			case 3025:
			case 6686:
				return 150;
			case 11937:
				return 250;
			case 7461:
				if (c.myShopId == 12)
					return 80;
				else
					return 8;
			case 7460:
				if (c.myShopId == 12)
					return 60;
				else
					return 50;
			case 7459:
				if (c.myShopId == 12)
					return 50;
				else
					return 40;
			case 7458:
				return 45;
			case 12006:
				return 30;
			case 12000:
				return 20;
			case 12002:
				return 200;
			case 2677:
				return 15;
			case 2572:
				return 50;
			case 2722:
				return 10;
			case 12399:
				return 160;
			case 13887:
			case 13893:
				if (c.myShopId == 9 || c.myShopId == 112)
					return 50;
				else
					return 280;
			case 13902:
				return 110;
			case 14484:
				return 400;
			case 13896:
			case 13884:
			case 13890:
				if (c.myShopId == 9 || c.myShopId == 112)
					return 40;
				else
					return 250;
			case 13858:
			case 13861:
			case 13864:
				if (c.myShopId == 9 || c.myShopId == 112)
					return 20;
				else
					return 130;
			case 13870:
			case 13873:
			case 13876:
				if (c.myShopId == 9 || c.myShopId == 112)
					return 30;
				else
					return 130;
			case 10551:
			case 10548:
				if (c.myShopId == 9 || c.myShopId == 112)
					return 10;
				else if (c.myShopId == 12)
					return 150;
				else
					return 20;
			case 10550:
				return 80;
			case 7509:
				return 5;
			case 13116:
				return 500;
			case 536:
			case 537:
				return 1;
			case 6735:
				return 250;
			case 9437:
				if (c.myShopId == 41)
					return 1000;
				else
					return 120;
				/*
				 * case 6916: case 6918: case 6920: case 6922: case 6924: return 75;
				 */
			case 9672:
			case 9674:
			case 9676:
				return 130000;
			case 1333:
				return 60000;
			case 4587:
				return 90000;
			case 10925:
				return 15000;
			case 3204:
				return 1000;
			case 6585:
			case 11840:
				if (c.myShopId == 12)
					return 20;
				else
					return 400;
			case 12905:
				return 1;
			case 2417:
			case 2415:
			case 2416:
				return 100;
			case 1409:
				return 150;
			case 3839:
			case 3841:
			case 3843:
				if (c.myShopId == 13)//slayer store
					return 1;
				else
					return 50;
			case 6916:
			case 6918:
			case 6922:
			case 6924:
				return 175;
			case 11230:
				return 10;
			case 6920:
				return 350;
			case 11864:
				return 400;
			case 13226:
				return 200;
			case 4168:
			case 4166:
			case 4551:
			case 4164:
			case 4155:
				return 0;
			case 6121:
				if (c.myShopId ==  80) {
					return 300;
				}
				return 250;
			case 19641:
			case 19645:
			case 19649:
				return 1000;
			case 6570:
				return 20;
			case 11235:
				return 100;
			case 11785:
				if (c.myShopId == 9 || c.myShopId == 112)
					return 30;
				else
					return 750;
			case 11791:
				if (c.myShopId == 9 || c.myShopId == 112)
					return 30;
				else
					return 400;
			case 11061:
				if (c.myShopId == 9 || c.myShopId == 112)
					return 50;
				else
					return 1000;
			case 11907:
				if (c.myShopId == 9 || c.myShopId == 112)
					return 50;
				else
					return 500;
			case 2528:
				return 6;
			case 11739:
				return 2;
			case 22993:
				return 2;
			case 6889:
				return 80;
			case 6914:
				return 200;
			case 4732:
			case 4734:
			case 4736:
			case 4738:
				return 12;
			case 4716:
			case 4718:
			case 4720:
			case 4722:
				return 20;
			case 4712:
			case 4710:
			case 4714:
			case 4708:
				return 14;
			case 4724:
			case 4726:
			case 4728:
			case 4730:
			case 4745:
			case 4747:
			case 4749:
			case 4751:
			case 4753:
			case 4755:
			case 4757:
			case 4759:
				return 12;
			case 10338:
			case 10342:
			case 10340:
			case 1989:
				return 500;
			case 11838:
			case 4151:
			case 1961:
				return 100;
			case 10352:
			case 10350:
			case 10348:
			case 10346:
				return 1000;
			case 11826:
				return 80;
			case 11828:
				return 100;
			case 11830:
				return 100;
			case 11283:
			case 1959:
			case 9703:
				if (c.myShopId == 9 || c.myShopId == 112)
					return 25;
				else
					return 400;
			case 11802:
				if (c.myShopId == 9 || c.myShopId == 112)
					return 50;
				else
					return 900;
			case 2581:
				return 40;
			case 2577:
				return 40;
			case 11832:
			case 11834:
				if (c.myShopId == 9 || c.myShopId == 112)
					return 30;
				else
					return 500;
			case 11804:
				return 700;
			case 11808:
			case 11806:
				return 600;
			// DONATOR
			case 1042:
				return 50;
			case 1048:
				return 50;
			case 1038:
				return 50;
			case 1046:
				return 50;
			case 1044:
				return 90;
			case 1040:
				return 80;
			case 1053:
			case 1055:
			case 1057:
				return 60;
			case 1419:
				return 40;
			case 4566:
				return 40;
			case 4084:
				return 60;
			case 1037:
				return 50;
			case 11919:
				return 10;
			case 12956:
			case 12957:
			case 12958:
			case 12959:
			case 10933:
			case 10939:
			case 10940:
			case 10941:
			case 13258:
			case 13259:
			case 13260:
			case 13261:
				return 5;
			case 12596:
				return 30;
			case 6199:
				if (c.myShopId == 78)
					return 30;
				else
					return 20;
				// VOTE
			case 12748:
				return 2;
			case 7409:
				return 15;
			case 9920:
				return 10;
			case 12637:
			case 12638:
			case 12639:
				return 20;
			case 775:
				return 10;
			case 3057:
			case 3058:
			case 3059:
			case 6654:
			case 6655:
			case 6656:
			case 6180:
			case 6181:
			case 6182:
			case 13640:
			case 13642:
			case 13644:
			case 13646:
			case 5553:
			case 5554:
			case 5555:
			case 5556:
			case 5557:
			case 20704:
			case 20706:
			case 20708:
			case 20710:
			case 12013:
			case 12014:
			case 12015:
			case 12016:
				return 5;
			case 776:
			case 20008:
			case 10071:
				return 15;
			case 6666:
				return 20;
			case 1050:
				return 80;
			case 11887:
				return 10;

		}
		return Integer.MAX_VALUE;
	}

	/**
	 * Sell item to shop (Shop Price)
	 **/
	public void sellToShopPrice(int removeId, int removeSlot) {
		if (c.myShopId == 0)
			return;
		if (c.myShopId == FireOfExchangeBurnPrice.SHOP_ID)
			return;
		boolean CANNOT_SELL = !ItemDef.forId(removeId).isTradable();

		// Can't sell anglerfish
		if (removeId == 13441 || removeId == 13442) {
			c.sendMessage("You can't sell " + ItemAssistant.getItemName(removeId).toLowerCase() + ".");
			return;
		}
		if (c.myShopId == 147
				|| c.myShopId == 18 || c.myShopId == 198 || c.myShopId == 199 || c.myShopId == 119) {
			c.sendMessage("You can't sell items here.");
			return;
		}

		if (c.myShopId != 116 && c.myShopId != 115 && CANNOT_SELL) {
			c.sendMessage("You can't sell " + ItemAssistant.getItemName(removeId).toLowerCase() + ".");
			return;
		}
		boolean IsIn = false;
		if (ShopHandler.ShopSModifier[c.myShopId] > 1) {
			for (int j = 0; j <= ShopHandler.ShopItemsStandard[c.myShopId]; j++) {
				if (removeId == (ShopHandler.ShopItems[c.myShopId][j] - 1)) {
					IsIn = true;
					break;
				}
			}
		} else {
			IsIn = true;
		}
		if (IsIn == false) {
			c.sendMessage("You can't sell that item to this store.");
		} else {
			int ShopValue = 0;
			String ShopAdd = "";
			if (c.itemId == 13240) {
				ShopValue *= 1;
			}
			if (c.myShopId != 26 && c.itemId != 13240) {
				ShopValue *= 0.337;
			}
			if (c.myShopId == 147) {
				ShopValue *= 0;
			}
			if (c.myShopId == 83) {
				int i = 0;
				for (final Wogwitems.itemsOnWell t : Wogwitems.itemsOnWell.values()) {
					if (t.getItemId() == removeId) {
						ShopValue = (int) Math.floor(t.getItemWorth());
						i++;
					}
				}
				if (i == 0) {
					c.sendMessage("You can't sell this item to this store.");
					return;
				}
			} else {
				ShopValue = (int) Math.floor(getItemShopValue(removeId));
			}

			if (c.myShopId == 44) {
				if (!ItemDef.forId(removeId).getName().contains("head")) {
					c.sendMessage("You cannot sell this to the slayer shop.");
					return;
				} else {
					c.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + ShopValue + " slayer points" + ShopAdd);
				}
			} else if (c.myShopId == 18) {
				c.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + ShopValue + " marks of grace" + ShopAdd);
			} else if (c.myShopId == 172 || c.myShopId == 173) {
				c.sendMessage("You cannot sell items to this shop.");
			} else if (c.myShopId == 116) {
				c.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + ((int) Math.ceil((getSpecialItemValue(removeId) * 0.60)) + " blood money"));
			} else if (c.myShopId == 600) { //Box
				c.sendMessage("You cannot sell items to this shop.");
			}  else if (c.myShopId == 123) {  //Flex
				c.sendMessage("You cannot sell items to this shop.");
			}  else if (c.myShopId == 29) {
				c.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + ShopValue + " tokkul" + ShopAdd);
			}  else if (c.myShopId == 195) {
				c.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + (getSpecialItemValue(removeId) + " AFK Points"));
			}  else if (c.myShopId == 199) {
				c.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + ((getSpecialItemValue(removeId) / 2)  + " Donation Coins"));
			} else {
				ShopValue *= 0.263;
				ShopValue *= 0.263;
				if (ShopValue >= 1_000_000)
					ShopAdd = " (" + Misc.formatCoins(ShopValue) + ")";
				c.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + Misc.insertCommas(ShopValue) + " coins" + ShopAdd + ".");
			}
		}
	}

	/**
	 * Selling items back to a store
	 * @param itemID
	 * 					itemID that is being sold
	 * @param fromSlot
	 * 					fromSlot the item currently is located in
	 * @param amount
	 * 					amount that is being sold
	 * @return
	 * 					true is player is allowed to sell back to the store,
	 * 					else false
	 */
	public boolean sellItem(int itemID, int fromSlot, int amount) {
		if (c.myShopId == 0 || !c.isInterfaceOpen(SHOP_INTERFACE_ID) && !c.isInterfaceOpen(SHOP_INTERFACE_ID2))
			return false;
		if (Configuration.DISABLE_SHOP_SELL) {
			c.sendMessage("Selling to shops is disabled atm.");
			return false;
		}
		if (Boundary.isIn(c, Boundary.TOURNAMENT_LOBBIES_AND_AREAS)) {
			c.sendMessage("You cannot do this right now.");
			return false;
		}
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return false;
		}
		if (!c.getMode().isItemSellable(c.myShopId, itemID)) {
			c.sendMessage("Your game mode does not permit you to sell this item to the shop.");
			return false;
		}
		if (c.myShopId == 195 && itemID != 7478) {
			c.sendErrorMessage("You can't sell items back to the afk store!");
			return false;
		}
		if (Configuration.DISABLE_FOE && c.myShopId == FireOfExchange.FOE_SHOP_ID) {
			c.sendMessage("Fire of Exchange has been temporarily disabled.");
			return false;
		}
		if (c.myShopId == FireOfExchangeBurnPrice.SHOP_ID)
			return false;
		if (c.myShopId != 115) {
			if (itemID == 995) {
				c.sendMessage("You can't sell this item.");
				return false;
			}
		}
		if (c.myShopId == 26) {
			if (itemID != 1893 &&
					itemID != 1894 &&
					itemID != 712 &&
					itemID != 2961 &&
					itemID != 6814 &&
					itemID != 950 &&
					itemID != 2962 &&
					itemID != 1613 &&
					itemID != 1614 &&
					itemID != 1993 &&
					itemID != 1994 &&
					itemID != 4692 &&
					itemID != 19473) {
				c.sendMessage("You can't sell this item.");
				return false;
			}
		}
		if (c.myShopId == 195) {
			if (itemID != 7478) {
				c.sendMessage("You can't sell this item.");
				return false;
			}
		}
		switch (c.myShopId) {
			case 9:
			case 12:
			case 199:
			case 13:
			case 14:
			case 21:
			case 22:
			case 23:
			case 75:
			case 77:
			case 121:
			case 15:
			case 80:
			case 171:
			case 172:
			case 173:
			case 120:
			case 10:
			case 131:
			case 78:
			case 147:
			case 117:
			case 18:
			case 197:
			case 600:
			case 191:
			case 193:
			case 196:
			case 123:
				c.sendMessage("You cannot sell items to this shop.");
				return false;
		}
		boolean CANNOT_SELL = !ItemDef.forId(itemID).isTradable();
		if (c.myShopId != 116 && c.myShopId != 115 && c.myShopId != 199) {
			if (CANNOT_SELL) {
				c.sendMessage("You can't sell " + ItemAssistant.getItemName(itemID).toLowerCase() + ".");
				return false;
			}
		}
		if (amount > 0 && itemID == (c.playerItems[fromSlot] - 1)) {
			if (ShopHandler.ShopSModifier[c.myShopId] > 1) {
				boolean IsIn = false;
				for (int i = 0; i <= ShopHandler.ShopItemsStandard[c.myShopId]; i++) {
					if (itemID == (ShopHandler.ShopItems[c.myShopId][i] - 1)) {
						IsIn = true;
						break;
					}
				}
				if (IsIn == false) {
					c.sendMessage("You can't sell that item to this store.");
					return false;
				}
			}

			boolean noted = ItemDef.forId(c.playerItems[fromSlot] - 1).isNoted();
			boolean stackable = ItemDef.forId(c.playerItems[fromSlot] - 1).isStackable();
			if (amount > c.playerItemsN[fromSlot] && (noted || stackable)) {
				amount = c.playerItemsN[fromSlot];
			} else if (amount > c.getItems().getItemAmount(itemID) && !noted && !stackable) {
				amount = c.getItems().getItemAmount(itemID);
			}
			// double ShopValue;
			// double TotPrice;
			int TotPrice2 = 0;
			int TotPrice3 = 0;
			int TotPrice4 = 0;
			// int Overstock;
			//for (int i = amount; i > 0; i--) {
			TotPrice2 = (int) Math.floor(getItemShopValue(itemID));//ShopValue *= 0.263;
			TotPrice3 = (int) Math.floor(getSpecialItemValue(itemID));
			if (c.myShopId == 83) {
				int i = 0;
				for (final Wogwitems.itemsOnWell t : Wogwitems.itemsOnWell.values()) {
					if (t.getItemId() == itemID) {
						TotPrice4 = (int) Math.floor(t.getItemWorth());
						i++;
					}
				}
				if (i == 0) {
					c.sendMessage("You can't sell that item to this store.");
					return false;
				}
			}
			TotPrice2 *= 0.263;
			TotPrice2 *= 0.263;
			TotPrice2 = TotPrice2 * amount;
			TotPrice4 = TotPrice4 * amount;
			if (c.getItems().freeSlots() > 0 || c.getItems().playerHasItem(995)) {
				if ((ItemDef.forId(itemID).isStackable() || ItemDef.forId(itemID).isNoted()) && c.getItems().playerHasItem(itemID, amount)) {
					c.getItems().deleteItemNoSave(itemID,c.getItems().getInventoryItemSlot(itemID),amount);
					logShop("sell", itemID, amount);
					if (c.myShopId != 12 && c.myShopId != 29 && c.myShopId != 44 && c.myShopId != 18  && c.myShopId != 83 && c.myShopId != 116 && c.myShopId != 115 && c.myShopId != 195 && c.myShopId != 199) {
						c.getItems().addItem(995, TotPrice2);
						logShop("received", 995, TotPrice2);
					} else if (c.myShopId == 29) {
						c.getItems().addItem(6529, TotPrice2);
						logShop("received", 6529, TotPrice2);
					} else if (c.myShopId == 18) {
						c.getItems().addItem(11849, TotPrice2);
						logShop("received", 11849, TotPrice2);
					} else if (c.myShopId == 83) {
						c.getItems().addItem(995, TotPrice4);
						logShop("received", 995, TotPrice4);
						addShopItem(itemID, amount);
					} else if (c.myShopId == 116) {
						c.getItems().addItem(13307, (int) Math.ceil(TotPrice3 *= 0.30));
						logShop("received", 13307, (int) Math.ceil(TotPrice3 *= 0.30));
					} else if (c.myShopId == 195) {
						if (c.debugMessage) {
							c.sendMessage("points = " + c.getAfkPoints() + " " + TotPrice3 + " " + amount);
						}
						c.setAfkPoints(c.getAfkPoints() + (TotPrice3 * amount));
						logShop("received", 7478, (TotPrice3 * amount));
					} else if (c.myShopId == 199) {
						c.getItems().addItem(33251, (TotPrice2/2));
						logShop("received", 33251, TotPrice4);
					} else if (c.myShopId == 44) {
						if (!ItemDef.forId(itemID).getName().contains("head")) {
							return false;
						} else {
							c.getSlayer().setPoints(c.getSlayer().getPoints() + TotPrice2);
							c.getQuestTab().updateInformationTab();
						}
					}
					//addShopItem(itemID, amount);
					ShopHandler.ShopItemsDelay[c.myShopId][fromSlot] = 0;
					c.getItems().sendInventoryInterface(3823);
					resetShop(c.myShopId);
					updatePlayerShop();
					return false;
				} else {
					if (c.myShopId != 12 && c.myShopId != 29 && c.myShopId != 44 && c.myShopId != 18 && c.myShopId != 83 && c.myShopId != 116 && c.myShopId != 115 && c.myShopId != 195 && c.myShopId != 199) {
						c.getItems().addItem(995, TotPrice2);
						logShop("received", 995, TotPrice2);
					} else if (c.myShopId == 29) {
						c.getItems().addItem(6529, TotPrice2);
						logShop("received", 995, TotPrice2);
					} else if (c.myShopId == 18) {
						c.getItems().addItem(11849, TotPrice2);
						logShop("received", 995, TotPrice2);
					} else if (c.myShopId == 83) {
						c.getItems().addItem(995, TotPrice4);
						logShop("received", 995, TotPrice4);
						addShopItem(itemID, amount);
					} else if (c.myShopId == 116) {
						c.getItems().addItem(13307, (int) Math.ceil(TotPrice3 *= 0.60));
						logShop("received", 13307, (int) Math.ceil(TotPrice3 *= 0.60));
					}  else if (c.myShopId == 195) {
						c.setAfkPoints(c.getAfkPoints() + TotPrice3);
						logShop("received", 7478, TotPrice3);
					} else if (c.myShopId == 199) {
						c.getItems().addItem(33251, ((TotPrice3/2) * amount));
						logShop("received", 33251, ((TotPrice3/2) * amount));
					} else if (c.myShopId == 44) {
						if (!ItemDef.forId(itemID).getName().contains("head")) {
							return false;
						} else {
							c.getSlayer().setPoints(c.getSlayer().getPoints() + TotPrice2);
							c.getQuestTab().updateInformationTab();
						}
					}

					if (!ItemDef.forId(itemID).isNoted()) {
						logShop("sell", itemID, amount);
						c.getItems().deleteItem2(itemID, amount);
					}
					// addShopItem(itemID, 1);
				}
			} else {
				c.sendMessage("You don't have enough space in your inventory.");
				c.getItems().sendInventoryInterface(3823);
				return false;
			}
			//}
			c.getItems().sendInventoryInterface(3823);
			resetShop(c.myShopId);
			updatePlayerShop();
			PlayerSave.saveGame(c);
			return true;
		}
		return true;
	}

	public boolean addShopItem(int itemID, int amount) {
		boolean Added = false;
		if (amount <= 0) {
			return false;
		}

		if (ItemDef.forId(itemID).isNoted()) {
			itemID = c.getItems().getUnnotedItem(itemID);
		}
		for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
			if ((ShopHandler.ShopItems[c.myShopId][i] - 1) == itemID) {
				ShopHandler.ShopItemsN[c.myShopId][i] += amount;
				Added = true;
			}
		}
		if (Added == false) {
			for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
				if (ShopHandler.ShopItems[c.myShopId][i] == 0) {
					ShopHandler.ShopItems[c.myShopId][i] = (itemID + 1);
					ShopHandler.ShopItemsN[c.myShopId][i] = amount;
					ShopHandler.ShopItemsDelay[c.myShopId][i] = 0;
					break;
				}
			}
		}
		return true;
	}

	/**
	 * Buying item(s) from a store
	 * @param itemID
	 * 					itemID that the player is buying
	 * @param fromSlot
	 * 					fromSlot the items is currently located in
	 * @param amount
	 * 					amount of items the player is buying
	 * @return
	 * 					true if the player is allowed to buy the item(s),
	 * 					else false
	 */
	public boolean buyItem(int itemID, int fromSlot, int amount) {
		if (c.myShopId == 0 || !c.isInterfaceOpen(SHOP_INTERFACE_ID) && !c.isInterfaceOpen(SHOP_INTERFACE_ID2))
			return false;
		if (Configuration.DISABLE_SHOP_BUY) {
			c.sendMessage("Buying from shops is disabled atm.");
			return false;
		}
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			if (c.debugMessage)
				c.sendMessage("");
			return false;
		}
		if (!Boundary.isIn(c, Boundary.TOURNAMENT_LOBBIES_AND_AREAS) && c.myShopId == 147) {
			c.sendMessage("You cannot do this right now.");
			return false;
		}
		if (Boundary.isIn(c, Boundary.TOURNAMENT_LOBBIES_AND_AREAS) && c.myShopId != 147) {
			c.sendMessage("You cannot do this right now.");
			return false;
		}
		if (!c.getMode().isItemPurchasable(c.myShopId, itemID)) {
			c.sendMessage("Your game mode does not allow you to buy this item.");
			return false;
		}
		if (c.myShopId == 83) {
			c.sendMessage("You cannot buy items from this shop.");
			return false;
		}
		if (c.myShopId == FireOfExchangeBurnPrice.SHOP_ID)
			return false;

		if (Configuration.DISABLE_FOE && c.myShopId == FireOfExchange.FOE_SHOP_ID) {
			c.sendMessage("Fire of Exchange has been temporarily disabled.");
			return false;
		}
		/*
		 * ACHIEVMENT LOST N FOUND AREA
		 */
		if (c.myShopId == 178) {
			if (itemID == 10941 || itemID == 10933 && c.getAchievements().isComplete(AchievementTier.TIER_1.ordinal(), Achievement.Woodcutting_Task_I.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 10939 && !c.getAchievements().isComplete(AchievementTier.TIER_2.ordinal(), Achievement.INTERMEDIATE_CHOPPER.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 10940 && !c.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.EXPERT_CHOPPER.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 13258 && !c.getAchievements().isComplete(AchievementTier.TIER_1.ordinal(), Achievement.Fishing_Task_I.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 13259 || itemID == 13261 && !c.getAchievements().isComplete(AchievementTier.TIER_2.ordinal(), Achievement.INTERMEDIATE_FISHER.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 13260 && !c.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.EXPERT_FISHER.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 12013 || itemID == 12016 && !c.getAchievements().isComplete(AchievementTier.TIER_1.ordinal(), Achievement.Mining_Task_I.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 12014 && !c.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.EXPERT_MINER.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 12015 && !c.getAchievements().isComplete(AchievementTier.TIER_2.ordinal(), Achievement.INTERMEDIATE_MINER.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 13646 && itemID == 13644 && !c.getAchievements().isComplete(AchievementTier.TIER_1.ordinal(), Achievement.Farming_Task_I.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 13640 && !c.getAchievements().isComplete(AchievementTier.TIER_2.ordinal(), Achievement.INTERMEDIATE_FARMER.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 13642 && !c.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.EXPERT_FARMER.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 20710 && !c.getAchievements().isComplete(AchievementTier.TIER_1.ordinal(), Achievement.Firemaking_Task_I.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 20706 || itemID == 20704  && !c.getAchievements().isComplete(AchievementTier.TIER_2.ordinal(), Achievement.INTERMEDIATE_PYRO.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 20708 || itemID == 20712  && !c.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.EXPERT_PYRO.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 5556 || itemID == 5557  && !c.getAchievements().isComplete(AchievementTier.TIER_1.ordinal(), Achievement.Theiving_Task_I.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 5555 || itemID == 5553  && !c.getAchievements().isComplete(AchievementTier.TIER_2.ordinal(), Achievement.INTERMEDIATE_THIEF.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 5554 && !c.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.EXPERT_THIEF.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		if (c.myShopId == 178) {
			if (itemID == 20164 || itemID == 6666 && !c.getAchievements().isComplete(AchievementTier.TIER_3.ordinal(), Achievement.CLUE_CHAMP.getId())) {
				c.sendMessage("You have not yet unlocked this item.");
				return false;
			}
		}
		/*
		 * ACHIEVMENT LOST N FOUND AREA ENDING
		 */
		if (c.myShopId == 81) {
			if (!c.getDiaryManager().getWildernessDiary().hasDoneHard()) {
				c.sendMessage("You must have completed wilderness hard diaries to purchase this.");
				return false;
			}
		}

		if (c.myShopId == 6) {
			if (c.getMode().isIronmanType()) {
				if (!c.getDiaryManager().getVarrockDiary().hasDoneMedium()) {
					c.sendMessage("You must have completed the varrock diary up to medium to purchase this.");
					return false;
				}
			}
		}

		if (c.myShopId == 115) {
			if (!c.getPosition().inClanWarsSafe()) {
				System.out.println("[District] " + c.getDisplayName() + " Attempting to purchase from free shop outside disitrict.");
				return false;
			}
			if (ItemDef.forId(itemID).isStackable()) {
				amount = 1000;
			}
			if (itemID == 12934) {
				amount = 10000;
			}
		}
		if (c.myShopId == 116) {
			if (!c.getPosition().inClanWarsSafe()) {
				System.out.println("[District] " + c.getDisplayName() + " Attempting to purchase from bm shop outside disitrict.");
				return false;
			}
		}

		if (itemID == LootingBag.LOOTING_BAG) {
			if (c.getItems().getItemCount(LootingBag.LOOTING_BAG, true) > 0
					|| c.getItems().getItemCount(LootingBag.LOOTING_BAG_OPEN, true) > 0) {
				c.sendMessage("It seems like you already have one of these.");
				return false;
			}
		}
		if (itemID == 10941) {
			if (c.getItems().getItemCount(10941, true) > 0) {
				c.sendMessage("It seems like you already have one of these.");
				return false;
			}
		}
		/**
		 * Slayer shop
		 */
		if (c.myShopId == 44) {
			if (ItemDef.forId(itemID).getName().contains("head")) {
				c.sendMessage("This product cannot be purchased.");
				return false;
			}
		}
		/**
		 * Avaas
		 */
		if (c.myShopId == 19) {
			if (itemID == 10498) {
				if (!c.getItems().playerHasItem(886, 75)) {
					c.sendMessage("You must have 75 steel arrows to exchange for this attractor");
					return false;
				}
				c.getItems().deleteItem(886, 775);
				c.getDiaryManager().getLumbridgeDraynorDiary().progress(LumbridgeDraynorDiaryEntry.ATTRACTOR);
			}
		}
		/**
		 * RFD Shop
		 */
		if (c.myShopId == 14) {
			if (itemID == 7458 && c.rfdGloves < 1) {
				c.sendMessage("You are not eligible to buy these.");
				return false;
			}
			if (itemID == 7459 && c.rfdGloves < 2) {
				c.sendMessage("You are not eligible to buy these.");
				return false;
			}
			if (itemID == 7460 && c.rfdGloves < 3) {
				c.sendMessage("You are not eligible to buy these.");
				return false;
			}
			if (itemID == 7461 && c.rfdGloves < 5) {
				c.sendMessage("You are not eligible to buy these.");
				return false;
			}
			if (itemID == 7462) {
				if (c.rfdGloves < 6) {
					c.sendMessage("You are not eligible to buy these.");
					return false;
				}
			}
		}
		if (c.myShopId == 17) {
			skillBuy(itemID);
			return false;
		}
		if (c.myShopId == 179) {
			millBuy(itemID);
			return false;
		}
		if (!shopSellsItem(itemID))
			return false;
		if (amount > 0) {
			if (fromSlot >= ShopHandler.ShopItemsN[c.myShopId].length) {
				c.sendMessage("There was a problem buying that item, please report it to staff!");
				return false;
			}

			if (c.myShopId != 115) {
				if (amount > ShopHandler.ShopItemsN[c.myShopId][fromSlot]) {
					amount = ShopHandler.ShopItemsN[c.myShopId][fromSlot];
				}
			}
			// double ShopValue;
			// double TotPrice;
			int TotPrice2 = 0;
			// int Overstock;
			int Slot = 0;
			int Slot1 = 0;

			switch (c.myShopId) {
				case 9:
				case 10:
				case 12:
				case 13:
				case 15:
				case 18:
				case 40:
				case 44:
				case 75:
				case 77:
				case 78:
				case 79:
				case 80:
				case 82:
				case 112:
				case 116:
				case 117:
				case 118:
				case 119:
				case 120:
				case 121:
				case 123:
				case 131:
				case 171:
				case 172:
				case 173:
				case 189:
				case 191:
				case 192:
				case 193:
				case 195:
				case 190:
				case 196:
				case 197:
				case 198:
				case 600:
				case 199:
					handleOtherShop(itemID, amount);
					return false;
			}
			TotPrice2 = (int) Math.floor(getBuyFromShopPrice(c.myShopId, itemID));
			TotPrice2 = c.getMode().getModifiedShopPrice(c.myShopId, itemID, TotPrice2);
			Slot = c.getItems().getInventoryItemSlot(995);
			Slot1 = c.getItems().getInventoryItemSlot(6529);
			if (TotPrice2 <= 1) {
				TotPrice2 = (int) Math.floor(getItemShopValue(itemID));
				TotPrice2 *= 1.66;
			}
			if (c.myShopId == 115) {
				TotPrice2 = -1;
			}
			if (c.myShopId == 147) {
				TotPrice2 = 0;
			}
			if(c.myShopId==124 && c.amDonated>=150 && itemID == 299){
				TotPrice2=0;
			}

			if (ItemDef.forId(itemID).isStackable()) {
				if (c.myShopId != 29 || c.myShopId != 147) {
					if (TotPrice2 != 0) {
						if (!c.getItems().playerHasItem(995, TotPrice2 * amount) || TotPrice2 == 0) {
							int coinAmount = c.getItems().getItemAmount(995);

							int amountThatCanBeBought = (int) Math.floor(coinAmount / TotPrice2);
							if (TotPrice2 == 0) {
								amountThatCanBeBought = 1000;
							}
							if (amountThatCanBeBought > 0) {
								amount = amountThatCanBeBought;
							}
							c.sendMessage("You don't have enough coins.");
						}
					}
					if (c.debugMessage) {
						c.sendErrorMessage(String.valueOf(TotPrice2 * amount));
					}
					if ((TotPrice2 * amount) < 0) {
						c.sendMessage("You can't buy this amount!");
						return false;
					}

					if (c.getItems().playerHasItem(995, TotPrice2 * amount) || TotPrice2 == 0) {
						if (c.getItems().freeSlots() > 0) {
							c.getItems().deleteItem(995, TotPrice2 * amount);
							c.getItems().addItem(itemID, amount);
							logShop("bought", itemID, amount);
							if (c.myShopId != 115) {
								//ShopHandler.ShopItemsN[c.myShopId][fromSlot] -= amount;
								ShopHandler.ShopItemsDelay[c.myShopId][fromSlot] = 0;
								if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[c.myShopId]) {
									ShopHandler.ShopItems[c.myShopId][fromSlot] = 0;
								}
							}
						} else {
							c.sendMessage("You don't have enough space in your inventory.");
							c.getItems().sendInventoryInterface(3823);
							return false;
						}
					} else {
						c.sendMessage("You don't have enough coins.");
						c.getItems().sendInventoryInterface(3823);
						return false;
					}
				}
			} else if (c.myShopId == 29) {
				if (c.getRechargeItems().hasItem(11136)) {
					TotPrice2 = (int) (TotPrice2 * 0.95);
				}
				if (c.getRechargeItems().hasItem(11138)) {
					TotPrice2 = (int) (TotPrice2 * 0.9);
				}
				if (c.getRechargeItems().hasItem(11140)) {
					TotPrice2 = (int) (TotPrice2 * 0.85);
				}
				if (c.getRechargeItems().hasItem(13103)) {
					TotPrice2 = (int) (TotPrice2 * 0.75);
				}
				if (c.playerItemsN[Slot1] >= TotPrice2 * amount) {
					if (c.getItems().freeSlots() > 0) {
						c.getItems().deleteItem(6529, TotPrice2 * amount);
						c.getItems().addItem(itemID, amount);
						logShop("bought", itemID, amount);
						ShopHandler.ShopItemsN[c.myShopId][fromSlot] -= amount;
						ShopHandler.ShopItemsDelay[c.myShopId][fromSlot] = 0;
						if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[c.myShopId]) {
							ShopHandler.ShopItems[c.myShopId][fromSlot] = 0;
						}
					} else {
						c.sendMessage("You don't have enough space in your inventory.");
						c.getItems().sendInventoryInterface(3823);
						return false;
					}
				} else {
					c.sendMessage("You don't have enough tokkul.");
					c.getItems().sendInventoryInterface(3823);
					return false;
				}
			} else {
				int boughtAmount = 0;


				// Iterate and buy each single item individually
				for (int i = amount; i > 0; i--) {
					if (c.myShopId == 115) {
						TotPrice2 = -1;
					}
					if (c.myShopId == 147) {
						TotPrice2 = -1;
					}
					if (Slot == -1 && c.myShopId != 29 && c.myShopId != 115&& c.myShopId != 147) {
						c.sendMessage("You don't have enough coins.");
						return false;
					}
					if (Slot1 == -1 && (c.myShopId == 29)) {
						c.sendMessage("You don't have enough tokkul.");
						return false;
					}

					// not tokkul
					if (c.myShopId != 29) {
						if (c.getItems().playerHasItem(995, TotPrice2) || TotPrice2 == 0) {
							if (c.getItems().freeSlots() > 0) {
								c.getItems().deleteItem(995, c.getItems().getInventoryItemSlot(995), TotPrice2);
								c.getItems().addItem(itemID, 1);
								boughtAmount++;
								if (c.myShopId == 6) {
									if (c.getMode().isIronmanType()) {
										if (!c.getDiaryManager().getVarrockDiary().hasDoneMedium()) {
											c.sendMessage("You must have completed the varrock diary up to medium to purchase this.");
											return false;
										}
										if (c.getRechargeItems().useItem(2, 1)) {

										} else {
											c.sendMessage("You have already purchased 150 battlestaves today.");
											return false;
										}
									}
								}
								//if (c.myShopId != 115) {
								//ShopHandler.ShopItemsN[c.myShopId][fromSlot] -= 1;
								//}
								ShopHandler.ShopItemsDelay[c.myShopId][fromSlot] = 0;
								if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[c.myShopId]) {
									ShopHandler.ShopItems[c.myShopId][fromSlot] = 0;
								}
							} else {
								c.sendMessage("You don't have enough space in your inventory.");
								c.getItems().sendInventoryInterface(3823);
								return false;
							}
						} else {
							c.sendMessage("You don't have enough coins.");
							c.getItems().sendInventoryInterface(3823);
							return false;
						}
					}

					// tokkul
					if (c.myShopId == 29) {
						if (c.getRechargeItems().hasItem(11136)) {
							TotPrice2 = (int) (TotPrice2 * 0.95);
						}
						if (c.getRechargeItems().hasItem(11138)) {
							TotPrice2 = (int) (TotPrice2 * 0.9);
						}
						if (c.getRechargeItems().hasItem(11140)) {
							TotPrice2 = (int) (TotPrice2 * 0.85);
						}
						if (c.getRechargeItems().hasItem(13103)) {
							TotPrice2 = (int) (TotPrice2 * 0.75);
						}
						if (c.playerItemsN[Slot1] >= TotPrice2) {
							if (c.getItems().freeSlots() > 0) {
								c.getItems().deleteItem(6529, c.getItems().getInventoryItemSlot(6529), TotPrice2);
								c.getItems().addItem(itemID, 1);
								ShopHandler.ShopItemsN[c.myShopId][fromSlot] -= 1;
								ShopHandler.ShopItemsDelay[c.myShopId][fromSlot] = 0;
								if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[c.myShopId]) {
									ShopHandler.ShopItems[c.myShopId][fromSlot] = 0;
								}
							} else {
								c.sendMessage("You don't have enough space in your inventory.");
								c.getItems().sendInventoryInterface(3823);
								return false;
							}
						} else {
							c.sendMessage("You don't have enough tokkul.");
							c.getItems().sendInventoryInterface(3823);
							return false;
						}
					}
				}



				if (boughtAmount > 0) {
					logShop("bought", itemID, boughtAmount);
				}
			}
			c.getItems().sendInventoryInterface(3823);
			resetShop(c.myShopId);
			updatePlayerShop();
			return true;
		}
		return false;
	}

	public void logShop(String action, int itemId, int amount) {
		try {
			switch (action) {
				case "bought":
					Server.getLogging().write(new ShopBuyLog(c, c.myShopId, ShopHandler.ShopName[c.myShopId], new GameItem(itemId, amount)));
					break;
				case "sell":
					Server.getLogging().write(new ShopSellLog(c, c.myShopId, ShopHandler.ShopName[c.myShopId], new GameItem(itemId, amount)));
					break;
				case "received": // Ignore this one for now
					break;
				default:
					throw new IllegalArgumentException("Action not supported " + action);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Special currency stores
	 * @param itemID
	 * 					itemID that is being bought
	 * @param amount
	 * 					amount that is being bought
	 */
	public void handleOtherShop(int itemID, int amount) {
		if (amount <= 0) {
			if (c.myShopId == 172 || c.myShopId == 173) {
				c.sendMessage("This item cannot be bought, its on showcase only.");
				return;
			}
			c.sendMessage("You need to buy at least one or more of this item.");
			return;
		}
		if (!c.getItems().isStackable(itemID)) {
			if (amount > c.getItems().freeSlots()) {
				amount = c.getItems().freeSlots();
			}
		}
		if (c.myShopId == 40) {
			if (c.getItems().freeSlots() < 1) {
				c.sendMessage("You need at least one free slot to buy this.");
				return;
			}
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (c.getArenaPoints() < itemValue) {
				c.sendMessage("You do not have enough arena points to buy this from the shop.");
				return;
			}
			c.setArenaPoints(c.getArenaPoints() - itemValue);
			c.getQuestTab().updateInformationTab();
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 197) {
			if (c.getItems().freeSlots() < 1) {
				c.sendMessage("You need at least one free slot to buy this.");
				return;
			}
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (c.getBloody_points() < itemValue) {
				c.sendMessage("You do not have enough Bloody points to buy this from the shop.");
				return;
			}
			c.setBloody_points(c.getBloody_points() - itemValue);
			c.getQuestTab().updateInformationTab();
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 191) {
			if (c.getItems().freeSlots() < 1) {
				c.sendMessage("You need at least one free slot to buy this.");
				return;
			}
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (c.getItems().getInventoryCount(25527) < itemValue) {
				c.sendMessage("You do not have enough Star Dust to buy this from the shop.");
				return;
			}
			c.getItems().deleteItem2(25527, itemValue);
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 198) {
			if (c.getItems().freeSlots() < 1) {
				c.sendMessage("You need at least one free slot to buy this.");
				return;
			}
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (c.getItems().getInventoryCount(22820) < itemValue) {
				c.sendMessage("You do not have enough Molch pearls to buy this from the shop.");
				return;
			}
			c.getItems().deleteItem2(22820, itemValue);
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 193) {
			if (c.getItems().freeSlots() < 1) {
				c.sendMessage("You need at least one free slot to buy this.");
				return;
			}
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (c.arboPoints < itemValue) {
				c.sendMessage("You do not have enough Arbograve Swamp Points to buy this from the shop.");
				return;
			}
			c.arboPoints -= itemValue;
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 195) {
			if (c.getItems().freeSlots() < 1) {
				c.sendMessage("You need at least one free slot to buy this.");
				return;
			}
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (c.getAfkPoints() < itemValue || itemValue < 0) {
				c.sendMessage("You do not have enough AFK points to buy this from the shop.");
				return;
			}
			c.setAfkPoints(c.getAfkPoints() - itemValue);
			c.getQuestTab().updateInformationTab();
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 196) {
			if (c.getItems().freeSlots() < 1) {
				c.sendMessage("You need at least one free slot to buy this.");
				return;
			}
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (c.getSeasonalPoints() < itemValue) {
				c.sendMessage("You do not have enough Seasonal points to buy this from the shop.");
				return;
			}
			c.setSeasonalPoints(c.getSeasonalPoints() - itemValue);
			c.getQuestTab().updateInformationTab();
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 199) {
			if (c.getItems().freeSlots() < 1) {
				c.sendMessage("You need at least one free slot to buy this.");
				return;
			}
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (c.getItems().getInventoryCount(33251) < itemValue) {
				c.sendMessage("You do not have enough Donation Coins to buy this from the shop.");
				return;
			}
			c.getItems().deleteItem2(33251, itemValue);
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 82) {
			if (c.getItems().freeSlots() < 1) {
				c.sendMessage("You need at least one free slot to buy this.");
				return;
			}
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (c.getShayPoints() < itemValue) {
				c.sendMessage("You do not have enough assault points to buy this from the shop.");
				return;
			}
			c.setShayPoints(c.getShayPoints() - itemValue);
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 189) {
			if (c.getItems().freeSlots() < 1) {
				c.sendMessage("You need at least one free slot to buy this.");
				return;
			}
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (c.WGPoints < itemValue) {
				c.sendMessage("You do not have enough WeaponGame points to buy this from the shop.");
				return;
			}
			c.WGPoints -= itemValue;
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 118) {
			if (c.getItems().freeSlots() < 1) {
				c.sendMessage("You need at least one free slot to buy this.");
				return;
			}
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (c.getDiscordPoints() < itemValue || itemValue < 0) {
				c.sendMessage("You do not have enough Discord points to buy this from the shop.");
				return;
			}
			c.setDiscordPoints(c.getDiscordPoints() - itemValue);
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 120) {
			if (c.getItems().freeSlots() < 1) {
				c.sendMessage("You need at least one free slot to buy this.");
				return;
			}
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (c.getPrestigePoints() < itemValue) {
				c.sendMessage("You do not have enough prestige points to buy this from the shop.");
				return;
			}
			c.prestigePoints =(c.getPrestigePoints() - itemValue);
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 13) {
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (!c.getItems().playerHasItem(HftdQuest.CASKET_TO_BUY_BOOK, itemValue)) {
				c.sendMessage("You do not have enough rusty caskets to switch with Jossik.");
				return;
			}
			c.getItems().deleteItem(HftdQuest.CASKET_TO_BUY_BOOK, itemValue);
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}

		if (c.myShopId == 117 && itemID == 12695) {
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (!c.getItems().playerHasItem(13307, itemValue)) {
				c.sendMessage("You do not have enough blood money to purchase this.");
				return;
			}
			c.getItems().deleteItem(13307, itemValue);
			c.getItems().addItem(itemID + 1, amount * 2);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 116/* || c.myShopId == 117*/) {
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (!c.getItems().playerHasItem(13307, itemValue)) {
				c.sendMessage("You do not have enough blood money to purchase this.");
				return;
			}
			c.getItems().deleteItem(13307, itemValue);
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}

		if (c.myShopId == 600) {
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (!c.getItems().playerHasItem(11681, itemValue)) {
				c.sendMessage("You do not have enough Scrap Paper to purchase this.");
				return;
			}
			c.getItems().deleteItem(11681, itemValue);
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 123) {
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (!c.getItems().playerHasItem(13204, itemValue)) { // Check if player has at least 1 item with ID 13204
				c.sendMessage("You do not have enough Platinum tokens to purchase this.");
				return;
			}
			c.getItems().deleteItem(13204, itemValue); // Consume 1 Platinum token for the purchase
//			c.getItems().deleteItem(itemID, itemValue); // Deduct the required value of items
			c.getItems().addItem(itemID, amount); // Add purchased items
			c.getItems().sendInventoryInterface(3823); // Update inventory interface
			logShop("bought", itemID, amount); // Log the purchase
			return;
		}
		if (c.myShopId == 18) {
			int itemValue = getSpecialItemValue(itemID) * amount;
			if (!c.getItems().playerHasItem(11849, itemValue)) {
				c.sendMessage("You do not have enough marks of grace to purchase this.");
				return;
			}
			c.getItems().deleteItem(11849, itemValue);
			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 79) {
			if (c.getItems().freeSlots() < 1) {
				c.sendMessage("You need one free inventory slot to buy this.");
				return;
			}
			if (amount > 100) {
				amount = 100;
			}

			c.getItems().addItem(itemID, amount);
			c.getItems().sendInventoryInterface(3823);
			logShop("bought", itemID, amount);
			return;
		}
		if (c.myShopId == 44) {
			if (itemID == 13226) {
				if (c.getItems().getItemCount(13226, true) == 1) {
					c.sendMessage("You already have a herb sack, theres no need for another.");
					return;
				}
			}
			if (itemID == 12020) {
				if (c.getItems().getItemCount(12020, true) == 1) {
					c.sendMessage("You already have a gem bag. theres no need for another.");
					return;
				}
			}
			if (c.getSlayer().getPoints() >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.getSlayer().setPoints(c.getSlayer().getPoints() - (getSpecialItemValue(itemID) * amount));
					c.getItems().addItem(itemID, amount);
					c.getItems().sendInventoryInterface(3823);
					c.getQuestTab().updateInformationTab();
					logShop("bought", itemID, amount);
				}
			} else {
				c.sendMessage("You do not have enough slayer points to buy this item.");
			}
		} else if (c.myShopId == 9 || c.myShopId == 112) {
			if (c.donatorPoints >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.donatorPoints -= getSpecialItemValue(itemID) * amount;
					c.getQuestTab().updateInformationTab();
					c.getItems().addItem(itemID, amount);
					c.getItems().sendInventoryInterface(3823);
					logShop("bought", itemID, amount);
				}
			} else {
				c.sendMessage("You do not have enough donator points to buy this item.");
			}
		} else if (c.myShopId == 10) {
			if (c.getSlayer().getPoints() >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.getSlayer().setPoints(c.getSlayer().getPoints() - (getSpecialItemValue(itemID) * amount));
					c.getItems().addItem(itemID, amount);
					c.getItems().sendInventoryInterface(3823);
					c.getQuestTab().updateInformationTab();
					logShop("bought", itemID, amount);
				}
			} else {
				c.sendMessage("You do not have enough slayer points to buy this item.");
			}
		} else if (c.myShopId == 78) {
			if (c.getAchievements().points >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.getAchievements().points -= getSpecialItemValue(itemID) * amount;
					c.getItems().addItem(itemID, amount);
					c.getItems().sendInventoryInterface(3823);
					logShop("bought", itemID, amount);
				}
			} else {
				c.sendMessage("You do not have enough achievement points to buy this item.");
			}
		} else if (c.myShopId == 75) {
			if (c.pcPoints >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.pcPoints -= getSpecialItemValue(itemID) * amount;
					c.getQuestTab().updateInformationTab();
					c.getItems().addItem(itemID, amount);
					c.getItems().sendInventoryInterface(3823);
					logShop("bought", itemID, amount);
				}
			} else {
				c.sendMessage("You do not have enough PC Points to buy this item.");
			}
		} else if (c.myShopId == 77) {
			if (c.votePoints >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.votePoints -= getSpecialItemValue(itemID) * amount;
					c.getQuestTab().updateInformationTab();
					c.getItems().addItem(itemID, amount);
					c.getItems().sendInventoryInterface(3823);
					logShop("bought", itemID, amount);
				}
			} else {
				c.sendMessage("You do not have enough vote points to buy this item.");
			}
		} else if (c.myShopId == 121) {
			if (c.bossPoints >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.bossPoints -= getSpecialItemValue(itemID) * amount;
					c.getQuestTab().updateInformationTab();
					c.getItems().addItem(itemID, amount);
					c.getItems().sendInventoryInterface(3823);
					logShop("bought", itemID, amount);
				}
			} else {
				c.sendMessage("You do not have enough Boss Points to buy this item.");
			}
		} else if (c.myShopId == 80) {
			if (c.pkp >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.pkp -= getSpecialItemValue(itemID) * amount;
					c.getQuestTab().updateInformationTab();
					c.getItems().addItem(itemID, amount);
					c.getItems().sendInventoryInterface(3823);
					c.getShops().openShop(80);
					logShop("bought", itemID, amount);
				}
			} else {
				c.sendMessage("You do not have enough PKP Points to buy this item.");
			}
		} else if (c.myShopId == 171) {
			if (c.foundryPoints >= (long) getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.foundryPoints -= (long) getSpecialItemValue(itemID) * amount;
					c.getQuestTab().updateInformationTab();
					c.getItems().addItem(itemID, amount);
					c.getItems().sendInventoryInterface(3823);
					logShop("bought", itemID, amount);
				}
			} else {
				c.sendMessage("You do not have enough Exchange Points to buy this item.");
			}
		} else if (c.myShopId == 172 || c.myShopId == 173) {
			if (c.showcase >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.showcase -= getSpecialItemValue(itemID) * amount;
					c.getQuestTab().updateInformationTab();
					c.getItems().addItem(itemID, amount);
					c.getItems().sendInventoryInterface(3823);
					logShop("bought", itemID, amount);
				}
			} else {
				c.sendMessage("This item is only a showcase.");
			}
		} else if (c.myShopId == 131) {
			if (c.tournamentPoints >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.tournamentPoints -= getSpecialItemValue(itemID) * amount;
					c.getQuestTab().updateInformationTab();
					c.getItems().addItem(itemID, amount);
					if (itemID == 8132 && c.getItems().getItemCount(8132, false) == 0) {
						c.getCollectionLog().handleDrop(c, 5, 8132, 1);
					}
					if (itemID == 10591 && c.getItems().getItemCount(10591, false) == 0) {
						c.getCollectionLog().handleDrop(c, 5, 10591, 1);
					}
					c.getItems().sendInventoryInterface(3823);
					logShop("bought", itemID, amount);
				}
			} else {
				c.sendMessage("You do not have enough Tournament Points to buy this item.");
			}
		} else if (c.myShopId == 119) {
			if (c.getItems().getInventoryCount(13307) >= getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.getItems().deleteItem2(13307, getSpecialItemValue(itemID) * amount);
					c.getItems().addItem(itemID, amount);
					c.getItems().sendInventoryInterface(3823);
					logShop("bought", itemID, amount);
				}
			} else {
				c.sendMessage("You do not have enough blood money points to buy this item.");
			}
		} else if (c.myShopId == 192) {
			if (c.instanceCurrency >= (long) getSpecialItemValue(itemID) * amount) {
				if (c.getItems().freeSlots() > 0) {
					c.instanceCurrency -= ((long) getSpecialItemValue(itemID) * amount);
					c.getItems().addItem(itemID, amount);
					c.getItems().sendInventoryInterface(3823);
					c.getCollectionLog().handleDrop(c, 10, itemID, amount);
					logShop("bought", itemID, amount);
				}
			} else {
				c.sendMessage("You do not have enough AOE Instance points to buy this item.");
			}
		}
	}

	public void openSkillCape() {
		int capes = get99Count();
		if (capes > 1)
			capes = 1;
		else
			capes = 0;
		c.myShopId = 17;
		setupSkillCapes(get99Count());
	}
	public void openMillCape() {
		int capes = 23;
		if (capes > 1)
			capes = 1;
		else
			capes = 0;
		c.myShopId = 179;
		c.getShops().openShop(179);
		//setupMillCapes(capes, 23);
	}
	public void setupSkillCapes(int capes2) {
		c.getPA().sendInterfaceHidden(1, 28050);
		c.getPA().sendInterfaceHidden(1, 28053);
		c.getItems().sendInventoryInterface(3823);
		c.isShopping = true;
		c.myShopId = 17;
		c.getPA().sendFrame248(SHOP_INTERFACE_ID2, 3822);
		c.getPA().sendFrame126("Skillcape Shop", 3901);

		int TotalItems = 0;
		TotalItems = capes2;
		if (TotalItems > ShopHandler.MaxShopItems) {
			TotalItems = ShopHandler.MaxShopItems;
		}

		if (c.getOutStream() != null) {
			c.getOutStream().createFrameVarSizeWord(53);
			c.getOutStream().writeUnsignedWord(3900);
			c.getOutStream().writeUnsignedWord(TotalItems);
			for (int i = 0; i < 22; i++) {
				if (c.getLevelForXP(c.playerXP[i]) < 99)
					continue;
				c.getOutStream().writeByte(1);
				c.getOutStream().writeWordBigEndianA(skillCapes[i] + 2);
			}
			c.getOutStream().endFrameVarSizeWord();
			c.flushOutStream();
		}
	}

	public void millBuy(int item) {
		int millcapeskill = (item - 33033);
		if (c.getItems().freeSlots() > 1) {
			if (c.getItems().playerHasItem(995, 10000000)) {
				if (c.playerXP[millcapeskill] >= 200000000) {
					c.getItems().deleteItem(995, c.getItems().getInventoryItemSlot(995), 10000000);

					c.getItems().addItem(item, 1);


				} else {
					c.sendMessage("You must have 200m XP in the skill of the cape you're trying to buy.");
				}
			} else {
				c.sendMessage("You need 10m to buy this item.");
			}
		} else {
			c.sendMessage("You must have at least 1 inventory spaces to buy this item.");
		}


	}


	public int[] skillCapes = { 9747, 9753, 9750, 9768, 9756, 9759, 9762, 9801, 9807, 9783, 9798, 9804, 9780, 9795, 9792, 9774, 9771, 9777, 9786, 9810, 9765, 9948 };

	public int get99Count() {
		int count = 0;
		for (int j = 0; j < 22; j++) {
			if (c.getLevelForXP(c.playerXP[j]) >= 99) {
				count++;
			}
		}
		return count;
	}


	public void skillBuy(int item) {
		int nn = get99Count();
		if (nn > 1)
			nn = 1;
		else
			nn = 0;
		for (int j = 0; j < skillCapes.length; j++) {
			if (skillCapes[j] == item || skillCapes[j] + 1 == item) {
				if (c.getItems().freeSlots() > 1) {
					if (c.getItems().playerHasItem(995, 99000)) {
						if (c.getLevelForXP(c.playerXP[j]) >= 99) {
							c.getItems().deleteItem(995, c.getItems().getInventoryItemSlot(995), 99000);
							c.getItems().addItem(skillCapes[j] + nn, 1);
							c.getItems().addItem(skillCapes[j] + 2, 1);
						} else {
							c.sendMessage("You must have 99 in the skill of the cape you're trying to buy.");
						}
					} else {
						c.sendMessage("You need 99k to buy this item.");
					}
				} else {
					c.sendMessage("You must have at least 1 inventory spaces to buy this item.");
				}
			}
		}
		c.getItems().sendInventoryInterface(3823);
	}

}
