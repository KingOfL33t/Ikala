package com.ikalagaming.entity;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.ikalagaming.entity.component.Component;
import com.ikalagaming.logging.Logging;
import com.ikalagaming.util.NameRegistry;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * An entity that can be represented in the game world which has a unique name.
 *
 * @author Ches Burks
 *
 */
public class Entity {
	private static NameRegistry registry = new NameRegistry();

	private static final String resourceLocation =
			"com.ikalagaming.entity.resources.Entity";
	private final String name;

	protected HashMap<String, Component> components;

	private ReentrantLock componentLock;

	/**
	 * Constructs an entity with the name Entity.
	 */
	public Entity() {
		this("Entity");
	}

	/**
	 * Creates an entity with the given name, followed by a dash and its id.
	 *
	 * @param nameHint the base name
	 */
	public Entity(String nameHint) {
		this.componentLock = new ReentrantLock();
		this.name = Entity.registry.registerName(nameHint);
		this.components = new HashMap<>();
		String message =
				SafeResourceLoader.getString("ENTITY_CREATED",
						Entity.resourceLocation, "Created entity $NAME");
		message = message.replaceFirst("\\$NAME", this.name);
		Logging.finest("entity", message);
	}

	/**
	 * Adds the specified component to this entity if it does not already have
	 * one.
	 *
	 * @param toAdd the component to add
	 */
	public void addComponent(Component toAdd) {
		this.componentLock.lock();
		try {
			if (this.components.containsKey(toAdd.getType())) {
				return;
			}
			this.components.put(toAdd.getType(), toAdd);
		}
		finally {
			this.componentLock.unlock();
		}
	}

	/**
	 * Clears out itself and its children from the scene and unregisters the
	 * name of this object
	 */
	public void destroy() {
		// when objects are deleted, unregister their id
		final int dashPos = this.name.lastIndexOf("-");
		Entity.registry.unregisterName(this.name.substring(0, dashPos));

		String freedName =
				SafeResourceLoader.getString("NAME_FREED",
						Entity.resourceLocation,
						"Freed the entity name $NAME for re-use");
		freedName = freedName.replaceFirst("\\$NAME", this.name);
		Logging.finest("entity", freedName);
	}

	/**
	 * Returns the Component of the specified type belonging to this entity. If
	 * no such component exists, null is returned instead. To check if a
	 * component exists, the {@link #hasComponent(String)} method can be used.
	 *
	 * @param type the type of component that should be returned.
	 * @return the component who has a componentType that matches the supplied
	 *         string or null if none exist
	 */
	public Component getComponent(String type) {
		Component ret;

		this.componentLock.lock();
		try {
			ret = this.components.get(type);
		}
		finally {
			this.componentLock.unlock();
		}
		return ret;
	}

	/**
	 * Returns the name of this entity. Names are unique, but will be recycled
	 * when entities are deleted.
	 *
	 * @return the entities name (including trailing dash and id)
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns true if there is a Component of the specified type that belongs
	 * to this entity.
	 *
	 * @param type the type of component that should be returned.
	 * @return true if this entity owns one of the specified components, false
	 *         otherwise
	 */
	public boolean hasComponent(final String type) {
		boolean result = false;
		this.componentLock.lock();
		try {
			result = this.components.containsKey(type);
		}
		finally {
			this.componentLock.unlock();
		}
		return result;
	}

	/**
	 * Initializes the entity and its components.
	 */
	public void init() {}

	/**
	 * Removes the specified component from this entity.
	 *
	 * @param type the type of component to remove
	 */
	public void removeComponent(final String type) {
		this.componentLock.lock();
		try {
			this.components.remove(type);
		}
		finally {
			this.componentLock.unlock();
		}
	}

}
