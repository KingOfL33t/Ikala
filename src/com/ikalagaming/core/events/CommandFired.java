package com.ikalagaming.core.events;

import com.ikalagaming.event.Event;

/**
 * A command was sent.
 * @author Ches Burks
 *
 */
public class CommandFired extends Event {

	/**
	 * The name of the package that the command
	 * was registered to.
	 */
	private String packageTypeTo;

	/**
	 * The command and parameters.
	 */
	private String message;

	/**
	 * Creates a new {@link CommandFired} with the supplied parameters.
	 * There is no guarantee that only the intended package will receive the
	 * message.
	 *
	 * @param to the Package type that owns the command
	 * @param message the command
	 */
	public CommandFired(String to, String message){
		this.packageTypeTo = to;
		this.message = message;
	}

	/**
	 * Returns the name of the package that the command
	 * was registered to.
	 *
	 * @return the name of the package
	 */
	public String getTo(){
		return this.packageTypeTo;
	}

	/**
	 * Returns the message transmitted.
	 * @return the message
	 */
	public String getMessage(){
		return this.message;
	}


}
