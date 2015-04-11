package com.ikalagaming.entity;

import com.ikalagaming.item.Item;
import com.ikalagaming.item.ItemStack;

/**
 * Contains slots for items and methods for modifying the contents.
 *
 * @author Ches Burks
 *
 */
public class Inventory {
	/**
	 * The array of slots that contain items in this inventory.
	 */
	private InventorySlot[] inventorySlots;

	/**
	 * Constructs a new Inventory with the given amount of slots.
	 *
	 * @param slots The number of slots the inventory will have
	 */
	public Inventory(int slots) {
		this.inventorySlots = new InventorySlot[slots];
	}

	/**
	 * Adds the item to the first available {@link InventorySlot InventorySlot},
	 * if possible.
	 *
	 * @param item The item to add
	 */
	public void addItem(Item item) {
		for (InventorySlot slot : this.inventorySlots) {
			if (slot.isEmpty()) {
				slot.getItemStack().setItem(item);
				slot.getItemStack().setAmount(1);
			}
			else if (slot.getItemStack().getItem().canStackWith(item)) {
				slot.getItemStack().addItems(1);
				break;
			}
		}
	}

	/**
	 * Combines the {@link ItemStack ItemStack} with the first available stack,
	 * if possible.
	 *
	 * @param items The itemstack to add
	 */
	public void addItemStack(ItemStack items) {
		// try and add to an existing slot
		for (InventorySlot slot : this.inventorySlots) {
			if (slot.getItemStack().getItem().canStackWith(items.getItem())) {
				ItemStack overflow = slot.combineItemStacks(items);
				/*
				 * Combine stacks with the first available. if it can't all fit,
				 * add the extra items to another slot/stack.
				 */
				if (!overflow.isEmpty()) {
					this.addItemStack(overflow);
				}
				break;
			}
		}

	}

	/**
	 * Returns how many slots the inventory contains.
	 *
	 * @return The size of the slot array
	 */
	public int getSize() {
		return this.inventorySlots.length;
	}

	/**
	 * Returns the {@link InventorySlot InventorySlot} in the given index, if it
	 * exists.
	 *
	 * @param index The index of the slot to retrieve
	 * @return The slot in the given index
	 */
	public InventorySlot getSlot(int index) {
		return this.inventorySlots[index];
	}

	/**
	 * Returns true if the inventory has no empty slots, false otherwise.
	 *
	 * @return True if the inventory has no empty slots, false otherwise.
	 */
	public boolean isFull() {
		boolean empty = false;
		for (InventorySlot slot : this.inventorySlots) {
			if (slot.isEmpty()) {
				empty = true;
				break;
			}
		}
		return !empty;
	}
}
