
package com.ikalagaming.core.packages;

/**
 * Configurable settings that allow changing the behavior of the package system.
 * 
 * @author Ches Burks
 * 
 */
public class PackageSettings {
	/**
	 * If packages should be enabled by the package manager when they are
	 * loaded. If this is false then packages must be enabled manually after
	 * they are loaded.
	 */
	public static boolean ENABLE_ON_LOAD = true;

	/**
	 * If the package manager should use events for altering packages. False if
	 * they should be loaded, unloaded, disabled, etc using direct function
	 * calls. If false, this may cause slower loading/unloading but would ensure
	 * the packages receive the commands immediately/directly. Each type of
	 * alteration has its own individual setting if this is true. <br>
	 * If no event system is available, defaults to direct access.
	 */
	public static boolean USE_EVENTS_FOR_ACCESS = true;

	/**
	 * True if the package manager should use an event to call the packages
	 * onLoad method. False if it should call directly. Has no effect if
	 * {@link #USE_EVENTS_FOR_ACCESS} is false.
	 */
	public static boolean USE_EVENTS_FOR_ON_LOAD = true;

	/**
	 * True if the package manager should use an event to call the packages
	 * onUnload method. False if it should call directly. Has no effect if
	 * {@link #USE_EVENTS_FOR_ACCESS} is false.
	 */
	public static boolean USE_EVENTS_FOR_ON_UNLOAD = true;

	/**
	 * True if the package manager should use an event to call the packages
	 * enable method. False if it should call directly. Has no effect if
	 * {@link #USE_EVENTS_FOR_ACCESS} is false.
	 */
	public static boolean USE_EVENTS_FOR_ENABLE = true;

	/**
	 * True if the package manager should use an event to call the packages
	 * disable method. False if it should call directly. Has no effect if
	 * {@link #USE_EVENTS_FOR_ACCESS} is false.
	 */
	public static boolean USE_EVENTS_FOR_DISABLE = true;
}
