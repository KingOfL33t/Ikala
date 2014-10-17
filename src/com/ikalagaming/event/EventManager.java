package com.ikalagaming.event;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import com.ikalagaming.core.Localization;
import com.ikalagaming.core.Node;
import com.ikalagaming.core.NodeManager;
import com.ikalagaming.core.ResourceLocation;
import com.ikalagaming.logging.ErrorCode;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.LoggingNode;

/**
 * Manages events and listeners.
 */
public class EventManager implements Node {

	private EventDispatcher dispatcher;
	private ResourceBundle resourceBundle;
	private boolean enabled = false;
	private final double version = 0.1;
	private NodeManager nodeManager;

	/**
	 * Registers event listeners in the supplied listener.
	 *
	 * @param listener
	 *            The listener to register
	 * @throws Exception
	 *             If there is an exception registering
	 */
	public void registerEventListeners(Listener listener) throws Exception {
		for (Map.Entry<Class<? extends Event>, Set<EventListener>> entry : createRegisteredListeners(
				listener).entrySet()) {
			getEventListeners(getRegistrationClass(entry.getKey()))
			.registerAll(entry.getValue());
		}
	}

	/**
	 * Returns a {@link HandlerList} for a give event type
	 *
	 * @param type
	 *            The type of event to find handlers for
	 * @throws Exception
	 *             If an exception occurred
	 */
	private HandlerList getEventListeners(Class<? extends Event> type)
			throws Exception {
		try {
			Method method = getRegistrationClass(type).getDeclaredMethod(
					"getHandlerList");
			method.setAccessible(true);
			// get the handler list from the class
			return (HandlerList) method.invoke(null);
		} catch (Exception e) {
			throw new Exception(e.getMessage(), e.getCause());
		}
	}

	/**
	 * Returns the class that has the handler list for the supplied
	 * {@link Event}.
	 *
	 * @param eventClass
	 *            The class to find handlers for
	 * @throws Exception
	 *             If a handler list cannot be found
	 */
	private Class<? extends Event> getRegistrationClass(
			Class<? extends Event> eventClass) throws Exception {
		try {
			eventClass.getDeclaredMethod("getHandlerList");
			return eventClass;
		} catch (NoSuchMethodException e) {
			if (eventClass.getSuperclass() != null
					&& !eventClass.getSuperclass().equals(Event.class)
					&& Event.class.isAssignableFrom(eventClass.getSuperclass())) {

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
	 * @param event
	 *            The event to fire
	 * @throws IllegalStateException
	 *             if the element cannot be added at this time due to capacity
	 *             restrictions
	 */
	public void fireEvent(Event event) throws IllegalStateException {
		try {
			dispatcher.dispatchEvent(event);
		} catch (IllegalStateException illegalState) {
			throw illegalState;
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * Creates {@link EventListener EventListeners} for a given {@link Listener
	 * listener}.
	 *
	 * @param listener
	 *            The listener to create EventListenrs for
	 * @return A map of events to a set of EventListeners belonging to it
	 */
	public Map<Class<? extends Event>, Set<EventListener>> createRegisteredListeners(
			Listener listener) {

		Map<Class<? extends Event>, Set<EventListener>> toReturn = new HashMap<Class<? extends Event>, Set<EventListener>>();
		Set<Method> methods;
		try {
			Method[] publicMethods = listener.getClass().getMethods();
			methods = new HashSet<Method>(publicMethods.length, Float.MAX_VALUE);
			for (Method method : publicMethods) {
				methods.add(method);
			}
			for (Method method : listener.getClass().getDeclaredMethods()) {
				methods.add(method);
			}
		} catch (NoClassDefFoundError e) {
			return toReturn;
		}

		// search the methods for listeners
		for (final Method method : methods) {
			final EventHandler handlerAnnotation = method
					.getAnnotation(EventHandler.class);
			if (handlerAnnotation == null)
				continue;
			final Class<?> checkClass;
			if (method.getParameterTypes().length != 1
					|| !Event.class.isAssignableFrom(checkClass = method
					.getParameterTypes()[0])) {
				continue;
			}
			final Class<? extends Event> eventClass = checkClass
					.asSubclass(Event.class);
			method.setAccessible(true);
			Set<EventListener> eventSet = toReturn.get(eventClass);
			if (eventSet == null) {
				eventSet = new HashSet<EventListener>();
				// add the listener methods to the list of events
				toReturn.put(eventClass, eventSet);
			}

			// creates a class to execute the listener for the event
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

	private void reportError(ErrorCode code, LoggingLevel level,
			String details) {
		String loggingNodeName = "";
		try {
			loggingNodeName = ResourceBundle.getBundle(
					ResourceLocation.LoggingNode, Localization.getLocale())
					.getString("nodeType");
		} catch (Exception excep) {
			// we are sort of screwed here, so just
			// dump as much as we can into the error stream and print any
			// Additional details to the error stream as well
			System.err.println(details);
			excep.printStackTrace(System.err);
			return;
		}
		if (nodeManager.isLoaded(loggingNodeName)){
			LoggingNode logger = (LoggingNode)
					nodeManager.getNode(loggingNodeName);
			logger.logError(code, level, details);
		}
		else {
			// we are sort of screwed here, so just
			// dump as much as we can into the error stream and print any
			// Additional details to the error stream as well
			System.err.println(code.getName());
			System.err.println(level.getName());
			System.err.println(details);
		}
	}

	@Override
	public String getType() {
		String type = "";
		try {
			type = resourceBundle.getString("nodeType");
		} catch (MissingResourceException missingResource) {
			reportError(ErrorCode.locale_resource_not_found,
					LoggingLevel.WARNING,
					"EventManager.getType()");
		} catch (ClassCastException classCast) {
			reportError(ErrorCode.locale_resource_wrong_type,
					LoggingLevel.WARNING,
					"EventManager.getType()");
		}
		return type;
	}

	@Override
	public double getVersion() {
		return version;
	}

	@Override
	public boolean enable() {
		this.enabled = true;
		try {
			this.onEnable();
		} catch (Exception e) {
			System.err.print(e.toString());
			reportError(ErrorCode.node_enable_fail,
					LoggingLevel.SEVERE,
					"EventManager.enable()");
			// better safe than sorry (probably did not initialize correctly)
			this.enabled = false;
			return false;
		}
		return true;
	}

	@Override
	public boolean disable() {
		this.enabled = false;
		try {
			this.onDisable();
		} catch (Exception e) {
			System.err.print(e.toString());
			reportError(ErrorCode.node_disable_fail,
					LoggingLevel.SEVERE,
					"EventManager.disable()");
			return false;
		}
		return true;
	}

	@Override
	public boolean reload() {
		if (this.enabled) {
			this.disable();
		}
		this.enable();
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void onEnable() {
		dispatcher = new EventDispatcher();
	}

	@Override
	public void onDisable() {
		dispatcher.terminate();
		try {
			dispatcher.join();
		} catch (InterruptedException e) {
			reportError(ErrorCode.thread_interrupted,
					LoggingLevel.WARNING,
					"EventManager.onDisable()");
			System.err.println(e.toString());
		}

	}

	@Override
	public void onLoad() {
		try {
			resourceBundle = ResourceBundle.getBundle(
					ResourceLocation.EventManager, Localization.getLocale());
		} catch (MissingResourceException missingResource) {
			reportError(ErrorCode.locale_resource_not_found,
					LoggingLevel.SEVERE,
					"EventManager.onLoad()");
		}
	}

	@Override
	public void onUnload() {
		this.resourceBundle = null;
		this.nodeManager = null;
	}

	@Override
	public void setNodeManager(NodeManager parent) {
		this.nodeManager = parent;
	}
}
