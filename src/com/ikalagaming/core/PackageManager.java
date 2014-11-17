
package com.ikalagaming.core;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.jar.JarFile;

import com.ikalagaming.core.events.PackageEvent;
import com.ikalagaming.core.packages.Package;
import com.ikalagaming.core.packages.PackageSettings;
import com.ikalagaming.event.EventManager;
import com.ikalagaming.event.Listener;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.LoggingPackage;
import com.ikalagaming.logging.PackageLogger;
import com.ikalagaming.permissions.Permission;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * Handles loading, unloading and storage of Packages. This is considered a
 * package, but is never loaded.
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
	private HashMap<String, Permission> permissionMap;

	/**
	 * Constructs a new {@link PackageManager} and initializes variables.
	 *
	 * @param g the game that owns this package manager
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
		cmdRegistry = new CommandRegistry(this);
		registerCommands();
		logger = new PackageLogger(this);
		permissionMap = new HashMap<String, Permission>();
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
		logger.log(LoggingLevel.FINE, "Loading package " + toLoad.getName()
				+ " (V" + toLoad.getVersion() + ")" + "...");
		// if the package exists and is older than toLoad, unload
		if (isLoaded(toLoad)) {
			logger.log(LoggingLevel.FINE, "Package " + toLoad.getName()
					+ " is already loaded. (V" + toLoad.getVersion() + ")");
			if (loadedPackages.get(toLoad.getName()).getVersion() < toLoad
					.getVersion()) {
				unloadPackage(loadedPackages.get(toLoad.getName()));
				// unload the old package and continue loading the new one
			}
			else {
				logger.log(LoggingLevel.FINE, "Package " + toLoad.getName()
						+ " (V" + toLoad.getVersion() + ")" + " was outdated. "
						+ "Aborting.");
				return false;
			}
		}

		// store the new package
		loadedPackages.put(toLoad.getName(), toLoad);
		toLoad.setPackageManager(this);

		if (PackageSettings.USE_EVENTS_FOR_ACCESS) {
			if (isLoaded("event-manager")) {
				EventManager manager =
						(EventManager) getPackage("event-manager");
				if (manager.isEnabled()) {
					if (toLoad instanceof Listener) {
						manager.registerEventListeners((Listener) toLoad);
						logger.log(
								LoggingLevel.FINER,
								"Registered event listeners for "
										+ toLoad.getName());
					}
				}
			}
		}

		// load it
		if (PackageSettings.USE_EVENTS_FOR_ACCESS
				&& PackageSettings.USE_EVENTS_FOR_ON_LOAD) {
			String toSend = "";

			toSend =
					SafeResourceLoader.getString("CMD_CALL", resourceBundle,
							"call")
							+ " "
							+ SafeResourceLoader.getString("ARG_ON_LOAD",
									resourceBundle, "onLoad");

			if (isLoaded("event-manager")
					&& getPackage("event-manager").isEnabled()) {
				logger.log(LoggingLevel.FINER,
						"Calling onLoad of " + toLoad.getName()
								+ " using event system.");
				/*
				 * Tries to send the event. If the return value is false, it
				 * failed and therefore we must load manually
				 */
				if (!fireEvent(toLoad.getName(), toSend)) {
					logger.log(LoggingLevel.FINER, "Event failed, "
							+ "calling method directly.");
					toLoad.onLoad();
				}
			}
			else {
				logger.log(LoggingLevel.FINER,
						"Calling onLoad of " + toLoad.getName());
				// errors creating message so the event would not work
				toLoad.onLoad();
			}
		}
		else {
			logger.log(LoggingLevel.FINER,
					"Calling onLoad of " + toLoad.getName());
			// not using events for onload, or not using events at all
			toLoad.onLoad();
		}

		// enable the package
		if (PackageSettings.ENABLE_ON_LOAD) {
			if (PackageSettings.USE_EVENTS_FOR_ACCESS
					&& PackageSettings.USE_EVENTS_FOR_ENABLE) {
				String toSend = "";
				toSend =
						SafeResourceLoader.getString("CMD_CALL",
								resourceBundle, "call")
								+ " "
								+ SafeResourceLoader.getString("ARG_ENABLE",
										resourceBundle, "enable");

				if (isLoaded("event-manager")
						&& getPackage("event-manager").isEnabled()) {
					logger.log(LoggingLevel.FINER, "Calling enable of "
							+ toLoad.getName() + " using event system.");
					/*
					 * Tries to send the event. If the return value is false, it
					 * failed and therefore we must load manually
					 */
					if (!fireEvent(toLoad.getName(), toSend)) {
						logger.log(LoggingLevel.FINER, "Event failed, "
								+ "calling method directly.");
						toLoad.enable();
					}
				}
				else {
					logger.log(LoggingLevel.FINER, "Calling enable of "
							+ toLoad.getName());
					// errors creating message so the event would not work
					toLoad.enable();
				}
			}
			else {
				logger.log(LoggingLevel.FINER,
						"Calling enable of " + toLoad.getName());
				// not using events for enable, or not using events at all
				toLoad.enable();
			}
		}
		logger.log(LoggingLevel.FINE, "Package " + toLoad.getName() + " (V"
				+ toLoad.getVersion() + ")" + " loaded!");
		return true;
	}

	public boolean loadPlugin(String name) {
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

		ListIterator<File> iterator = files.listIterator();
		File tmp;
		while (iterator.hasNext()) {
			tmp = iterator.next();

		}
		return false;
	}

	private void getPluginDescription(File jarfile) {
		JarFile jar = null;
		InputStream stream = null;
		/*
		 * try { jar = new JarFile(jarfile); JarEntry entry =
		 * jar.getJarEntry("plugin.yml"); if (entry == null) { //TODO log error
		 * no plugin.yml jar.close(); } stream = jar.getInputStream(entry);
		 * return new PluginDescriptionFile(stream); } catch (IOException ex) {
		 * throw new InvalidDescriptionException(ex); } catch (YAMLException ex)
		 * { throw new InvalidDescriptionException(ex); } finally { if (jar !=
		 * null) { try { jar.close(); } catch (IOException e) { } } if (stream
		 * != null) { try { stream.close(); } catch (IOException e) { } } }
		 */
	}

	/**
	 * Fires an event with a message to a package type from the package manager.
	 * If an error occurs, this will return false. The event should not have
	 * been sent if false was returned.
	 *
	 * @param to the package to send the message to
	 * @param content the message to transfer
	 * @return true if the event was fired correctly
	 */
	private boolean fireEvent(String to, String content) {

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
	 * Returns the permission assigned to the given string. This is not case
	 * sensitive and my return null.
	 *
	 * @param name the name of the permission
	 * @return the pemission associated with the string
	 */
	public Permission getPermission(String name) {
		return permissionMap.get(name.toLowerCase());
	}

	/**
	 * Removes the permission assigned to the given string.
	 *
	 * @param name the name of the permission
	 */
	public void removePermission(String name) {
		if (permissionMap.containsKey(name)) {
			permissionMap.remove(name);
		}
	}

	/**
	 * Adds the permission to the list of permissions.
	 *
	 * @param perm the permission to add
	 */
	public void addPermission(Permission perm) {
		permissionMap.put(perm.getName(), perm);
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
		logger.log(LoggingLevel.FINE, "Unloading package " + toUnload + "...");
		if (!isLoaded(toUnload)) {
			logger.log(LoggingLevel.FINE, "Package " + toUnload
					+ " is not loaded. Aborting.");
			return false;
		}

		if (PackageSettings.DISABLE_ON_UNLOAD) {
			if (loadedPackages.get(toUnload).isEnabled()) {
				if (PackageSettings.USE_EVENTS_FOR_ACCESS
						&& PackageSettings.USE_EVENTS_FOR_DISABLE) {
					String toSend = "";

					toSend =
							SafeResourceLoader.getString("CMD_CALL",
									resourceBundle, "call")
									+ " "
									+ SafeResourceLoader.getString(
											"ARG_ON_DISABLE", resourceBundle,
											"onDisable");

					if (isLoaded("event-manager")
							&& getPackage("event-manager").isEnabled()) {
						logger.log(LoggingLevel.FINER, "Calling disable "
								+ "method of " + toUnload + " using events.");
						/*
						 * Tries to send the event. If the return value is
						 * false, it failed and therefore we must load manually
						 */
						if (!fireEvent(toUnload, toSend)) {
							logger.log(LoggingLevel.FINER, "Events failed, "
									+ "calling method instead.");
							loadedPackages.get(toUnload).disable();
						}
					}
					else {
						logger.log(LoggingLevel.FINER, "Calling disable "
								+ "method of " + toUnload);
						// errors creating message so the event would not work
						loadedPackages.get(toUnload).disable();
					}
				}
				else {
					logger.log(LoggingLevel.FINER, "Calling disable "
							+ "method of " + toUnload);
					loadedPackages.get(toUnload).disable();
				}
			}
		}

		if (PackageSettings.USE_EVENTS_FOR_ACCESS
				&& PackageSettings.USE_EVENTS_FOR_ON_UNLOAD) {
			String toSend = "";

			toSend =
					SafeResourceLoader.getString("CMD_CALL", resourceBundle,
							"call")
							+ " "
							+ SafeResourceLoader.getString("ARG_UNLOAD",
									resourceBundle, "unload");

			if (isLoaded("event-manager")
					&& getPackage("event-manager").isEnabled()) {
				logger.log(LoggingLevel.FINER, "Calling onUnload "
						+ "method of " + toUnload + " using events.");
				/*
				 * Tries to send the event. If the return value is false, it
				 * failed and therefore we must load manually
				 */
				if (!fireEvent(toUnload, toSend)) {
					logger.log(LoggingLevel.FINER, "Events failed, "
							+ "calling method instead.");
					loadedPackages.get(toUnload).onUnload();
				}
			}
			else {
				logger.log(LoggingLevel.FINER, "Calling onUnload "
						+ "method of " + toUnload);
				// errors creating message so the event would not work
				loadedPackages.get(toUnload).onUnload();
			}
		}
		else {
			logger.log(LoggingLevel.FINER, "Calling onUnload " + "method of "
					+ toUnload);
			loadedPackages.get(toUnload).onUnload();
		}

		loadedPackages.remove(toUnload);

		logger.log(LoggingLevel.FINE, "Package " + toUnload + " unloaded!");
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
			logger.log(LoggingLevel.FINE, "Package " + toUnload.getName()
					+ " is not loaded. Aborting.");
			return;
		}
		unloadPackage(type);
	}

	/**
	 * Returns the logger for the system. If one does not exist, it will be
	 * created.
	 *
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

	@Override
	public boolean disable() {
		return false;
	}

	@Override
	public boolean enable() {
		return false;
	}

	@Override
	public PackageManager getPackageManager() {
		return this;
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
	public void setPackageManager(PackageManager parent) {}
}
