
package com.ikalagaming.logging;

/**
 * A centralized list of errors and their corresponding code.
 * 
 * @author Ches Burks
 * 
 */
public enum ErrorCode {

	// system errors
	/**
	 * The system ran out of memory.
	 */
	SYS_OUT_OF_MEMORY(nextIndex(), "sys_no_mem"),
	/**
	 * A thread was busy and got interrupted
	 */
	THREAD_INTERRUPTED(nextIndex(), "thread_interrupt"),
	/**
	 * The event queue could not add more items because it is full
	 */
	EVENT_QUEUE_FULL(nextIndex(), "evt_queue_full"),
	/**
	 * An Exception occurred, but the type is unknown
	 */
	EXCEPTION(nextIndex(), "exception"),

	// localization errors
	/**
	 * A requested locale does not exist.
	 */
	LOCALE_NOT_FOUND(nextIndex(), "no_locale"),
	/**
	 * A resource was requested but could not be found.
	 */
	LOCALE_RESOURCE_NOT_FOUND(nextIndex(), "locale_resource_missing"),
	/**
	 * The resource loaded is different than the type that was expected. For
	 * example, a string was found where an integer was supposed to be.
	 */
	LOCALE_RESOURCE_WRONG_TYPE(nextIndex(), "locale_wrong_type"),

	// package errors
	/**
	 * A package did not enable properly.
	 */
	PACKAGE_ENABLE_FAIL(nextIndex(), "package_enable_fail"),
	/**
	 * A package did not disable properly.
	 */
	PACKAGE_DISABLE_FAIL(nextIndex(), "package_disable_fail"),
	/**
	 * A package did not get loaded properly.
	 */
	PACKAGE_LOAD_FAIL(nextIndex(), "package_load_fail"),
	/**
	 * A package did not get unloaded properly.
	 */
	PACKAGE_UNLOAD_FAIL(nextIndex(), "package_unload_fail"),
	/**
	 * Someone tried to communicate with a package that is not loaded
	 */
	PACKAGE_NOT_LOADED(nextIndex(), "package_not_packageed"),
	/**
	 * Someone tried to communicate with a package that is not enabled
	 */
	PACKAGE_NOT_ENABLED(nextIndex(), "package_not_enabled"),
	// Command errors
	/**
	 * A package tried to register a command that already exists
	 */
	COMMAND_ALREADY_REGISTERED(nextIndex(), "command_already_registered"),
	/**
	 * A command was sent that was not registered
	 */
	COMMAND_UNKNOWN(nextIndex(), "command_unknown");

	/*
	 * This allows enum values to be shifted around or values to be added later
	 * without having to change all of the values by hand.
	 */
	/**
	 * The current index value for the class.
	 */
	private static int i = 0;

	/**
	 * Returns the next index value.
	 * 
	 * @return the next value
	 */
	private static int nextIndex() {
		++i;
		return i;
	}

	/** The int associated with the error */
	private int code;
	/** The name used to look up the translated error message */
	private String name;

	/**
	 * Constructs a new ErrorCode with the given int index and name.
	 * 
	 * @param val the int associated with the error
	 * @param label the name used to look up the translated error message
	 */
	private ErrorCode(int val, String label) {
		this.code = val;
		this.name = label;
	}

	/**
	 * Returns the int value associated with this error.
	 * 
	 * @return the error code
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * Returns the name used to look up the translated error message
	 * 
	 * @return the name code
	 */
	public String getName() {
		return this.name;
	}
}
