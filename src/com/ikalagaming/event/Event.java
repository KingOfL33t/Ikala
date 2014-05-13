package com.ikalagaming.event;

/**
 * An abstract event to be extended.
 */
public abstract class Event {
	private String name;

	/**
	 * Returns the name of the event.
	 * 
	 * @return The events name (simple class name if no name is specified)
	 */
	public String getEventName() {
		if (name == null) {
			name = getClass().getSimpleName();
		}
		return name;
	}
	
	/**
	 * Returns the {@link HandlerList handler list}.
	 * 
	 * @return The handler list.
	 * 
	 */
	public abstract HandlerList getHandlers();

}
