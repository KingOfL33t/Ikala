
package com.ikalagaming.logging;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import com.ikalagaming.core.Localization;
import com.ikalagaming.core.ResourceLocation;

/**
 * A set of levels for logging. This is the standard java logging list in an
 * enum form, with the values changed.
 * 
 * @see Level
 * @author Ches Burks
 * 
 */
public enum LoggingLevel {

	/**
	 * OFF is a special level that can be used to turn off logging. This should
	 * not be used as a value when constructing a message for logging. This
	 * level is initialized to <CODE>1000</CODE>.
	 */
	OFF("OFF", 1000),
	/**
	 * SEVERE is a message level indicating a serious failure.
	 * <p>
	 * In general SEVERE messages should describe events that are of
	 * considerable importance and which will prevent normal program execution.
	 * They should be reasonably intelligible to end users and to system
	 * administrators. This level is initialized to <CODE>70</CODE>.
	 */
	SEVERE("SEVERE", 70),
	/**
	 * WARNING is a message level indicating a potential problem.
	 * <p>
	 * In general WARNING messages should describe events that will be of
	 * interest to end users or system managers, or which indicate potential
	 * problems. This level is initialized to <CODE>60</CODE>.
	 */
	WARNING("WARNING", 60),
	/**
	 * INFO is a message level for informational messages.
	 * <p>
	 * Typically INFO messages will be written to the console or its equivalent.
	 * So the INFO level should only be used for reasonably significant messages
	 * that will make sense to end users and system administrators. This level
	 * is initialized to <CODE>50</CODE>.
	 */
	INFO("INFO", 50),
	/**
	 * CONFIG is a message level for static configuration messages.
	 * <p>
	 * CONFIG messages are intended to provide a variety of static configuration
	 * information, to assist in debugging problems that may be associated with
	 * particular configurations. For example, CONFIG message might include the
	 * OS architecture, the screen size, the GUI look-and-feel, etc. This level
	 * is initialized to <CODE>40</CODE>.
	 */
	CONFIG("CONFIG", 40),
	/**
	 * FINE is a message level providing tracing information.
	 * <p>
	 * All of FINE, FINER, and FINEST are intended for relatively detailed
	 * tracing. The exact meaning of the three levels will vary between
	 * subsystems, but in general, FINEST should be used for the most detailed
	 * output, FINER for somewhat less detailed output, and FINE for the lowest
	 * volume (and most important) messages.
	 * <p>
	 * In general the FINE level should be used for information that will be
	 * broadly interesting to developers who do not have a specialized interest
	 * in the specific subsystem.
	 * <p>
	 * FINE messages might include things like minor (recoverable) failures.
	 * Issues indicating potential performance problems are also worth logging
	 * as FINE. This level is initialized to <CODE>30</CODE>.
	 */
	FINE("FINE", 30),
	/**
	 * FINER indicates a fairly detailed tracing message. By default logging
	 * calls for entering, returning, or throwing an exception are traced at
	 * this level. This level is initialized to <CODE>20</CODE>.
	 */
	FINER("FINER", 20),
	/**
	 * FINEST indicates a highly detailed tracing message. This level is
	 * initialized to <CODE>10</CODE>.
	 */
	FINEST("FINEST", 10),
	/**
	 * ALL indicates that all messages should be logged. This should not be used
	 * as a value when constructing a message for logging. This level is
	 * initialized to <CODE>-10</CODE>.
	 */
	ALL("ALL", -10);

	private String name;
	private int value;

	// for caching the names
	private transient String localizedLevelName;
	private transient Locale cachedLocale;

	/**
	 * Constructs a named logging level with the given integer value.
	 * 
	 * @param name the name of the level
	 * @param value an integer value for the level
	 */
	private LoggingLevel(String name, int value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Returns the (non-localized) name of the logging level.
	 * 
	 * @return the non-localized name of the logging level
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Attempts to find the localized name for the logging level. If none
	 * exists, the default level will be returned.
	 * 
	 * @return the localized name of the logging level
	 */
	public String getLocalizedName() {
		return getLocalizedLevelName();
	}

	// Avoid looking up the localizedLevelName twice if we already have it
	/**
	 * This is from line 289 of {@link Level Level}
	 * 
	 * @see Level
	 * @return The cached name for this level
	 */
	private final String getCachedLocalizedLevelName() {

		if (localizedLevelName != null) {
			if (cachedLocale != null) {
				if (cachedLocale.equals(Localization.getLocale())) {
					// OK: our cached value was looked up with the same
					// locale. We can use it.
					return localizedLevelName;
				}
			}
		}

		if (ResourceLocation.LoggingLevel == null) {
			// No resource bundle; just use the default name.
			return name;
		}
		// We need to compute the localized name.
		// Either because it's the first time, or because our cached
		// value is for a different locale.
		return null;
	}

	// Avoid looking up the localizedLevelName twice if we already have it.
	/**
	 * This is from line 312 of {@link Level Level}
	 * 
	 * @see Level
	 * @return The localized version of the level name
	 */
	private final String getLocalizedLevelName() {

		// See if we have a cached localized name
		final String cachedLocalizedName = getCachedLocalizedLevelName();
		if (cachedLocalizedName != null) {
			return cachedLocalizedName;
		}

		// try to fetch the localized name
		try {
			localizedLevelName =
					ResourceBundle.getBundle(ResourceLocation.LoggingLevel,
							Localization.getLocale()).getString(name);
		}
		catch (Exception ex) {
			// if we fail, just use the default
			localizedLevelName = name;
		}
		cachedLocale = Localization.getLocale();
		return localizedLevelName;
	}

	/**
	 * Returns the integer value assigned to the level.
	 * 
	 * @return the integer value for this level
	 */
	public int intValue() {
		return this.value;
	}
}
