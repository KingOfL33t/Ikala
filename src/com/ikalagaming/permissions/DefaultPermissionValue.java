
package com.ikalagaming.permissions;

import java.util.HashMap;
import java.util.Map;

/**
 * The possible default values for permissions.
 * 
 * @author Ches Burks
 * 
 */
public enum DefaultPermissionValue {
	/**
	 * Defaults to true
	 */
	TRUE("true"),

	/**
	 * Defaults to false
	 */
	FALSE("false"),
	/**
	 * Defaults to true for operators, false for everyone else
	 */
	OPERATOR("operator", "op"),
	/**
	 * Defaults to true for everyone except operators, false for operators
	 */
	NOT_OPERATOR("notoperator", "notop");

	private final String[] names;
	private final static Map<String, DefaultPermissionValue> lookup =
			new HashMap<String, DefaultPermissionValue>();

	private DefaultPermissionValue(String... names) {
		this.names = names;
	}

	/**
	 * Calculates the value of this permission for the given value of op.
	 * 
	 * @param op If the target is an operator
	 * @return True if the default should be true, otherwise false
	 */
	public boolean getValue(boolean op) {
		switch (this) {
		case TRUE:
			return true;
		case FALSE:
			return false;
		case OPERATOR:
			return op;
		case NOT_OPERATOR:
			return !op;
		default:
			return false;
		}
	}

	/**
	 * Looks up a DefaultPermissionValue by name
	 * 
	 * @param name Name of the default
	 * @return Specified value, or null if not found
	 */
	public static DefaultPermissionValue getByName(String name) {
		return lookup.get(name.toLowerCase().replaceAll("[^a-z!]", ""));
	}

	@Override
	public String toString() {
		return names[0];
	}

	static {
		for (DefaultPermissionValue value : values()) {
			for (String name : value.names) {
				lookup.put(name, value);
			}
		}
	}
}
