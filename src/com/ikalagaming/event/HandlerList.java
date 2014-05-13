package com.ikalagaming.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Stores handlers per event. 
 * Based on lahwran's fevents.
 */
public class HandlerList {
	/*
	 * Handler list. This changes when register() and unregister() are called.
	 * This is a HashSet for speed.
	 */
	private static HashSet<EventListener> handlerslots;

	/**
	 * Unregisters all handlers.
	 */
	public static void unregisterAll() {
		synchronized (handlerslots) {
			handlerslots.clear();
		}
	}

	/**
	 * Unregister a specific listener from the handler list.
	 * 
	 * @param listener The listener to unregister
	 */
	public static void unregisterAll(Listener listener) {
		synchronized (handlerslots) {
			handlerslots.remove(listener);
		}
	}

	/**
	 * Create a new handler list and initialize using an EventPriority.
	 * The HandlerList is then added to meta-list for use in bakeAll().
	 */
	public HandlerList() {
		handlerslots = new HashSet<EventListener>();
	}

	/**
	 * Register a new listener in this handler list.
	 * 
	 * @param listener The listener to register
	 * @throws IllegalStateException if the listener is already registered
	 */
	public synchronized void register(EventListener listener) {
		if (handlerslots.contains(listener)){
			IllegalStateException excep = new IllegalStateException(
					"This listener is already registered");
			throw excep;
		}
		handlerslots.add(listener);
	}

	/**
	 * Register a collection of new listeners in this handler list.
	 * 
	 * @param listeners The collection to register
	 */
	public void registerAll(Collection<EventListener> listeners) {
		for (EventListener listener : listeners) {
			register(listener);
		}
	}

	/**
	 * Remove a listener from a specific order slot.
	 * 
	 * @param listener The listener to unregister
	 */
	public synchronized void unregister(EventListener listener) {
		handlerslots.remove(listener);
	}


	/**
	 * Remove a specific listener from this handler 
	 */
	public synchronized void unregister(Listener listener) {
		//loop through and unregister a listener from the list if it 
		//matches the param
		for (Iterator<EventListener> i = handlerslots.iterator(); i.hasNext();)
		{
			if (i.next().getListener().equals(listener)) {
				i.remove();
			}
		}
	}

	/**
	 * Get the baked registered listeners associated with this handler list
	 * 
	 * @return The listeners registered
	 */
	public EventListener[] getRegisteredListeners() {        
		return handlerslots.toArray(new EventListener[handlerslots.size()]);
	}

}

