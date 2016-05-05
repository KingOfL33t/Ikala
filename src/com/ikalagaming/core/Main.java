package com.ikalagaming.core;

import com.ikalagaming.event.EventManager;
import com.ikalagaming.gui.TaskManager;
import com.ikalagaming.gui.console.Console;
import com.ikalagaming.packages.PackageManager;
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
		PackageManager.getInstance();// creates the package manager

		if (displayConsole) {
			Console c = new Console(EventManager.getInstance());
			PackageManager.getInstance().loadPackage(c);
			if (!PackageManager.getInstance().enableOnLoad()) {
				if (!PackageManager.getInstance().isEnabled(c.getName())) {
					PackageManager.getInstance().enable(c.getName());
				}
			}
		}
		TaskManager manager = new TaskManager(PackageManager.getInstance());
		manager.setVisible(true);

		ServerController servController = new ServerController();
		PackageManager.getInstance().loadPackage(servController);
		PackageManager.getInstance().enable(servController);
		EventManager.getInstance().fireEvent(new ServerStarted());
	}

}
