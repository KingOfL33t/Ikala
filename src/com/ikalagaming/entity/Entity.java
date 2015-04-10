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
 * When the entity is dereferenced/garbage collected, it automatically removes
 * itself from the scene and unregisters its name for re-use on other entities.
 * This means that if you do not keep a reference somewhere to the object, it
 * will remove itself from the game after a short while (whenever GC decides to
 * run on the object).
 * 
 * @author Ches Burks
 *
 */
public class Entity {
	private static HashMap<String, IntegerTree> registeredNames =
			new HashMap<String, IntegerTree>();

	@Override
	protected void finalize() throws Throwable {

		// Detach the object from the scene
		root.detachAllChildren();
		if (root.getParent() != null) {
			root.getParent().detachChild(root);
		}

		String message =
				SafeResourceLoader.getString("ENTITY_DESTROYED",
						resourceLocation, "Destroyed entity $NAME");
		message = message.replaceFirst("\\$NAME", name);
		Log log = new Log(message, LoggingLevel.FINEST, "entity");
		Game.getEventManager().fireEvent(log);

		// when objects are deleted, unregister their id
		int dashPos = name.lastIndexOf("-");
		int id = Integer.parseInt(name.substring(dashPos));
		registeredNames.get(name.substring(0, dashPos)).remove(id);

		String freedName =
				SafeResourceLoader.getString("NAME_FREED", resourceLocation,
						"Freed the entity name $NAME for re-use");
		freedName = freedName.replaceFirst("\\$NAME", name);
		Log logName = new Log(freedName, LoggingLevel.FINEST, "entity");
		Game.getEventManager().fireEvent(logName);

		super.finalize();
	}

	private static String getValidName(String nameHint) {
		String ret = nameHint;
		IntegerTree tree;
		int addition;
		if (registeredNames.containsKey(nameHint)) {
			tree = registeredNames.get(nameHint);
			addition = tree.getSmallestUnusedInt();
		}
		else {
			tree = new IntegerTree();
			registeredNames.put(nameHint, tree);
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

	private Node root;
	private final String name;
	private static final String resourceLocation =
			"com.ikalagaming.entity.resources.Entity";

	/**
	 * Creates an entity with the given name, followed by a dash and its id.
	 * 
	 * @param nameHint the base name
	 * @param app the application this entity was created by
	 */
	public Entity(String nameHint) {
		name = getValidName(nameHint);
		String message =
				SafeResourceLoader.getString("ENTITY_CREATED",
						resourceLocation, "Created entity $NAME");
		message = message.replaceFirst("\\$NAME", name);
		Log log = new Log(message, LoggingLevel.FINEST, "entity");
		Game.getEventManager().fireEvent(log);
		root = new Node(name + "-" + "rootNode");
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

		root.attachChild(cube);
	}

	/**
	 * Returns the name of this entity. Names are unique, but will be recycled
	 * when entities are deleted.
	 * 
	 * @return the entities name (including trailing dash and id)
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return this entities root node.
	 * 
	 * @return the root node for this entity
	 */
	public Node getRoot() {
		return root;
	}

}
