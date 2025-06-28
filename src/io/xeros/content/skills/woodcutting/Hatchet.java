package io.xeros.content.skills.woodcutting;

import io.xeros.model.entity.player.Player;
import lombok.Getter;

@Getter
public enum Hatchet {

	//8324 Crystal hatchet
	BRONZE(1351, 1, 879, 1.0), 
	IRON(1349, 1, 877, 1.0), 
	STEEL(1353, 6, 875, .9), 
	BLACK(1361, 6, 873, .9), 
	MITHRIL(1355, 21, 871, .80), 
	ADAMANT(1357, 31, 869, .65), 
	RUNE(1359, 41, 867, .55), 
	DRAGON(6739, 61, 2846, .45), 
	INFERNAL_AXE(13241, 61, 2117, .45),
	THIRD_AGE(20011, 61, 7264, 30),
	DRAGON_OR(25378, 65, 24, 30),
	INFERNAL_OR(25066, 75, 24, 30),
	BLAZING(25110, 80, 8778, .15),
	CRYSTAL(23673, 61, 8324, .45);

    /**
     * -- GETTER --
     *  The item id associated with the hatchet.
     *
     */
    private final int itemId;
    /**
     * -- GETTER --
     *  The level required to operate the hatchet whether its in your inventory or in your equipment.
     *
     */
    private final int levelRequired;
    /**
     * -- GETTER --
     *  The animation displayed when the hatchet is being operated
     *
     */
    private final int animation;
    /**
     * -- GETTER --
     *  The speed at which this axe effects log cut time
     *
     */
    private final double chopSpeed;

	/**
	 * Constructs a new {@link Hatchet} used to cut down trees.
	 * 
	 * @param itemId the item identification value of the hatchet
	 * @param levelRequired the level required for use
	 * @param animation the animation displayed during use
	 * @param chopSpeed the effectiveness of the hatchet when determining a log has been cut
	 */
    Hatchet(int itemId, int levelRequired, int animation, double chopSpeed) {
		this.itemId = itemId;
		this.levelRequired = levelRequired;
		this.animation = animation;
		this.chopSpeed = chopSpeed;
	}

    /**
	 * Determines the best hatchet the player has in their inventory, or equipment.
	 * 
	 * @param player the player we're trying to find the best axe for
	 * @return null if the player doesn't have a hatchet they can operate, otherwise the best hatchet on their person.
	 */
	public static Hatchet getBest(Player player) {
		Hatchet hatchet = null;
		for (Hatchet h : values()) {
			if ((player.getItems().playerHasItem(h.itemId) || player.getItems().isWearingItem(h.itemId)) && player.playerLevel[Player.playerWoodcutting] >= h.levelRequired) {
				if (hatchet == null) {
					hatchet = h;
					continue;
				}
				if (hatchet.levelRequired < h.levelRequired) {
					hatchet = h;
				}
			}
		}
		return hatchet;
	}
}
