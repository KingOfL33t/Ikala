
package com.ikalagaming.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Stores handlers per event. Based on lahwran's fevents.
 */
public class HandlerList {
	/*
	 * Handler list. This changes when register() and unregister() are called.
	 * This is a HashSet for speed.
	 */
	private HashSet<EventListener> handlerslots;
	private EventListener[] bakedList;

	/**
	 * Unregisters all handlers.
	 */
	public void unregisterAll() {
		synchronized (handlerslots) {
			handlerslots.clear();
			bake();
		}
	}

	/**
	 * Unregister a specific listener from the handler list.
	 * 
	 * @param listener The listener to unregister
	 */
	public void unregisterAll(Listener listener) {
		synchronized (handlerslots) {
			handlerslots.remove(listener);
			bake();
		}
	}

	/**
	 * Create a new handler list and initialize using an EventPriority. The
	 * HandlerList is then added to meta-list for use in bakeAll().
	 */
	public HandlerList() {
		handlerslots = new HashSet<EventListener>();
		bakedList = new EventListener[0];
	}

	/**
	 * Register a new listener in this handler list.
	 * 
	 * @param listener The listener to register
	 * @throws IllegalStateException if the listener is already registered
	 */
	public synchronized void register(EventListener listener) {
		if (handlerslots.contains(listener)) {
			IllegalStateException excep =
					new IllegalStateException(
							"This listener is already registered");
			throw excep;
		}
		handlerslots.add(listener);
		bake();
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
		bake();
	}

	/**
	 * Remove a listener from a specific order slot.
	 * 
	 * @param listener The listener to unregister
	 */
	public synchronized void unregister(EventListener listener) {
		handlerslots.remove(listener);
		bake();
	}

	/**
	 * Remove a specific listener from this handler
	 * 
	 * @param listener the Listener to unregister
	 */
	public synchronized void unregister(Listener listener) {

		// loop through and unregister a listener from the list if it
		// matches the param
		for (Iterator<EventListener> i = handlerslots.iterator(); i.hasNext();) {
			if (i.next().getListener().equals(listener)) {
				i.remove();
			}
		}
		bake();
	}

	/**
	 * Creates an array of listeners that can be returned. This is done whenever
	 * the hashset changes and saves on memory.
	 */
	private synchronized void bake() {
		bakedList = new EventListener[handlerslots.size()];
		bakedList = handlerslots.toArray(bakedList);
	}

	/**
	 * Get the baked registered listeners associated with this handler list
	 * 
	 * @return The listeners registered
	 */
	public EventListener[] getRegisteredListeners() {
		return bakedList;
	}

}
