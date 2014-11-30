
package com.ikalagaming.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.ikalagaming.core.events.CommandFired;
import com.ikalagaming.core.packages.Package;
import com.ikalagaming.event.EventHandler;
import com.ikalagaming.event.Listener;
import com.ikalagaming.gui.events.ConsoleMessage;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * The event listener for the package management system.
 *
 * @author Ches Burks
 *
 */
public class PMEventListener implements Listener {

	private PackageManager manager;

	/**
	 * Constructs a listener for the given package manager.
	 *
	 * @param parent the manager to handle events for
	 */
	public PMEventListener(PackageManager parent) {
		this.manager = parent;
	}

	/**
	 * Called when a command event is sent.
	 *
	 * @param event the command sent
	 */
	@EventHandler
	public void onCommand(CommandFired event) {
		String help =
				SafeResourceLoader.getString("COMMAND_HELP",
						manager.getResourceBundle(), "COMMAND_HELP");
		String packages =
				SafeResourceLoader.getString("COMMAND_LIST_PACKAGES",
						manager.getResourceBundle(), "COMMAND_LIST_PACKAGES");
		if (event.getCommand().equalsIgnoreCase(help)) {
			printHelp();
		}
		else if (event.getCommand().equalsIgnoreCase(packages)){
			printPackages();
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

	private void printPackages() {
		String tmp;
		ConsoleMessage message;
		HashMap<String, Package> loadedPackages = manager.getLoadedPackages();
		ArrayList<String> names = new ArrayList<String>();
		names.addAll(loadedPackages.keySet());
		Collections.sort(names);

		for (String name : names){
			tmp = "";
			tmp += loadedPackages.get(name).getName();
			tmp += " " + "v" + loadedPackages.get(name).getVersion() + "";
			//TODO localized version and enabled status
			if (loadedPackages.get(name).isEnabled()){
				tmp += " " + "(enabled)";
			}
			else {
				tmp += " " + "(disabled)";
			}
			message = new ConsoleMessage(tmp);
			manager.fireEvent(message);
		}
	}
}
