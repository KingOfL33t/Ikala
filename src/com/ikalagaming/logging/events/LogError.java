
package com.ikalagaming.logging.events;

import com.ikalagaming.core.packages.Package;
import com.ikalagaming.event.Event;
import com.ikalagaming.logging.LoggingLevel;

/**
 * An error message needs to be logged.
 * 
 * @author Ches Burks
 * 
 */
public class LogError extends Event {

	private final String error;

	private final LoggingLevel level;

	private final String details;

	private final String sender;

	/**
	 * Creates a LogError event with the given information.
	 * 
	 * @param error What went wrong
	 * @param details extra information about the error
	 * @param level the level of importance
	 * @param sender who is logging the error
	 */
	public LogError(String error, String details, LoggingLevel level,
			String sender) {
		this.error = error;
		this.details = details;
		this.level = level;
		this.sender = sender;
	}

	/**
	 * Creates a LogError event with the given information.
	 * 
	 * @param error What went wrong
	 * @param details extra information about the error
	 * @param level the level of importance
	 * @param sender who is logging the error
	 */
	public LogError(String error, String details, LoggingLevel level,
			Package sender) {
		this(error, details, level, sender.getName());
	}

	/**
	 * Creates a LogError event with the given information.
	 * 
	 * @param error What went wrong
	 * @param level the level of importance
	 * @param sender who is logging the error
	 */
	public LogError(String error, LoggingLevel level, Package sender) {
		this(error, "", level, sender.getName());
	}

	/**
	 * Creates a LogError event with the given information.
	 * 
	 * @param error What went wrong
	 * @param level the level of importance
	 * @param sender who is logging the error
	 */
	public LogError(String error, LoggingLevel level, String sender) {
		this(error, "", level, sender);
	}

	/**
	 * Returns a string containing the information to be recorded or displayed.
	 * 
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * Returns a string containing extra information to be recorded or
	 * displayed.
	 * 
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * Returns the logging level assigned to this log. This determines how
	 * important the information is and whether or not it will be displayed.
	 * 
	 * @return the logging level
	 */
	public LoggingLevel getLevel() {
		return level;
	}

	/**
	 * Returns the name of the package that sent the information to be logged.
	 * 
	 * @return the senders name
	 */
	public String getSender() {
		return sender;
	}

}
