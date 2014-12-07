
package com.ikalagaming.core.events;

import com.ikalagaming.event.Event;

/**
 * A command was sent.
 * 
 * @author Ches Burks
 * 
 */
public class CommandFired extends Event {

	/**
	 * The name of the package that the command was registered to.
	 */
	private final String packageTypeTo;

	/**
	 * The command.
	 */
	private final String cmd;

	/**
	 * The parameters for the command.
	 */
	private final String[] arguments;

	/**
	 * Creates a new {@link CommandFired} with the supplied parameters. There is
	 * no guarantee that only the intended package will receive the message.
	 * 
	 * @param to the Package type that owns the command
	 * @param command the command
	 */
	public CommandFired(String to, String command) {
		this(to, command, null);
	}

	/**
	 * Creates a new {@link CommandFired} with the supplied parameters. There is
	 * no guarantee that only the intended package will receive the message. If
	 * the arguments list is null, it will be created from the command, if the
	 * command has multiple parts.
	 * 
	 * @param to the Package type that owns the command
	 * @param command the command
	 * @param args arguments for the command
	 */
	public CommandFired(String to, String command, String[] args) {
		this.packageTypeTo = to;
		String[] parts = command.trim().split("\\s+");
		this.cmd = parts[0];
		if (args == null) {
			if (parts.length > 1) {
				args = new String[parts.length - 1];
				System.arraycopy(parts, 1, args, 0, parts.length - 1);
			}
		}
		this.arguments = args;
	}

	/**
	 * Returns the name of the package that the command was registered to.
	 * 
	 * @return the name of the package
	 */
	public String getTo() {
		return this.packageTypeTo;
	}

	/**
	 * Returns the command transmitted.
	 * 
	 * @return the command
	 */
	public String getCommand() {
		return this.cmd;
	}

	/**
	 * Return the list of arguments, {@link #hasArgs() if there are any}.
	 * 
	 * @return the arguments for the command.
	 */
	public String[] getArgs() {
		return this.arguments;
	}

	/**
	 * Returns true if arguments exist, or false if the arguments list is null
	 * or empty.
	 * 
	 * @return true if there are arguments
	 */
	public boolean hasArgs() {
		if (this.arguments == null) {
			return false;
		}
		else if (this.arguments.length > 0) {
			return true;
		}
		return false;
	}

}
