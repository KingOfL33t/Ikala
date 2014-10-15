package com.ikalagaming.scripting;

/**
 * Contains constants used in the scanner to keep track of what it is
 * currently scanning
 *
 * @author Ches Burks
 *
 */
public enum ScanState {
	/**
	 * The scanner has not started reading characters yet.
	 */
	NOT_STARTED,
	/**
	 * String of text.
	 */
	STRING,
	/**
	 * A number. May or may not have a decimal point.
	 */
	NUMBER,
	/**
	 * An operator.
	 */
	OPERATOR,
	/**
	 * A space, tab, enter or other character.
	 */
	WHITESPACE,
	/**
	 * Has completed scanning the given command.
	 */
	FINISHED;
}
