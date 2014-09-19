package com.ikalagaming.scripting;

/**
 * Contains settings for the scripting engine that may be modified to affect
 * how the engine functions.
 *
 * @author Ches Burks
 *
 */
public class ScriptingSettings {

	//CHARS
	/**
	 * The character that is used to indicate the end of a command. The Java
	 * equivalent of this is {@code ';'}.
	 */
	public static char TRAILING_CHAR = ';';

	/**
	 * The character that is used to indicate that the following string is
	 * going to be a command. This should follow a node name.
	 * The java equivalent of this is {@code '.'}.
	 */
	public static char METHOD_INDICATOR = '.';

	/**
	 * The character that indicates the following information is arguments of
	 * a command, up until the matching {@link #ARGUMENT_END}.
	 * The java equivalent of this is {@code '('}.
	 */
	public static char ARGUMENT_BEGIN = '(';

	/**
	 * The character that indicates the end of the arguments for a command.
	 * This follows a matching {@link #ARGUMENT_BEGIN}.
	 * The java equivalent of this is {@code ')'}.
	 */
	public static char ARGUMENT_END = ')';

	/**
	 * The character that is used to begin and end strings inside of commands.
	 * The java equivalent of this is {@code '}'{@code '}.
	 */
	public static char STRING_CHAR = '\'';

	/**
	 * The character that is used to signify that the following line is
	 * a comment and should not be executed. The Java equivalent of this is
	 * {@code "//"}.
	 */
	public static char COMMENT_CHAR = '%';

	/**
	 * The character used to specify that the following line should not be
	 * executed but is a set of instructions for the compiler. This is similar
	 * to the {@code '#'} in C++.
	 */
	public static char COMPILER_CHAR = '#';

	/**
	 * The character used to separate different arguments in a method.
	 * The Java equivalent of this is {@code ','}.
	 */
	public static char ARGUMENT_SEPARATOR = ',';
}
