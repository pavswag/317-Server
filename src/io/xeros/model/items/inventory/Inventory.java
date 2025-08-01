package io.xeros.model.items.inventory;

import com.google.common.base.Preconditions;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.items.GameItem;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an inventory - a collection of {@link GameItem}s.
 *
 * @author Graham
 */
public final class Inventory {

	/**
	 * An enumeration containing the different 'stacking modes' of an {@link Inventory}.
	 *
	 * @author Graham
	 */
	public static enum StackMode {

		/**
		 * When in {@link #STACK_ALWAYS} mode, an {@link Inventory} will stack every single item, regardless of the
		 * settings of individual items.
		 */
		STACK_ALWAYS,

		/**
		 * When in {@link #STACK_NEVER} mode, an {@link Inventory} will never stack items.
		 */
		STACK_NEVER,

		/**
		 * When in {@link #STACK_STACKABLE_ITEMS} mode, an {@link Inventory} will stack items depending on their
		 * settings.
		 */
		STACK_STACKABLE_ITEMS;

	}

	/**
	 * The capacity of this inventory.
	 */
	private final int capacity;

	/**
	 * A flag indicating if events are being fired.
	 */
	private boolean firingEvents = true;

	/**
	 * The items in this inventory.
	 */
	private GameItem[] items;

	/**
	 * A list of inventory listeners.
	 */
	private final List<InventoryListener> listeners = new ArrayList<>();

	/**
	 * The stacking mode.
	 */
	private final StackMode mode;

	/**
	 * The size of this inventory - the number of 'used slots'.
	 */
	private int size = 0;

	/**
	 * Creates an inventory.
	 *
	 * @param capacity The capacity.
	 */
	public Inventory(int capacity) {
		this(capacity, StackMode.STACK_STACKABLE_ITEMS);
	}

	/**
	 * Creates an inventory.
	 *
	 * @param capacity The capacity.
	 * @param mode The {@link StackMode}.
	 * @throws IllegalArgumentException If the capacity is negative.
	 * @throws NullPointerException If the mode is {@code null}.
	 */
	public Inventory(int capacity, StackMode mode) {
		Preconditions.checkArgument(capacity >= 0, "Capacity cannot be negative.");
		Preconditions.checkNotNull(mode, "Stacking mode cannot be null.");

		this.capacity = capacity;
		this.mode = mode;
		items = new GameItem[capacity];
	}

	/**
	 * Create a deep copy of this inventory.
	 * @return The deep copy of this inventory.
	 */
	public Inventory copy() {
		Inventory inventory = new Inventory(capacity, mode);
		for (int i = 0; i < capacity; i++)
			if (items[i] != null)
				inventory.items[i] = items[i].copy();
		return inventory;
	}

	/**
	 * An alias for {@code add(id, 1)}.
	 *
	 * @param id The id.
	 * @return {@code true} if the item was added, {@code false} if there was not enough room.
	 */
	public boolean add(int id) {
		return add(id, 1) == 0;
	}

	/**
	 * An alias for {@code add(new GameItem(id, amount)}.
	 *
	 * @param id The id.
	 * @param amount The amount.
	 * @return The amount that remains.
	 */
	public int add(int id, int amount) {
		Optional<GameItem> item = add(new GameItem(id, amount));
		return item.isPresent() ? item.get().getAmount() : 0;
	}

	/**
	 * Add an item to the inventory, then return a copied game item with the amount set
	 * to the amount of item that were added to this container.
	 * @param item The item to add to this inventory.
	 * @return An item with the amount being the amount of items that were added to this inventory.
	 */
	public GameItem addAndReturnAmountAdded(GameItem item) {
		Optional<GameItem> remaining = add(item);
		int amountDeleted = remaining.isEmpty() ? item.getAmount() : item.getAmount() - remaining.get().getAmount();
		return new GameItem(item.getId(), amountDeleted);
	}

	/**
	 * Attempts to add as much of the specified {@code item} to this inventory as possible. If any of the item remains,
	 * an {@link GameItem item with the remainder} will be returned (in the case of stack-able items, any quantity that
	 * remains in the stack is returned). If nothing remains, the method will return {@link Optional#empty an empty
	 * Optional}.
	 *
	 * <p>
	 * If anything remains at all, the listener will be notified which could be used for notifying a player that their
	 * inventory is full, for example.
	 *
	 * @param item The item to add to this inventory.
	 * @return The item that may remain, if nothing remains, {@link Optional#empty an empty Optional} is returned.
	 */
	public Optional<GameItem> add(GameItem item) {
		int id = item.getId();
		boolean stackable = isStackable(item.getDef());

		if (stackable) {
			return addStackable(item, id);
		}

		int remaining = item.getAmount();

		if (remaining == 0) {
			return Optional.empty();
		}

		stopFiringEvents();
		try {
			GameItem single = new GameItem(item.getId(), 1);

			for (int slot = 0; slot < capacity; slot++) {
				if (items[slot] == null) {
					set(slot, single); // share the instances

					if (--remaining <= 0) {
						return Optional.empty();
					}
				}
			}
		} finally {
			startFiringEvents();

			if (remaining != item.getAmount()) {
				notifyItemsUpdated();
			}
		}

		notifyCapacityExceeded();

		return Optional.of(new GameItem(item.getId(), remaining));
	}

	/**
	 * This method adds as much of the specified stackable {@code item} to this inventory as possible
	 *
	 * @param item The item to add to this inventory.
	 * @param id The item's id
	 * @return The item that may remain, if nothing remains, {@link Optional#empty an empty Optional} is returned.
	 */
	private Optional<GameItem> addStackable(GameItem item, int id) {
		int slot = slotOf(id);

		if (slot != -1) {
			GameItem other = items[slot];

			long total = (long) item.getAmount() + other.getAmount();
			int amount, remaining;

			if (total > Integer.MAX_VALUE) {
				amount = (int) (total - Integer.MAX_VALUE);
				remaining = (int) (total - amount);
				notifyCapacityExceeded();
			} else {
				amount = (int) total;
				remaining = 0;
			}

			set(slot, new GameItem(id, amount));
			return remaining > 0 ? Optional.of(new GameItem(id, remaining)) : Optional.empty();
		}

		for (slot = 0; slot < capacity; slot++) {
			if (items[slot] == null) {
				set(slot, item);
				return Optional.empty();
			}
		}

		notifyCapacityExceeded();
		return Optional.of(item);
	}

	/**
	 * Check if the inventory has space for the entire item to be added.
	 * @param item The item that would be added.
	 * @return True if there is enough space to fully add the item.
	 */
	public boolean hasSpaceFor(GameItem item) {
		int id = item.getId();
		boolean stackable = isStackable(item.getDef());

		if (stackable) {
			return hasSpaceForStackable(item, id);
		}

		int remaining = item.getAmount();

		if (remaining == 0) {
			return true;
		}

		for (int slot = 0; slot < capacity; slot++) {
			if (items[slot] == null) {
				if (--remaining <= 0) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Check if the inventory has space for the entire stackable item to be added.
	 * @param item The item that would be added.
	 * @param id The item id that would be added.
	 * @return True if there is enough space to fully add the stackable item.
	 */
	private boolean hasSpaceForStackable(GameItem item, int id) {
		int slot = slotOf(id);

		if (slot != -1) {
			GameItem other = items[slot];
			long total = (long) item.getAmount() + other.getAmount();
			return total <= Integer.MAX_VALUE;
		}

		for (slot = 0; slot < capacity; slot++) {
			if (items[slot] == null) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Adds an {@link InventoryListener}.
	 *
	 * @param listener The listener.
	 */
	public void addListener(InventoryListener listener) {
		listeners.add(listener);
	}

	/**
	 * Gets the capacity of this inventory.
	 *
	 * @return The capacity.
	 */
	public int capacity() {
		return capacity;
	}

	/**
	 * Checks the bounds of the specified slots.
	 *
	 * @param slots The slots.
	 * @throws IndexOutOfBoundsException If the slot is out of bounds.
	 */
	private void checkBounds(int... slots) {
		for (int slot : slots) {
			Preconditions.checkElementIndex(slot, capacity, "Slot " + slot + " out of bounds.");
		}
	}

	/**
	 * Clears the inventory.
	 */
	public void clear() {
		items = new GameItem[capacity];
		size = 0;
		notifyItemsUpdated();
	}

	/**
	 * Creates a copy of this inventory. Listeners are not copied.
	 *
	 * @return The duplicated inventory.
	 */
	public Inventory duplicate() {
		Inventory copy = new Inventory(capacity, mode);
		System.arraycopy(items, 0, copy.items, 0, capacity);
		copy.size = size;
		return copy;
	}

	/**
	 * Checks if this inventory contains an item with the specified id.
	 *
	 * @param id The item's id.
	 * @return {@code true} if so, {@code false} if not.
	 */
	public boolean contains(int id) {
		return slotOf(id) != -1;
	}

	/**
	 * Returns whether or not this inventory contains any items with one of the specified ids.
	 *
	 * @param ids The ids.
	 * @return {@code true} if the inventory does contain at least one of the items, otherwise {@code false}.
	 */
	public boolean containsAny(int... ids) {
		return Arrays.stream(ids).anyMatch(id -> slotOf(id) != -1);
	}

	/**
	 * Returns whether or not this inventory contains an item for each of the specified ids.
	 *
	 * @param ids The ids.
	 * @return {@code true} if items in this inventory every id is
	 */
	public boolean containsAll(int... ids) {
		return Arrays.stream(ids).allMatch(id -> slotOf(id) != -1);
	}

	/**
	 * Forces the capacity to exceeded event to be fired.
	 */
	public void forceCapacityExceeded() {
		notifyCapacityExceeded();
	}

	/**
	 * Forces the refresh of this inventory.
	 */
	public void forceRefresh() {
		notifyItemsUpdated();
	}

	/**
	 * Forces a refresh of a specific slot.
	 *
	 * @param slot The slot.
	 */
	public void forceRefresh(int slot) {
		notifyItemUpdated(slot);
	}

	/**
	 * Gets the number of free slots.
	 *
	 * @return The number of free slots.
	 */
	public int freeSlots() {
		return capacity - size;
	}

	/**
	 * Gets the item in the specified slot.
	 *
	 * @param slot The slot.
	 * @return The item, or {@code null} if the slot is empty.
	 */
	public GameItem get(int slot) {
		checkBounds(slot);
		return items[slot];
	}

	/**
	 * Gets the amount of items with the specified id in this inventory.
	 *
	 * @param id The id.
	 * @return The number of matching items, or {@code 0} if none were found.
	 */
	public int getAmount(int id) {
		if (isStackable(ItemDef.forId(id))) {
			int slot = slotOf(id);
			return slot == -1 ? 0 : items[slot].getAmount();
		}

		int amount = 0, used = 0;
		for (int index = 0; index < capacity && used <= size; index++) {
			GameItem item = items[index];

			if (item != null) {
				if (item.getId() == id) {
					amount++;
				}

				used++;
			}
		}
		return amount;
	}

	/**
	 * Gets a clone of the items array.
	 *
	 * @return A clone of the items array.
	 */
	public GameItem[] getItems() {
		return items.clone();
	}

	/**
	 * Set the items array to a new array that is cloned.
	 */
	public void set(GameItem[] newItems) {
		this.items = new GameItem[capacity];

		for (int i = 0; i < newItems.length; i++) {
			if (i >= this.items.length) {
				throw new IllegalStateException("Loaded container was larger than this container.");
			}

			if (newItems[i] != null) {
				this.items[i] = newItems[i];
				this.size = i;
			}
		}
	}

	/**
	 * Build a new list from the copied item list {@link Inventory#getItems()}.
	 * @return A generated list of items.
	 */
	public List<GameItem> buildList() {
		return Arrays.stream(getItems()).filter(Objects::nonNull).collect(Collectors.toList());
	}

	/**
	 * Checks if the item with the specified {@link ItemDef} should be stacked.
	 *
	 * @param definition The item definition.
	 * @return {@code true} if the item should be stacked, {@code false} otherwise.
	 */
	private boolean isStackable(ItemDef definition) {
		if (mode == StackMode.STACK_ALWAYS) {
			return true;
		} else if (mode == StackMode.STACK_STACKABLE_ITEMS) {
			return definition.isStackable();
		}

		return false;
	}

	/**
	 * Notifies listeners that the capacity of this inventory has been exceeded.
	 */
	private void notifyCapacityExceeded() {
		if (firingEvents) {
			for (InventoryListener listener : listeners) {
				listener.capacityExceeded(this);
			}
		}
	}

	/**
	 * Notifies listeners that all the items have been updated.
	 */
	private void notifyItemsUpdated() {
		if (firingEvents) {
			for (InventoryListener listener : listeners) {
				listener.itemsUpdated(this);
			}
		}
	}

	/**
	 * Notifies listeners that the specified slot has been updated.
	 *
	 * @param slot The slot.
	 */
	private void notifyItemUpdated(int slot) {
		if (firingEvents) {
			listeners.forEach(listener -> listener.itemUpdated(this, slot, items[slot]));
		}
	}

	/**
	 * Removes one item with the specified id.
	 *
	 * @param id The id.
	 * @return {@code true} if the item was removed, {@code false} otherwise.
	 */
	public boolean remove(int id) {
		return remove(id, 1) == 1;
	}

	/**
	 * Removes one item with each of the specified ids.
	 * <p>
	 * This method will attempt to remove one of each item, and will continue even if a previous item could not be
	 * removed.
	 *
	 * @param ids The ids of the item to remove.
	 * @return {@code true} if one of each item could be removed, otherwise {@code false}.
	 */
	public boolean remove(int... ids) {
		boolean successful = true;
		for (int id : ids) {
			successful &= remove(id);
		}

		return successful;
	}

	/**
	 * Removes {@code amount} of the item with the specified {@code id}. If the item is stackable, it will remove it
	 * from the stack. If not, it'll remove {@code amount} items.
	 *
	 * @param id The id.
	 * @param amount The amount.
	 * @return The amount that was removed.
	 */
	public int remove(int id, int amount) {
		ItemDef def = ItemDef.forId(id);
		boolean stackable = isStackable(def);

		if (stackable) {
			int slot = slotOf(id);

			if (slot != -1) {
				GameItem item = items[slot];

				if (amount >= item.getAmount()) {
					set(slot, null);
					return item.getAmount();
				}

				set(slot, new GameItem(item.getId(), item.getAmount() - amount));
				return amount;
			}

			return 0;
		}

		int removed = 0;

		stopFiringEvents();

		try {
			for (int slot = 0; slot < capacity; slot++) {
				GameItem item = items[slot];
				if (item != null && item.getId() == id) {
					set(slot, null);

					if (++removed >= amount) {
						break;
					}
				}
			}

		} finally {
			startFiringEvents();

			if (removed > 0) {
				notifyItemsUpdated();
			}
		}

		return removed;
	}

	/**
	 * An alias for {@code remove(item.getId(), item.getAmount())}.
	 *
	 * @param item The item to remove.
	 * @return The amount that was removed.
	 */
	public int remove(GameItem item) {
		return remove(item.getId(), item.getAmount());
	}

	/**
	 * Remove all items with the given {@code id} and return the number of
	 * items removed.
	 *
	 * @param id The id of items to remove.
	 * @return The amount that was removed.
	 */
	public int removeAll(int id) { return remove(id, getAmount(id)); }

	/**
	 * Removes all the listeners.
	 */
	public void removeAllListeners() {
		listeners.clear();
	}

	/**
	 * Removes a listener.
	 *
	 * @param listener The listener to remove.
	 */
	public void removeListener(InventoryListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Removes {@code amount} of the item at the specified {@code slot}. If the item is not stacked, it will only remove
	 * the single item at the slot (meaning it will ignore any amount higher than 1). This means that this method will
	 * under no circumstances make any changes to other slots.
	 *
	 * @param slot The slot.
	 * @param amount The amount to remove.
	 * @return The amount that was removed (0 if nothing was removed).
	 */
	public int removeSlot(int slot, int amount) {
		if (amount != 0) {
			GameItem item = items[slot];
			if (item != null) {
				int itemAmount = item.getAmount();
				int removed = Math.min(amount, itemAmount);
				int remainder = itemAmount - removed;

				set(slot, remainder > 0 ? new GameItem(item.getId(), remainder) : null);
				return removed;
			}
		}

		return 0;
	}

	/**
	 * Removes the item (if any) that is in the specified slot.
	 *
	 * @param slot The slot to reset.
	 * @return The item that was in the slot.
	 */
	public GameItem reset(int slot) {
		checkBounds(slot);

		GameItem old = items[slot];
		if (old != null) {
			size--;
		}

		items[slot] = null;
		notifyItemUpdated(slot);
		return old;
	}

	/**
	 * Sets the item that is in the specified slot.
	 *
	 * @param slot The slot.
	 * @param item The item, or {@code null} to remove the item that is in the slot.
	 * @return The item that was in the slot.
	 */
	public GameItem set(int slot, GameItem item) {
		if (item == null) {
			return reset(slot);
		}
		checkBounds(slot);

		GameItem old = items[slot];
		if (old == null) {
			size++;
		}

		items[slot] = item;
		notifyItemUpdated(slot);
		return old;
	}

	/**
	 * Shifts all items to the top left of the container, leaving no gaps.
	 */
	public void shift() {
		GameItem[] old = items;
		items = new GameItem[capacity];
		for (int slot = 0, position = 0; slot < items.length; slot++) {
			if (old[slot] != null) {
				items[position++] = old[slot];
			}
		}

		if (firingEvents) {
			notifyItemsUpdated();
		}
	}

	/**
	 * Gets the size of this inventory - the number of used slots.
	 *
	 * @return The size.
	 */
	public int size() {
		return size;
	}

	/**
	 * Gets the inventory slot for the specified id.
	 *
	 * @param id The id.
	 * @return The first slot containing the specified item, or {@code -1} if none of the slots matched the conditions.
	 */
	public int slotOf(int id) {
		int used = 0;
		for (int slot = 0; slot < capacity && used <= size; slot++) {
			GameItem item = items[slot];

			if (item != null) {
				if (item.getId() == id) {
					return slot;
				}

				used++;
			}
		}
		return -1;
	}

	/**
	 * Starts the firing of events.
	 */
	public void startFiringEvents() {
		firingEvents = true;
	}

	/**
	 * Stops the firing of events.
	 */
	public void stopFiringEvents() {
		firingEvents = false;
	}

	public void swap(boolean insert, int oldSlot, int newSlot) {
		swap(insert, oldSlot, newSlot, true);
	}

	/**
	 * Swaps the two items at the specified slots.
	 *
	 * @param insert If the swap should be done in insertion mode.
	 * @param oldSlot The old slot.
	 * @param newSlot The new slot.
	 */
	public void swap(boolean insert, int oldSlot, int newSlot, boolean refresh) {
		checkBounds(oldSlot, newSlot);

		if (insert) {
			if (newSlot > oldSlot) {
				for (int slot = oldSlot; slot < newSlot; slot++) {
					swap(slot, slot + 1);
				}
			} else if (oldSlot > newSlot) {
				for (int slot = oldSlot; slot > newSlot; slot--) {
					swap(slot, slot - 1);
				}
			}
			if (refresh) {
				forceRefresh();
			}
		} else {
			GameItem item = items[oldSlot];
			items[oldSlot] = items[newSlot];
			items[newSlot] = item;
			if (refresh) {
				notifyItemUpdated(oldSlot);
				notifyItemUpdated(newSlot);
			}
		}
	}

	/**
	 * Swaps the two items at the specified slots.
	 *
	 * @param oldSlot The old slot.
	 * @param newSlot The new slot.
	 */
	public void swap(int oldSlot, int newSlot) {
		swap(false, oldSlot, newSlot, false);
	}

}