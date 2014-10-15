package com.ikalagaming.scripting;

/**
 * Parentheses, periods, and other characters used in commands.
 * @author Ches Burks
 *
 */
public class Operator extends ScriptToken{
	private String contents = " ";
	/**
	 * Constructs a new command string with no content.
	 */
	public Operator(){}
	/**
	 * Constructs a new command string with the supplied text
	 * @param value the content of the string
	 */
	public Operator(String value){
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
	 * Set the contents of the operator to the supplied one
	 *
	 * @param newOperator the new operator
	 */
	public void setContents(String newOperator){
		contents = newOperator;
	}

	/**
	 * Returns the {@link TokenType type} of this element
	 * @return the type of element this is
	 */
	@Override
	public TokenType getType(){
		return TokenType.OPERATOR;
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
