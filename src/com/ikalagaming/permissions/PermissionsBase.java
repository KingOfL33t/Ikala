
package com.ikalagaming.permissions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.ikalagaming.core.Game;

/**
 * Base Permissions for use in any Permissions object through extension
 * 
 * @author Ches Burks
 * 
 */
public class PermissionsBase implements Permissible {
	private Operator oppable = null;
	private Permissible parent = this;
	private final Map<String, Boolean> permissions =
			new LinkedHashMap<String, Boolean>();

	// TODO connect plugins and permissions
	public PermissionsBase(Operator opable) {
		this.oppable = opable;
		if (opable instanceof Permissible) {
			this.parent = (Permissible) opable;
		}
		recalculatePermissions();
	}

	@Override
	public boolean isOp() {
		if (oppable == null) {
			return false;
		}
		else {
			return oppable.isOp();
		}
	}

	@Override
	public void setOp(boolean value) {
		if (oppable == null) {
			throw new UnsupportedOperationException(
					"Cannot change op value as no Operator is set");
		}
		else {
			oppable.setOp(value);
		}
	}

	@Override
	public boolean isPermissionSet(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Permission name cannot be null");
		}
		return permissions.containsKey(name.toLowerCase());
	}

	@Override
	public boolean isPermissionSet(Permission perm) {
		if (perm == null) {
			throw new IllegalArgumentException("Permission cannot be null");
		}
		return isPermissionSet(perm.getName());
	}

	@Override
	public boolean hasPermission(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Permission name cannot be null");
		}
		String nameLowercase = name.toLowerCase();
		if (isPermissionSet(nameLowercase)) {
			return permissions.get(nameLowercase);
		}
		else {
			Permission perm =
					Game.getPackageManager().getPermission(nameLowercase);
			if (perm != null) {
				return perm.getDefault().getValue(isOp());
			}
			else {
				return Permission.DEFAULT_PERMISSION.getValue(isOp());
			}
		}
	}

	@Override
	public boolean hasPermission(Permission perm) {
		if (perm == null) {
			throw new IllegalArgumentException("Permission cannot be null");
		}
		String name = perm.getName().toLowerCase();
		if (isPermissionSet(name)) {
			return permissions.get(name);
		}
		return perm.getDefault().getValue(isOp());
	}

	public synchronized void clearPermissions() {
		Set<String> perms = permissions.keySet();
		for (String name : perms) {
			Game.getPackageManager().removePermission(name);
		}
		permissions.clear();
	}

	@Override
	public void recalculatePermissions() {
		clearPermissions();// TODO recalculate
	}

}
