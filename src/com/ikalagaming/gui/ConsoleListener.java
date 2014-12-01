package com.ikalagaming.gui;

import com.ikalagaming.core.events.CommandFired;
import com.ikalagaming.event.EventHandler;
import com.ikalagaming.event.Listener;
import com.ikalagaming.gui.events.ConsoleMessage;

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
		if (!event.getTo().equalsIgnoreCase(console.getName())) {
			return;
		}

	}
}
