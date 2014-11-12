
package com.ikalagaming.packages.userinput;

import java.util.ResourceBundle;

import com.ikalagaming.core.IQueue;
import com.ikalagaming.core.Package;
import com.ikalagaming.core.PackageManager;
import com.ikalagaming.core.events.CommandFired;
import com.ikalagaming.core.events.PackageEvent;
import com.ikalagaming.event.EventHandler;
import com.ikalagaming.event.EventManager;
import com.ikalagaming.event.Listener;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * The main interface for the package that handles input from the console and
 * from the System.in stream.
 * 
 * @author Ches Burks
 * 
 */
public class InputPackage implements Package, Listener {

	private PackageManager parent;
	private final double version = 0.1;
	private final String packageName = "user-input";
	private boolean enabled = false;
	private IQueue<String> inputBuffer;
	private InputProcessingThread processing;
	private InputRetrievalThread retrieval;

	/**
	 * Adds the given string to the buffer pending processing.
	 * 
	 * @param str the string to add
	 */
	public synchronized void addToInputBuffer(String str) {
		inputBuffer.add(str);
		System.out.println(str);
	}

	/**
	 * Handle lines of input, if any exist
	 */
	public void processInput() {
		String line = "";
		synchronized (inputBuffer) {
			if (inputBuffer.isEmpty()) {
				return;
			}
			line = inputBuffer.remove();
		}
		String firstWord = line.trim().split(" ")[0];
		if (parent.getCommandRegistry().contains(firstWord)) {
			if (parent.isLoaded("event-manager")) {
				EventManager mgr =
						(EventManager) parent.getPackage("event-manager");
				CommandFired event =
						new CommandFired(parent.getCommandRegistry()
								.getParent(firstWord).getType(), line);
				mgr.fireEvent(event);
			}
			else {
				parent.getLogger().logError(
						SafeResourceLoader.getString("package_not_loaded",
								parent.getResourceBundle(),
								"Package not loaded"), LoggingLevel.WARNING,
						"event-manager");
			}
		}
		else {
			parent.getLogger().logError(
					SafeResourceLoader.getString("command_unknown",
							parent.getResourceBundle(), "Unknown command"),
					LoggingLevel.INFO, firstWord);
		}

	}

	@Override
	public boolean disable() {
		onDisable();
		enabled = false;
		return true;
	}

	@Override
	public boolean enable() {
		onEnable();
		enabled = true;
		return true;
	}

	@Override
	public PackageManager getPackageManager() {
		return parent;
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
		return enabled;
	}

	@Override
	public void onDisable() {
		retrieval.terminate();
		processing.terminate();
	}

	@Override
	public void onEnable() {
		retrieval.start();
		processing.start();
	}

	@Override
	public void onLoad() {
		inputBuffer = new IQueue<String>();
		retrieval = new InputRetrievalThread(this);
		processing = new InputProcessingThread(this);
	}

	@Override
	public void onUnload() {
		try {
			retrieval.join();
			processing.join();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean reload() {
		enable();
		disable();
		return false;
	}

	@Override
	public void setPackageManager(PackageManager parent) {
		this.parent = parent;
	}

	/**
	 * Called when a package event is sent out by the event system.
	 * 
	 * @param event the event that was fired
	 */
	@EventHandler
	public void onPackageEvent(PackageEvent event) {
		if (event.getTo() != packageName) {
			return;
		}
		String callMethod = "call";
		String onLoad = "onLoad";
		String onUnload = "onUnload";
		String enable = "enable";
		String disable = "disable";

		ResourceBundle packageBundle;
		packageBundle = parent.getResourceBundle();

		SafeResourceLoader.getString("CMD_CALL", packageBundle, "call");
		SafeResourceLoader.getString("ARG_ON_LOAD", packageBundle, "onLoad");
		SafeResourceLoader
				.getString("ARG_ON_UNLOAD", packageBundle, "onUnload");
		SafeResourceLoader.getString("ARG_ENABLE", packageBundle, "enable");
		SafeResourceLoader.getString("ARG_DISABLE", packageBundle, "disable");

		if (event.getMessage().startsWith(callMethod)) {
			String trimmed = event.getMessage().replaceFirst(callMethod, "");
			trimmed = trimmed.replaceFirst(" ", "");
			if (trimmed.startsWith(onLoad)) {
				onLoad();
			}
			else if (trimmed.startsWith(onUnload)) {
				onUnload();
			}
			else if (trimmed.startsWith(enable)) {
				enable();
			}
			else if (trimmed.startsWith(disable)) {
				disable();
			}
		}
	}
}
