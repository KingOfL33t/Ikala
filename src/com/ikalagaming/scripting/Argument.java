package com.ikalagaming.scripting;

/**
 * Either data or a function may be an argument
 *
 * @author Ches Burks
 *
 */
public class Argument extends ScriptToken{
	private ScriptToken contents;
	/**
	 * Constructs a new command string with no content.
	 */
	public Argument(){}
	/**
	 * Constructs a new command string with the supplied argument
	 * @param value the argument
	 */
	public Argument(ScriptToken value){
		contents = value;
	}

	/**
	 * Return the contents of this argument.
	 * @return the string
	 */
	public ScriptToken getContents(){
		return contents;
	}
	/**
	 * Set the contents of the string to the provided argument
	 *
	 * @param newContents the new argument
	 */
	public void setContents(ScriptToken newContents){
		contents = newContents;
	}

	/**
	 * Returns the {@link TokenType type} of this argument
	 * @return the type of element this is
	 */
	@Override
	public TokenType getType(){
		return contents.getType();
	}

	/**
	 * Returns the contents of this string.
	 * @return this as a string
	 */
	@Override
	public String toString(){
		return contents.toString();
	}

	/**
	 * Returns true if this is a valid element and follows the syntax rules
	 * that apply to it.
	 *
	 * @return true if this is valid
	 */
	@Override
	public boolean isValid(){
		return contents.isValid();
	}
}
