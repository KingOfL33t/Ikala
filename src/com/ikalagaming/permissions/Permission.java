
package com.ikalagaming.permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ikalagaming.core.Game;
import com.ikalagaming.logging.LoggingLevel;
import com.ikalagaming.logging.events.LogError;
import com.ikalagaming.util.SafeResourceLoader;

/**
 * Represents a unique permission.
 * 
 * @author Ches Burks
 * 
 */
public class Permission {

	/**
	 * The default value for a permission.
	 */
	private static final boolean DEFAULT_PERMISSION = false;

	private boolean defaultValue;

	private final String name;

	private final String description;

	private static final String resourceLocation =
			"com.ikalagaming.permissions.resources.Permissions";

	/**
	 * A mapping of child permissions that this permissions includes. Each child
	 * permission has a boolean assigned to it. If the boolean is true, the
	 * child permission will inherit this permissions value. If it is false, it
	 * will inherit the inverse of this permissions value.
	 */
	private final Map<String, Boolean> childPermissions;
	/**
	 * A map of the full list of subchildren, calculated recursively.
	 */
	private HashMap<String, Boolean> fullChildMap;
	/**
	 * Used so that the subchild map is only calculated once to save memory.
	 */
	private boolean childrenCalculated = false;

	private static HashMap<String, Permission> permissionByName =
			new HashMap<String, Permission>();

	/**
	 * Checks to see if a permission with the given name has already been
	 * created. If it exists, this returns true.
	 * 
	 * @param permissionName the fully qualified permission name
	 * @return true if the permission exists
	 */
	public static boolean exists(String permissionName) {
		return permissionByName.containsKey(permissionName);
	}

	private static Map<String, Boolean> extractChildren(Map<?, ?> input,
			String name, boolean defaultPermission, List<Permission> output) {
		Map<String, Boolean> children = new LinkedHashMap<String, Boolean>();
		for (Map.Entry<?, ?> entry : input.entrySet()) {
			if ((entry.getValue() instanceof Boolean)) {
				children.put(entry.getKey().toString(),
						(Boolean) entry.getValue());
			}
			else if ((entry.getValue() instanceof Map)) {
				try {
					Permission perm =
							loadPermission(entry.getKey().toString(),
									(Map<?, ?>) entry.getValue(),
									defaultPermission, output);
					children.put(name, true);
					if (output != null) {
						output.add(perm);
					}
				}
				catch (Throwable ex) {// TODO localize this and use logging
					throw new IllegalArgumentException("Permission node '"
							+ entry.getKey().toString() + "' in child of "
							+ name + " is invalid", ex);
				}
			}
			else {
				throw new IllegalArgumentException("Child '"
						+ entry.getKey().toString()
						+ "' contains invalid value");
			}
		}
		return children;
	}

	/**
	 * If the permission named {@link #exists(String) already exists}, returns
	 * that permission. Returns null if the permission does not already exist.
	 * 
	 * @param permissionName the fully qualified permission name
	 * @return the permission with the given name, if it exists
	 */
	public static Permission getByName(String permissionName) {
		if (exists(permissionName)) {
			return permissionByName.get(permissionName);
		}
		else {
			return null;
		}
	}

	private static boolean isValidName(String name) {
		if (name.endsWith(".")) {
			return false;
		}
		if (name.startsWith(".")) {
			return false;
		}
		for (char c : name.toCharArray()) {
			if (!(Character.isLetterOrDigit(c) || c == '.')) {
				// if the character is not a number, letter or period
				return false;
			}
		}
		return true;
	}

	/**
	 * Loads a Permission from a map, usually used from retrieval from a yaml
	 * file.
	 * <p>
	 * The data may contain the following keys:
	 * <ul>
	 * <li>default: Boolean true or false. If not specified, false.
	 * <li>children: Map(String, Boolean) of child permissions. If not
	 * specified, empty list.
	 * <li>description: Short string containing a very small description of this
	 * permission. If not specified, empty string.
	 * </ul>
	 * 
	 * @param name Name of the permission
	 * @param data Map of keys to load from
	 * @param def Default permission value to use if not set
	 * @param output A list to append any created child-Permissions to. This may
	 *            be null
	 * @return Permission the permissions object
	 * @throws NullPointerException if the name or data provided is null
	 * @throws IllegalArgumentException if the permissions had invalid data
	 */
	public static Permission loadPermission(String name, Map<?, ?> data,
			boolean def, List<Permission> output) throws NullPointerException,
			IllegalArgumentException {
		if (name == null) {// TODO localize and use logging
			throw new NullPointerException("Name cannot be null");
		}
		if (data == null) {
			throw new NullPointerException("Data cannot be null");
		}
		String desc = null;
		Map<String, Boolean> children = null;
		if (data.get("default") != null) {
			Object theDefault = data.get("default");
			if (!DefaultPermissionValue.isValid(theDefault.toString())) {
				throw new IllegalArgumentException(
						"'default' key contained unknown value");
			}// TODO localize and use logging

			def = DefaultPermissionValue.getByName(theDefault.toString());

		}
		if (data.get("children") != null) {
			Object childrenNode = data.get("children");
			if (childrenNode instanceof Iterable) {
				children = new LinkedHashMap<String, Boolean>();
				for (Object child : (Iterable<?>) childrenNode) {
					if (child != null) {
						children.put(child.toString(), true);
					}
				}
			}
			else if (childrenNode instanceof Map) {
				children =
						extractChildren((Map<?, ?>) childrenNode, name, def,
								output);
			}
			else {// TODO localize and use logging
				throw new IllegalArgumentException(
						"'children' key is of wrong type");
			}
		}
		if (data.get("description") != null) {
			desc = data.get("description").toString();
		}

		return new Permission(name, desc, def, children);
	}

	/**
	 * Loads a Permission from a map, usually used from retrieval from a yaml
	 * file.
	 * <p>
	 * The data may contain the following keys:
	 * <ul>
	 * <li>default: Boolean true or false. If not specified, false.
	 * <li>children: Map(String, Boolean) of child permissions. If not
	 * specified, empty list.
	 * <li>description: Short string containing a very small description of this
	 * description. If not specified, empty string.
	 * </ul>
	 * 
	 * @param name Name of the permission
	 * @param data Map of keys
	 * @return Permission the permissions object
	 */
	public static Permission loadPermission(String name,
			Map<String, Object> data) {
		return loadPermission(name, data, DEFAULT_PERMISSION, null);
	}

	/**
	 * Loads a list of Permissions from a map, usually used from retrieval from
	 * a yaml file.
	 * <p>
	 * The data may contain a list of name:data, where the data contains the
	 * following keys:
	 * <ul>
	 * <li>default: Boolean true or false. If not specified, false.
	 * <li>children: Map(String, Boolean) of child permissions. If not
	 * specified, empty list.
	 * <li>description: Short string containing a very small description of this
	 * description. If not specified, empty string.
	 * </ul>
	 * 
	 * @param data Map of permissions
	 * @param error An error message to show if a permission is invalid.
	 * @param defaultPerm Default permission value to use if missing
	 * @return Permission object
	 */
	public static List<Permission> loadPermissions(Map<?, ?> data,
			String error, boolean defaultPerm) {

		List<Permission> result = new ArrayList<Permission>();

		for (Map.Entry<?, ?> entry : data.entrySet()) {
			try {
				result.add(Permission.loadPermission(entry.getKey().toString(),
						(Map<?, ?>) entry.getValue(), defaultPerm, result));
			}
			catch (Throwable ex) {
				LogError log =
						new LogError(SafeResourceLoader.getString(
								"INVALID_PERMISSIONS", resourceLocation,
								"Invalid permissions"), LoggingLevel.WARNING,
								"Permissions");
				Game.getEventManager().fireEvent(log);
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * <p>
	 * Constructs a new permission with a name. The description defaults to an
	 * empty string. The default value defaults to the default for permissions.
	 * The children list defaults to empty.<br>
	 * </p>
	 * <p>
	 * The name is a string that identifies the permission, and is the same
	 * string as would be found in permission yaml files. <br>
	 * Example: "entity.movement"
	 * </p>
	 * 
	 * @param name The name of the permission
	 * 
	 * @see #Permission(String, String, boolean, Map)
	 */
	public Permission(String name) {
		this(name, null, DEFAULT_PERMISSION, null);
	}

	/**
	 * <p>
	 * Constructs a new permission with a name and default value. The
	 * description defaults to an empty string. The children list defaults to
	 * empty.<br>
	 * </p>
	 * <p>
	 * The name is a string that identifies the permission, and is the same
	 * string as would be found in permission yaml files. <br>
	 * Example: "entity.movement"
	 * </p>
	 * <p>
	 * The default value is a value that determines the default for this
	 * permission. <br>
	 * Example: "false"
	 * </p>
	 * 
	 * @param name The name of the permission
	 * @param defaultValue the default value for the permission
	 * 
	 * @see #Permission(String, String, boolean, Map)
	 */
	public Permission(String name, boolean defaultValue) {
		this(name, null, defaultValue, null);
	}

	/**
	 * <p>
	 * Constructs a new permission with a name, default value, and children
	 * list. The description defaults to an empty string.<br>
	 * </p>
	 * <p>
	 * The name is a string that identifies the permission, and is the same
	 * string as would be found in permission yaml files. <br>
	 * Example: "entity.movement"
	 * </p>
	 * <p>
	 * The default value is a value that determines the default for this
	 * permission. <br>
	 * Example: "false"
	 * </p>
	 * <p>
	 * The children is a list of permissions that are granted or revoked when
	 * this permission is granted/revoked. Each child permission has a boolean
	 * assigned to it. If the boolean is true, the child permission will inherit
	 * this permissions value. If it is false, it will inherit the inverse of
	 * this permissions value. <br>
	 * Example: "entity.jump" is mapped to "true"
	 * </p>
	 * 
	 * @param name The name of the permission
	 * @param defaultValue the default value for the permission
	 * @param children children this permission includes
	 * 
	 * @see #Permission(String, String, boolean, Map)
	 */
	public Permission(String name, boolean defaultValue,
			Map<String, Boolean> children) {
		this(name, null, defaultValue, children);
	}

	/**
	 * <p>
	 * Constructs a new permission with a name, and children list. The default
	 * value defaults to the default for permissions. The description defaults
	 * to an empty string.<br>
	 * </p>
	 * <p>
	 * The name is a string that identifies the permission, and is the same
	 * string as would be found in permission yaml files. <br>
	 * Example: "entity.movement"
	 * </p>
	 * <p>
	 * The children is a list of permissions that are granted or revoked when
	 * this permission is granted/revoked. Each child permission has a boolean
	 * assigned to it. If the boolean is true, the child permission will inherit
	 * this permissions value. If it is false, it will inherit the inverse of
	 * this permissions value. <br>
	 * Example: "entity.jump" is mapped to "true"
	 * </p>
	 * 
	 * @param name The name of the permission
	 * @param children children this permission includes
	 * 
	 * @see #Permission(String, String, boolean, Map)
	 */
	public Permission(String name, Map<String, Boolean> children) {
		this(name, null, DEFAULT_PERMISSION, children);
	}

	/**
	 * <p>
	 * Constructs a new permission with a name and description. The default
	 * value defaults to the default for permissions. The children list defaults
	 * to empty.<br>
	 * </p>
	 * <p>
	 * The name is a string that identifies the permission, and is the same
	 * string as would be found in permission yaml files. <br>
	 * Example: "entity.movement"
	 * </p>
	 * <p>
	 * The description is a short description of what the permission is for. <br>
	 * Example: "This allows entities to move around the world"
	 * </p>
	 * 
	 * @param name The name of the permission
	 * @param description A short description of the permissions purpose
	 * 
	 * @see #Permission(String, String, boolean, Map)
	 */
	public Permission(String name, String description) {
		this(name, description, DEFAULT_PERMISSION, null);
	}

	/**
	 * <p>
	 * Constructs a new permission with a name, description, and default value.
	 * The children list defaults to empty.<br>
	 * </p>
	 * <p>
	 * The name is a string that identifies the permission, and is the same
	 * string as would be found in permission yaml files. <br>
	 * Example: "entity.movement"
	 * </p>
	 * <p>
	 * The description is a short description of what the permission is for. <br>
	 * Example: "This allows entities to move around the world"
	 * </p>
	 * <p>
	 * The default value is a value that determines the default for this
	 * permission. <br>
	 * Example: "false"
	 * </p>
	 * 
	 * @param name The name of the permission
	 * @param description A short description of the permissions purpose
	 * @param defaultValue the default value for the permission
	 * 
	 * @see #Permission(String, String, boolean, Map)
	 */
	public Permission(String name, String description, boolean defaultValue) {
		this(name, description, defaultValue, null);
	}

	/**
	 * <p>
	 * Constructs a new permission with a name, description, default value, and
	 * children list.<br>
	 * </p>
	 * <p>
	 * The name is a string that identifies the permission, and is the same
	 * string as would be found in permission yaml files. <br>
	 * Example: "entity.movement"
	 * </p>
	 * <p>
	 * The description is a short description of what the permission is for. <br>
	 * Example: "This allows entities to move around the world"
	 * </p>
	 * <p>
	 * The default value is a value that determines the default for this
	 * permission. <br>
	 * Example: "false"
	 * </p>
	 * <p>
	 * The children is a list of permissions that are granted or revoked when
	 * this permission is granted/revoked. Each child permission has a boolean
	 * assigned to it. If the boolean is true, the child permission will inherit
	 * this permissions value. If it is false, it will inherit the inverse of
	 * this permissions value. <br>
	 * Example: "entity.jump" is mapped to "true"
	 * </p>
	 * 
	 * @param name The name of the permission
	 * @param description A short description of the permissions purpose
	 * @param defaultValue the default value for the permission
	 * @param children children this permission includes
	 */
	public Permission(String name, String description, boolean defaultValue,
			Map<String, Boolean> children) {
		if (!isValidName(name)) {
			// TODO error
		}
		if (exists(name)) {
			// TODO error
		}
		this.name = name;
		if (description == null) {
			this.description = "";
		}
		else {
			this.description = description.isEmpty() ? "" : description;
		}
		this.defaultValue = defaultValue;
		this.childPermissions =
				children == null ? new LinkedHashMap<String, Boolean>()
						: children;
		permissionByName.put(name, this);
	}

	/**
	 * <p>
	 * Constructs a new permission with a name, description, and children list.
	 * The default value defaults to the default for permissions.<br>
	 * </p>
	 * <p>
	 * The name is a string that identifies the permission, and is the same
	 * string as would be found in permission yaml files. <br>
	 * Example: "entity.movement"
	 * </p>
	 * <p>
	 * The description is a short description of what the permission is for. <br>
	 * Example: "This allows entities to move around the world"
	 * </p>
	 * <p>
	 * The children is a list of permissions that are granted or revoked when
	 * this permission is granted/revoked. Each child permission has a boolean
	 * assigned to it. If the boolean is true, the child permission will inherit
	 * this permissions value. If it is false, it will inherit the inverse of
	 * this permissions value. <br>
	 * Example: "entity.jump" is mapped to "true"
	 * </p>
	 * 
	 * @param name The name of the permission
	 * @param description A short description of the permissions purpose
	 * @param children children this permission includes
	 * 
	 * @see #Permission(String, String, boolean, Map)
	 */
	public Permission(String name, String description,
			Map<String, Boolean> children) {
		this(name, description, DEFAULT_PERMISSION, children);
	}

	/**
	 * Returns the children is a list of permissions that are granted or revoked
	 * when this permission is granted/revoked. Each child permission has a
	 * boolean assigned to it. If the boolean is true, the child permission will
	 * inherit this permissions value. If it is false, it will inherit the
	 * inverse of this permissions value. <br>
	 * Example: "entity.jump" is mapped to "true"
	 * 
	 * @return the list of child permissions
	 */
	public Map<String, Boolean> getChildPermissions() {
		return childPermissions;
	}

	/**
	 * Returns a complete list of permissions this permission grants or revokes.
	 * It recursively finds all subpermissions, with parents overriding child
	 * permissions.
	 * 
	 * @return the full list of child permissions
	 * @see #getChildPermissions()
	 */
	public HashMap<String, Boolean> getAllSubpermissions() {
		if (childrenCalculated) {
			return fullChildMap;
		}
		Map<String, Boolean> perms = getChildPermissions();
		HashMap<String, Boolean> submap;
		for (String s : perms.keySet()) {
			if (!Permission.exists(s)) {
				System.out.println(s + " does not exist");
				// TODO log this
				continue;// it was not created somehow
			}
			// this is recursive
			submap = Permission.getByName(s).getAllSubpermissions();
			for (String submapString : submap.keySet()) {
				if (!perms.containsKey(submapString)) {
					// add the permission if it does not exist
					// this will prevent subperms overriding the parents
					perms.put(submapString, submap.get(submapString));
				}
			}
		}
		fullChildMap = new HashMap<String, Boolean>();
		fullChildMap.putAll(perms);
		childrenCalculated = true;
		return fullChildMap;
	}

	/**
	 * Returns true if the permission or one of the subpermissions contains the
	 * given permission. If the permission is the same as this one, it returns
	 * true as well.
	 * 
	 * @param other the permission to search for
	 * @return true if this permission contains or equals the given permission
	 */
	public boolean contains(Permission other) {
		if (this.equals(other)) {
			return true;
		}
		if (getAllSubpermissions().containsKey(other.getName())) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the default for this permission. <br>
	 * Example: "false"
	 * 
	 * @return default value of this permission.
	 */
	public boolean getDefault() {
		return defaultValue;
	}

	/**
	 * Returns a short description of the purpose for the permission, if it is
	 * set. Returns an empty string if it is not set. <br>
	 * Example: "This allows entities to move around the world"
	 * 
	 * @return Brief description of this permission
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the name that identifies the permission, which is the same string
	 * as would be found in permission yaml files. <br>
	 * Example: "entity.movement"
	 * 
	 * @return fully qualified name for this permission
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the default value of this permission.
	 * <p>
	 * This will not be saved to disk, and is a temporary operation until the
	 * server reloads permissions. Changing this default will cause all
	 * {@link PermissionHolder}s that contain this permission to recalculate
	 * their permissions
	 * 
	 * @param value The new default to set
	 */
	public void setDefault(DefaultPermissionValue value) {
		defaultValue = value.value();
	}
}
