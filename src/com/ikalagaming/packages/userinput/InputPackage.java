
package com.ikalagaming.packages.userinput;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.ikalagaming.core.Game;
import com.ikalagaming.core.PackageManager;
import com.ikalagaming.core.events.CommandFired;
import com.ikalagaming.core.packages.Package;
import com.ikalagaming.core.packages.PackageState;
import com.ikalagaming.event.EventManager;
import com.ikalagaming.event.Listener;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.PackageLogger;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * The main interface for the package that handles input from the console and
 * from the System.in stream.
 * 
 * @author Ches Burks
 * 
 */
public class InputPackage implements Package, Listener {

	private final double version = 0.1;
	private final String packageName = "user-input";
	private PackageState state = PackageState.DISABLED;
	private PackageLogger logger;
	private LinkedList<String> inputBuffer;
	private BufferedReader br;
	private String buffer = "";
	private Thread thread;

	/**
	 * Adds the given string to the buffer pending processing.
	 * 
	 * @param str the string to add
	 */
	public void addToInputBuffer(String str) {
		inputBuffer.add(str);
	}

	/**
	 * Handle lines of input, if any exist
	 */
	public void processInput() {
		String line = "";
		if (inputBuffer.isEmpty()) {
			return;
		}
		line = inputBuffer.remove();
		String firstWord = line.trim().split(" ")[0];
		if (Game.getPackageManager().getCommandRegistry().contains(firstWord)) {
			if (Game.getPackageManager().isLoaded("event-manager")) {
				EventManager mgr =
						(EventManager) Game.getPackageManager().getPackage(
								"event-manager");
				CommandFired event =
						new CommandFired(Game.getPackageManager()
								.getCommandRegistry().getParent(firstWord)
								.getName(), line);
				mgr.fireEvent(event);
			}
			else {
				logger.logError(SafeResourceLoader.getString(
						"package_not_loaded", Game.getPackageManager()
								.getResourceBundle(), "Package not loaded"),
						LoggingLevel.WARNING, "event-manager");
			}
		}
		else {
			logger.logError(SafeResourceLoader.getString("command_unknown",
					Game.getPackageManager().getResourceBundle(),
					"Unknown command"), LoggingLevel.INFO, firstWord);
		}

	}

	@Override
	public boolean disable() {
		state = PackageState.DISABLING;
		onDisable();
		return true;
	}

	@Override
	public boolean enable() {
		state = PackageState.ENABLING;
		onEnable();
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
		if (state == PackageState.ENABLED) {
			return true;
		}
		return false;
	}

	@Override
	public void onDisable() {
		try {
			br.close();
			state = PackageState.DISABLED;
		}
		catch (IOException e) {
			e.printStackTrace();
			state = PackageState.CORRUPTED;
		}
	}

	@Override
	public void onEnable() {
		br = new BufferedReader(new InputStreamReader(System.in));

		state = PackageState.ENABLED;

		thread = new Thread(loop);
		thread.start();
	}

	Runnable loop = new Runnable() {
		public void run() {
			while (isEnabled()) {
				try {
					if (br.ready()) {
						buffer += br.read();
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				if (buffer.endsWith(System.lineSeparator())) {
					addToInputBuffer(buffer);
				}

				if (!inputBuffer.isEmpty()) {
					processInput();
				}
				try {
					Thread.sleep(10L);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	@Override
	public void onLoad() {
		state = PackageState.LOADING;
		logger = new PackageLogger(this);
		inputBuffer = new LinkedList<String>();
		state = PackageState.DISABLED;
	}

	@Override
	public void onUnload() {
		state = PackageState.UNLOADING;
		logger = null;
		state = PackageState.PENDING_REMOVAL;
	}

	@Override
	public boolean reload() {
		state = PackageState.UNLOADING;
		logger = null;
		onLoad();
		enable();
		return false;
	}

	@Override
	public Set<Listener> getListeners() {
		HashSet<Listener> listeners = new HashSet<Listener>();
		listeners.add(this);
		return listeners;
	}

	@Override
	public PackageState getPackageState() {
		return state;
	}
}
