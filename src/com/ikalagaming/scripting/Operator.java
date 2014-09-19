package com.ikalagaming.scripting;

/**
 * Parentheses, periods, and other characters used in commands.
 * @author Ches Burks
 *
 */
public class Operator extends CommandElement{
	private char contents = ' ';
	/**
	 * Constructs a new command string with no content.
	 */
	public Operator(){}
	/**
	 * Constructs a new command string with the supplied text
	 * @param value the content of the string
	 */
	public Operator(char value){
		contents = value;
	}

	/**
	 * Return the contents of this string.
	 * @return the string
	 */
	public char getContents(){
		return contents;
	}
	/**
	 * Set the contents of the operator to the supplied one
	 *
	 * @param newOperator the new operator
	 */
	public void setContents(char newOperator){
		contents = newOperator;
	}

	/**
	 * Returns the {@link CmdComponentType type} of this element
	 * @return the type of element this is
	 */
	@Override
	public CmdComponentType getType(){
		return CmdComponentType.OPERATOR;
	}

	/**
	 * Returns the contents of this string.
	 * @return this as a string
	 */
	@Override
	public String toString(){
		return contents+"";
	}
}
