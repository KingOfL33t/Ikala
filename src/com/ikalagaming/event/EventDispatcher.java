package com.ikalagaming.event;

import java.util.NoSuchElementException;

import com.ikalagaming.logging.ErrorCode;
import com.ikalagaming.logging.LoggingLevel;

/**
 * Holds an EventQueue and dispatches the events in order when possible.
 *
 * @author Ches Burks
 *
 */
public class EventDispatcher extends Thread{

	private EventQueue queue = new EventQueue();

	private Event currentEvent;
	private HandlerList handlers;
	private EventListener[] listeners;
	private EventManager manager;

	private boolean running;
	private boolean hasEvents;

	/**
	 * Creates and starts the thread. It will begin attempting to dispatch
	 * events immediately if there are any available.
	 * @param manager the event manager that this dispatcher belongs to
	 */
	public EventDispatcher(EventManager manager){
		this.manager = manager;
		this.hasEvents = false;
		this.running = true;
	}

	/**
	 * Adds the {@link Event event} to the queue pending dispatch.
	 *
	 * @param event The event to send out
	 * @throws IllegalStateException if the element cannot be added at this
	 * time due to capacity restrictions
	 */
	public synchronized void dispatchEvent(Event event)
			throws IllegalStateException{
		try{
			queue.add(event);//FIXME does not add to queue correctly
			this.hasEvents = true;
		}
		catch(IllegalStateException illegalState){
			throw illegalState;
		}
		catch(NullPointerException nullPointer){
			;//do nothing since its a null event
		}
		catch(Exception e){
			if (manager.getNodeManager() != null){
				manager.getNodeManager().getLogger().logError(
						ErrorCode.exception, LoggingLevel.WARNING,
						e.toString());
			}
			else {
				System.err.println(e.toString());
			}
		}
	}

	/**
	 * Checks for events in the queue, and dispatches them if possible.
	 * Does not do anything if {@link #terminate()} has been called.
	 */
	public void run() {
		if (!running){
			return;
		}
		while (running){
			if (hasEvents()){

				try{
					if (queue.peek() != null){
						currentEvent = queue.remove();
						System.out.println();
						handlers = manager.getHandlers(currentEvent);
						listeners = handlers.getRegisteredListeners();
						for (EventListener registration : listeners) {
							try {
								registration.callEvent(currentEvent);
							} catch (EventException e) {
								throw e;
							}
						}
					}
					else{
						Object[] debug = queue.toArray();
						System.out.print("[");
						for (int i = 0; i < debug.length; ++i){
							System.out.print(debug[i]);
							if (i< debug.length - 1){
								System.out.print(", ");
							}
						}
						System.out.println("]");
						break;
					}
				}
				catch(NoSuchElementException noElement){
					//the queue is empty
					//hasEvents = false;
					continue;
				}
				catch(Exception e){
					if (manager.getNodeManager() != null){
						manager.getNodeManager().getLogger().logError(
								ErrorCode.exception, LoggingLevel.WARNING,
								e.toString());
					}
					else {
						System.err.println(e.toString());
					}
				}
			}
		}
	}

	private boolean hasEvents(){
		return hasEvents;
	}

	/**
	 * Stops the thread from executing its run method in preparation for
	 * shutting down the thread.
	 */
	public synchronized void terminate(){
		running = false;
		manager = null;//stop memory freeing from being stopped
	}
}
