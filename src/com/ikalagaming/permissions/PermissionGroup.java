
package com.ikalagaming.permissions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A group that can be assigned permissions. Entities that are members of these
 * groups are assigned the permissions of the group, as well as any permissions
 * specific to that entity which may have been assigned. <br>
 * Groups may have a parent group which they inherit permissions from.
 * Permissions set in a group override permissions set in a parent group if
 * there are any conflicts.
 *
 * @author Ches Burks
 *
 */
public class PermissionGroup implements PermissionHolder {

	private static final PermissionGroup ROOT = new PermissionGroup("root",
			null);
	private final String groupName;
	private final PermissionGroup parent;
	/**
	 * Permissions that are set for this group. Inherits permissions from the
	 * parent, but children override parents for permission values if they both
	 * have a permission. The boolean for each permission is true if that
	 * permission is granted to the group, and false if the permission is
	 * revoked from the group.
	 */
	private HashMap<Permission, Boolean> permissions;
	private final String description;

	private static HashMap<String, PermissionGroup> groupsByName =
			new HashMap<String, PermissionGroup>();

	/**
	 * Constructs a new {@link PermissionGroup} with the supplied information.
	 * The parent defaults to "root", and the description to an empty string.
	 * <p>
	 * The name is a unique name for the group, which matches the name in
	 * configuration files. Errors are thrown if the name is null or empty, or
	 * if a group with the supplied name already exists.<br>
	 * Example: "heroes" or "moderator"
	 * </p>
	 * <p>
	 * The permissions are a list of permissions and a boolean indicating if the
	 * group is allowed or denied that permission. If the permission is mapped
	 * to true, the group has that permission, if it is set to false, it does
	 * not. Permissions that are not set use their default value.<br>
	 * Example: "entity.jump" is mapped to "true"
	 * </p>
	 *
	 * @param name The name of the group
	 * @param permissions Permissions this group is assigned
	 */
	public PermissionGroup(String name, HashMap<Permission, Boolean> permissions) {
		this(name, null, null, permissions);
	}

	/**
	 * Constructs a new {@link PermissionGroup} with the supplied information.
	 * The description defaults to an empty string.
	 * <p>
	 * The name is a unique name for the group, which matches the name in
	 * configuration files. Errors are thrown if the name is null or empty, or
	 * if a group with the supplied name already exists.<br>
	 * Example: "heroes" or "moderator"
	 * </p>
	 * <p>
	 * The parent group is the group this group inherits permissions from.
	 * Permissions set in this group override all parent permissions. If no
	 * parent is set, it will default to the "root" group.<br>
	 * Example: "npcs" or "heroes"
	 * </p>
	 * <p>
	 * The permissions are a list of permissions and a boolean indicating if the
	 * group is allowed or denied that permission. If the permission is mapped
	 * to true, the group has that permission, if it is set to false, it does
	 * not. Permissions that are not set use their default value.<br>
	 * Example: "entity.jump" is mapped to "true"
	 * </p>
	 *
	 * @param name The name of the group
	 * @param parent The parent of this group
	 * @param permissions Permissions this group is assigned
	 */
	public PermissionGroup(String name, PermissionGroup parent,
			HashMap<Permission, Boolean> permissions) {
		this(name, null, parent, permissions);
	}

	/**
	 * Constructs a new {@link PermissionGroup} with the supplied information.
	 * The parent defaults to "root".
	 * <p>
	 * The name is a unique name for the group, which matches the name in
	 * configuration files. Errors are thrown if the name is null or empty, or
	 * if a group with the supplied name already exists.<br>
	 * Example: "heroes" or "moderator"
	 * </p>
	 * <p>
	 * The description is a brief explanation of the groups purpose. Who is in
	 * the group or what it adds/removes from members may be a description. If
	 * no description is provided, it defaults to an empty string. <br>
	 * Examples: "Players that may use all transportation systems" or "Entities
	 * that should not die"
	 * </p>
	 * <p>
	 * The permissions are a list of permissions and a boolean indicating if the
	 * group is allowed or denied that permission. If the permission is mapped
	 * to true, the group has that permission, if it is set to false, it does
	 * not. Permissions that are not set use their default value.<br>
	 * Example: "entity.jump" is mapped to "true"
	 * </p>
	 *
	 * @param name The name of the group
	 * @param description The description of the group
	 * @param permissions Permissions this group is assigned
	 */
	public PermissionGroup(String name, String description,
			HashMap<Permission, Boolean> permissions) {
		this(name, description, null, permissions);
	}

	/**
	 * Constructs a new {@link PermissionGroup} with the supplied information.
	 * <p>
	 * The name is a unique name for the group, which matches the name in
	 * configuration files. Errors are thrown if the name is null or empty, or
	 * if a group with the supplied name already exists.<br>
	 * Example: "heroes" or "moderator"
	 * </p>
	 * <p>
	 * The description is a brief explanation of the groups purpose. Who is in
	 * the group or what it adds/removes from members may be a description. If
	 * no description is provided, it defaults to an empty string. <br>
	 * Examples: "Players that may use all transportation systems" or "Entities
	 * that should not die"
	 * </p>
	 * <p>
	 * The parent group is the group this group inherits permissions from.
	 * Permissions set in this group override all parent permissions. If no
	 * parent is set, it will default to the "root" group.<br>
	 * Example: "npcs" or "heroes"
	 * </p>
	 * <p>
	 * The permissions are a list of permissions and a boolean indicating if the
	 * group is allowed or denied that permission. If the permission is mapped
	 * to true, the group has that permission, if it is set to false, it does
	 * not. Permissions that are not set use their default value.<br>
	 * Example: "entity.jump" is mapped to "true"
	 * </p>
	 *
	 * @param name The name of the group
	 * @param description The description of the group
	 * @param parent The parent of this group
	 * @param permissions Permissions this group is assigned
	 */
	public PermissionGroup(String name, String description,
			PermissionGroup parent, HashMap<Permission, Boolean> permissions) {
		if (name == null || name.isEmpty()) {
			// TODO throw error
			throw new Error("");
		}
		if (groupExists(name)) {
			// TODO throw an error

		}
		this.groupName = name;

		if (description == null) {
			this.description = "";
		}
		else {
			this.description = description.isEmpty() ? "" : description;
		}
		if (parent != null) {
			this.parent = parent;
		}
		else {
			if (!this.equals(ROOT)) {
				this.parent = ROOT;
			}
			else {
				// the root node
				this.parent = null;
			}
		}
		this.permissions =
				permissions == null ? new HashMap<Permission, Boolean>()
						: permissions;

		//TODO calculate permissions
		groupsByName.put(name, this);

	}

	/**
	 * Returns true if this has a parent. If this groups parent is null, returns
	 * false.
	 *
	 * @return true if this group has a parent group
	 */
	public boolean hasParent() {
		return parent != null;
	}

	/**
	 * Return the parent, if it exists. May return null.
	 *
	 * @return this groups parent group
	 */
	public PermissionGroup getParent() {
		return parent;
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		for (Permission permission : permissions.keySet()) {
			if (permission.contains(perm)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean hasPermission(Permission perm) {
		ArrayList<Permission> containers = new ArrayList<Permission>();
		boolean duplicateEntryFlag = false;
		for (Permission firstLevel : permissions.keySet()) {
			if (firstLevel.getName() == perm.getName()) {
				return permissions.get(firstLevel);
			}
			else if (firstLevel.contains(perm)) {
				containers.add(firstLevel);
				duplicateEntryFlag = true;
			}
		}
		// there was more than one subtree that has the permission
		if (duplicateEntryFlag) {
			Permission lowest = null;
			int lowestVal = Integer.MAX_VALUE;
			int currentVal = 0;
			for (Permission p : containers) {
				currentVal = getDepth(p, perm, 0);
				if (currentVal <= lowestVal) {
					/*
					 * If two values have the same depth, this will end up being
					 * the last value added that has that level.
					 */
					lowestVal = currentVal;
					lowest = p;
				}
			}
			if (lowest != null && permissions.containsKey(lowest)) {
				return permissions.get(lowest);
			}
		}

		return perm.getDefault();
	}

	private int getDepth(Permission container, Permission child, int oldDepth) {
		if (child.getName().equals(container.getName())) {
			return oldDepth;
		}
		if (container.contains(child)) {
			for (String s : container.getChildPermissions().keySet()) {
				if (Permission.getByName(s).contains(child)) {
					return getDepth(Permission.getByName(s), child,
							oldDepth + 1);
				}
			}

		}
		// It did not contain the value, but should have.
		// make sure it is not going to be the least depth
		return Integer.MAX_VALUE - 1;
	}

	/**
	 * Returns true if a group with the given name has been created.
	 *
	 * @param name the name of the group
	 * @return true if the group exists
	 */
	public static boolean groupExists(String name) {
		return groupsByName.containsKey(name);
	}

	/**
	 * If the group {@link #groupExists(String) exists}, returns the group. If
	 * the group has not been created, returns null.
	 *
	 * @param name the name of the group to return
	 * @return the group, or null if no group with that name exists
	 */
	public static PermissionGroup getGroupByName(String name) {
		if (groupExists(name)) {
			return groupsByName.get(name);
		}
		return null;
	}

	@Override
	public void recalculatePermissions() {}

	/**
	 * Returns the name of this group.
	 *
	 * @return the groups name
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Returns the description of this group.
	 *
	 * @return the groups description
	 */
	public String getDescription() {
		return description;
	}

}
