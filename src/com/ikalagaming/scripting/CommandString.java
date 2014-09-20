package com.ikalagaming.scripting;

/**
 * A string that is an element in a function.
 *
 * @author Ches Burks
 *
 */
public class CommandString extends CommandElement{
	private String contents = "";

	/**
	 * Constructs a new command string with no content.
	 */
	public CommandString(){}
	/**
	 * Constructs a new command string with the supplied text
	 * @param value the content of the string
	 */
	public CommandString(String value){
		contents = value;
	}

	/**
	 * Return the contents of this string.
	 * @return the string
	 */
	public String getContents(){
		return contents;
	}
	/**
	 * Set the contents of the string to the provided text
	 *
	 * @param newString the new string
	 */
	public void setContents(String newString){
		contents = newString;
	}

	/**
	 * Returns the {@link CmdComponentType type} of this element
	 * @return the type of element this is
	 */
	@Override
	public CmdComponentType getType(){
		return CmdComponentType.STRING;
	}

	/**
	 * Returns the contents of this string.
	 * @return this as a string
	 */
	@Override
	public String toString(){
		return contents;
	}

	/**
	 * Returns true if this is a valid element and follows the syntax rules
	 * that apply to it.
	 *
	 * @return true if this is valid
	 */
	@Override
	public boolean isValid(){
		return true;
	}
}
