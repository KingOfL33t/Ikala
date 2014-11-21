
package com.ikalagaming.event;

import java.util.NoSuchElementException;

import com.ikalagaming.core.IQueue;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.PackageLogger;

/**
 * Holds an EventQueue and dispatches the events in order when possible.
 *
 * @author Ches Burks
 *
 */
public class EventDispatcher extends Thread {

	private IQueue<Event> queue;

	private Event currentEvent;
	private HandlerList handlers;
	private EventListener[] listeners;
	private EventManager manager;
	private PackageLogger logger;

	private boolean running;
	private boolean hasEvents;

	/**
	 * Creates and starts the thread. It will begin attempting to dispatch
	 * events immediately if there are any available.
	 *
	 * @param manager the event manager that this dispatcher belongs to
	 */
	public EventDispatcher(EventManager manager) {
		queue = new IQueue<Event>();
		this.manager = manager;
		this.hasEvents = false;
		this.running = true;
		logger = new PackageLogger(manager);
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
			if (manager.getPackageManager() != null) {
				logger.logError("error firing event", LoggingLevel.WARNING,
						e.toString());
			}
			else {
				System.err.println(e.toString());
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

	private void handleEvent(){
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
			return;
		}
		catch (Exception e) {
			if (manager.getPackageManager() != null) {
				logger.logError("error running event dispatcher",
						LoggingLevel.WARNING, e.toString()
								+ " at EventDispatcher.run()");
				e.printStackTrace();
			}
			else {
				System.err.println(e.toString());
			}
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
		manager = null;// stop memory freeing from being stopped
		logger = null;
	}
}
