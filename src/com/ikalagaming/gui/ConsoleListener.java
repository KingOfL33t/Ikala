package com.ikalagaming.gui;

import java.util.ResourceBundle;

import com.ikalagaming.core.events.CommandFired;
import com.ikalagaming.core.events.PackageEvent;
import com.ikalagaming.event.EventHandler;
import com.ikalagaming.event.Listener;
import com.ikalagaming.gui.events.ConsoleMessage;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * The listener for the console gui. This handles events for the console.
 * @author Ches Burks
 *
 */
public class ConsoleListener implements Listener{
	private Console console;

	/**
	 * Constructs a listener for the given console.
	 * @param console the console to listen for
	 */
	public ConsoleListener(Console console){
		this.console = console;
	}
	/**
	 * Called when a package event is sent out by the event system.
	 *
	 * @param event the event that was fired
	 */
	@EventHandler
	public void onPackageEvent(PackageEvent event) {
		if (event.getTo() != console.getName()) {
			return;
		}
		String callMethod = "call";
		String onLoad = "onLoad";
		String onUnload = "onUnload";
		String enable = "enable";
		String disable = "disable";

		ResourceBundle packageBundle;
		packageBundle = console.getPackageManager().getResourceBundle();

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
				console.onLoad();
			}
			else if (trimmed.startsWith(onUnload)) {
				console.onUnload();
			}
			else if (trimmed.startsWith(enable)) {
				console.enable();
			}
			else if (trimmed.startsWith(disable)) {
				console.disable();
			}
		}
	}

	/**
	 * When a console message is sent, append it to the console.
	 * @param event the event that was received
	 */
	@EventHandler
	public void onConsoleMessage(ConsoleMessage event){
		console.appendMessage(event.getMessage());
	}

	/**
	 * Called when a command event is sent.
	 *
	 * @param event the command sent
	 */
	@EventHandler
	public void onCommand(CommandFired event) {
		String toAdd = "got cmd " + event.getCommand();
		if (event.hasArgs()){
			for (String s : event.getArgs()){
				toAdd += " " + s;
			}
		}
		console.appendMessage(toAdd);
		if (!event.getTo().equalsIgnoreCase(console.getName())) {
			return;
		}

	}
}
