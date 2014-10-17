package com.ikalagaming.core;

/**
 * Configurable settings that allow changing the behavior of the node system.
 *
 * @author Ches Burks
 *
 */
public class NodeSettings {
	/**
	 * If nodes should be enabled when they are loaded.
	 * If this is false then nodes must be enabled manually after they
	 * are loaded.
	 */
	public static boolean ENABLE_ON_LOAD = true;
	/**
	 * If nodes should be disabled when they are unloaded.
	 * If this is false then nodes would have to disable themselves in the
	 * unload methods or not need to disable before unloading.
	 */
	public static boolean DISABLE_ON_UNLOAD = true;

	/**
	 * If the node manager should use events for altering nodes. False
	 * if they should be loaded, unloaded, disabled, etc using direct
	 * function calls. If false, this may cause slower loading/unloading
	 * but would ensure the nodes receive the commands immediately/directly.
	 * Each type of alteration has its own individual setting if this is true.
	 * <br>
	 * If no event system is available, defaults to direct access.
	 */
	public static boolean USE_EVENTS_FOR_ACCESS = true;

	/**
	 * True if the node manager should use an event to call the nodes onLoad
	 * method. False if it should call directly. Has no effect if
	 * {@link #USE_EVENTS_FOR_ACCESS} is false.
	 */
	public static boolean USE_EVENTS_FOR_ON_LOAD = true;

	/**
	 * True if the node manager should use an event to call the nodes onUnload
	 * method. False if it should call directly. Has no effect if
	 * {@link #USE_EVENTS_FOR_ACCESS} is false.
	 */
	public static boolean USE_EVENTS_FOR_ON_UNLOAD = true;

	/**
	 * True if the node manager should use an event to call the nodes enable
	 * method. False if it should call directly. Has no effect if
	 * {@link #USE_EVENTS_FOR_ACCESS} is false.
	 */
	public static boolean USE_EVENTS_FOR_ENABLE = true;

	/**
	 * True if the node manager should use an event to call the nodes disable
	 * method. False if it should call directly. Has no effect if
	 * {@link #USE_EVENTS_FOR_ACCESS} is false.
	 */
	public static boolean USE_EVENTS_FOR_DISABLE = true;
}
