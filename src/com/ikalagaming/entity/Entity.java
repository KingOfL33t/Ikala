package com.ikalagaming.entity;

import java.util.HashMap;

import com.ikalagaming.core.Game;
import com.ikalagaming.entity.component.Component;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.events.Log;
import com.ikalagaming.util.DuplicateEntry;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * An entity that can be represented in the game world which has a unique name.
 *
 * @author Ches Burks
 *
 */
public class Entity {
	private static String getValidName(String nameHint) {
		String ret = nameHint;
		IntegerTree tree;
		int addition;
		if (Entity.registeredNames.containsKey(nameHint)) {
			tree = Entity.registeredNames.get(nameHint);
			addition = tree.getSmallestUnusedInt();
		}
		else {
			tree = new IntegerTree();
			Entity.registeredNames.put(nameHint, tree);
			addition = 0;
		}
		try {
			tree.insert(addition);
		}
		catch (DuplicateEntry e) {
			System.err.println(e.getCause());
			e.printStackTrace(System.err);
		}
		ret = ret + "-" + addition;
		return ret;
	}

	private static HashMap<String, IntegerTree> registeredNames =
			new HashMap<>();

	private final String name;
	private static final String resourceLocation =
			"com.ikalagaming.entity.resources.Entity";

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
		this.name = Entity.getValidName(nameHint);
		this.components = new HashMap<>();
		String message =
				SafeResourceLoader.getString("ENTITY_CREATED",
						Entity.resourceLocation, "Created entity $NAME");
		message = message.replaceFirst("\\$NAME", this.name);
		Log log = new Log(message, LoggingLevel.FINEST, "entity");
		Game.getEventManager().fireEvent(log);
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
		int dashPos = this.name.lastIndexOf("-");
		int id = Integer.parseInt(this.name.substring(dashPos));
		Entity.registeredNames.get(this.name.substring(0, dashPos)).remove(id);

		String freedName =
				SafeResourceLoader.getString("NAME_FREED",
						Entity.resourceLocation,
						"Freed the entity name $NAME for re-use");
		freedName = freedName.replaceFirst("\\$NAME", this.name);
		Log logName = new Log(freedName, LoggingLevel.FINEST, "entity");
		Game.getEventManager().fireEvent(logName);
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
	 * Returns the name of this entity. Names are unique, but will be recycled
	 * when entities are deleted.
	 *
	 * @return the entities name (including trailing dash and id)
	 */
	public String getName() {
		return this.name;
	}

}
