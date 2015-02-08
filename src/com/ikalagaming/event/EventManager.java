
package com.ikalagaming.event;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ikalagaming.core.Game;
import com.ikalagaming.core.packages.Package;
import com.ikalagaming.core.packages.PackageSettings;
import com.ikalagaming.core.packages.PackageState;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.events.Log;
import com.ikalagaming.logging.events.LogError;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * Manages events and listeners.
 */
public class EventManager implements Package {

	private EventDispatcher dispatcher;
	private PackageState state = PackageState.DISABLED;
	private final double version = 0.1;
	private HashMap<Class<? extends Event>, HandlerList> handlerMap;
	private String packageName = "event-manager";

	/**
	 * Registers event listeners in the supplied listener.
	 * 
	 * @param listener The listener to register
	 */
	public void registerEventListeners(Listener listener) {
		for (Map.Entry<Class<? extends Event>, Set<EventListener>> entry : createRegisteredListeners(
				listener).entrySet()) {
			getEventListeners(entry.getKey()).registerAll(entry.getValue());
		}
	}

	/**
	 * Unregisters event listeners in the supplied listener.
	 * 
	 * @param listener The listener to unregister
	 */
	public void unregisterEventListeners(Listener listener) {
		for (HandlerList list : handlerMap.values()) {
			list.unregisterAll(listener);
		}
	}

	/**
	 * Returns a {@link HandlerList} for a give event type. Creates one if none
	 * exist.
	 * 
	 * @param type the type of event to find handlers for
	 */
	private HandlerList getEventListeners(Class<? extends Event> type) {
		if (!handlerMap.containsKey(type)) {
			handlerMap.put(type, new HandlerList());
		}
		return handlerMap.get(type);
	}

	/**
	 * Sends the {@link Event event} to all of its listeners.
	 * 
	 * @param event The event to fire
	 * @throws IllegalStateException if the element cannot be added at this time
	 *             due to capacity restrictions
	 */
	public void fireEvent(Event event) throws IllegalStateException {
		if (!isEnabled()) {
			return;
		}
		try {
			dispatcher.dispatchEvent(event);
		}
		catch (IllegalStateException illegalState) {
			throw illegalState;
		}
		catch (Exception e) {
			if (event instanceof Log || event instanceof LogError) {
				e.printStackTrace(System.err);
			}
			else {
				LogError err =
						new LogError(SafeResourceLoader.getString(
								"EVT_QUEUE_FULL",
								Game.getPackageManager().getResourceBundle(),
								"Event queue full"),
								"EventManager.fireEvent(Event)",
								LoggingLevel.WARNING, this);
				dispatcher.dispatchEvent(err);
			}

		}
	}

	/**
	 * Creates {@link EventListener EventListeners} for a given {@link Listener
	 * listener}.
	 * 
	 * @param listener The listener to create EventListenrs for
	 * @return A map of events to a set of EventListeners belonging to it
	 */
	private Map<Class<? extends Event>, Set<EventListener>> createRegisteredListeners(
			Listener listener) {

		Map<Class<? extends Event>, Set<EventListener>> toReturn =
				new HashMap<Class<? extends Event>, Set<EventListener>>();
		Set<Method> methods;
		try {
			Method[] publicMethods = listener.getClass().getMethods();
			methods =
					new HashSet<Method>(publicMethods.length, Float.MAX_VALUE);
			for (Method method : publicMethods) {
				methods.add(method);
			}
			for (Method method : listener.getClass().getDeclaredMethods()) {
				methods.add(method);
			}
		}
		catch (NoClassDefFoundError e) {
			return toReturn;
		}

		// search the methods for listeners
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

	/**
	 * Returns the handlerlist for the given event.
	 * 
	 * @param event the class to find handlers for
	 * @return the handlerlist for that class
	 */
	public HandlerList getHandlers(Event event) {
		return getEventListeners(event.getClass());
	}

	@Override
	public String getName() {
		return packageName;
	}

	@Override
	public double getVersion() {
		return version;
	}

	@Override
	public boolean enable() {
		if (isEnabled()) {
			return false;
		}
		state = PackageState.ENABLING;
		try {
			this.onEnable();
		}
		catch (Exception e) {
			LogError err =
					new LogError(SafeResourceLoader.getString(
							"package_enable_fail",
							Game.getPackageManager().getResourceBundle(),
							"Package failed to enable"),
							"EventManager.enable()", LoggingLevel.SEVERE, this);
			dispatcher.dispatchEvent(err);
			e.printStackTrace(System.err);
			// better safe than sorry (probably did not initialize correctly)
			state = PackageState.CORRUPTED;
			return false;
		}
		return true;
	}

	@Override
	public boolean disable() {
		if (!isEnabled()) {
			return false;
		}
		state = PackageState.DISABLING;
		try {
			this.onDisable();
		}
		catch (Exception e) {
			LogError err =
					new LogError(SafeResourceLoader.getString(
							"package_disable_fail",
							Game.getPackageManager().getResourceBundle(),
							"Package failed to disable"),
							"EventManager.disable()", LoggingLevel.SEVERE, this);
			dispatcher.dispatchEvent(err);
			state = PackageState.CORRUPTED;
			return false;
		}
		return true;
	}

	@Override
	public boolean reload() {
		state = PackageState.UNLOADING;
		if (!PackageSettings.DISABLE_ON_UNLOAD) {
			disable();
		}
		if (state == PackageState.ENABLED) {
			disable();
		}
		onLoad();
		enable();// The event system does not need manual enabling
		return true;
	}

	@Override
	public boolean isEnabled() {
		if (state == PackageState.ENABLED) {
			return true;
		}
		return false;
	}

	@Override
	public void onEnable() {
		dispatcher.start();
		state = PackageState.ENABLED;
	}

	@Override
	public void onDisable() {
		for (HandlerList l : handlerMap.values()) {
			l.unregisterAll();
		}
		handlerMap.clear();

		dispatcher.terminate();
		try {
			dispatcher.join();
		}
		catch (InterruptedException e) {
			LogError err =
					new LogError(SafeResourceLoader.getString(
							"thread_interrupted",
							Game.getPackageManager().getResourceBundle(),
							"Thread interrupted"), "EventManager.onDisable()",
							LoggingLevel.SEVERE, this);
			dispatcher.dispatchEvent(err);
			e.printStackTrace(System.err);
			state = PackageState.CORRUPTED;
		}
		state = PackageState.DISABLED;
	}

	@Override
	public void onLoad() {
		state = PackageState.LOADING;
		state = PackageState.DISABLED;
		dispatcher = new EventDispatcher(this);
		handlerMap = new HashMap<Class<? extends Event>, HandlerList>();
	}

	@Override
	public void onUnload() {
		state = PackageState.UNLOADING;
		if (state == PackageState.ENABLED) {
			disable();
			state = PackageState.UNLOADING;
		}
		state = PackageState.PENDING_REMOVAL;
	}

	@Override
	public Set<Listener> getListeners() {
		return new HashSet<Listener>();
	}

	@Override
	public PackageState getPackageState() {
		return state;
	}

}
