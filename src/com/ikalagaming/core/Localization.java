
package com.ikalagaming.core;

import java.util.Locale;

/**
 * Allows changing languages and locales in the program.
 * 
 * @author Ches Burks
 * 
 */
public class Localization {

	private static Localization instance;
	private Locale locale = new Locale("en", "US");

	/**
	 * Returns the static instance. Creates one if it does not exist.
	 * 
	 * @return The current instance of the class
	 */
	private static Localization getInstance() {
		if (instance == null) {
			instance = new Localization();
		}
		return instance;
	}

	/**
	 * Sets the current locale to the given locale.
	 * 
	 * @param locale The new locale to use
	 */
	public static void setLocale(Locale locale) {
		getInstance().locale = locale;
	}

	/**
	 * Returns the current locale. If no locale was set, defaults to "en_US".
	 * 
	 * @return The current locale
	 */
	public static Locale getLocale() {
		return getInstance().locale;
	}
}
