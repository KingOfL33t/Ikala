
package com.ikalagaming.core;

import java.util.ArrayList;

import com.ikalagaming.core.packages.Package;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.events.Log;
import com.ikalagaming.logging.events.LogError;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * Handles storing and managing commands for controlling the server. Commands
 * are case insensitive.
 * 
 * @author Ches Burks
 * 
 */
public class CommandRegistry {

	/**
	 * A list of all of the commands registered. This list is sorted.
	 */
	private ArrayList<RegisteredCommand> commands;

	private PackageManager manager;

	/**
	 * Constructs a new command registry and sets up the internal structures.
	 * 
	 * @param manager the manager to use
	 */
	public CommandRegistry(PackageManager manager) {
		commands = new ArrayList<RegisteredCommand>();
		this.manager = manager;
	}

	/**
	 * Attempts to register the command for the given class. If the command
	 * already exists, an error is logged and the method returns false.
	 * 
	 * @param command the command to register
	 * @param owner what package is registering the command
	 * @return true if the command registered successfully
	 */
	public boolean registerCommand(String command, Package owner) {
		if (contains(command)) {
			int index = getIndexOf(command);
			String msg =
					SafeResourceLoader
							.getString("COMMAND_ALREADY_REGISTERED",
									manager.getResourceBundle(),
									"Command $COMMAND is already registered to $PACKAGE");
			msg = msg.replaceFirst("\\$COMMAND", command);
			msg =
					msg.replaceFirst("\\$PACKAGE", commands.get(index)
							.getOwner().getName());
			LogError err =
					new LogError(msg, LoggingLevel.WARNING, manager.getName());
			Game.getEventManager().fireEvent(err);
			return false;
		}
		else {
			RegisteredCommand cmd = new RegisteredCommand(command, owner);
			commands.add(cmd);
			String msg =
					SafeResourceLoader.getString("REGISTERED_COMMAND",
							manager.getResourceBundle(),
							"Registered command $COMMAND to $PACKAGE");
			msg = msg.replaceFirst("\\$COMMAND", command);
			msg = msg.replaceFirst("\\$PACKAGE", owner.getName());
			Log log = new Log(msg, LoggingLevel.FINEST, manager.getName());
			Game.getEventManager().fireEvent(log);
			java.util.Collections.sort(commands);
			return true;
		}
	}

	/**
	 * Unregisters the given command.
	 * 
	 * @param command the command to remove
	 * @return true if the command was removed
	 */
	public boolean unregisterCommand(String command) {
		if (contains(command)) {
			while (contains(command)) {// just in case there are multiple
				int index = getIndexOf(command);
				commands.remove(index);
			}
			String msg =
					SafeResourceLoader.getString("UNREGISTERED_COMMAND",
							manager.getResourceBundle(),
							"Unregistered command $COMMAND");
			msg = msg.replaceFirst("\\$COMMAND", command);
			Log log = new Log(msg, LoggingLevel.FINEST, manager.getName());
			Game.getEventManager().fireEvent(log);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Removes all commands that the given package registered.
	 * 
	 * @param owner the package which is having commands removed
	 */
	public void unregisterPackageCommands(Package owner) {
		ArrayList<String> registeredCommands = new ArrayList<String>();
		// find all the commands registered to the package
		for (RegisteredCommand c : commands) {
			if (c.getOwner().getName().equalsIgnoreCase(owner.getName())) {
				registeredCommands.add(c.getCommand());
			}
		}
		// unload the commands
		for (String s : registeredCommands) {
			unregisterCommand(s);
		}

	}

	/**
	 * Returns true if the array contains the given string.
	 * 
	 * @param s the string to look for
	 * @return true if the string exists
	 */
	public boolean contains(String s) {
		int i;
		for (i = 0; i < commands.size(); ++i) {
			if (commands.get(i).getCommand().equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the package that registered the given string, or null if it
	 * cannot be found.
	 * 
	 * @param s the string to look for
	 * @return the owner of the command
	 */
	public Package getParent(String s) {
		int i;
		for (i = 0; i < commands.size(); ++i) {
			if (commands.get(i).getCommand().equalsIgnoreCase(s)) {
				return commands.get(i).getOwner();
			}
		}
		return null;
	}

	/**
	 * Returns a clone of the commands list.
	 * 
	 * @return a copy of the stored list
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<RegisteredCommand> getCommands() {
		return (ArrayList<RegisteredCommand>) commands.clone();
	}

	/**
	 * Returns the index of the given string if it exists. If it is not in the
	 * array, returns -1.
	 * 
	 * @param s the string to look for
	 * @return the index of the string
	 */
	private int getIndexOf(String s) {
		int i;
		for (i = 0; i < commands.size(); ++i) {
			if (commands.get(i).getCommand().equalsIgnoreCase(s)) {
				return i;
			}
		}
		return -1;
	}
}
