
package com.ikalagaming.permissions;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains mappings of values to either true or false. This is used in loading
 * from YAML files.
 * 
 * @author Ches Burks
 * 
 */
public enum DefaultPermissionValue {
	/**
	 * Defaults to true
	 */
	TRUE("true", "t", "yes"),

	/**
	 * Defaults to false
	 */
	FALSE("false", "f", "no");

	private final String[] names;
	private final static Map<String, Boolean> lookup =
			new HashMap<String, Boolean>();

	private DefaultPermissionValue(String... names) {
		this.names = names;
	}

	/**
	 * Looks up a DefaultPermissionValue by name
	 * 
	 * @param name Name of the default
	 * @return Specified value, or false if it does not exist
	 */
	public static boolean getByName(String name) {
		if (lookup.containsKey(name)) {
			return lookup.get(name.toLowerCase().replaceAll("[^a-z!]", ""));
		}
		return false;
	}

	/**
	 * Returns true if the given name is registered to a value.
	 * 
	 * @param name Name of the default
	 * @return true if the name exists, false otherwise
	 */
	public static boolean isValid(String name) {
		return lookup.containsKey(name);
	}

	/**
	 * Returns the boolean value for this object.
	 * 
	 * @return a boolean representing the value of this object
	 */
	public boolean value() {
		if (this == TRUE) {
			return true;
		}
		if (this == FALSE) {
			return false;
		}
		return false;
	}

	@Override
	public String toString() {
		return names[0];
	}

	static {
		for (String name : TRUE.names) {
			lookup.put(name, true);
		}
		for (String name : FALSE.names) {
			lookup.put(name, true);
		}
	}
}
