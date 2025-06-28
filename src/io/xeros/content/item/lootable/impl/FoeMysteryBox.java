package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.MysteryBoxLootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import QuickUltra.Rarity;

/**
 * Revamped a simple means of receiving a random item based on chance.
 *
 * @author Junior
 */
public class FoeMysteryBox extends MysteryBoxLootable {

	/**
	 * A map containing a List of {@link GameItem}'s that contain items relevant to their rarity.
	 */
	private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

	static {
		items.put(LootRarity.COMMON,
				Arrays.asList(
						new GameItem(30010),  // Postie pete
						new GameItem(30010),  // Postie pete

						new GameItem(30011),  // Imp
						new GameItem(30011),  // Imp

						new GameItem(30012),  // Toucan
						new GameItem(30012),  // Toucan

						new GameItem(30013),  // Penguin king
						new GameItem(30013),  // Penguin king

						new GameItem(30015),  // Shadow warrior
						new GameItem(30015),  // Shadow warrior

						new GameItem(30016),  // Shadow archer
						new GameItem(30016),  // Shadow archer

						new GameItem(30017),  // Shadow wizard
						new GameItem(30017),  // Shadow wizard

						new GameItem(30023),  // Raincloud
						new GameItem(30023),  // Raincloud

						new GameItem(7582),   // Hellcat
						new GameItem(7582),   // Hellcat

						new GameItem(10591),  // Terror dog
						new GameItem(10591)   // Terror dog
				));

		items.put(LootRarity.UNCOMMON,
				Arrays.asList(
						new GameItem(30018),  // Healer death spawn
						new GameItem(30019),  // Holy death spawn
						new GameItem(13323),  // Baby chinchompa
						new GameItem(13324),  // Baby chinchompa
						new GameItem(13325),  // Baby chinchompa
						new GameItem(20659)   // Giant squirrel
				));

		items.put(LootRarity.RARE,
				Arrays.asList(
						new GameItem(30020),  // Corrupt beast
						new GameItem(30020),  // Corrupt beast
						new GameItem(30020),  // Corrupt beast

						new GameItem(23939),  // Seren
						new GameItem(23939),  // Seren
						new GameItem(23939),  // Seren

						new GameItem(8132),   // Guard dog

						new GameItem(13326),  // Golden chinchompa

						new GameItem(25519),  // Jalrek jad

						new GameItem(19730)   // Bloodhound pet
				));

		items.put(LootRarity.VERY_RARE,
				Arrays.asList(
						new GameItem(30022),  // Kratos

						new GameItem(30021),  // Roc

						new GameItem(33065),  // Beaver

						new GameItem(30014),  // K'klik
						new GameItem(30014),  // K'klik
						new GameItem(30014),  // K'klik
						new GameItem(30014)   // K'klik
				));
	}

	/**
	 * Constructs a new myster box to handle item receiving for this player and this player alone
	 *
	 * @param player the player
	 */
	public FoeMysteryBox(Player player) {
		super(player);
	}

	@Override
	public int getItemId() {
		return 8167;
	}

	@Override
	public Map<LootRarity, List<GameItem>> getLoot() {
		return items;
	}
}