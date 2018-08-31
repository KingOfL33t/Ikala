package com.ikalagaming.core;

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
	private static final ThreadLocal<Yaml> YAML = new ThreadLocal<>();

	private static List<String> makePluginNameList(final Map<?, ?> map,
			final String key) throws InvalidDescriptionException {
		final Object value = map.get(key);
		if (value == null) {
			return new GapList<>();
		}
		final GapList<String> pluginNameList = new GapList<>();
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

	String rawName = null;
	private String name = null;
	private String main = null;
	private String classLoader = null;
	private List<String> depend = new GapList<>();
	private List<String> softDepend = new GapList<>();
	private List<String> loadBefore = new GapList<>();
	private double version = -1.0;
	private Map<String, Map<String, Object>> commands = null;
	private String description = null;
	private List<String> authors = null;
	private String prefix = null;
	private List<Permission> permissions = null;
	private Map<?, ?> lazyPermissions = null;

	private DefaultPermissionValue defaultPerm = DefaultPermissionValue.FALSE;

	/**
	 * Returns a plugin description loaded by the given inputstream.
	 *
	 * @param stream the steam to load info from
	 * @throws InvalidDescriptionException if the description is not valid
	 */
	public PluginDescription(final InputStream stream)
			throws InvalidDescriptionException {
		// TODO finish javadoc
		// TODO provide examples
		// TODO list yaml tags
		this.loadMap(this.asMap(PluginDescription.YAML.get().load(stream)));
	}

	private Map<?, ?> asMap(Object object) throws InvalidDescriptionException {
		if (object instanceof Map) {
			return (Map<?, ?>) object;
		}
		throw new InvalidDescriptionException(object
				+ " is not properly structured.");
	}

	/**
	 * Returns the list of authors for the program. This is used to give credit
	 * to developers.
	 *
	 * @return the list of authors for the plugin
	 */
	public List<String> getAuthors() {
		return this.authors;
	}

	/**
	 * @return the classLoader
	 */
	public String getClassLoader() {
		return this.classLoader;
	}

	/**
	 * A map of strings to commands
	 *
	 * @return the command map
	 */
	public Map<String, Map<String, Object>> getCommands() {
		return this.commands;
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
		return this.depend;
	}

	/**
	 * This is a short human-friendly description of what the plugin does. It
	 * may be multiple lines.
	 *
	 * @return the plugins description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Returns the full name of the plugin
	 *
	 * @return the full name
	 */
	public String getFullName() {
		return this.name + " v" + this.version;
	}

	/**
	 * plugins to load before this plugin
	 *
	 * @return a list of plugins that should be loaded first
	 */
	public List<String> getLoadBefore() {
		return this.loadBefore;
	}

	// TODO javadoc

	/**
	 * The fully qualified name of the main method for the plugin. This includes
	 * the class name. The format should follow the
	 * {@link ClassLoader#loadClass(String)} syntax. Typically this will be the
	 * class that implements {@link Plugin2}.
	 *
	 * @return the absolute path to the main method of the plugin
	 */
	public String getMain() {
		return this.main;
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
		return this.name;
	}

	/**
	 * Returns the default permission value for this plugin
	 *
	 * @return the default permission value
	 */
	public DefaultPermissionValue getPermissionDefault() {
		return this.defaultPerm;
	}

	/**
	 * returns permissions for this plugin
	 *
	 * @return this plugins permissions
	 */
	public List<Permission> getPermissions() {
		if (this.permissions == null) {
			if (this.lazyPermissions == null) {
				this.permissions = new GapList<>();
			}
			else {
				/*
				 * permissions = Permission.loadPermissions(lazyPermissions,
				 * "Permission node '%s' in plugin description file for " +
				 * getFullName() + " is invalid", defaultPerm);
				 */
				this.lazyPermissions = null;
			}// TODO load permissions
		}
		return this.permissions;
	}

	/**
	 * returns the prefix to use in logging
	 *
	 * @return this plugins prefix
	 */
	public String getPrefix() {
		return this.prefix;
	}

	/**
	 * Returns a list of dependencies that are not needed to run
	 *
	 * @return soft dependencies
	 */
	public List<String> getSoftDependencies() {
		return this.softDepend;
	}

	/**
	 * The version of the plugin. This value is a double that follows the
	 * MajorVersion.MinorVersion format. It should be increased when new
	 * features are added or bugs are fixed.
	 *
	 * @return the version of the plugin
	 */
	public double getVersion() {
		return this.version;
	}

	private void loadMap(Map<?, ?> map) throws InvalidDescriptionException {
		try {
			this.name = this.rawName = map.get("name").toString().toLowerCase();
			if (!this.name.matches("^[a-z0-9 _.-]+$")) {
				throw new InvalidDescriptionException("name '" + this.name
						+ "' contains invalid characters.");
			}
			this.name = this.name.replace(' ', '_');
		}
		catch (NullPointerException ex) {
			throw new InvalidDescriptionException("name is not defined", ex);
		}
		catch (ClassCastException ex) {
			throw new InvalidDescriptionException("name is of wrong type", ex);
		}
		try {
			this.version = (Double) map.get("version");
		}
		catch (NullPointerException ex) {
			throw new InvalidDescriptionException("version is not defined", ex);
		}
		catch (ClassCastException ex) {
			throw new InvalidDescriptionException("version is of wrong type",
					ex);
		}
		try {
			this.main = map.get("main").toString();
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
			HashMap<String, Map<String, Object>> commandsMap = new HashMap<>();
			try {
				for (Map.Entry<?, ?> command : ((Map<?, ?>) map.get("commands"))
						.entrySet()) {
					HashMap<String, Object> commandMap = new HashMap<>();
					if (command.getValue() != null) {
						for (Map.Entry<?, ?> commandEntry : ((Map<?, ?>) command
								.getValue()).entrySet()) {
							if (commandEntry.getValue() instanceof Iterable) {
								HashSet<Object> commandSubList =
										new HashSet<>();
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
			this.commands = commandsMap;
		}
		if (map.get("class-loader-of") != null) {
			this.setClassLoader(map.get("class-loader-of").toString());
		}
		this.depend = PluginDescription.makePluginNameList(map, "depend");
		this.softDepend =
				PluginDescription.makePluginNameList(map, "softdepend");
		this.loadBefore =
				PluginDescription.makePluginNameList(map, "loadbefore");
		if (map.get("description") != null) {
			this.description = map.get("description").toString();
		}
		if (map.get("authors") != null) {
			GapList<String> authorsList = new GapList<>();
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
			this.authors = authorsList;
		}
		else if (map.get("author") != null) {
			GapList<String> authorsList = new GapList<>();
			authorsList.add(map.get("author").toString());
		}
		else {
			this.authors = new GapList<>();
		}
		if (map.get("default-permission") != null) {
			try {// TODO load permissions
					// defaultPerm =
					// DefaultPermissionValue.getByName(map.get(
					// "default-permission").toString());
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
			this.lazyPermissions = (Map<?, ?>) map.get("permissions");
		}
		catch (ClassCastException ex) {
			throw new InvalidDescriptionException(
					"permissions are of the wrong type", ex);
		}
		if (map.get("prefix") != null) {
			this.prefix = map.get("prefix").toString();
		}
	}

	/**
	 * @param newClassLoader the classLoaderOf to set
	 */
	public void setClassLoader(String newClassLoader) {
		this.classLoader = newClassLoader;
	}
}
