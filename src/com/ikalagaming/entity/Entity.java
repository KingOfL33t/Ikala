package com.ikalagaming.entity;

import java.util.HashMap;

import com.ikalagaming.util.DuplicateEntry;
import com.jme3.scene.Node;

/**
 * An entity that can be represented in the game world which has a unique name.
 * 
 * @author Ches Burks
 *
 */
public class Entity {
	private static HashMap<String, IntegerTree> registeredNames =
			new HashMap<String, IntegerTree>();

	@Override
	protected void finalize() throws Throwable {
		int dashPos = name.lastIndexOf("-");
		int id = Integer.parseInt(name.substring(dashPos));
		registeredNames.get(name.substring(0, dashPos)).remove(id);
		// when objects are deleted, unregister their id
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

	private Node rootNode;
	private final String name;

	/**
	 * Creates an entity with the given name, followed by a dash and its id.
	 * 
	 * @param nameHint the base name
	 */
	public Entity(String nameHint) {
		name = getValidName(nameHint);
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

}
