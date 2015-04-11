package com.ikalagaming.entity;

import java.util.HashMap;

import com.ikalagaming.core.Game;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.events.Log;
import com.ikalagaming.util.DuplicateEntry;
import com.ikalagaming.util.SafeResourceLoader;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

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

	private Node root;

	private final String name;
	private static final String resourceLocation =
			"com.ikalagaming.entity.resources.Entity";

	/**
	 * Creates an entity with the given name, followed by a dash and its id.
	 *
	 * @param nameHint the base name
	 */
	public Entity(String nameHint) {
		this.name = Entity.getValidName(nameHint);
		String message =
				SafeResourceLoader.getString("ENTITY_CREATED",
						Entity.resourceLocation, "Created entity $NAME");
		message = message.replaceFirst("\\$NAME", this.name);
		Log log = new Log(message, LoggingLevel.FINEST, "entity");
		Game.getEventManager().fireEvent(log);
		this.root = new Node(this.name + "-" + "rootNode");
	}

	/**
	 * Clears out itself and its children from the scene and unregisters the
	 * name of this object
	 */
	public void destroy() {
		// Detach the object from the scene
		this.root.detachAllChildren();
		if (this.root.getParent() != null) {
			this.root.getParent().detachChild(this.root);
		}

		String message =
				SafeResourceLoader.getString("ENTITY_DESTROYED",
						Entity.resourceLocation, "Destroyed entity $NAME");
		message = message.replaceFirst("\\$NAME", this.name);
		Log log = new Log(message, LoggingLevel.FINEST, "entity");
		Game.getEventManager().fireEvent(log);

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
	 * Returns the name of this entity. Names are unique, but will be recycled
	 * when entities are deleted.
	 *
	 * @return the entities name (including trailing dash and id)
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return this entities root node.
	 *
	 * @return the root node for this entity
	 */
	public Node getRoot() {
		return this.root;
	}

	/**
	 * Creates a box to represent the entity.
	 *
	 * @param app the app this entity belongs to
	 */
	public void init(SimpleApplication app) {
		Box box1 = new Box(1, 1, 1);
		Geometry cube = new Geometry("Box", box1);
		cube.setLocalTranslation(new Vector3f(0, 1, 0));
		Material mat1 =
				new Material(app.getAssetManager(),
						"Common/MatDefs/Misc/Unshaded.j3md");
		mat1.setColor("Color", ColorRGBA.Blue);
		cube.setMaterial(mat1);

		this.root.attachChild(cube);
	}

}
