package com.ikalagaming.entity;

import java.util.ArrayList;
import java.util.HashMap;

import com.ikalagaming.entity.component.Component;
import com.ikalagaming.entity.effects.Effect;
import com.ikalagaming.event.EventManager;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.events.Log;
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

	private final String name;
	private static final String resourceLocation =
			"com.ikalagaming.entity.resources.Entity";
	private ArrayList<Effect> currentEffects;

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
		this.name = Entity.registry.registerName(nameHint);
		this.components = new HashMap<>();
		this.currentEffects = new ArrayList<>();
		String message =
				SafeResourceLoader.getString("ENTITY_CREATED",
						Entity.resourceLocation, "Created entity $NAME");
		message = message.replaceFirst("\\$NAME", this.name);
		Log log = new Log(message, LoggingLevel.FINEST, "entity");
		EventManager.getInstance().fireEvent(log);
	}

	protected HashMap<String, Component> components;

	/**
	 * Initializes the entity and its components.
	 */
	public void init() {}

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
		Log logName = new Log(freedName, LoggingLevel.FINEST, "entity");
		EventManager.getInstance().fireEvent(logName);
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
		if (this.components.containsKey(type)) {
			return this.components.get(type);
		}
		return null;
	}

	/**
	 * Returns true if there is a Component of the specified type that belongs
	 * to this entity.
	 *
	 * @param type the type of component that should be returned.
	 * @return true if this entity owns one of the specified components, false
	 *         otherwise
	 */
	public boolean hasComponent(String type) {
		return this.components.containsKey(type);
	}

	/**
	 * Adds the specified component to this entity if it does not already have
	 * one.
	 *
	 * @param toAdd the component to add
	 */
	public void addComponent(Component toAdd) {
		if (this.components.containsKey(toAdd.getType())) {
			return;
		}
		this.components.put(toAdd.getType(), toAdd);
	}

	/**
	 * Removes the specified component from this entity.
	 *
	 * @param type the type of component to remove
	 */
	public void removeComponent(String type) {
		this.components.remove(type);
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
	 * Calls the effect's {@link Effect#tick(Entity) tick} method using this
	 * entity as a target.
	 *
	 * @param effect
	 */
	public void applyEffect(Effect effect) {
		effect.tick(this);
	}

}
