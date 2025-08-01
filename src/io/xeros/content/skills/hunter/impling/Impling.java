package io.xeros.content.skills.hunter.impling;

import io.xeros.content.skills.Skill;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Impling {

	public static boolean isImp(int id) {
		List<Integer> imps = Arrays.asList(1635, 1636, 1637, 1638, 1639, 1640, 1641, 1642, 1643, 1654, 7302);
		return imps.stream().anyMatch(i -> i.intValue() == id);
	}

	/**
	 * Handles the removal of the caught impling
	 */
	public static void kill(NPC npc) {
		if (npc == null)
			return;
		ImplingData data = ImplingData.forId(npc.getNpcId());
		if(npc != null && npc.getNpcId() == data.npcId || data != null) {
			npc.setDead(true);
			npc.setUpdateRequired(true);
		}
	}

	public static void catchImpling(Player player, NPC npc) {

		if (player == null || npc == null)
			return;

		ImplingData data = ImplingData.forId(npc.getNpcId());

		if (player.lastImpling > System.currentTimeMillis())
			return;

		if (npc == null)
			return;

		if (player.playerLevel[21] < data.requirement) {
			player.sendMessage("You need a hunter level of at least " + data.requirement + " to catch this impling.");
			return;
		}
		boolean bareHanded = player.playerLevel[21] > data.requirement + 10;
		if (!(player.amDonated > 0) && !bareHanded && !player.getItems().playerHasItem(10010) && !player.getItems().isWearingItem(10010)) {
			player.sendMessage("You need to be a donator to use your bare-hands or 10 levels above the requirement.");
			return;
		}
		if (bareHanded) {
			if (!player.getItems().playerHasItem(10010) && !player.getItems().isWearingItem(10010)) {
				if (!(player.amDonated > 0)) {
					player.sendMessage("You need to be a donator to use your bare-hands.");
					return;
				}
			}
		}
		if (!player.getItems().playerHasItem(11260)) {
			player.sendMessage("You must have an empty impling jar to catch this impling with.");
			return;
		}
		if (npc.isDead())
			return;

		player.lastImpling = System.currentTimeMillis() + 4_000;
		player.startAnimation(6605);
		boolean fail = Misc.random(10) >= ((player.playerLevel[Skill.HUNTER.getId()] - 10) / 10) + 1;
		if (!fail) {
			kill(npc);
			player.getItems().deleteItem(11260, 1);
			player.getItems().addItem(data.jar, 1);
			player.sendMessage("You successfully caught the " + data.name + ".");
			player.getPA().addSkillXPMultiplied(data.experience * 30, 21, true);
		} else {
			npc.startAnimation(6616);
			player.sendMessage("You fail to catch the " + data.name + ".");
		}
		player.interruptActions(false, false, true);
	}

	/**
	 * An enum containing the possible rewards of an impling jar
	 * @author Matt - https://www.rune-server.org/members/matt%27/
	 *
	 * @date 12 aug. 2016
	 */
	public enum ImpRewards {
		BABY(11238, new int[][] {
				{1755,1}, {1734,1}, {1733, 1}, {946,1}, {1985,1},
				{2347,1}, {1759,1}, {1927,1}, {319,1}, {2007,1},
				{1779,1}, {7170,1}, {1438,1}, {2355,1}, {1607,1},
				{1743,1}, {379,1}, {1761,1}
		}),
		YOUNG(11240, new int[][] {
				{361,1}, {1902,1}, {1539,5}, {1524,1}, {7936,1},
				{855,1}, {1353,1}, {2293,1}, {7178,1}, {247,1},
				{453,1}, {1777,1}, {231,1}, {1761,1}, {8778,1},
				{133,1}, {2359,1}
		}),
		GOURMENT(11242, new int[][] {
				{365,1}, {361,1}, {2011,1}, {1897,1}, {2327,1},
				{5970,1}, {380,4}, {7179, 1, 5}, {386,3},
				{1883,1}, {3145, 2}, {5755,1}, {10137, 5}
		}),
		EARTH(11244, new int[][] {
				{6033,6}, {1440,1}, {5535, 1}, {557, 32}, {1442,1},
				{1784,4}, {1273,1}, {447,1}, {1606,2}
		}),
		ESSENCE(11246, new int[][] {
				{7936,20}, {555,30}, {556,30}, {558,25}, {559,28},
				{562, 4}, {1448, 1}, {564, 4}, {563, 13}, {565, 7},
				{566, 11}
		}),
		ECLECTIC(11248, new int[][] {
				{1273,1}, {5970,1}, {231,1}, {556, 30, 47}, {8779, 4},
				{1199,1}, {4527,1}, {444,1}, {2358, 5}, {7937, 20, 35},
				{237,1}, {2493,1}, {10083,1}, {1213,1}, {450, 10},
				{5760, 2}, {7208,1}, {5321, 3}, {1391, 1}, {1601,1}
		}),
		NATURE(11250, new int[][] {
				{5100,1}, {5104, 1}, {5281,1}, {5294,1}, {6016,1},
				{1513,1}, {254, 4}, {5313,1}, {5286,1}, {5285, 1},
				{3000,1}, {5974,1}, {5297,1}, {5299,1}, {5298, 5},
				{5304,1}, {5295, 1}, {270,2}, {5303,1}
		}),
		MAGPIE(11252, new int[][] {
				{1701,3}, {1732, 3}, {2569,3}, {3391,1}, {4097,1},
				{5541,1}, {1747, 6}, {1347,1}, {2571, 4}, {4095, 1},
				{2364, 2}, {1215, 1}, {1185, 1}, {1602, 4}, {5287, 1},
				{987,1}, {985,1}, {5300,1}
		}),
		NINJA(11254, new int[][] {
				{4097,1}, {3385,1}, {892, 70}, {140,4}, {1748 , 10, 16},
				{1113, 1}, {1215, 1}, {1333,1}, {1347,1}, {9342, 2},
				{5938,4}, {6156, 3}, {9194, 4}, {6313,1}, {805, 50}
		}),
		DRAGON(11256, new int[][] {
				{11212, 10, 15}, {9341, 3, 40}, {1305,1}, {11237, 10, 150}, {9193, 10, 49},
				{535, 51, 100}, {1216, 3}, {11230, 10, 15}, {5316, 1}, {537, 10, 20},
				{1616, 3, 6}, {1705, 2, 4}, {5300, 6}, {7219, 5, 15}, {4093, 1},
				{5547, 1}, {1701, 2, 4}
		});

		public static HashMap<Integer, ImpRewards> impReward = new HashMap<>();

		static {
			for(ImpRewards t : values()) {
				impReward.put(t.itemId, t);
			}
		}

		private final int itemId;
		private final int[][] rewards;

		ImpRewards(int itemId, int[][] rewards) {
			this.itemId = itemId;
			this.rewards = rewards;
		}
		public int getItemId() {
			return itemId;
		}
		public int[][] getRewards() {
			return rewards;
		}
	}

	public static final Random random = new Random();

	/**
	 * Handles the rewards of looting an impling jar
	 * @param c
	 * @param itemId
	 */
	public static void getReward(Player c, int itemId) {
		if (c == null || itemId == -1) {
			return;
		}
		if (c.getItems().freeSlots() < 2) {
			c.sendMessage("Make sure you have at least 2 free inventory spots before looting.");
			return;
		}
		int randomClue = Misc.random(1000);
		if (itemId == 19732) {
			c.getItems().deleteItem(19732, 1);
			c.sendMessage("You loot the lucky impling jar and received:");
			if (randomClue >= 0 && randomClue <= 500) {
				c.getItems().addItem(2677, 1);
				c.sendMessage("x1 Easy clue");
			} else if (randomClue >= 501 && randomClue <= 850) {
				c.getItems().addItem(2801, 1);
				c.sendMessage("x1 Medium clue");
			} else if (randomClue >= 851 && randomClue <= 990) {
				c.getItems().addItem(2722, 1);
				c.sendMessage("x1 Hard clue");
			} else if (randomClue >= 991 && randomClue <= 1000) {
				c.getItems().addItem(19841, 1);
				c.sendMessage("x1 Master casket");
			}
			return;
		}
		ImpRewards t = ImpRewards.impReward.get(itemId);
		c.getItems().deleteItem(t.getItemId(), c.getItems().getInventoryItemSlot(t.getItemId()), 1);
		int r = random.nextInt(t.getRewards().length);
		if(Misc.random(15) == 0) {
			c.sendMessage("The impling jar cracked but you were lucky enough to receive the reward.");
		} else {
			c.getItems().addItem(11260, 1);
			c.sendMessage("You successfully looted the impling jar and received:");
		}
		if(t.getRewards()[r].length == 3) {
			int amount = t.getRewards()[r][1] + random.nextInt(t.getRewards()[r][2] - t.getRewards()[r][1]);
			c.getItems().addItem(t.getRewards()[r][0], amount);
			c.sendMessage("x" + amount + " " + ItemAssistant.getItemName(t.getRewards()[r][0]));
		} else {
			c.getItems().addItem(t.getRewards()[r][0], t.getRewards()[r][1]);
			c.sendMessage("x" + t.getRewards()[r][1] + " " + ItemAssistant.getItemName(t.getRewards()[r][0]));
		}
	}

}
