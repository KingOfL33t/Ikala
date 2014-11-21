
package com.ikalagaming.permissions;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base Permissions for use in any Permissions object through extension
 *
 * @deprecated will be removed
 * @author Ches Burks
 *
 */
public class PlayerPermissions implements PermissionHolder {
	private final Map<String, Boolean> permissions =
			new LinkedHashMap<String, Boolean>();

	/**
	 * Constructs a new permissions object for the given entity.
	 *
	 * @param opable the entity to create permissions for
	 */
	public PlayerPermissions() {
		recalculatePermissions();
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		if (perm == null) {
			throw new IllegalArgumentException("Permission cannot be null");
		}
		return isPermissionSet(perm);
	}

	@Override
	public boolean hasPermission(Permission perm) {
		if (perm == null) {
			throw new IllegalArgumentException("Permission cannot be null");
		}
		String name = perm.getName().toLowerCase();
		if (isPermissionSet(perm)) {
			return permissions.get(name);
		}
		return false;// perm.getDefault().getValue(isOp());
	}

	public synchronized void clearPermissions() {

		permissions.clear();
	}

	@Override
	public void recalculatePermissions() {
		clearPermissions();// TODO recalculate
	}

}
