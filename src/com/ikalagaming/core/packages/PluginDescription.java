
package com.ikalagaming.core.packages;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.magicwerk.brownies.collections.GapList;
import org.yaml.snakeyaml.Yaml;

import com.ikalagaming.permissions.DefaultPermissionValue;
import com.ikalagaming.permissions.Permission;

/**
 * Contains data about a particular plugin.
 *
 * @author Ches Burks
 *
 */
public class PluginDescription {
	private static final ThreadLocal<Yaml> YAML = new ThreadLocal<Yaml>();

	String rawName = null;
	private String name = null;
	private String main = null;
	private String classLoaderOf = null;
	private List<String> depend = new GapList<String>();
	private List<String> softDepend = new GapList<String>();
	private List<String> loadBefore = new GapList<String>();
	private double version = -1.0;
	private Map<String, Map<String, Object>> commands = null;
	private String description = null;
	private List<String> authors = null;
	private String prefix = null;
	private List<Permission> permissions = null;
	private Map<?, ?> lazyPermissions = null;
	private DefaultPermissionValue defaultPerm =
			DefaultPermissionValue.OPERATOR;

	//TODO finish javadoc
	//TODO provide examples
	//TODO list yaml tags
	/**
	 * Returns a plugin description loaded by the given inputstream.
	 *
	 * @param stream the steam to load info from
	 * @throws InvalidDescriptionException if the description is not valid
	 */
	public PluginDescription(final InputStream stream)
			throws InvalidDescriptionException {
		loadMap(asMap(YAML.get().load(stream)));
	}

	private Map<?, ?> asMap(Object object) throws InvalidDescriptionException {
		if (object instanceof Map) {
			return (Map<?, ?>) object;
		}
		throw new InvalidDescriptionException(object
				+ " is not properly structured.");
	}

	private void loadMap(Map<?, ?> map) throws InvalidDescriptionException {
		try {
			name = rawName = map.get("name").toString().toLowerCase();
			if (!name.matches("^[a-z0-9 _.-]+$")) {
				throw new InvalidDescriptionException("name '" + name
						+ "' contains invalid characters.");
			}
			name = name.replace(' ', '_');
		}
		catch (NullPointerException ex) {
			throw new InvalidDescriptionException("name is not defined", ex);
		}
		catch (ClassCastException ex) {
			throw new InvalidDescriptionException("name is of wrong type", ex);
		}
		try {
			version = (Double) map.get("version");
		}
		catch (NullPointerException ex) {
			throw new InvalidDescriptionException("version is not defined", ex);
		}
		catch (ClassCastException ex) {
			throw new InvalidDescriptionException("version is of wrong type",
					ex);
		}
		try {
			main = map.get("main").toString();
		}
		catch (NullPointerException ex) {
			throw new InvalidDescriptionException("main class is not defined",
					ex);
		}
		catch (ClassCastException ex) {
			throw new InvalidDescriptionException("main is of the wrong type",
					ex);
		}
		if (map.get("commands") != null) {
			HashMap<String, Map<String, Object>> commandsMap =
					new HashMap<String, Map<String, Object>>();
			try {
				for (Map.Entry<?, ?> command : ((Map<?, ?>) map.get("commands"))
						.entrySet()) {
					HashMap<String, Object> commandMap =
							new HashMap<String, Object>();
					if (command.getValue() != null) {
						for (Map.Entry<?, ?> commandEntry : ((Map<?, ?>) command
								.getValue()).entrySet()) {
							if (commandEntry.getValue() instanceof Iterable) {
								HashSet<Object> commandSubList =
										new HashSet<Object>();
								for (Object commandSubListItem : (Iterable<?>) commandEntry
										.getValue()) {
									if (commandSubListItem != null) {
										commandSubList.add(commandSubListItem);
									}
								}
								commandMap.put(
										commandEntry.getKey().toString(),
										commandSubList);

							}
							else if (commandEntry.getValue() != null) {
								commandMap.put(
										commandEntry.getKey().toString(),
										commandEntry.getValue());
							}
						}
					}
					commandsMap.put(command.getKey().toString(), commandMap);
				}
			}
			catch (ClassCastException ex) {
				throw new InvalidDescriptionException(
						"commands are of the wrong type", ex);
			}
			commands = commandsMap;
		}
		if (map.get("class-loader-of") != null) {
			classLoaderOf = map.get("class-loader-of").toString();
		}
		depend = makePluginNameList(map, "depend");
		softDepend = makePluginNameList(map, "softdepend");
		loadBefore = makePluginNameList(map, "loadbefore");
		if (map.get("description") != null) {
			description = map.get("description").toString();
		}
		if (map.get("authors") != null) {
			GapList<String> authorsList = new GapList<String>();
			if (map.get("author") != null) {
				authorsList.add(map.get("author").toString());
			}
			try {
				for (Object o : (Iterable<?>) map.get("authors")) {
					authorsList.add(o.toString());
				}
			}
			catch (ClassCastException ex) {
				throw new InvalidDescriptionException(
						"authors are of the wrong type", ex);
			}
			catch (NullPointerException ex) {
				throw new InvalidDescriptionException(
						"authors are not defined properly", ex);
			}
			authors = authorsList;
		}
		else if (map.get("author") != null) {
			GapList<String> authorsList = new GapList<String>();
			authorsList.add(map.get("author").toString());
		}
		else {
			authors = new GapList<String>();
		}
		if (map.get("default-permission") != null) {
			try {
				defaultPerm =
						DefaultPermissionValue.getByName(map.get(
								"default-permission").toString());
			}
			catch (ClassCastException ex) {
				throw new InvalidDescriptionException(
						"default-permission is of the wrong type", ex);
			}
			catch (IllegalArgumentException ex) {
				throw new InvalidDescriptionException(
						"default-permission is not a valid choice", ex);
			}
		}
		try {
			lazyPermissions = (Map<?, ?>) map.get("permissions");
		}
		catch (ClassCastException ex) {
			throw new InvalidDescriptionException(
					"permissions are of the wrong type", ex);
		}
		if (map.get("prefix") != null) {
			prefix = map.get("prefix").toString();
		}
	}

	private static List<String> makePluginNameList(final Map<?, ?> map,
			final String key) throws InvalidDescriptionException {
		final Object value = map.get(key);
		if (value == null) {
			return new GapList<String>();
		}
		final GapList<String> pluginNameList = new GapList<String>();
		try {
			for (final Object entry : (Iterable<?>) value) {
				pluginNameList.add(entry.toString().replace(' ', '_'));
			}
		}
		catch (ClassCastException ex) {
			throw new InvalidDescriptionException(
					key + " is of the wrong type", ex);
		}
		catch (NullPointerException ex) {
			throw new InvalidDescriptionException("invalid " + key + " format",
					ex);
		}
		return pluginNameList;
	}

	/**
	 * Returns the name of the plugin. Names are unique for each plugin. The
	 * name can contain the following characters:
	 * <ul>
	 * <li>a-z
	 * <li>0-9
	 * <li>period
	 * <li>hyphen
	 * <li>underscore
	 * </ul>
	 *
	 * @return the name of the plugin
	 */
	public String getName() {
		return name;
	}

	/**
	 * The version of the plugin. This value is a double that follows the
	 * MajorVersion.MinorVersion format. It should be increased when new
	 * features are added or bugs are fixed.
	 *
	 * @return the version of the plugin
	 */
	public double getVersion() {
		return version;
	}

	/**
	 * The fully qualified name of the main method for the plugin. This includes
	 * the class name. The format should follow the
	 * {@link ClassLoader#loadClass(String)} syntax. Typically this will be the
	 * class that implements {@link Plugin}.
	 *
	 * @return the absolute path to the main method of the plugin
	 */
	public String getMain() {
		return main;
	}

	/**
	 * This is a short human-friendly description of what the plugin does. It
	 * may be multiple lines.
	 *
	 * @return the plugins description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the list of authors for the program. This is used to give credit
	 * to developers.
	 *
	 * @return the list of authors for the plugin
	 */
	public List<String> getAuthors() {
		return authors;
	}

	/**
	 * Returns a list of plugins this plugin requires in order to run. Use the
	 * value of {@link #getName()} for the target plugin to specify it in the
	 * dependencies. If any plugin in this list is not found, this plugin will
	 * fail to load at startup. If multiple plugins list each other in depend,
	 * and they create a <a
	 * href="https://en.wikipedia.org/wiki/Circular_dependency">circular
	 * dependency</a>, none of the plugins will load.
	 *
	 * @return the list of plugins this depends on
	 */
	public List<String> getDependencies() {
		return depend;
	}

	public List<String> getSoftDependencies() {
		return softDepend;
	}

	public List<String> getLoadBefore() {
		return loadBefore;
	}

	public String getPrefix() {
		return prefix;
	}

	public Map<String, Map<String, Object>> getCommands() {
		return commands;
	}

	public List<Permission> getPermissions() {
		if (permissions == null) {
			if (lazyPermissions == null) {
				permissions = new GapList<Permission>();
			}
			else {
				permissions =
						Permission.loadPermissions(lazyPermissions,
								"Permission node '%s' in plugin description file for "
										+ getFullName() + " is invalid",
								defaultPerm);
				lazyPermissions = null;
			}
		}
		return permissions;
	}

	public DefaultPermissionValue getPermissionDefault() {
		return defaultPerm;
	}

	public String getFullName() {
		return name + " v" + version;
	}
}
