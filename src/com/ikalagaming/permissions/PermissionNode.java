package com.ikalagaming.permissions;

import java.util.Collections;
import java.util.LinkedList;

/**
 * A permission node can contain mappings of
 * 
 * @deprecated this will be removed
 * @author Ches Burks
 * 
 */
public class PermissionNode implements Comparable<PermissionNode> {
	/**
	 * A list of subnodes. These are sorted alphabetically.
	 */
	private LinkedList<PermissionNode> subnodes =
			new LinkedList<PermissionNode>();
	private static final DefaultPermissionValue DEFAULT_PERMISSION =
			DefaultPermissionValue.FALSE;
	private boolean value;
	private String name;
	private PermissionNode parent;
	private boolean isLeaf;
	private String description;
	private DefaultPermissionValue defaultValue = DEFAULT_PERMISSION;

	/**
	 * Constructs a new PermissionNode with the given information. The children,
	 * description and default value default to empty.
	 * 
	 * @param name the name of the permission
	 */
	public PermissionNode(String name) {
		this(name, null, null);
	}

	/**
	 * Constructs a new PermissionNode with the given information. The children
	 * and default value default to empty.
	 * 
	 * @param name the name of the permission
	 * @param description a brief description of the permission
	 */
	public PermissionNode(String name, String description) {
		this(name, description, null);
	}

	/**
	 * Constructs a new PermissionNode with the given information. The children
	 * and description default to empty.
	 * 
	 * @param name the name of the permission
	 * @param defaultValue the default value for the permission
	 */
	public PermissionNode(String name, DefaultPermissionValue defaultValue) {
		this(name, null, defaultValue);
	}

	/**
	 * Constructs a new PermissionNode with the given information.
	 * 
	 * @param name the name of the permission
	 * @param description a brief description of the permission
	 * @param defaultValue the default value for the permission
	 */
	public PermissionNode(String name, String description,
			DefaultPermissionValue defaultValue) {
		this.name = name;
		this.description = (description == null) ? "" : description;
		if (defaultValue != null) {
			this.defaultValue = defaultValue;
		}
	}

	/**
	 * Adds the given node to this node. Sorts this node's subnodes and
	 * recursively sorts the added node's subnodes. Fails if the specified node
	 * already exists. This nodes leaf status is updated if necessary.
	 * 
	 * @param toAdd the node to add
	 */
	public void addNode(PermissionNode toAdd) {
		if (subnodes.contains(toAdd)) {
			return;
		}
		subnodes.add(toAdd);
		sortNonRecursively();
		toAdd.sort();
		isLeaf = false;
	}

	/**
	 * Removes the given node if it exists in this nodes subnodes. This nodes
	 * leaf status is updated if necessary.
	 * 
	 * @param toRemove the PermissionNode to remove
	 */
	public void removeNode(PermissionNode toRemove) {
		if (subnodes.contains(toRemove)) {
			subnodes.remove(toRemove);
		}
		if (subnodes.isEmpty()) {
			isLeaf = true;
		}
	}

	/**
	 * Returns true if the given node is contained anywhere in this node. It
	 * must have the same full name in order to match with a permission node
	 * contained in this tree.
	 * 
	 * @param permissionNode the node to check for
	 * @return true if this contains the given node
	 */
	public boolean contains(PermissionNode permissionNode) {
		for (String s : getSubnodeFullNamesRecursively()) {
			if (permissionNode.getFullName() == s) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a list of subnodes owned by this node. This will be null if it is
	 * a leaf node.
	 * 
	 * @see #isLeaf()
	 * @return any subnodes this node contains
	 */
	public LinkedList<PermissionNode> getSubnodes() {
		return subnodes;
	}

	/**
	 * Returns a list of all subnodes attached to this node. Nodes attached to
	 * the subnodes are not included (this is the first level down on the tree).
	 * This list is sorted alphabetically.
	 * 
	 * @return the list of subnodes
	 */
	public LinkedList<String> getSubnodeNames() {
		LinkedList<String> names = new LinkedList<String>();
		for (PermissionNode node : getSubnodes()) {
			names.add(node.getName());
		}
		Collections.sort(names);
		return names;
	}

	/**
	 * Returns a list of the full names of all subnodes, recursively. This list
	 * is sorted alphabetically.
	 * 
	 * @return the list of all subnodes full names
	 */
	public LinkedList<String> getSubnodeFullNamesRecursively() {
		LinkedList<String> names = new LinkedList<String>();
		for (PermissionNode node : getSubnodesRecursively()) {
			names.add(node.getFullName());
		}
		Collections.sort(names);
		return names;
	}

	/**
	 * Returns a list of all nodes in the tree of subnodes belonging to this
	 * node. This is recursive.
	 * 
	 * @return all subnodes belonging to this node
	 */
	public LinkedList<PermissionNode> getSubnodesRecursively() {
		LinkedList<PermissionNode> toReturn = new LinkedList<PermissionNode>();
		if (!isLeaf()) {
			toReturn.addAll(getSubnodes());
			for (PermissionNode node : getSubnodes()) {
				if (!node.isLeaf()) {
					toReturn.addAll(node.getSubnodes());
				}
			}
		}
		return toReturn;
	}

	/**
	 * Returns true if this is a leaf node (that is, has no subnodes). If this
	 * has nodes belonging to it, it is not a leaf node.
	 * 
	 * @return true if this has no subnodes
	 */
	public boolean isLeaf() {
		return isLeaf;
	}

	/**
	 * Gets the default value of this permission.
	 * 
	 * @return default value of this permission.
	 */
	public DefaultPermissionValue getDefault() {
		return defaultValue;
	}

	/**
	 * Sets the default value of this permission.
	 * <p>
	 * This will not be saved to disk, and is a temporary operation until the
	 * server reloads permissions. Changing this default will cause all
	 * {@link PermissionHolder}s that contain this permission to recalculate
	 * their permissions
	 * 
	 * @param value the new default to set
	 */
	public void setDefault(DefaultPermissionValue value) {
		if (defaultValue == null) {
			throw new IllegalArgumentException("Default value cannot be null");
		}
		defaultValue = value;
		// TODO recalculate groups and members with this permission
	}

	/**
	 * Returns true if this node has a parent node. If this is the root of a
	 * tree of nodes, it will not have a parent. This returns false if
	 * {@link #getParent()} will return null.
	 * 
	 * @return true if this node has a parent
	 */
	public boolean hasParent() {
		if (parent == null) {
			return false;
		}
		else {
			return true;
		}
	}

	/**
	 * Returns the value associated with this node. This value may be different
	 * than the values in subnodes if the subnodes exist.
	 * 
	 * @return the value for this node
	 */
	public boolean getValue() {
		return value;
	}

	/**
	 * Returns the parent node, if one exists. If there is no parent node,
	 * returns null.
	 * 
	 * @see #hasParent()
	 * @return the parent node, if any
	 */
	public PermissionNode getParent() {
		return parent;
	}

	/**
	 * Returns the name of the permission. This is just the name of the current
	 * node. The full path for the permission includes the names of parent nodes
	 * if they exist.
	 * 
	 * @return the name of this node
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the full name of the permission. This is the name of the root
	 * node for this permission tree, followed by subnode names all the way down
	 * to this node name. Names are separated by a period (".").
	 * 
	 * @return The full name of this permission
	 */
	public String getFullName() {
		PermissionNode current = this;
		String name = getName();
		while (current.hasParent()) {
			// change current to the parent node
			current = current.getParent();
			// add the name of the parent to the start of the name being built
			name = current.getName() + "." + name;
		}
		return name;
	}

	/**
	 * Returns a brief description of this permission, if set.
	 * 
	 * @return breif description of this permission
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * If the node is not a leaf, sorts the subnodes alphabetically. This is
	 * recursive and sorts all subnodes.
	 */
	public void sort() {
		if (!isLeaf()) {
			Collections.sort(subnodes);
			for (PermissionNode node : subnodes) {
				node.sort();
			}
		}
	}

	/**
	 * Sorts just the top layer of subnodes.
	 */
	private void sortNonRecursively() {
		if (!isLeaf()) {
			Collections.sort(subnodes);
		}
	}

	/**
	 * Compares the names of the nodes. This is the same thing as alphabetically
	 * sorting the strings representing the names of the nodes.
	 */
	@Override
	public int compareTo(PermissionNode o) {
		return getName().compareTo(o.getName());
	}

}
