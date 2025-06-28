package io.xeros.content.item.lootable.impl;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.event.eventcalendar.EventChallenge;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.content.prestige.PrestigePerks;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrystalChest implements Lootable {

	private static final int KEY = 989;
	private static final int DRAGONSTONE = 1631;
	private static final int KEY_HALVE1 = 985;
	private static final int KEY_HALVE2 = 987;
	private static final int ANIMATION = 881;

	private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

	static {
		items.put(LootRarity.COMMON, Arrays.asList(

				new GameItem(995, 250_000),  //coins
				new GameItem(995, 250_000),  //coins
				new GameItem(995, 250_000),  //coins

				new GameItem(2435, 10),  //prayer pots
				new GameItem(2435, 10),  //prayer pots
				new GameItem(2435, 10),  //prayer pots

				new GameItem(1712, 1),  //glory (4)
				new GameItem(1712, 1),  //glory (4)
				new GameItem(1712, 1),  //glory (4)

				new GameItem(2677, 1),  //clue easy
				new GameItem(2677, 1),  //clue easy
				new GameItem(2677, 1),  //clue easy

				new GameItem(454, 25),  //coal
				new GameItem(454, 25),  //coal
				new GameItem(454, 25),  //coal

				new GameItem(1516, 25),  //yew log
				new GameItem(1516, 25),  //yew log
				new GameItem(1516, 25),  //yew log

				new GameItem(208, 10),  //grimy ranarr
				new GameItem(208, 10),  //grimy ranarr
				new GameItem(208, 10),  //grimy ranarr

				new GameItem(565, 500),  //bloods
				new GameItem(565, 500),  //bloods
				new GameItem(565, 500),  //bloods

				new GameItem(560, 500),  //deaths
				new GameItem(560, 500),  //deaths
				new GameItem(560, 500),  //deaths

				new GameItem(1632, 10),  //uncut d stone
				new GameItem(1632, 10),  //uncut d stone
				new GameItem(1632, 10),  //uncut d stone

				new GameItem(537, 10),  //d bones
				new GameItem(537, 10),  //d bones
				new GameItem(537, 10),  //d bones

				new GameItem(384, 25),  //sharks
				new GameItem(384, 25),  //sharks
				new GameItem(384, 25),  //sharks

				new GameItem(4131, 1),  //Rune boots
				new GameItem(4131, 1),  //rune boots
				new GameItem(4131, 1),  //Rune boots

				new GameItem(21547, 1),  //small foe bone
				new GameItem(21547, 1),  //small foe bone
				new GameItem(21547, 1),  //small foe bone
				new GameItem(21547, 1),  //small foe bone
				new GameItem(21547, 1),  //small foe bone
				new GameItem(21547, 1),  //small foe bone
				new GameItem(21547, 1),  //small foe bone
				new GameItem(21547, 1),  //small foe bone

				new GameItem(21549, 1),  //medium foe bone
				new GameItem(21549, 1),  //medium foe bone
				new GameItem(21549, 1),  //medium foe bone

				new GameItem(21551, 1),  //large foe bone
				new GameItem(21551, 1),  //large foe bone

				new GameItem(21553, 1)));  //rare foe bone


		items.put(LootRarity.RARE, Arrays.asList(
				new GameItem(386, 50),  //shark
				new GameItem(386, 50),  //shark
				new GameItem(386, 50),  //shark
				new GameItem(386, 50),  //shark
				new GameItem(386, 50),  //shark
				new GameItem(386, 50),  //shark

				new GameItem(990, 5),  //crystal key
				new GameItem(990, 5),  //crystal key
				new GameItem(990, 5),  //crystal key
				new GameItem(990, 5),  //crystal key
				new GameItem(990, 5),  //crystal key
				new GameItem(990, 5),  //crystal key

				new GameItem(995, 500_000),  //coins
				new GameItem(995, 500_000),  //coins
				new GameItem(995, 500_000),  //coins
				new GameItem(995, 500_000),  //coins
				new GameItem(995, 500_000),  //coins
				new GameItem(995, 500_000),  //coins

				new GameItem(1377, 1),  //dbaxe
				new GameItem(1377, 1),  //dbaxe
				new GameItem(1377, 1),  //dbaxe
				new GameItem(1377, 1),  //dbaxe
				new GameItem(1377, 1),  //dbaxe
				new GameItem(1377, 1),  //dbaxe

				new GameItem(2368, 1),  //right shield half
				new GameItem(2368, 1),  //right shield half
				new GameItem(2368, 1),  //right shield half
				new GameItem(2368, 1),  //right shield half
				new GameItem(2368, 1),  //right shield half
				new GameItem(2368, 1),  //right shield half

				new GameItem(2801, 1),  //clue medium
				new GameItem(2801, 1),  //clue medium
				new GameItem(2801, 1),  //clue medium
				new GameItem(2801, 1),  //clue medium
				new GameItem(2801, 1),  //clue medium
				new GameItem(2801, 1),  //clue medium

				new GameItem(3145, 50),  //karambwan
				new GameItem(3145, 50),  //karambwan
				new GameItem(3145, 50),  //karambwan
				new GameItem(3145, 50),  //karambwan
				new GameItem(3145, 50),  //karambwan
				new GameItem(3145, 50),  //karambwan

				new GameItem(6688, 25),  //sara brews
				new GameItem(6688, 25),  //sara brews
				new GameItem(6688, 25),  //sara brews
				new GameItem(6688, 25),  //sara brews
				new GameItem(6688, 25),  //sara brews
				new GameItem(6688, 25),  //sara brews

				new GameItem(24034, 1),  //dragonstone armour
				new GameItem(24037, 1),  //dragonstone armour
				new GameItem(24040, 1),  //dragonstone armour
				new GameItem(24043, 1),  //dragonstone armour
				new GameItem(24046, 1),  //dragonstone armour

				new GameItem(11840, 1),  //d boots
				new GameItem(11840, 1),  //d boots

				new GameItem(21547, 1),  //small foe bone

				new GameItem(21549, 1),  //medium foe bone

				new GameItem(21551, 1),  //large foe bone

				new GameItem(21553, 1),  //rare foe bone

				new GameItem(6679, 25),  //Mini mbox

				new GameItem(6677,10)  //Mini smb
		));
	}

	private static GameItem randomChestRewards(Player c) {
		int random = Misc.random(100);
		int rareChance = 90;
		if (c.getItems().playerHasItem(21046)) {
			rareChance = 85;
			c.getItems().deleteItem(21046, 1);
			c.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate." );
			c.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
		}
		List<GameItem> itemList = random < rareChance ? items.get(LootRarity.COMMON) : items.get(LootRarity.RARE);
		return Misc.getRandomItem(itemList);
	}

	public static void makeKey(Player c) {
		if (c.getItems().playerHasItem(KEY_HALVE1, 1) && c.getItems().playerHasItem(KEY_HALVE2, 1)) {
			c.getItems().deleteItem(KEY_HALVE1, 1);
			c.getItems().deleteItem(KEY_HALVE2, 1);
			c.getItems().addItem(KEY, 1);
		}
	}

	@Override
	public Map<LootRarity, List<GameItem>> getLoot() {
		return items;
	}

	@Override
	public void roll(Player c) {
		if (c.getItems().playerHasItem(KEY)) {
			c.getItems().deleteItem(KEY, 1);
			c.startAnimation(ANIMATION);
			c.getItems().addItemToBankOrDrop(DRAGONSTONE, 1);
			GameItem reward = randomChestRewards(c);
			c.getItems().addItem(reward.getId(), (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? reward.getAmount() * 2 : reward.getAmount()));
			Achievements.increase(c, AchievementType.LOOT_CRYSTAL_CHEST, 1);
		} else {
			c.sendMessage("@blu@The chest is locked, it won't budge!");
		}
	}

}