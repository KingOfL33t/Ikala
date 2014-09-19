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
	 * Currently reading in characters of a node name.
	 */
	NODE_NAME,
	/**
	 * Currently reading in characters of a method name.
	 */
	METHOD_NAME,
	/**
	 * Currently reading in an argument for a method.
	 */
	METHOD_ARGS,
	/**
	 * Has completed scanning the given command.
	 */
	FINISHED;
}
