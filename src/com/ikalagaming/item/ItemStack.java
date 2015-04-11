package com.ikalagaming.item;

/**
 * Contains a {@link Item stackable item} and the quantity of items in the
 * stack.
 *
 * @author Ches Burks
 */
public class ItemStack {
	/**
	 * Creates and returns a new stack with null item and 0 item count.
	 *
	 * @return an empty stack.
	 */
	public static ItemStack getEmptyStack() {
		return new ItemStack(null, 0);
	}

	private Item storedItem;
	private int stackAmount;

	private boolean isEmpty;

	/**
	 * Constructs a new {@link ItemStack} for the given {@link Item item}. The
	 * stack defaults to a size of one.
	 *
	 * @param item The item the stack contains
	 */
	public ItemStack(Item item) {
		this.storedItem = item;
		this.setAmount(1);
	}

	/**
	 * Constructs a new {@link ItemStack} for the given {@link Item item} and
	 * the given amount.
	 *
	 * @param item The item the stack contains
	 * @param amount How many of the item are in the stack
	 */
	public ItemStack(Item item, int amount) {
		this.storedItem = item;
		this.setAmount(0);
	}

	/**
	 * Adds the specified amount of the items to the stack. The stack will be
	 * set to non-empty if the stack size is above zero. This will not increase
	 * the amount above the {@link Item item's} maximum stack size. This returns
	 * an item stack containing overflow items if the stack has become too
	 * large.
	 *
	 * @see #setAmount(int)
	 * @param count How many items to add
	 * @return the overflow items that exceed the maximum stack size
	 */
	public ItemStack addItems(int count) {
		ItemStack overflow = this.setAmount(this.stackAmount + count);
		return overflow;
	}

	/**
	 * Returns a new {@link ItemStack} with the same item and size as this one.
	 *
	 * @return The new itemstack
	 */
	@Override
	public ItemStack clone() {
		return new ItemStack(this.storedItem, this.stackAmount);
	}

	/**
	 * Returns the amount of items in the stack.
	 *
	 * @return How many items are held in the stack
	 */
	public int getAmount() {
		return this.stackAmount;
	}

	/**
	 * Returns the {@link Item item} this stack holds.
	 *
	 * @return The item
	 */
	public Item getItem() {
		return this.storedItem;
	}

	/**
	 * Returns true if the stack is empty, false otherwise.
	 *
	 * @return True if the stack is empty, false otherwise.
	 */
	public boolean isEmpty() {
		return this.isEmpty;
	}

	/**
	 * If the amount of items in this stack is the same as the {@link Item 
	 * item's} maximum stack size, returns true. Otherwise returns false.
	 *
	 * @return True if the stack is full, false otherwise
	 */
	public boolean isFull() {
		return (this.stackAmount == this.storedItem.getMaxStackSize());
	}

	/**
	 *
	 * Removes the specified amount of the items to the stack. The amount will
	 * not decrease below zero. The stack will be set to empty if the stack size
	 * is zero. This returns an item stack containing overflow items, which
	 * should theoretically always be empty.
	 *
	 * @see #setAmount(int)
	 * @param count How many items to remove
	 * @return the overflow items that exceed the maximum stack size
	 */
	public ItemStack removeItems(int count) {
		ItemStack overflow = this.setAmount(this.stackAmount - count);
		return overflow;
	}

	/**
	 * Sets the stack size to the given amount. The stack will be set to empty
	 * or non-empty accordingly. Any negative size will be set to zero. Any
	 * value above the {@link Item item's} maximum stack size will be set to the
	 * maximum stack size.
	 * <p>
	 * If the amount is set to greater than the max stack size, a new stack of
	 * the same item type will be returned containing the number of extra items.
	 * This may be an invalid number of items for an item of that type as it is
	 * not checked here. An empty stack of null items is returned if there are
	 * no extra items.
	 * </p>
	 *
	 * @param amount How many items the stack should contain
	 * @return the overflow items that exceed the maximum stack size
	 */
	public ItemStack setAmount(int amount) {
		this.stackAmount = amount;
		if (this.stackAmount < 0) {
			this.stackAmount = 0;
		}
		if (this.stackAmount == 0) {
			this.isEmpty = true;
			return ItemStack.getEmptyStack();
		}
		this.isEmpty = false;
		if (this.stackAmount > this.storedItem.getMaxStackSize()) {
			int overflowQuantity = amount - this.storedItem.getMaxStackSize();
			this.stackAmount = this.storedItem.getMaxStackSize();
			return new ItemStack(this.storedItem, overflowQuantity);
		}
		return ItemStack.getEmptyStack();
	}

	/**
	 * Sets the item held in this stack to the given item but does not change
	 * how many items are stored.
	 *
	 * @param newItem the new item to store
	 * @see #setItem(Item, int)
	 */
	public void setItem(Item newItem) {
		this.storedItem = newItem;
	}

	/**
	 * Sets the item held in this stack to the given item and changes the amount
	 * of the item to the supplied value.
	 *
	 * @param newItem the new item to store
	 * @param amount how many should now be in the stack
	 * @see #setItem(Item)
	 */
	public void setItem(Item newItem, int amount) {
		this.storedItem = newItem;
		this.setAmount(amount);
	}
}
