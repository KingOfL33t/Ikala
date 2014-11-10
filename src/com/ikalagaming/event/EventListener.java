
package com.ikalagaming.event;

/**
 * A listener that handles events.
 */
public class EventListener {
	private final Listener listener;
	private final EventExecutor executor;

	/**
	 * Constructs a new {@link EventListener}.
	 * 
	 * @param listener The listener to call
	 * @param executor The executor for the events
	 */
	public EventListener(final Listener listener, final EventExecutor executor) {
		this.listener = listener;
		this.executor = executor;
	}

	/**
	 * Returns the {@link Listener listener}.
	 * 
	 * @return The listener
	 */
	public Listener getListener() {
		return listener;
	}

	/**
	 * Calls the event executor.
	 * 
	 * @param event The event to execute
	 * @throws EventException If an exception occurs during execution
	 */
	public void callEvent(final Event event) throws EventException {
		executor.execute(listener, event);
	}

}
