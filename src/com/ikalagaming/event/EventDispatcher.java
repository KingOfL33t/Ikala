
package com.ikalagaming.event;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.ikalagaming.core.Game;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.events.Log;
import com.ikalagaming.logging.events.LogError;

/**
 * Holds an EventQueue and dispatches the events in order when possible.
 * 
 * @author Ches Burks
 * 
 */
public class EventDispatcher extends Thread {

	private LinkedList<Event> queue;

	private Event currentEvent;
	private HandlerList handlers;
	private EventListener[] listeners;
	private EventManager manager;

	private boolean running;
	private boolean hasEvents;

	/**
	 * Creates and starts the thread. It will begin attempting to dispatch
	 * events immediately if there are any available.
	 * 
	 * @param manager the event manager that this dispatcher belongs to
	 */
	public EventDispatcher(EventManager manager) {
		setName("EventDispatcher");
		queue = new LinkedList<Event>();
		this.manager = manager;
		this.hasEvents = false;
		this.running = true;
	}

	/**
	 * Adds the {@link Event event} to the queue pending dispatch.
	 * 
	 * @param event The event to send out
	 * @throws IllegalStateException if the element cannot be added at this time
	 *             due to capacity restrictions
	 */
	public void dispatchEvent(Event event) throws IllegalStateException {
		try {
			queue.add(event);
			hasEvents = true;
		}
		catch (IllegalStateException illegalState) {
			throw illegalState;
		}
		catch (NullPointerException nullPointer) {
			;// do nothing since its a null event
		}
		catch (Exception e) {
			if (event instanceof Log) {
				System.err.println(e.toString());
			}
			else if (event instanceof LogError) {
				System.err.println(e.toString());
			}
			else {
				/*
				 * It was not a problem with the logging, so try to log the
				 * error. If that fails, logging is broken so print to
				 * System.err for the devs.
				 */
				// TODO This code is in need of major cleanup.
				try {
					// TODO Localize this error message
					Game.getEventManager().fireEvent(
							new LogError("Error occurred firing event",
									LoggingLevel.WARNING, "event-manager"));
				}
				catch (Exception messyErrorHandling) {
					System.err.println(messyErrorHandling.toString());
				}

			}

		}
	}

	/**
	 * Checks for events in the queue, and dispatches them if possible. Does not
	 * do anything if {@link #terminate()} has been called.
	 */
	public void run() {
		if (!running) {
			return;
		}
		while (running) {
			try {
				sleep(5);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (hasEvents()) {
				handleEvent();
			}
		}
	}

	private void handleEvent() {
		try {
			if (queue.isEmpty()) {
				hasEvents = false;
			}
			else if (queue.peek() != null) {
				currentEvent = queue.remove();
				handlers = manager.getHandlers(currentEvent);
				if (handlers != null) {
					listeners = handlers.getRegisteredListeners();
					for (EventListener registration : listeners) {
						try {
							registration.callEvent(currentEvent);
						}
						catch (EventException e) {
							throw e;
						}
					}
				}
			}
			else {
				return;
			}
		}
		catch (NoSuchElementException noElement) {
			// the queue is empty
			// hasEvents = false;
			System.err.println(noElement.toString());
			return;
		}
		catch (Exception e) {
			// TODO Localize this error message
			String error = "Error running event dispatcher";
			Game.getEventManager().fireEvent(
					new LogError(error, LoggingLevel.WARNING, "event-manager"));
			e.printStackTrace();

			System.err.println(e.toString());

		}
	}

	private boolean hasEvents() {
		return hasEvents;
	}

	/**
	 * Stops the thread from executing its run method in preparation for
	 * shutting down the thread.
	 */
	public void terminate() {
		hasEvents = false;
		running = false;
		manager = null;
	}
}
