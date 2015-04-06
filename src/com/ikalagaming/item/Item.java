
package com.ikalagaming.item;

/**
 * A base item with a name.
 * 
 * @author Ches Burks
 *
 */
public class Item {
	protected String unlocalizedName;
	protected int maxStackSize = 1;

	/**
	 * Sets the name of the item to the given string. The name is an identifier
	 * for the object in the game. It does not necessarily have to be unique.
	 * 
	 * @param name The new name
	 */
	public void setUnlocalizedName(String name) {
		this.unlocalizedName = name;
	}

	/**
	 * Returns the name of the item. The name is an identifier for the object in
	 * the game. It does not necessarily have to be unique.
	 * 
	 * @return The name
	 */
	public String getUnlocalizedName() {
		return this.unlocalizedName;
	}

	/**
	 * Returns true if the two items can stack together. If they are of a
	 * different type (Class), they will not stack.
	 * 
	 * @param other The other item to check
	 * @return True if they can stack, false otherwise
	 */
	public boolean canStackWith(Item other) {
		if (this.getClass() == other.getClass()) {
			return true;
		}
		return false;
	}

	/**
	 * Returns The maximum size of an {@link ItemStack} containing the item.
	 * 
	 * @return The maximum stack size for the item.
	 */
	public int getMaxStackSize() {
		return maxStackSize;
	}
}
