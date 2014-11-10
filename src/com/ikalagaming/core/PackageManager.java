package com.ikalagaming.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.ikalagaming.core.events.PackageEvent;
import com.ikalagaming.event.EventManager;
import com.ikalagaming.event.Listener;
import com.ikalagaming.logging.ErrorCode;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.LoggingPackage;

/**
 * Handles loading, unloading and storage of Packages.
 * This is considered a package, but is never loaded.
 * @author Ches Burks
 *
 */
public class PackageManager implements Package{

	private ResourceBundle resourceBundle;

	/**maps strings to packages loaded in memory*/
	private HashMap<String, Package> loadedPackages;
	private String packageName = "package-manager";
	private CommandRegistry cmdRegistry;

	/**
	 * Constructs a new {@link PackageManager} and initializes variables.
	 */
	public PackageManager() {
		loadedPackages = new HashMap<String, Package>();
		try{
			resourceBundle = ResourceBundle.getBundle(
					ResourceLocation.PackageManager,
					Localization.getLocale());
		}
		catch(MissingResourceException e){
			e.printStackTrace(System.err);
		}
		cmdRegistry = new CommandRegistry(this);
		registerCommands();
	}

	/**
	 * Registers commands with the registry that
	 * the package manager uses
	 */
	private void registerCommands(){
		ArrayList<String> commands = new ArrayList<String>();
		commands.add("COMMAND_ENABLE");
		commands.add("COMMAND_DISABLE");
		commands.add("COMMAND_LOAD");
		commands.add("COMMAND_UNLOAD");
		commands.add("COMMAND_LIST_PACKAGES");
		commands.add("COMMAND_RELOAD");
		commands.add("COMMAND_HELP");

		String tmp = "";
		try {
			for (String s : commands){
				tmp = resourceBundle.getString(s);
				cmdRegistry.registerCommand(tmp, this);
			}
		}
		catch (MissingResourceException missingResource){
			getLogger().logError(ErrorCode.LOCALE_RESOURCE_NOT_FOUND,
					LoggingLevel.WARNING,
					"PackageManager.loadPackage(Package) load");
		}
		catch (ClassCastException classCast){
			getLogger().logError(ErrorCode.LOCALE_RESOURCE_WRONG_TYPE,
					LoggingLevel.WARNING,
					"PackageManager.loadPackage(Package) load");
		}
	}

	/**
	 * Returns the {@link CommandRegistry} for this package
	 * manager.
	 * @return the command registry
	 */
	public CommandRegistry getCommandRegistry(){
		if (cmdRegistry == null){
			cmdRegistry = new CommandRegistry(this);
		}
		return cmdRegistry;
	}
	/**
	 * Returns the resource bundle for the package manager. This
	 * is not safe and could be null.
	 * @return the current resource bundle
	 */
	public ResourceBundle getResourceBundle(){
		return resourceBundle;
	}

	/**
	 * <p>Loads the given package into memory, stores it by type, and
	 * enables it if packages are
	 * {@link com.ikalagaming.core.PackageSettings#ENABLE_ON_LOAD
	 * enabled on load} by default.</p>
	 * <p> If the type of package already exists in the manager,
	 * and the new package has a higher version number, then the old package
	 * is unloaded and the new one is loaded in its place. If the versions
	 * are equal, or the new package is older, then it does not load the new
	 * version and returns false.
	 * </p>
	 * @param toLoad the package to load
	 * @return true if the package was loaded properly, false otherwise
	 */
	public boolean loadPackage(Package toLoad){
		getLogger().log(LoggingLevel.FINE,
				"Loading package " + toLoad.getType()
				+ " (V"+toLoad.getVersion()+ ")"+"...");
		//if the package exists and is older than toLoad, unload
		if (isLoaded(toLoad)){
			getLogger().log(LoggingLevel.FINE, "Package " + toLoad.getType()
					+ " is already loaded. (V" + toLoad.getVersion() + ")");
			if (loadedPackages.get(toLoad.getType()).getVersion()
					< toLoad.getVersion()){
				unloadPackage(loadedPackages.get(toLoad.getType()));
				//unload the old package and continue loading the new one
			}
			else{
				getLogger().log(LoggingLevel.FINE,
						"Package " + toLoad.getType()
						+ " (V"+toLoad.getVersion()+ ")"+" was outdated. "
						+ "Aborting.");
				return false;
			}
		}

		//store the new package
		loadedPackages.put(toLoad.getType(), toLoad);
		toLoad.setPackageManager(this);

		if (PackageSettings.USE_EVENTS_FOR_ACCESS){
			if (isLoaded("event-manager")){
				EventManager manager =
						(EventManager)getPackage("event-manager");
				if (manager.isEnabled()){
					try {
						if (toLoad instanceof Listener){
							manager.registerEventListeners((Listener)toLoad);
							getLogger().log(
									LoggingLevel.FINER,
									"Registered event listeners for " +
											toLoad.getType());
						}
					} catch (Exception e) {
						getLogger().logError(ErrorCode.EXCEPTION,
								LoggingLevel.WARNING, "Console.onLoad()");
					}
				}
			}
		}

		//load it
		if (PackageSettings.USE_EVENTS_FOR_ACCESS
				&& PackageSettings.USE_EVENTS_FOR_ON_LOAD){
			String toSend = "";
			boolean messageValid = true;
			try {
				toSend = resourceBundle.getString("CMD_CALL")
						+ " "
						+ resourceBundle.getString("ARG_ON_LOAD");
			}
			catch (MissingResourceException missingResource){
				getLogger().logError(ErrorCode.LOCALE_RESOURCE_NOT_FOUND,
						LoggingLevel.WARNING,
						"PackageManager.loadPackage(Package) load");
				messageValid = false;
			}
			catch (ClassCastException classCast){
				getLogger().logError(ErrorCode.LOCALE_RESOURCE_WRONG_TYPE,
						LoggingLevel.WARNING,
						"PackageManager.loadPackage(Package) load");
				messageValid = false;
			}

			if (messageValid
					&& isLoaded("event-manager")
					&& getPackage("event-manager").isEnabled()){
				getLogger().log(LoggingLevel.FINER, "Calling onLoad of "
						+ toLoad.getType()
						+ " using event system.");
				/*
				 * Tries to send the event. If the return value is false,
				 * it failed and therefore we must load manually
				 */
				if (!fireEvent(toLoad.getType(), toSend)){
					getLogger().log(LoggingLevel.FINER, "Event failed, "
							+ "calling method directly.");
					toLoad.onLoad();
				}
			}
			else {
				getLogger().log(LoggingLevel.FINER, "Calling onLoad of "
						+ toLoad.getType());
				//errors creating message so the event would not work
				toLoad.onLoad();
			}
		}
		else{
			getLogger().log(LoggingLevel.FINER, "Calling onLoad of "
					+ toLoad.getType());
			//not using events for onload, or not using events at all
			toLoad.onLoad();
		}

		//enable the package
		if (PackageSettings.ENABLE_ON_LOAD){
			if (PackageSettings.USE_EVENTS_FOR_ACCESS
					&& PackageSettings.USE_EVENTS_FOR_ENABLE){
				String toSend = "";
				boolean messageValid = true;
				try {
					toSend = resourceBundle.getString("CMD_CALL")
							+ " "
							+ resourceBundle.getString("ARG_ENABLE");
				}
				catch (MissingResourceException missingResource){
					getLogger().logError(ErrorCode.LOCALE_RESOURCE_NOT_FOUND,
							LoggingLevel.WARNING,
							"PackageManager.loadPackage(Package) enable");
					messageValid = false;
				}
				catch (ClassCastException classCast){
					getLogger().logError(ErrorCode.LOCALE_RESOURCE_WRONG_TYPE,
							LoggingLevel.WARNING,
							"PackageManager.loadPackage(Package) enable");
					messageValid = false;
				}

				if (messageValid
						&& isLoaded("event-manager")
						&& getPackage("event-manager").isEnabled()){
					getLogger().log(LoggingLevel.FINER, "Calling enable of "
							+ toLoad.getType()
							+ " using event system.");
					/*
					 * Tries to send the event. If the return value is false,
					 * it failed and therefore we must load manually
					 */
					if (!fireEvent(toLoad.getType(), toSend)){
						getLogger().log(LoggingLevel.FINER, "Event failed, "
								+ "calling method directly.");
						toLoad.enable();
					}
				}
				else {
					getLogger().log(LoggingLevel.FINER, "Calling enable of "
							+ toLoad.getType());
					//errors creating message so the event would not work
					toLoad.enable();
				}
			}
			else{
				getLogger().log(LoggingLevel.FINER, "Calling enable of "
						+ toLoad.getType());
				//not using events for enable, or not using events at all
				toLoad.enable();
			}
		}
		getLogger().log(LoggingLevel.FINE, "Package " + toLoad.getType()
				+ " (V"+toLoad.getVersion()+ ")"+" loaded!");
		return true;
	}

	/**
	 * Fires an event with a message to a package type from the
	 * package manager.
	 * If an error occurs, this will return false. The event should not have
	 * been sent if false was returned.
	 *
	 * @param to the package to send the message to
	 * @param content the message to transfer
	 * @return true if the event was fired correctly
	 */
	private boolean fireEvent(String to, String content){

		if (!isLoaded("event-manager")){
			getLogger().logError(ErrorCode.PACKAGE_NOT_LOADED,
					LoggingLevel.WARNING, to);
			return false;
		}

		if (!getPackage("event-manager").isEnabled()){
			getLogger().logError(ErrorCode.PACKAGE_NOT_ENABLED,
					LoggingLevel.WARNING, to);
			return false;
		}

		PackageEvent tmpEvent;

		tmpEvent = new PackageEvent(
				packageName,
				to,
				content);
		try{
			if (tmpEvent != null){//just in case the assignment failed
				((EventManager)
						getPackage("event-manager")).fireEvent(tmpEvent);

			}
		}
		catch (IllegalStateException illegalState){
			//the queue was full
			getLogger().logError(ErrorCode.EVENT_QUEUE_FULL,
					LoggingLevel.WARNING,
					"PackageManager.fireEvent(String, String)");
			return false;
		}
		return true;
	}

	/**
	 * Returns true if a package exists with the given type
	 *  (for example: "Graphics")'
	 * @param type the package type
	 * @return true if the package is loaded in memory,
	 * false if it does not exist
	 */
	public boolean isLoaded(String type){
		return loadedPackages.containsKey(type);
	}


	/**
	 * Returns true if a package exists that has the same type as the provided
	 * package (for example: "Graphics"). This is the same as calling
	 * <code>{@link #isLoaded(String) isLoaded}(Package.getType())</code>
	 * @param type the package type
	 * @return true if the package is loaded in memory,
	 * false if it does not exist
	 */
	public boolean isLoaded(Package type){
		return loadedPackages.containsKey(type.getType());
	}

	/**
	 * If a package of type exists ({@link #isLoaded(String)}),
	 * then the package that is of that type is returned.
	 * If no package exists of that type,
	 * null is returned.
	 *
	 * @param type The package type
	 * @return the Package with the given type or null if none exists
	 */
	public Package getPackage(String type){
		if (isLoaded(type)){
			return loadedPackages.get(type);
		}
		else{
			return null;
		}
	}

	/**
	 * Attempts to unload the package from memory.
	 * If no package exists with the given name ({@link #isLoaded(String)}),
	 *  returns false and does nothing.
	 * @param toUnload The type of package to unload
	 * @return true if the package was unloaded properly
	 */
	public boolean unloadPackage(String toUnload){
		getLogger().log(LoggingLevel.FINE, "Unloading package " + toUnload
				+"...");
		if (!isLoaded(toUnload)){
			getLogger().log(LoggingLevel.FINE, "Package " + toUnload
					+" is not loaded. Aborting.");
			return false;
		}

		if (PackageSettings.DISABLE_ON_UNLOAD){
			if (loadedPackages.get(toUnload).isEnabled()){
				if (PackageSettings.USE_EVENTS_FOR_ACCESS &&
						PackageSettings.USE_EVENTS_FOR_DISABLE){
					String toSend = "";
					boolean messageValid = true;
					try {
						toSend = resourceBundle.getString("CMD_CALL")
								+ " "
								+ resourceBundle.getString("ARG_ON_DISABLE");
					}
					catch (MissingResourceException missingResource){
						getLogger().logError(
								ErrorCode.LOCALE_RESOURCE_NOT_FOUND,
								LoggingLevel.WARNING,
								"PackageManager.unloadPackage(String) disable");
						messageValid = false;
					}
					catch (ClassCastException classCast){
						getLogger().logError(
								ErrorCode.LOCALE_RESOURCE_WRONG_TYPE,
								LoggingLevel.WARNING,
								"PackageManager.unloadPackage(String) disable");
						messageValid = false;
					}

					if (messageValid
							&& isLoaded("event-manager")
							&& getPackage("event-manager").isEnabled()){
						getLogger().log(LoggingLevel.FINER, "Calling disable "
								+ "method of " + toUnload + " using events.");
						/*
						 * Tries to send the event. If the return value is
						 * false, it failed and therefore we must load manually
						 */
						if (!fireEvent(toUnload, toSend)){
							getLogger().log(LoggingLevel.FINER,
									"Events failed, "
											+ "calling method instead.");
							loadedPackages.get(toUnload).disable();
						}
					}
					else {
						getLogger().log(LoggingLevel.FINER, "Calling disable "
								+ "method of " + toUnload);
						//errors creating message so the event would not work
						loadedPackages.get(toUnload).disable();
					}
				}
				else{
					getLogger().log(LoggingLevel.FINER, "Calling disable "
							+ "method of " + toUnload);
					loadedPackages.get(toUnload).disable();
				}
			}
		}

		if (PackageSettings.USE_EVENTS_FOR_ACCESS &&
				PackageSettings.USE_EVENTS_FOR_ON_UNLOAD){
			String toSend = "";
			boolean messageValid = true;
			try {
				toSend = resourceBundle.getString("CMD_CALL")
						+ " "
						+ resourceBundle.getString("ARG_UNLOAD");
			}
			catch (MissingResourceException missingResource){
				getLogger().logError(ErrorCode.LOCALE_RESOURCE_NOT_FOUND,
						LoggingLevel.WARNING,
						"PackageManager.unloadPackage(String) unload");
				messageValid = false;
			}
			catch (ClassCastException classCast){
				getLogger().logError(ErrorCode.LOCALE_RESOURCE_WRONG_TYPE,
						LoggingLevel.WARNING,
						"PackageManager.unloadPackage(String) unload");
				messageValid = false;
			}

			if (messageValid
					&& isLoaded("event-manager")
					&& getPackage("event-manager").isEnabled()){
				getLogger().log(LoggingLevel.FINER, "Calling onUnload "
						+ "method of " + toUnload + " using events.");
				/*
				 * Tries to send the event. If the return value is false,
				 * it failed and therefore we must load manually
				 */
				if (!fireEvent(toUnload, toSend)){
					getLogger().log(LoggingLevel.FINER,
							"Events failed, "
									+ "calling method instead.");
					loadedPackages.get(toUnload).onUnload();
				}
			}
			else {
				getLogger().log(LoggingLevel.FINER, "Calling onUnload "
						+ "method of " + toUnload);
				//errors creating message so the event would not work
				loadedPackages.get(toUnload).onUnload();
			}
		}
		else{
			getLogger().log(LoggingLevel.FINER, "Calling onUnload "
					+ "method of " + toUnload);
			loadedPackages.get(toUnload).onUnload();
		}

		loadedPackages.remove(toUnload);

		getLogger().log(LoggingLevel.FINE, "Package " + toUnload
				+" unloaded!");
		return true;
	}

	/**
	 * Attempts to unload the package from memory.
	 * Does nothing if the package is
	 * not loaded. Packages are disabled before unloading. This calls
	 * {@link #unloadPackage(String)} using the package type.
	 *
	 * @param toUnload The type of package to unload
	 */
	public void unloadPackage(Package toUnload){
		/* using a string the packages type to ensure
		 * the package that is stored in this class is modified and not just
		 * the package passed to the method.
		 */
		String type = toUnload.getType();
		if (!isLoaded(type)){
			getLogger().log(LoggingLevel.FINE, "Package " + toUnload.getType()
					+" is not loaded. Aborting.");
			return;
		}
		unloadPackage(type);
	}

	/**
	 * Returns a logger for the system. If one does not exist, it will
	 * be created.
	 *
	 * @return a logger for the engine
	 */
	public LoggingPackage getLogger(){
		String loggingPackageName = "logging";

		if (!loadedPackages.containsKey(loggingPackageName)){
			LoggingPackage pack = new LoggingPackage();
			//store the new package
			loadedPackages.put(loggingPackageName, pack);
			//we don't know if there is an event system.
			//this has to work properly
			pack.onLoad();
			//enable the package
			if (PackageSettings.ENABLE_ON_LOAD){
				pack.enable();
			}
		}
		//safe cast since we know its a LoggingPackage
		return (LoggingPackage)loadedPackages.get(loggingPackageName);

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
	public String getType() {
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
