
package com.ikalagaming.logging;

import com.ikalagaming.core.Game;
import com.ikalagaming.core.packages.Package;

/**
 * Provides a reference to the logger package used to log info for a particular
 * package.
 * 
 * @deprecated use events instead
 * 
 * @author Ches Burks
 * 
 */
public class PackageLogger {

	private Package owner;

	/**
	 * Constructs a new logger for the given package. This logger logs messages
	 * using the package name.
	 * 
	 * @deprecated use events instead
	 * 
	 * @param logged the package to log messages for
	 */
	public PackageLogger(Package logged) {
		owner = logged;
	}

	/**
	 * This should not be called directly. Use {@link PackageLogger} to log
	 * errors. <br>
	 * Logs the provided error. Attempts to use localized names for the error
	 * code and logging level. This only logs errors that are above or equal to
	 * the threshold. The package name is listed before the info.
	 * 
	 * @deprecated use events instead
	 * 
	 * @param error The error that occurred
	 * @param level what level is the requested log
	 * @param details additional information about the error
	 */
	public void logError(String error, LoggingLevel level, String details) {
		if (Game.getPackageManager() == null) {
			System.err.println("Null PackageManager in PackageLogger.logError");
			System.err.println(level.getName() + " " + error + " " + details);
			return;
		}
		try {
			Game.getPackageManager().getLogger()
					.logError(owner, error, level, details);
		}
		catch (Exception e) {
			System.err.println(level.getName() + " " + error + " " + details);
			e.printStackTrace(System.err);
		}

	}

	/**
	 * This should not be called directly. Use {@link PackageLogger} to log
	 * info. <br>
	 * Logs the provided error. Attempts to use localized names for the logging
	 * level. This only logs information that is above or equal to the logging
	 * threshold.
	 * 
	 * @deprecated use events instead
	 * 
	 * @param level what level is the requested log
	 * @param details what to log
	 */
	public void log(LoggingLevel level, String details) {
		if (Game.getPackageManager() == null) {
			System.err.println("Null PackageManager in PackageLogger.log");
			System.err.println(level.getName() + " " + details);
			return;
		}
		try {
			Game.getPackageManager().getLogger().log(owner, level, details);
		}
		catch (Exception e) {
			System.err.println(level.getName() + " " + details);
			e.printStackTrace(System.err);
		}
	}

	protected void finalize() throws Throwable {
		owner = null;// clear reference for gc
		super.finalize();
	}
}
