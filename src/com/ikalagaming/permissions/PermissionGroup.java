
package com.ikalagaming.permissions;

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

	private final String groupName;
	private final PermissionGroup parent;
	/**
	 * Permissions that are set for this group. Inherits permissions from the
	 * parent, but children override parents for permission values if they both
	 * have a permission. The boolean for each permission is true if that
	 * permission is granted to the group, and false if the permission is
	 * revoked from the group.
	 */
	private final HashMap<Permission, Boolean> permissions;
	private final String description;

	/**
	 * Constructs a permisisonGroup.
	 * 
	 * @deprecated this will be replaced with several constructors
	 */
	public PermissionGroup() {
		// TODO remove this constructor
		groupName = "";
		parent = null;
		permissions = null;
		description = "";
		// TODO constructors
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
			if (permission == perm) {
				return true;
			}
		}
		if (hasParent()) {
			return getParent().isPermissionSet(perm);
		}
		return false;
	}

	@Override
	public boolean hasPermission(Permission perm) {
		if (perm.getDefault()) {
			// if it defaults to true
		}
		else {
			// defaults to false
		}
		return false;
	}

	@Override
	public void recalculatePermissions() {}

}
