package com.ikalagaming.scripting;

/**
 * Allows differentiation between the contents of commandComponents based on
 * what they represent.
 *
 * @author Ches Burks
 *
 */
public enum CmdComponentType {
	/**
	 * An integer, float, or similar numerical constant.
	 */
	NUMBER,
	/**
	 * A string of text
	 */
	STRING,
	/**
	 * Parentheses, periods, and other characters used in commands.
	 */
	OPERATOR,
	/**
	 * An argument. This may be data or a function
	 */
	ARGUMENT,
	/**
	 * A function that is being carried out.
	 */
	FUNCTION,
	/**
	 * Contains arguments and operators
	 */
	ARGUMENT_GROUP,
	/**
	 * Contains a node and method name as well as an operator
	 */
	METHOD_GROUP,
	/**
	 * Default value for command elements.
	 */
	NULL;
}
