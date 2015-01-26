
package com.ikalagaming.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.ikalagaming.core.events.CommandFired;
import com.ikalagaming.core.events.PackageEvent;
import com.ikalagaming.core.packages.Package;
import com.ikalagaming.event.EventHandler;
import com.ikalagaming.event.Listener;
import com.ikalagaming.gui.events.ConsoleMessage;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * The event listener for the package management system.
 * 
 * @author Ches Burks
 * 
 */
public class PMEventListener implements Listener {

	private final String callMethod;
	private final String onLoad;
	private final String onUnload;
	private final String enable;
	private final String disable;

	private String cmd_help;
	private String cmd_packages;
	private String cmd_enable;
	private String cmd_disable;
	private String cmd_load;
	private String cmd_unload;
	private String cmd_reload;
	private String cmd_version;

	private PackageManager manager;

	/**
	 * Constructs a listener for the given package manager.
	 * 
	 * @param parent the manager to handle events for
	 */
	public PMEventListener(PackageManager parent) {
		this.manager = parent;
		callMethod =
				SafeResourceLoader.getString("CMD_CALL",
						manager.getResourceBundle(), "call");
		onLoad =
				SafeResourceLoader.getString("ARG_ON_LOAD",
						manager.getResourceBundle(), "onLoad");
		onUnload =
				SafeResourceLoader.getString("ARG_ON_UNLOAD",
						manager.getResourceBundle(), "onUnload");
		enable =
				SafeResourceLoader.getString("ARG_ENABLE",
						manager.getResourceBundle(), "enable");
		disable =
				SafeResourceLoader.getString("ARG_DISABLE",
						manager.getResourceBundle(), "disable");

		cmd_help =
				SafeResourceLoader.getString("COMMAND_HELP",
						manager.getResourceBundle(), "help");
		cmd_packages =
				SafeResourceLoader.getString("COMMAND_LIST_PACKAGES",
						manager.getResourceBundle(), "packages");
		cmd_enable =
				SafeResourceLoader.getString("COMMAND_ENABLE",
						manager.getResourceBundle(), "enable");
		cmd_disable =
				SafeResourceLoader.getString("COMMAND_DISABLE",
						manager.getResourceBundle(), "disable");
		cmd_load =
				SafeResourceLoader.getString("COMMAND_LOAD",
						manager.getResourceBundle(), "load");
		cmd_unload =
				SafeResourceLoader.getString("COMMAND_UNLOAD",
						manager.getResourceBundle(), "unload");
		cmd_reload =
				SafeResourceLoader.getString("COMMAND_RELOAD",
						manager.getResourceBundle(), "reload");
		cmd_version =
				SafeResourceLoader.getString("COMMAND_VERSION",
						manager.getResourceBundle(), "version");

	}

	/**
	 * Called when a command event is sent.
	 * 
	 * @param event the command sent
	 */
	@EventHandler
	public void onCommand(CommandFired event) {

		if (event.getCommand().equalsIgnoreCase(cmd_help)) {
			printHelp();
		}
		else if (event.getCommand().equalsIgnoreCase(cmd_packages)) {
			printPackages();
		}
		else if (event.getCommand().equalsIgnoreCase(cmd_enable)) {
			printWIP();
		}
		else if (event.getCommand().equalsIgnoreCase(cmd_disable)) {
			String name = "";
			if (event.getArgs().length >= 1) {
				name = event.getArgs()[0];
			}
			disable(name);
			printWIP();
		}
		else if (event.getCommand().equalsIgnoreCase(cmd_load)) {
			printWIP();
		}
		else if (event.getCommand().equalsIgnoreCase(cmd_unload)) {
			printWIP();
		}
		else if (event.getCommand().equalsIgnoreCase(cmd_reload)) {
			printWIP();
		}
		else if (event.getCommand().equalsIgnoreCase(cmd_version)) {
			String name = "";
			if (event.getArgs().length >= 1) {
				name = event.getArgs()[0];
			}
			printVersion(name);

		}

	}

	private void printHelp() {
		ArrayList<RegisteredCommand> commands;
		commands = manager.getCommandRegistry().getCommands();
		String tmp;
		ConsoleMessage message;
		for (RegisteredCommand cmd : commands) {
			tmp = cmd.getCommand();

			message = new ConsoleMessage(tmp);
			manager.fireEvent(message);
		}
	}

	/**
	 * Alerts the user that the given feature is not yet completed or working
	 * correctly.
	 */
	private void printWIP() {
		ConsoleMessage message =
				new ConsoleMessage(SafeResourceLoader.getString("WIP_TEXT",
						manager.getResourceBundle(),
						"WIP. This may not function correctly."));
		manager.fireEvent(message);
	}

	private void printPackages() {
		String tmp;
		ConsoleMessage message;
		HashMap<String, Package> loadedPackages = manager.getLoadedPackages();
		ArrayList<String> names = new ArrayList<String>();
		names.addAll(loadedPackages.keySet());
		Collections.sort(names);

		for (String name : names) {
			tmp = "";
			tmp += loadedPackages.get(name).getName();
			tmp +=
					" "
							+ SafeResourceLoader.getString("PACKAGE_VERSION",
									manager.getResourceBundle(), "v")
							+ loadedPackages.get(name).getVersion();
			if (loadedPackages.get(name).isEnabled()) {
				tmp +=
						" "
								+ "("
								+ SafeResourceLoader.getString(
										"ENABLED_STATUS",
										manager.getResourceBundle(), "Enabled")
								+ ")";
			}
			else {
				tmp +=
						" "
								+ "("
								+ SafeResourceLoader
										.getString("DISABLED_STATUS",
												manager.getResourceBundle(),
												"Disabled") + ")";
			}
			message = new ConsoleMessage(tmp);
			manager.fireEvent(message);
		}
	}

	/**
	 * Prints the version of the package specified, if it exists. If no package
	 * exists, alerts the user to this fact.
	 * 
	 * @param packageName The package to find a version for
	 */
	private void printVersion(String packageName) {
		String tmp = "";
		ConsoleMessage message;

		Package pack = manager.getPackage(packageName);

		if (pack != null) {
			tmp =
					SafeResourceLoader.getString("PACKAGE_VERSION",
							manager.getResourceBundle(), "v")
							+ pack.getVersion();
		}
		else {
			tmp =
					SafeResourceLoader.getString("NO_SUCH_PACKAGE",
							manager.getResourceBundle(), "Package not found");
		}
		message = new ConsoleMessage(tmp);
		manager.fireEvent(message);
	}

	/**
	 * Disables the specified package. If no package exists, alerts the user to
	 * this fact.
	 * 
	 * @param packageName The package to find a version for
	 */
	private void disable(String packageName) {
		String tmp = "";
		ConsoleMessage message;

		Package pack = manager.getPackage(packageName);

		if (pack == null) {
			tmp =
					SafeResourceLoader.getString("NO_SUCH_PACKAGE",
							manager.getResourceBundle(), "Package not found");
			message = new ConsoleMessage(tmp);
			manager.fireEvent(message);
			// stop right here. It does not exist
			return;
		}
		if (!pack.isEnabled()) {
			tmp =
					SafeResourceLoader.getString("package_disable_fail",
							manager.getResourceBundle(),
							"Package failed to disable");
			message = new ConsoleMessage(tmp);
			manager.fireEvent(message);
			return;
		}
		else {
			// unload the package
			manager.fireEvent(new PackageEvent("package-manager", packageName,
					callMethod + " " + disable));
		}
	}

	/**
	 * Called when a package event is sent out by the event system.
	 * 
	 * @param event the event that was fired
	 */
	@EventHandler
	public void onPackageEvent(PackageEvent event) {

		if (!manager.isLoaded(event.getTo())) {
			manager.getLogger().logError(
					manager,
					SafeResourceLoader.getString("NO_SUCH_PACKAGE",
							manager.getResourceBundle(), "Package not found"),
					LoggingLevel.INFO, event.getTo());
			return;
		}
		Package pack = manager.getPackage(event.getTo());
		if (event.getMessage().startsWith(callMethod)) {

			String trimmed = event.getMessage().replaceFirst(callMethod, "");
			trimmed = trimmed.replaceFirst(" ", "");
			if (trimmed.startsWith(onLoad)) {
				pack.onLoad();
			}
			else if (trimmed.startsWith(onUnload)) {
				pack.onUnload();
			}
			else if (trimmed.startsWith(enable)) {
				pack.enable();
			}
			else if (trimmed.startsWith(disable)) {
				pack.disable();
				String details =
						SafeResourceLoader.getString("ALERT_DISABLED",
								manager.getResourceBundle(),
								"Package $PACKAGE ($VERSION) disabled!");
				details = details.replaceFirst("\\$PACKAGE", pack.getName());
				details =
						details.replaceFirst("\\$VERSION",
								"" + pack.getVersion());
				manager.getLogger().log(pack, LoggingLevel.FINE, details);
			}
		}
	}
}
