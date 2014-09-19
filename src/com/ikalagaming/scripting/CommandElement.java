package com.ikalagaming.scripting;

/**
 * A basic element that composes a function.
 *
 * @author Ches Burks
 *
 */
public class CommandElement {
	/**
	 * Returns the {@link CmdComponentType type} of this element
	 * @return the type of element this is
	 */
	public CmdComponentType getType(){
		return CmdComponentType.NULL;
	}

	/**
	 * Returns true if this is a valid element and follows the syntax rules
	 * that apply to it.
	 *
	 * @return true if this is valid
	 */
	public boolean isValid(){
		return true;
	}

	/**
	 * Returns this element in a string format.
	 * @return this as a string
	 */
	public String toString(){
		return "";
	}
}
