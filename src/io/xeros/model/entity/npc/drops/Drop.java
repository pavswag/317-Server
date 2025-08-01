package io.xeros.model.entity.npc.drops;

import java.util.List;

import com.google.common.base.Preconditions;
import lombok.Getter;

public class Drop {
	
	/**
	 * The npc identification value
	 */
	@Getter
	private final List<Integer> npcIds;

	/**
	 * The item identification value
	 */
	private final int itemId;

	/**
	 * The minimum amount of the item you can receive
	 */
	private final int minimumAmount;

	/**
	 * The maximum amount of the item you can receive
	 */
	private final int maximumAmount;

	/**
	 * A new {@link Drop} that exists within a particular {@link Table}
	 * 
	 * @param itemId the id of the item in the drop
	 * @param minimumAmount the minimum amount received
	 * @param maximumAmount the maximum amount received
	 */
	public Drop(List<Integer> npcIds, int itemId, int minimumAmount, int maximumAmount) {
		Preconditions.checkArgument(minimumAmount <= maximumAmount, "The minimum amount must be less than or equal to the maximum amount.", minimumAmount, maximumAmount);
		this.npcIds = npcIds;
		this.itemId = itemId;
		this.minimumAmount = minimumAmount;
		this.maximumAmount = maximumAmount;
	}

	/**
	 * The item identification value
	 * 
	 * @return the item id
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * The absolute minimum amount received from a particular drop.
	 * 
	 * @return the minimum amount
	 */
	public int getMinimumAmount() {
		return minimumAmount;
	}

	/**
	 * The maximum amount of an item that can be received from a singular drop
	 * 
	 * @return the maximum amount
	 */
	public int getMaximumAmount() {
		return maximumAmount;
	}

}
