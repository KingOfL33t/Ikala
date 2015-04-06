
package com.ikalagaming.entity;

import com.ikalagaming.item.ItemStack;

/**
 * A slot in an inventory that holds an {@link ItemStack ItemStack}. This may be
 * empty.
 * 
 * @author Ches Burks
 *
 */
public class InventorySlot {
	private ItemStack itemStack;

	/**
	 * Constructs an empty InventorySlot.
	 */
	public InventorySlot() {
		this.setEmpty();
	}

	/**
	 * Sets the current stack to nothing and returns the old stack if it exists.
	 * 
	 * @return The old stack. This may be null.
	 */
	public ItemStack setEmpty() {
		ItemStack oldStack = this.itemStack;
		this.itemStack = ItemStack.getEmptyStack();
		return oldStack;
	}

	/**
	 * Returns true if the item stack is empty, false otherwise.
	 * 
	 * @return True if this is empty, false otherwise
	 */
	public boolean isEmpty() {
		return this.itemStack.isEmpty();
	}

	/**
	 * Sets the ItemStack to the specified ItemStack and returns the old stack.
	 * 
	 * @param newStack The stack to replace the old one
	 * @return The stack that was replaced. This may not contain items.
	 */
	public ItemStack setItemStack(ItemStack newStack) {
		ItemStack oldStack;// the old stack to be returned

		if (this.itemStack == null) {
			// Placeholder stack since the current stack is null
			oldStack = new ItemStack(null, 0);
		}
		else {
			oldStack = this.itemStack;
		}

		if (newStack == null) {
			this.itemStack = new ItemStack(null, 0);
		}
		else {
			this.itemStack = newStack;
		}

		return oldStack;
	}

	/**
	 * Returns the pointer to the ItemStack object.
	 * 
	 * @return The current ItemStack
	 */
	public ItemStack getItemStack() {
		return this.itemStack;
	}

	/**
	 * Tries to take the requested amount of items from the stack and return it
	 * in a new stack. If too many items are requested then it will return as
	 * many as the stack currently has. The current stack size is reduced
	 * appropriately. A negative or 0 value will return an empty stack.
	 * 
	 * @param count The amount of items to take
	 * @return The items that were removed, in a stack form
	 */
	public ItemStack takeItems(int count) {
		ItemStack toReturn;
		if (count <= 0) {
			toReturn = new ItemStack(null, 0);
		}
		else if (this.itemStack == null) {
			toReturn = new ItemStack(null, 0);
		}
		else if (this.itemStack.getAmount() <= count) {
			/*
			 * if the itemstack is the same size or less than the count
			 * requested then return the whole stack and empty this slot
			 */
			toReturn = this.itemStack;
			setEmpty();
		}
		else {
			/*
			 * the stack is larger than the requested stack size create a new
			 * stack of the stacks items
			 */
			toReturn = new ItemStack(this.itemStack.getItem(), count);
			// remove the items from the old stack
			this.itemStack.removeItems(count);
		}

		return toReturn;
	}

	/*
	 * Then replace them and return the old ones. Return a stack containing
	 * extra items that could not be added if adding the whole new stack results
	 * in more than the max items being stored.
	 */

	/**
	 * Try to combine this item stack with another. If there are now more items
	 * than the maximum stack size allowed by the current item, then extra items
	 * are returned in a stack.
	 * 
	 * @param toAdd The stack to combine with this stack
	 * @return A stack of overflow items
	 */
	public ItemStack combineItemStacks(ItemStack toAdd) {
		// if the items are different, replace the old ones and add a new one.
		if (!toAdd.getItem().canStackWith(this.itemStack.getItem())) {
			ItemStack overflow = this.itemStack;
			this.itemStack = toAdd;
			return overflow;
		}
		ItemStack overflow = this.itemStack.addItems(toAdd.getAmount());
		return overflow;
	}

	/**
	 * Returns true if the other stack can stack with this item type, and this
	 * stack has enough space to accommodate the other stack.
	 * 
	 * @param other The stack to test combining with
	 * @return True if the stacks can be combined, false otherwise
	 */
	public boolean canMergeWith(ItemStack other) {
		if (!(this.itemStack.getItem().canStackWith(other.getItem()))) {
			return false;// the items cant stack
		}
		if (this.itemStack.isFull()) {
			return false;// the stack is full
		}
		if ((this.itemStack.getAmount() + other.getAmount()) > this.itemStack
				.getItem().getMaxStackSize()) {
			return false;// the total of the two is larger than the max stack
							// size
		}
		return true;// they can be combined, and the stacks combined is within
					// stack limits
	}

}
