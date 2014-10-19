package com.ikalagaming.logging;

/**
 * A centralized list of errors and their corresponding code.
 *
 * @author Ches Burks
 *
 */
public enum ErrorCode {

	//system errors
	/**
	 * The system ran out of memory.
	 */
	sys_out_of_memory(nextIndex(), "sys_no_mem"),
	/**
	 * A thread was busy and got interrupted
	 */
	thread_interrupted(nextIndex(), "thread_interrupt"),
	/**
	 * The event queue could not add more items because it is full
	 */
	event_queue_full(nextIndex(), "evt_queue_full"),

	//localization errors
	/**
	 * A requested locale does not exist.
	 */
	locale_not_found(nextIndex(),"no_locale"),
	/**
	 * A resource was requested but could not be found.
	 */
	locale_resource_not_found(nextIndex(), "locale_resource_missing"),
	/**
	 * The resource loaded is different than the type that was expected.
	 * For example, a string was found where an integer was supposed to be.
	 */
	locale_resource_wrong_type(nextIndex(), "locale_wrong_type"),

	//node errors
	/**
	 * A node did not enable properly.
	 */
	node_enable_fail(nextIndex(), "node_enable_fail"),
	/**
	 * A node did not disable properly.
	 */
	node_disable_fail(nextIndex(), "node_disable_fail"),
	/**
	 * A node did not get loaded properly.
	 */
	node_load_fail(nextIndex(), "node_load_fail"),
	/**
	 * A node did not get unloaded properly.
	 */
	node_unload_fail(nextIndex(), "node_unload_fail");


	/*
	 * This allows enum values to be shifted around or values to be
	 * added later without having to change all of the values by hand.
	 */
	/**
	 * The current index value for the class.
	 */
	private static int i = 0;
	/**
	 * Returns the next index value.
	 * @return the next value
	 */
	private static int nextIndex(){
		++i;
		return i;
	}

	/** The int associated with the error*/
	private int code;
	/** The name used to look up the translated error message*/
	private String name;

	/**
	 * Constructs a new ErrorCode with the given int index and name.
	 *
	 * @param val the int associated with the error
	 * @param label the name used to look up the translated error message
	 */
	private ErrorCode(int val, String label){
		this.code = val;
		this.name = label;
	}


	/**
	 * Returns the int value associated with this error.
	 * @return the error code
	 */
	public int getCode(){
		return this.code;
	}

	/**
	 * Returns the name used to look up the translated error message
	 *
	 * @return the name code
	 */
	public String getName(){
		return this.name;
	}
}
