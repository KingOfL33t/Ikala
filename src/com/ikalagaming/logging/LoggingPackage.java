
package com.ikalagaming.logging;

import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import com.ikalagaming.core.Localization;
import com.ikalagaming.core.PackageManager;
import com.ikalagaming.core.ResourceLocation;
import com.ikalagaming.core.packages.Package;
import com.ikalagaming.event.Listener;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * Handles reporting and logging errors.
 * 
 * @author Ches Burks
 * 
 */
public class LoggingPackage implements Package {

	private ResourceBundle resourceBundle;
	private boolean enabled = false;
	private final double version = 0.1;
	private PackageManager packageManager;
	private String packageName = "logging";
	private LogDispatcher dispatcher;
	private String newLog = "";
	/**
	 * Only logs events that are of this level or higher
	 */
	private LoggingLevel threshold = LoggingLevel.ALL;

	/**
	 * Logs the provided error. Attempts to use localized names for the error
	 * code and logging level. This only logs errors that are above or equal to
	 * the threshold. The package name is listed before the info. <br>
	 * If the package is not enabled, simply logs straight to System.err
	 * 
	 * @param origin The package that is logging the error
	 * @param error The error that occurred
	 * @param level what level is the requested log
	 * @param details additional information about the error
	 */
	public void logError(Package origin, String error, LoggingLevel level,
			String details) {
		newLog = "";
		if (!enabled) {
			System.err.println(level.getName() + " " + error + " " + details);
			return;
		}
		if (level.intValue() < threshold.intValue()) {
			return;
		}

		newLog =
				SafeResourceLoader.getString("level_prefix", resourceBundle,
						"[")
						+ level.getLocalizedName()
						+ SafeResourceLoader.getString("level_postfix",
								resourceBundle, "]")
						+ " "
						+ SafeResourceLoader.getString("level_prefix",
								resourceBundle, "[")
						+ origin.getName()
						+ SafeResourceLoader.getString("level_postfix",
								resourceBundle, "]")
						+ " "
						+ error
						+ " "
						+ details;

		dispatcher.log(newLog);
	}

	/**
	 * Logs the provided error. Attempts to use localized names for the logging
	 * level. This only logs information that is above or equal to the logging
	 * threshold. <br>
	 * If the package is not enabled, simply logs straight to System.out
	 * 
	 * @param origin The package that is logging the info
	 * @param level what level is the requested log
	 * @param details what to log
	 */
	public void log(Package origin, LoggingLevel level, String details) {
		newLog = "";
		if (!enabled) {
			System.out.println(level.getName() + " " + details);
			return;
		}
		if (level.intValue() < threshold.intValue()) {
			return;
		}
		try {
			newLog =
					resourceBundle.getString("level_prefix")
							+ level.getLocalizedName()
							+ resourceBundle.getString("level_postfix")
							+ " "
							+ SafeResourceLoader.getString("level_prefix",
									resourceBundle, "[")
							+ origin.getName()
							+ SafeResourceLoader.getString("level_postfix",
									resourceBundle, "]") + " " + details;
		}
		catch (Exception e) {
			System.err.println(level.getName());
			System.err.println(details);
			e.printStackTrace(System.err);// we need to know what broke the log
		}
		dispatcher.log(newLog);
	}

	@Override
	public String getName() {
		return packageName;
	}

	@Override
	public double getVersion() {
		return version;
	}

	@Override
	public boolean enable() {
		this.enabled = true;
		try {
			this.onEnable();
		}
		catch (Exception e) {
			logError(this, "Package enable fail", LoggingLevel.SEVERE,
					"LoggingPackage.enable()");
			// better safe than sorry (probably did not initialize correctly)
			this.enabled = false;
			return false;
		}
		return true;
	}

	@Override
	public boolean disable() {
		this.enabled = false;
		try {
			this.onDisable();
		}
		catch (Exception e) {
			logError(this, "package disable fail", LoggingLevel.SEVERE,
					"LoggingPackage.enable()");
			return false;
		}
		return true;
	}

	@Override
	public boolean reload() {
		if (this.enabled) {
			this.disable();
		}
		this.enable();
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void onEnable() {}

	@Override
	public void onDisable() {}

	@Override
	public void onLoad() {
		try {
			resourceBundle =
					ResourceBundle.getBundle(ResourceLocation.LoggingPackage,
							Localization.getLocale());
		}
		catch (MissingResourceException missingResource) {
			logError(this, "locale not found", LoggingLevel.SEVERE,
					"LoggingPackage.onLoad()");
		}
		dispatcher = new LogDispatcher(this);
		dispatcher.start();
	}

	@Override
	public void onUnload() {
		this.resourceBundle = null;
		this.packageManager = null;
	}

	@Override
	public void setPackageManager(PackageManager parent) {
		this.packageManager = parent;
	}

	@Override
	public PackageManager getPackageManager() {
		return this.packageManager;
	}

	@Override
	public Set<Listener> getListeners() {
		return new HashSet<Listener>();
	}

}
