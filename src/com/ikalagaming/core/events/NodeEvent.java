package com.ikalagaming.core.events;

import com.ikalagaming.event.Event;

/**
 * An event that relates to nodes.
 * @author Ches Burks
 *
 */
public class NodeEvent extends Event {

	/**
	 * The name of the node that sent the event, if any.
	 */
	private String nodeTypeFrom;
	/**
	 * The name of the node that the event is sent to, if any.
	 */
	private String nodeTypeTo;

	/**
	 * The content of the event.
	 */
	private String message;

	/**
	 * Creates a new {@link NodeEvent} with the supplied parameters.
	 * There is no guarantee that only the intended node will receive the
	 * message.
	 *
	 * @param from the Node type of the sender
	 * @param to the Node type of the intended receiver
	 * @param message the data to transfer
	 */
	public NodeEvent(String from, String to, String message){
		this.nodeTypeFrom = from;
		this.nodeTypeTo = to;
		this.message = message;
	}

	/**
	 * Returns the name of the node that sent the message, if any.
	 * @return the name of the node
	 */
	public String getFrom(){
		return this.nodeTypeFrom;
	}

	/**
	 * Returns the name of the node that is intended to receive the message,
	 * if any.
	 * @return the name of the node
	 */
	public String getTo(){
		return this.nodeTypeTo;
	}

	/**
	 * Returns the message transmitted. This may be a command.
	 * @return the message
	 */
	public String getMessage(){
		return this.message;
	}


}
