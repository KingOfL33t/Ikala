package com.ikalagaming.core;

import com.ikalagaming.event.EventManager;
import com.ikalagaming.gui.TaskManager;
import com.ikalagaming.plugins.PluginManager;
import com.ikalagaming.server.ServerController;
import com.ikalagaming.server.events.ServerStarted;

/**
 * The entrypoint of the program.
 *
 * @author Ches Burks
 *
 */
public class Main {
	private static final String noConsoleArg = "nogui";

	private static final String help = "help";

	/**
	 * The entrypoint of the program. This is the main method that is run when
	 * the game is run.
	 *
	 * @param args arguments to be passed to the program via command line
	 */
	public static void main(String[] args) {
		boolean displayConsole = true;

		if (args.length > 0) {
			// it has arguments
			if (args.length == 1 && args[0].equalsIgnoreCase(Main.help)) {
				System.out.println("Starts the KOI server");
				System.out.println("\nArguments:");
				System.out.println(Main.noConsoleArg + " - does not create "
					+ "a seperate console.");
			}
			int i;
			for (i = 0; i < args.length; ++i) {
				if (args[i].equalsIgnoreCase(Main.noConsoleArg)) {
					displayConsole = false;
				}
			}
		}

		// initialize the systems
		EventManager.getInstance();// creates the event manager
		PluginManager.getInstance();// creates the plugin manager

		TaskManager manager = new TaskManager(PluginManager.getInstance());
		manager.setVisible(true);

		ServerController servController = new ServerController();
		// PluginManager.getInstance().loadPlugin(servController.PLUGIN_NAME);
		PluginManager.getInstance().enable(servController.PLUGIN_NAME);
		EventManager.getInstance().fireEvent(new ServerStarted());
	}

}
