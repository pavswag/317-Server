package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Revamped a simple means of receiving a random item based on chance.
 *
 * @author Jason MacKeigan
 * @date Oct 29, 2014, 1:43:44 PM
 */
public class VoteMysteryBox implements Lootable {

	/**
	 * The item id of the mystery box required to trigger the event
	 */
	public static final int VOTE_MYSTERY_BOX = 11739;

	/**
	 * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
	 */
	private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

	/**
	 * Stores an array of items into each map with the corresponding rarity to the list
	 */
	static {
		items.put(LootRarity.COMMON,
				Arrays.asList(
						new GameItem(33237, 64598),  //1 nomad
						new GameItem(691, 5),     //10k nomad
						new GameItem(692, 5),     //25k nomad
						new GameItem(696, 1),     //50k nomad
						new GameItem(6679, 25),   //mini mystery
						new GameItem(6677, 25),   //mini mystery
						new GameItem(22374),  //hespori key
						new GameItem(12873),  //guthns armour
						new GameItem(12875),  //veracs armour
						new GameItem(12877),  //dharoks armour
						new GameItem(12879),  //torags armour
						new GameItem(12881),  //ahrims armour

						new GameItem(12883),   //karils armour
						new GameItem(989, 10),     //crystal key
						new GameItem(23083, 5)    //Brimstone key
				));

		items.put(LootRarity.UNCOMMON,
				Arrays.asList(
						new GameItem(696, 1),  //250k nomad
						new GameItem(6677, 25),  //mini smb
						new GameItem(6679, 50),  //mini smb
						new GameItem(696)   //250k nomads
				));
		items.put(LootRarity.RARE,
				Arrays.asList(
						new GameItem(6678, 10),  //mini umb
						new GameItem(696, 3),    //250k nomad
						new GameItem(6889),   //mages book
						new GameItem(26486),  //rune c'bow (or)
						new GameItem(4185, 2),   //porazdir key
						new GameItem(6792, 2),   //serens key
						new GameItem(11666),  //elite void token
						new GameItem(12004),  //kraken tenticle
						new GameItem(9185)    //rune c'bow
				));

		items.put(LootRarity.VERY_RARE,
				Arrays.asList(
						new GameItem(8167),   //nomad chest
						new GameItem(22324),  //ghrazi rapier
						new GameItem(12904),  //toxic staff
						new GameItem(11907),  //trident
						new GameItem(26482),  //whip (or)
						new GameItem(696, 5)   //10m nomad
				));
	}


	@Override
	public Map<LootRarity, List<GameItem>> getLoot() {
		return items;
	}

	/**
	 * Opens a mystery box if possible, and ultimately triggers and event, if possible.
	 */
	public void roll(Player player) {
		if (System.currentTimeMillis() - player.lastMysteryBox < 600) {
			return;
		}
		if (player.getItems().freeSlots() < 2) {
			player.sendMessage("You need at least two free slots to open a hourly box.");
			return;
		}
		if (!player.getItems().playerHasItem(VOTE_MYSTERY_BOX)) {
			player.sendMessage("You need a hourly box to do this.");
			return;
		}
		player.getItems().deleteItem(VOTE_MYSTERY_BOX, 1);
		player.lastMysteryBox = System.currentTimeMillis();
		int random = Misc.random(100);
		List<GameItem> itemList = random < 55 ? items.get(LootRarity.COMMON) : random >= 55 && random <= 80 ? items.get(LootRarity.UNCOMMON) : items.get(LootRarity.RARE);
		GameItem item = Misc.getRandomItem(itemList);
		GameItem itemDouble = Misc.getRandomItem(itemList);

		if (Misc.random(10) == 0) {
			player.getItems().addItem(item.getId(), item.getAmount());
			player.getItems().addItem(itemDouble.getId(), itemDouble.getAmount());
			player.sendMessage("You receive <col=255>" + item.getAmount() + " x " + ItemAssistant.getItemName(item.getId()) + "</col>.");
			player.sendMessage("You receive <col=255>" + itemDouble.getAmount() + " x " + ItemAssistant.getItemName(itemDouble.getId()) + "</col>.");
		} else {
			player.getItems().addItem(item.getId(), item.getAmount());
			player.sendMessage("You receive <col=255>" + item.getAmount() + " x " + ItemAssistant.getItemName(item.getId()) + "</col>.");
		}
	}

}