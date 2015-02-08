
package com.ikalagaming.logging;

import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import com.ikalagaming.core.Localization;
import com.ikalagaming.core.ResourceLocation;
import com.ikalagaming.core.packages.Package;
import com.ikalagaming.core.packages.PackageSettings;
import com.ikalagaming.core.packages.PackageState;
import com.ikalagaming.event.EventHandler;
import com.ikalagaming.event.Listener;
import com.ikalagaming.logging.events.Log;
import com.ikalagaming.logging.events.LogError;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * Handles reporting and logging errors.
 * 
 * @author Ches Burks
 * 
 */
public class LoggingPackage implements Package, Listener {

	private ResourceBundle resourceBundle;
	private PackageState state = PackageState.DISABLED;
	private final double version = 0.1;
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
	private void logError(String origin, String error, LoggingLevel level,
			String details) {
		newLog = "";
		if (!isEnabled()) {
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
						+ origin
						+ SafeResourceLoader.getString("level_postfix",
								resourceBundle, "]")
						+ " "
						+ error
						+ " "
						+ details;

		dispatcher.log(newLog);
	}

	/**
	 * Logs the provided information. Attempts to use localized names for the
	 * logging level. This only logs information that is above or equal to the
	 * logging threshold. <br>
	 * If the package is not enabled, simply logs straight to System.out
	 * 
	 * @param origin The package that is logging the info
	 * @param level what level is the requested log
	 * @param details what to log
	 */
	private void log(String origin, LoggingLevel level, String details) {
		newLog = "";
		if (!isEnabled()) {
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
							+ origin
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
		state = PackageState.ENABLING;
		try {
			this.onEnable();
		}
		catch (Exception e) {
			// TODO localize this
			logError(packageName, "Package enable fail", LoggingLevel.SEVERE,
					"LoggingPackage.enable()");
			// better safe than sorry (probably did not initialize correctly)
			state = PackageState.CORRUPTED;
			return false;
		}
		return true;
	}

	@Override
	public boolean disable() {
		state = PackageState.DISABLING;
		try {
			this.onDisable();
		}
		catch (Exception e) {
			// TODO localize this
			logError(packageName, "package disable fail", LoggingLevel.SEVERE,
					"LoggingPackage.enable()");
			state = PackageState.CORRUPTED;
			return false;
		}
		return true;
	}

	@Override
	public boolean reload() {
		state = PackageState.UNLOADING;
		if (!PackageSettings.DISABLE_ON_UNLOAD) {
			disable();
		}
		if (state == PackageState.ENABLED) {
			disable();
		}
		this.resourceBundle = null;
		state = PackageState.LOADING;
		onLoad();
		enable();// it will start up enabled
		return true;
	}

	@Override
	public boolean isEnabled() {
		if (state == PackageState.ENABLED) {
			return true;
		}
		return false;
	}

	@Override
	public void onEnable() {
		state = PackageState.ENABLED;
	}

	@Override
	public void onDisable() {
		state = PackageState.DISABLED;
	}

	@Override
	public void onLoad() {
		state = PackageState.LOADING;
		try {
			resourceBundle =
					ResourceBundle.getBundle(ResourceLocation.LoggingPackage,
							Localization.getLocale());
		}
		catch (MissingResourceException missingResource) {
			// TODO Attempt to localize this somehow
			logError(packageName, "locale not found", LoggingLevel.SEVERE,
					"LoggingPackage.onLoad()");
		}
		dispatcher = new LogDispatcher(this);
		dispatcher.start();
		state = PackageState.DISABLED;
	}

	@Override
	public void onUnload() {
		state = PackageState.UNLOADING;
		this.resourceBundle = null;
		state = PackageState.PENDING_REMOVAL;
	}

	@Override
	public Set<Listener> getListeners() {
		return new HashSet<Listener>();
	}

	@Override
	public PackageState getPackageState() {
		return state;
	}

	/**
	 * Logs the provided information. Attempts to use localized names for the
	 * logging level. This only logs information that is above or equal to the
	 * logging threshold. <br>
	 * If the package is not enabled, simply logs straight to System.out
	 * 
	 * @param event the Event to record
	 */
	@EventHandler
	public void onLogEvent(Log event) {
		log(event.getSender(), event.getLevel(), event.getDetails());
	}

	/**
	 * Logs the provided error. Attempts to use localized names for the error
	 * code and logging level. This only logs errors that are above or equal to
	 * the threshold. The package name is listed before the info. <br>
	 * If the package is not enabled, simply logs straight to System.err
	 * 
	 * @param event the Event to record
	 */
	@EventHandler
	public void onLogErrorEvent(LogError event) {
		logError(event.getSender(), event.getError(), event.getLevel(),
				event.getDetails());
	}

}
