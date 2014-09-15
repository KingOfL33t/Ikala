package com.ikalagaming.event;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages events and listeners.
 */
public class EventManager {

	private static EventManager instance;

	//TODO get rid of this in favor of having multiple instances possible
	/**
	 * Returns the static instance.
	 * Creates one if it does not exist.
	 *
	 * @return The current instance of the class
	 */
	public static synchronized EventManager getInstance(){
		if (instance == null){
			instance = new EventManager();
		}
		return instance;
	}
	/**
	 * Registers event listeners in the supplied listener.
	 *
	 * @param listener The listener to register
	 * @throws Exception If there is an exception registering
	 */
	public void registerEventListeners(Listener listener) throws Exception {
		for (Map.Entry<Class<? extends Event>, Set<EventListener>> entry :
			createRegisteredListeners(listener).entrySet()) {
			getEventListeners(getRegistrationClass(
					entry.getKey())).registerAll(entry.getValue());
		}
	}

	/**
	 * Returns a {@link HandlerList} for a give event type
	 *
	 * @param type The type of event to find handlers for
	 * @throws Exception If an exception occurred
	 */
	private HandlerList getEventListeners(Class<? extends Event> type)
			throws Exception {
		try {
			Method method = getRegistrationClass(type).getDeclaredMethod(
					"getHandlerList");
			method.setAccessible(true);
			//get the handler list from the class
			return (HandlerList) method.invoke(null);
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Returns the class that has the handler list for
	 * the supplied {@link Event}.
	 *
	 * @param eventClass The class to find handlers for
	 * @throws Exception If a handler list cannot be found
	 */
	private Class<? extends Event> getRegistrationClass(
			Class<? extends Event> eventClass) throws Exception {
		try {
			eventClass.getDeclaredMethod("getHandlerList");
			return eventClass;
		} catch (NoSuchMethodException e) {
			if (eventClass.getSuperclass() != null
					&& !eventClass.getSuperclass().equals(Event.class)
					&& Event.class.isAssignableFrom(
							eventClass.getSuperclass())) {

				return getRegistrationClass(eventClass.getSuperclass()
						.asSubclass(Event.class));
			} else {
				Exception excep = new Exception(
						"Unable to find handler list for event "
								+ eventClass.getName());
				throw excep;
			}
		}
	}

	/**
	 * Sends the {@link Event event} to all of its listeners.
	 *
	 * @param event The event to fire
	 * @throws EventException if an error occurs while firing
	 */
	public void fireEvent(Event event) throws EventException {
		HandlerList handlers = event.getHandlers();
		EventListener[] listeners = handlers.getRegisteredListeners();
		for (EventListener registration : listeners) {
			try {
				registration.callEvent(event);
			} catch (EventException e) {
				throw e;
			}
		}
	}

	/**
	 * Creates {@link EventListener EventListeners} for a given
	 * {@link Listener listener}.
	 *
	 * @param listener The listener to create EventListenrs for
	 * @return A map of events to a set of EventListeners belonging to it
	 */
	public Map<Class<? extends Event>, Set<EventListener>>
	createRegisteredListeners(Listener listener) {

		Map<Class<? extends Event>, Set<EventListener>> toReturn =
				new HashMap<Class<? extends Event>, Set<EventListener>>();
		Set<Method> methods;
		try {
			Method[] publicMethods = listener.getClass().getMethods();
			methods = new HashSet<Method>(publicMethods.length,
					Float.MAX_VALUE);
			for (Method method : publicMethods) {
				methods.add(method);
			}
			for (Method method : listener.getClass().getDeclaredMethods()) {
				methods.add(method);
			}
		} catch (NoClassDefFoundError e) {
			return toReturn;
		}

		//search the methods for listeners
		for (final Method method : methods) {
			final EventHandler handlerAnnotation =
					method.getAnnotation(EventHandler.class);
			if (handlerAnnotation == null)
				continue;
			final Class<?> checkClass;
			if (method.getParameterTypes().length != 1
					|| !Event.class.isAssignableFrom(checkClass =
					method.getParameterTypes()[0])) {
				continue;
			}
			final Class<? extends Event> eventClass =
					checkClass.asSubclass(Event.class);
			method.setAccessible(true);
			Set<EventListener> eventSet = toReturn.get(eventClass);
			if (eventSet == null) {
				eventSet = new HashSet<EventListener>();
				//add the listener methods to the list of events
				toReturn.put(eventClass, eventSet);
			}

			//creates a class to execute the listener for the event
			EventExecutor executor = new EventExecutor() {
				public void execute(Listener listener, Event event)
						throws EventException {
					try {
						if (!eventClass.isAssignableFrom(event.getClass())) {
							return;
						}
						method.invoke(listener, event);
					}

					catch (Throwable t) {
						EventException evtExcept = new EventException(t);
						throw evtExcept;
					}
				}
			};

			eventSet.add(new EventListener(listener, executor));

		}
		return toReturn;
	}
}
