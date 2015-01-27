
package com.ikalagaming.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import com.ikalagaming.core.events.PackageEvent;
import com.ikalagaming.core.packages.Package;
import com.ikalagaming.core.packages.PackageSettings;
import com.ikalagaming.core.packages.PackageState;
import com.ikalagaming.event.Event;
import com.ikalagaming.event.EventManager;
import com.ikalagaming.event.Listener;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.LoggingPackage;
import com.ikalagaming.logging.PackageLogger;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * Handles loading, unloading and storage of Packages. This is considered a
 * package, but is always enabled and never loaded.
 * 
 * @author Ches Burks
 * 
 */
public class PackageManager implements Package {

	private ResourceBundle resourceBundle;

	/** maps strings to packages loaded in memory */
	private HashMap<String, Package> loadedPackages;
	private String packageName = "package-manager";
	private CommandRegistry cmdRegistry;
	private PackageLogger logger;
	private PMEventListener listener;
	private PackageState state = PackageState.ENABLED;

	/**
	 * Constructs a new {@link PackageManager} and initializes variables.
	 */
	public PackageManager() {
		loadedPackages = new HashMap<String, Package>();
		try {
			resourceBundle =
					ResourceBundle.getBundle(ResourceLocation.PackageManager,
							Localization.getLocale());
		}
		catch (MissingResourceException e) {
			e.printStackTrace(System.err);
		}

	}

	/**
	 * Registers commands with the registry that the package manager uses
	 */
	private void registerCommands() {
		ArrayList<String> commands = new ArrayList<String>();
		commands.add("COMMAND_ENABLE");
		commands.add("COMMAND_DISABLE");
		commands.add("COMMAND_LOAD");
		commands.add("COMMAND_UNLOAD");
		commands.add("COMMAND_LIST_PACKAGES");
		commands.add("COMMAND_RELOAD");
		commands.add("COMMAND_HELP");
		commands.add("COMMAND_VERSION");

		String tmp = "";

		for (String s : commands) {

			tmp = SafeResourceLoader.getString(s, resourceBundle, s);
			cmdRegistry.registerCommand(tmp, this);
		}

	}

	/**
	 * Returns the {@link CommandRegistry} for this package manager.
	 * 
	 * @return the command registry
	 */
	public CommandRegistry getCommandRegistry() {
		if (cmdRegistry == null) {
			cmdRegistry = new CommandRegistry(this);
		}
		return cmdRegistry;
	}

	/**
	 * Returns the resource bundle for the package manager. This is not safe and
	 * could be null.
	 * 
	 * @return the current resource bundle
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/**
	 * <p>
	 * Loads the given package into memory, stores it by type, and enables it if
	 * packages are
	 * {@link com.ikalagaming.core.packages.PackageSettings#ENABLE_ON_LOAD
	 * enabled on load} by default.
	 * </p>
	 * <p>
	 * If the type of package already exists in the manager, and the new package
	 * has a higher version number, then the old package is unloaded and the new
	 * one is loaded in its place. If the versions are equal, or the new package
	 * is older, then it does not load the new version and returns false.
	 * </p>
	 * 
	 * @param toLoad the package to load
	 * @return true if the package was loaded properly, false otherwise
	 */
	public boolean loadPackage(Package toLoad) {
		String loading =
				SafeResourceLoader.getString("ALERT_PACKAGE_LOADING",
						resourceBundle,
						"Loading package $PACKAGE (v$VERSION)...");
		loading = loading.replaceFirst("\\$PACKAGE", toLoad.getName());
		loading = loading.replaceFirst("\\$VERSION", "" + toLoad.getVersion());
		logger.log(LoggingLevel.FINE, loading);
		// if the package exists and is older than toLoad, unload
		if (isLoaded(toLoad)) {
			String alreadyLoaded =
					SafeResourceLoader.getString(
							"ALERT_PACKAGE_ALREADY_LOADED", resourceBundle,
							"Package $PACKAGE is already loaded. (v$VERSION)");
			alreadyLoaded =
					alreadyLoaded.replaceFirst("\\$PACKAGE", toLoad.getName());
			alreadyLoaded =
					alreadyLoaded.replaceFirst("\\$VERSION",
							"" + toLoad.getVersion());
			logger.log(LoggingLevel.FINE, alreadyLoaded);
			if (loadedPackages.get(toLoad.getName()).getVersion() < toLoad
					.getVersion()) {
				unloadPackage(loadedPackages.get(toLoad.getName()));
				// unload the old package and continue loading the new one
			}
			else {
				String outdated =
						SafeResourceLoader.getString("ALERT_PACKAGE_OUTDATED",
								resourceBundle, "Package $PACKAGE (v$VERSION) "
										+ "was outdated. Aborting.");
				outdated =
						outdated.replaceFirst("\\$PACKAGE", toLoad.getName());
				outdated =
						outdated.replaceFirst("\\$VERSION",
								"" + toLoad.getVersion());
				logger.log(LoggingLevel.FINE, outdated);
				return false;
			}
		}

		// store the new package
		loadedPackages.put(toLoad.getName(), toLoad);

		if (PackageSettings.USE_EVENTS_FOR_ACCESS) {
			if (isLoaded("event-manager")) {
				EventManager manager =
						(EventManager) getPackage("event-manager");
				if (manager.isEnabled()) {
					for (Listener l : toLoad.getListeners()) {
						manager.registerEventListeners(l);
					}
					logger.log(
							LoggingLevel.FINER,
							SafeResourceLoader.getString(
									"ALERT_REG_EVENT_LISTENERS",
									resourceBundle,
									"Registered event listeners for $PACKAGE")
									.replaceFirst("\\$PACKAGE",
											toLoad.getName()));
				}
			}
		}

		// load it
		if (PackageSettings.USE_EVENTS_FOR_ACCESS
				&& PackageSettings.USE_EVENTS_FOR_ON_LOAD) {
			changeState(toLoad, "load", true);
		}
		else {
			changeState(toLoad, "load", false);
		}

		// enable the package
		if (PackageSettings.ENABLE_ON_LOAD) {
			if (PackageSettings.USE_EVENTS_FOR_ACCESS
					&& PackageSettings.USE_EVENTS_FOR_ENABLE) {
				changeState(toLoad, "enable", true);
			}
			else {
				changeState(toLoad, "enable", false);
			}
		}

		String loaded =
				SafeResourceLoader.getString("ALERT_PACKAGE_LOADED",
						resourceBundle, "Package $PACKAGE (v$VERSION) loaded!");
		loaded = loaded.replaceFirst("\\$PACKAGE", toLoad.getName());
		loaded = loaded.replaceFirst("\\$VERSION", "" + toLoad.getVersion());
		logger.log(LoggingLevel.FINE, loaded);

		if (toLoad.getName() == "event-manager") {
			EventManager manager = (EventManager) getPackage("event-manager");
			if (manager.isEnabled()) {
				for (Listener l : getListeners()) {
					manager.registerEventListeners(l);
				}
				logger.log(
						LoggingLevel.FINER,
						SafeResourceLoader.getString(
								"ALERT_REG_EVENT_LISTENERS", resourceBundle,
								"Registered event listeners for $PACKAGE")
								.replaceFirst("\\$PACKAGE", getName()));
			}
		}
		return true;
	}

	/**
	 * Logs a call to the given method of the given package. Uses the
	 * ALERT_CALL_METHOD_EVENT or ALERT_CALL_METHOD_DIRECT depending on whether
	 * usingEvents is true or false.
	 * 
	 * @param method the method being called
	 * @param pack the package the method belongs to
	 * @param usingEvents true if it is using the event system, false if direct
	 */
	private void logMethodCall(String method, String pack, boolean usingEvents) {
		String call;
		if (usingEvents) {
			call =
					SafeResourceLoader.getString("ALERT_CALL_METHOD_EVENT",
							resourceBundle,
							"Calling $METHOD of $PACKAGE using event system");
		}
		else {
			call =
					SafeResourceLoader.getString("ALERT_CALL_METHOD_DIRECT",
							resourceBundle,
							"Calling $METHOD of $PACKAGE directly");
		}
		call = call.replaceFirst("\\$METHOD", method);
		call = call.replaceFirst("\\$PACKAGE", pack);

		logger.log(LoggingLevel.FINER, call);
	}

	/**
	 * Change the state of a given package. The operations allowed are
	 * <ul>
	 * <li>load</li>
	 * <li>enable</li>
	 * <li>disable</li>
	 * <li>unload</li>
	 * </ul>
	 * If events fail and should be used, reverts to direct calling of methods.
	 * 
	 * @param toChange the package to change
	 * @param operation what you want to do to the package
	 * @param usingEvents true if you want to use events, otherwise false
	 */
	private void changeState(Package toChange, String operation,
			boolean usingEvents) {
		String toSend = "";
		String localMethodName = "";
		String backupMethodName = "";
		boolean callDirectly = usingEvents;
		if (operation == "load") {
			localMethodName = "ARG_ON_LOAD";
			backupMethodName = "onLoad";
		}
		else if (operation == "enable") {
			localMethodName = "ARG_ENABLE";
			backupMethodName = "enable";
		}
		else if (operation == "disable") {
			localMethodName = "ARG_DISABLE";
			backupMethodName = "disable";
		}
		else if (operation == "onUnload") {
			localMethodName = "ARG_ON_UNLOAD";
			backupMethodName = "onUnload";
		}

		else {
			localMethodName = "";
			backupMethodName = "";
		}

		if (!callDirectly) {
			toSend =
					SafeResourceLoader.getString("CMD_CALL", resourceBundle,
							"call")
							+ " "
							+ SafeResourceLoader.getString(localMethodName,
									resourceBundle, backupMethodName);

			if (isLoaded("event-manager")
					&& getPackage("event-manager").isEnabled()) {
				logMethodCall(backupMethodName, toChange.getName(), true);
				/*
				 * Tries to send the event. If the return value is false, it
				 * failed and therefore we must load manually
				 */
				if (!firePackageEvent(toChange.getName(), toSend)) {
					logger.log(LoggingLevel.FINER, SafeResourceLoader
							.getString("ALERT_CALL_EVENT_FAILED",
									resourceBundle,
									"Event failed. Calling method directly"));
					callDirectly = true;
				}
			}
			else {
				callDirectly = true;
			}
		}
		// not an else, in case callDirectly was set earlier
		if (callDirectly) {
			logMethodCall(backupMethodName, toChange.getName(), false);
			if (operation == "load") {
				toChange.onLoad();
			}
			else if (operation == "enable") {
				toChange.enable();
			}
			else if (operation == "disable") {
				toChange.disable();
			}
			else if (operation == "unload") {
				toChange.onUnload();
			}
		}
	}

	/**
	 * Loads a plugin from a name
	 * 
	 * @param name the filename to load from
	 * @return true on success, false if it failed
	 */
	public boolean loadPlugin(String name) {
		// TODO javadoc
		// TODO load a package from file
		/*
		 * Check for being a jar file check for package info file load and check
		 * for valid info load the file if necessary
		 */
		File pluginFolder = Game.getPluginFolder();
		if (!pluginFolder.exists()) {
			// TODO log error
			return false;
		}
		if (!pluginFolder.isDirectory()) {
			// TODO log error
			return false;
		}
		String[] filenames;
		filenames = pluginFolder.list();
		if (filenames == null) {
			// TODO log error
			return false;
		}
		if (filenames.length == 0) {
			// empty
			// TODO log error
			return false;
		}
		filenames = null;

		ArrayList<File> files = new ArrayList<File>();

		// adds valid jar files to the list of files
		for (File f : pluginFolder.listFiles()) {
			if (f.isDirectory()) {
				continue;// its a folder
			}
			if (!f.getName().toLowerCase().endsWith(".jar")) {
				continue;// its not a jar file
			}
			files.add(f);
		}

		if (files.size() == 0) {
			// TODO log error
			return false;
		}

		/*
		 * ListIterator<File> iterator = files.listIterator(); File tmp; while
		 * (iterator.hasNext()) { tmp = iterator.next(); }
		 */
		return false;
	}

	/*
	 * private void getPluginDescription(File jarfile) { JarFile jar = null;
	 * InputStream stream = null;
	 * 
	 * try { jar = new JarFile(jarfile); JarEntry entry =
	 * jar.getJarEntry("plugin.yml"); if (entry == null) { //TODO log error no
	 * plugin.yml jar.close(); } stream = jar.getInputStream(entry); return new
	 * PluginDescriptionFile(stream); } catch (IOException ex) { throw new
	 * InvalidDescriptionException(ex); } catch (YAMLException ex) { throw new
	 * InvalidDescriptionException(ex); } finally { if (jar != null) { try {
	 * jar.close(); } catch (IOException e) { } } if (stream != null) { try {
	 * stream.close(); } catch (IOException e) { } } }
	 * 
	 * }
	 */

	/**
	 * Fires an event with a message to a package type from the package manager.
	 * If an error occurs, this will return false. The event should not have
	 * been sent if false was returned.
	 * 
	 * @param to the package to send the message to
	 * @param content the message to transfer
	 * @return true if the event was fired correctly
	 */
	private boolean firePackageEvent(String to, String content) {

		if (!isLoaded("event-manager")) {
			logger.logError(SafeResourceLoader.getString("package_not_loaded",
					resourceBundle, "Package not loaded"),
					LoggingLevel.WARNING, to);
			return false;
		}

		if (!getPackage("event-manager").isEnabled()) {
			logger.logError(SafeResourceLoader.getString("package_not_enabled",
					resourceBundle, "Package not enabled"),
					LoggingLevel.WARNING, to);
			return false;
		}

		PackageEvent tmpEvent;

		tmpEvent = new PackageEvent(packageName, to, content);

		if (tmpEvent != null) {// just in case the assignment failed
			((EventManager) getPackage("event-manager")).fireEvent(tmpEvent);

		}

		return true;
	}

	/**
	 * Fires an event with a message to a package type from the package manager.
	 * If an error occurs, this will return false. The event should not have
	 * been sent if false was returned.
	 * 
	 * @param event the event to fire
	 * 
	 * @return true if the event was fired correctly
	 */
	public boolean fireEvent(Event event) {
		if (!isLoaded("event-manager")) {
			logger.logError(SafeResourceLoader.getString("package_not_loaded",
					resourceBundle, "Package not loaded"),
					LoggingLevel.WARNING, "event-manager");
			return false;
		}

		if (!getPackage("event-manager").isEnabled()) {
			logger.logError(SafeResourceLoader.getString("package_not_enabled",
					resourceBundle, "Package not enabled"),
					LoggingLevel.WARNING, "event-manager");
			return false;
		}

		if (event != null) {// just in case the assignment failed
			((EventManager) getPackage("event-manager")).fireEvent(event);

		}

		return true;
	}

	/**
	 * Returns true if a package exists with the given type (for example:
	 * "Graphics")'
	 * 
	 * @param type the package type
	 * @return true if the package is loaded in memory, false if it does not
	 *         exist
	 */
	public boolean isLoaded(String type) {
		return loadedPackages.containsKey(type);
	}

	/**
	 * Returns true if a package exists that has the same type as the provided
	 * package (for example: "Graphics"). This is the same as calling
	 * <code>{@link #isLoaded(String) isLoaded}(Package.getType())</code>
	 * 
	 * @param type the package type
	 * @return true if the package is loaded in memory, false if it does not
	 *         exist
	 */
	public boolean isLoaded(Package type) {
		return loadedPackages.containsKey(type.getName());
	}

	/**
	 * If a package of type exists ({@link #isLoaded(String)}), then the package
	 * that is of that type is returned. If no package exists of that type, null
	 * is returned.
	 * 
	 * @param type The package type
	 * @return the Package with the given type or null if none exists
	 */
	public Package getPackage(String type) {
		if (isLoaded(type)) {
			return loadedPackages.get(type);
		}
		else {
			return null;
		}
	}

	/**
	 * Attempts to unload the package from memory. If no package exists with the
	 * given name ({@link #isLoaded(String)}), returns false and does nothing.
	 * 
	 * @param toUnload The type of package to unload
	 * @return true if the package was unloaded properly
	 */
	public boolean unloadPackage(String toUnload) {
		String unloading =
				SafeResourceLoader.getString("ALERT_PACKAGE_UNLOADING",
						resourceBundle, "Unloading package $PACKAGE...");
		unloading = unloading.replaceFirst("\\$PACKAGE", toUnload);
		logger.log(LoggingLevel.FINE, unloading);
		if (!isLoaded(toUnload)) {
			logger.log(LoggingLevel.FINE, SafeResourceLoader.getString(
					"package_not_loaded", resourceBundle, "Package not loaded"));
			return false;
		}

		if (PackageSettings.DISABLE_ON_UNLOAD) {
			if (loadedPackages.get(toUnload).isEnabled()) {
				if (PackageSettings.USE_EVENTS_FOR_ACCESS
						&& PackageSettings.USE_EVENTS_FOR_DISABLE) {
					changeState(loadedPackages.get(toUnload), "disable", true);
				}
				else {
					changeState(loadedPackages.get(toUnload), "disable", false);
				}
			}
		}

		if (PackageSettings.USE_EVENTS_FOR_ACCESS
				&& PackageSettings.USE_EVENTS_FOR_ON_UNLOAD) {
			changeState(loadedPackages.get(toUnload), "unload", true);
		}
		else {
			changeState(loadedPackages.get(toUnload), "unload", false);
		}

		if (isLoaded("event-manager")) {
			EventManager manager = (EventManager) getPackage("event-manager");
			if (manager.isEnabled()) {
				for (Listener l : loadedPackages.get(toUnload).getListeners()) {
					manager.unregisterEventListeners(l);
				}
				logger.log(
						LoggingLevel.FINER,
						SafeResourceLoader.getString(
								"ALERT_UNREG_EVENT_LISTENERS", resourceBundle,
								"Unregistered event listeners for $PACKAGE")
								.replaceFirst("\\$PACKAGE", getName()));
			}
		}
		loadedPackages.remove(toUnload);

		String unloaded =
				SafeResourceLoader.getString("ALERT_PACKAGE_UNLOADED",
						resourceBundle, "Package $PACKAGE unloaded!");
		unloaded = unloaded.replaceFirst("\\$PACKAGE", toUnload);
		logger.log(LoggingLevel.FINE, unloaded);

		return true;
	}

	/**
	 * Attempts to unload the package from memory. Does nothing if the package
	 * is not loaded. Packages are disabled before unloading. This calls
	 * {@link #unloadPackage(String)} using the package type.
	 * 
	 * @param toUnload The type of package to unload
	 */
	public void unloadPackage(Package toUnload) {
		/*
		 * using a string the packages type to ensure the package that is stored
		 * in this class is modified and not just the package passed to the
		 * method.
		 */
		String type = toUnload.getName();
		if (!isLoaded(type)) {
			logger.log(LoggingLevel.FINE, SafeResourceLoader.getString(
					"package_not_loaded", resourceBundle, "Package not loaded"));
			return;
		}
		unloadPackage(type);
	}

	/**
	 * Returns the logger for the system. If one does not exist, it will be
	 * created.
	 * 
	 * @deprecated Use events to log
	 * @return a logger for the engine
	 */
	public LoggingPackage getLogger() {
		String loggingPackageName = "logging";

		if (!loadedPackages.containsKey(loggingPackageName)) {
			LoggingPackage pack = new LoggingPackage();
			// store the new package
			loadedPackages.put(loggingPackageName, pack);
			// we don't know if there is an event system.
			// this has to work properly
			pack.onLoad();
			// enable the package
			pack.enable();

		}
		// safe cast since we know its a LoggingPackage
		return (LoggingPackage) loadedPackages.get(loggingPackageName);

	}

	/**
	 * Returns the map of package name to package of the currently loaded
	 * packages.
	 * 
	 * @return the package map
	 */
	public HashMap<String, Package> getLoadedPackages() {
		return loadedPackages;
	}

	@Override
	public boolean disable() {
		return false;
	}

	@Override
	public boolean enable() {
		cmdRegistry = new CommandRegistry(this);
		logger = new PackageLogger(this);
		listener = new PMEventListener(this);
		registerCommands();
		return true;
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
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}

	@Override
	public void onLoad() {}

	@Override
	public void onUnload() {}

	@Override
	public boolean reload() {
		return false;
	}

	@Override
	public Set<Listener> getListeners() {
		HashSet<Listener> listeners = new HashSet<Listener>();
		listeners.add(listener);
		return listeners;
	}

	@Override
	public PackageState getPackageState() {
		return state;
	}
}
