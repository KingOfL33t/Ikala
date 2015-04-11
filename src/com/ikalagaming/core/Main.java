package com.ikalagaming.core;

import com.ikalagaming.gui.TaskManager;
import com.ikalagaming.gui.console.Console;
import com.ikalagaming.packages.PackageSettings;

/**
 * The entrypoint of the program.
 *
 * @author Ches Burks
 *
 */
public class Main {
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

		Game g = new Game();
		g.init();

		if (displayConsole) {
			Console c = new Console(Game.getEventManager());
			Game.getPackageManager().loadPackage(c);
			if (!PackageSettings.ENABLE_ON_LOAD) {
				if (!Game.getPackageManager().getPackage("console").isEnabled()) {
					Game.getPackageManager().getPackage("console").enable();
				}
			}
		}
		TaskManager manager =
				new TaskManager(Game.getPackageManager(),
						Game.getEventManager());
		manager.setVisible(true);

		JME3MainWindow window = new JME3MainWindow();
		window.start();
	}

	private static final String noConsoleArg = "nogui";

	private static final String help = "help";

}
