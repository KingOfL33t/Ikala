
package com.ikalagaming.event;

import com.ikalagaming.event.Event;

/**
 * An event that is used for testing
 * 
 * @author Ches Burks
 * 
 */
public class TestEvent extends Event {

	/**
	 * The content of the event.
	 */
	private String message = "";

	/**
	 * Creates a new {@link TestEvent} with the supplied parameters. This will
	 * contain a String to test the transmission of the event
	 * 
	 * @param message the data
	 */
	public TestEvent(String message) {
		this.message = message;
	}

	/**
	 * Returns the message transmitted.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Sets the message
	 * 
	 * @param newMsg the new message to use
	 */
	public void setMessage(String newMsg) {
		this.message = newMsg;
	}

}
