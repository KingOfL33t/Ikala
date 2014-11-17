
package com.ikalagaming.permissions;

/**
 * Represents an object that may be a server operator, such as a Player.
 * 
 * @author Ches Burks
 * 
 */
public interface Operator {
	/**
	 * Checks if this object is a server operator
	 * 
	 * @return true if this is an operator, otherwise false
	 */
	public boolean isOp();

	/**
	 * Sets the operator status of this object
	 * 
	 * @param value New operator value
	 */
	public void setOp(boolean value);
}
