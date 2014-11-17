
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
import com.sun.webkit.plugin.Plugin;
import com.sun.webkit.plugin.PluginManager;

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
	private boolean database = false;
	private List<Permission> permissions = null;
	private Map<?, ?> lazyPermissions = null;
	private DefaultPermissionValue defaultPerm =
			DefaultPermissionValue.OPERATOR;

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
			// this may need to be uncommented.
			// If it is, we can't create plugins in the ikala namespace
			/*
			 * if (main.startsWith("com.ikalagaming.")) { throw new
			 * InvalidDescriptionException(
			 * "main may not be within the com.ikalagaming namespace"); }
			 */
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
		if (map.get("database") != null) {
			try {
				database = (Boolean) map.get("database");
			}
			catch (ClassCastException ex) {
				throw new InvalidDescriptionException(
						"database is of wrong type", ex);
			}
		}
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
	 * Returns the name of the plugin. This name is a unique identifier for
	 * plugins.
	 * <ul>
	 * <li>Must consist of lowercase alphanumeric characters, and hyphen
	 * (a-z,0-9,-). Any other character will cause the a fail loading.
	 * <li>Used to determine the name of the plugin's data folder. Data folders
	 * are placed in the ./data/ directory by default and named as the plugin
	 * with hyphens removed
	 * <li>Case insensitive.
	 * <li>Named based on what the purpose of the plugin is
	 * <li>The is the token referenced in {@link #getDepend()},
	 * {@link #getSoftDepend()}, and {@link #getLoadBefore()}.
	 * </ul>
	 * <p>
	 * In the plugin.yml, this entry is named <code>name</code>.
	 * <p>
	 * Example:<blockquote>
	 * 
	 * <pre>
	 * name: simple-pathfinding
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return the name of the plugin
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gives the version of the plugin.
	 * <ul>
	 * <li>Version is double with the format format MajorRelease.MinorRelease.
	 * <li>Typically you will increment this every time you release a new
	 * feature or bug fix.
	 * </ul>
	 * <p>
	 * In the plugin.yml, this entry is named <code>version</code>.
	 * <p>
	 * Example:<blockquote>
	 * 
	 * <pre>
	 * version: 1.2
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return the version of the plugin
	 */
	public double getVersion() {
		return version;
	}

	/**
	 * Gives the fully qualified name of the main class for a plugin. The format
	 * should follow the {@link ClassLoader#loadClass(String)} syntax to
	 * successfully be resolved at runtime. For most plugins, this is the class
	 * that extends {@link Package}.
	 * <ul>
	 * <li>This must contain the full namespace including the class file itself.
	 * <li>If your namespace is <code>org.bukkit.plugin</code>, and your class
	 * file is called <code>MyPlugin</code> then this must be
	 * <code>org.bukkit.plugin.MyPlugin</code>
	 * <li>No plugin can use <code>org.bukkit.</code> as a base plugin for
	 * <b>any class</b>, including the main class.
	 * </ul>
	 * <p>
	 * In the plugin.yml, this entry is named <code>main</code>.
	 * <p>
	 * Example: <blockquote>
	 * 
	 * <pre>
	 * main: org.bukkit.plugin.MyPlugin
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return the fully qualified main class for the plugin
	 */
	public String getMain() {
		return main;
	}

	/**
	 * Gives a human-friendly description of the functionality the plugin
	 * provides.
	 * <ul>
	 * <li>The description can have multiple lines.
	 * <li>Displayed when a user types <code>/version PluginName</code>
	 * </ul>
	 * <p>
	 * In the plugin.yml, this entry is named <code>description</code>.
	 * <p>
	 * Example: <blockquote>
	 * 
	 * <pre>
	 * description: This plugin is so 31337. You can set yourself on fire.
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return description of this plugin, or null if not specified
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gives the list of authors for the plugin.
	 * <ul>
	 * <li>Gives credit to the developer.
	 * <li>Used in some server error messages to provide helpful feedback on who
	 * to contact when an error occurs.
	 * <li>A bukkit.org forum handle or email address is recommended.
	 * <li>Is displayed when a user types <code>/version PluginName</code>
	 * <li><code>authors</code> must be in <a
	 * href="http://en.wikipedia.org/wiki/YAML#Lists">YAML list format</a>.
	 * </ul>
	 * <p>
	 * In the plugin.yml, this has two entries, <code>author</code> and
	 * <code>authors</code>.
	 * <p>
	 * Single author example: <blockquote>
	 * 
	 * <pre>
	 * author: CaptainInflamo
	 * </pre>
	 * 
	 * </blockquote> Multiple author example: <blockquote>
	 * 
	 * <pre>
	 * authors: [Cogito, verrier, EvilSeph]
	 * </pre>
	 * 
	 * </blockquote> When both are specified, author will be the first entry in
	 * the list, so this example: <blockquote>
	 * 
	 * <pre>
	 * author: Grum
	 * authors:
	 * - feildmaster
	 * - amaranth
	 * </pre>
	 * 
	 * </blockquote> Is equivilant to this example: <blockquote>
	 * 
	 * <pre>authors: [Grum, feildmaster, aramanth]
	 * 
	 * <pre>
	 * </blockquote>
	 * 
	 * @return an immutable list of the plugin's authors
	 */
	public List<String> getAuthors() {
		return authors;
	}

	/**
	 * Gives if the plugin uses a database.
	 * <ul>
	 * <li>Using a database is non-trivial.
	 * <li>Valid values include <code>true</code> and <code>false</code>
	 * </ul>
	 * <p>
	 * In the plugin.yml, this entry is named <code>database</code>.
	 * <p>
	 * Example: <blockquote>
	 * 
	 * <pre>
	 * database: false
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return if this plugin requires a database
	 * @see Plugin#getDatabase()
	 */
	public boolean isDatabaseEnabled() {
		return database;
	}

	/**
	 * Gives a list of other plugins that the plugin requires.
	 * <ul>
	 * <li>Use the value in the {@link #getName()} of the target plugin to
	 * specify the dependency.
	 * <li>If any plugin listed here is not found, your plugin will fail to load
	 * at startup.
	 * <li>If multiple plugins list each other in <code>depend</code>, creating
	 * a network with no individual plugin does not list another plugin in the
	 * <a href=https://en.wikipedia.org/wiki/Circular_dependency>network</a>,
	 * all plugins in that network will fail.
	 * <li><code>depend</code> must be in must be in <a
	 * href="http://en.wikipedia.org/wiki/YAML#Lists">YAML list format</a>.
	 * </ul>
	 * <p>
	 * In the plugin.yml, this entry is named <code>depend</code>.
	 * <p>
	 * Example: <blockquote>
	 * 
	 * <pre>
	 * depend:
	 * - OnePlugin
	 * - AnotherPlugin
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return immutable list of the plugin's dependencies
	 */
	public List<String> getDepend() {
		return depend;
	}

	/**
	 * Gives a list of other plugins that the plugin requires for full
	 * functionality. The {@link PluginManager} will make best effort to treat
	 * all entries here as if they were a {@link #getDepend() dependency}, but
	 * will never fail because of one of these entries.
	 * <ul>
	 * <li>Use the value in the {@link #getName()} of the target plugin to
	 * specify the dependency.
	 * <li>When an unresolvable plugin is listed, it will be ignored and does
	 * not affect load order.
	 * <li>When a circular dependency occurs (a network of plugins depending or
	 * soft-dependending each other), it will arbitrarily choose a plugin that
	 * can be resolved when ignoring soft-dependencies.
	 * <li><code>softdepend</code> must be in <a
	 * href="http://en.wikipedia.org/wiki/YAML#Lists">YAML list format</a>.
	 * </ul>
	 * <p>
	 * In the plugin.yml, this entry is named <code>softdepend</code>.
	 * <p>
	 * Example: <blockquote>
	 * 
	 * <pre>
	 * softdepend: [OnePlugin, AnotherPlugin]
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return immutable list of the plugin's preferred dependencies
	 */
	public List<String> getSoftDepend() {
		return softDepend;
	}

	/**
	 * Gets the list of plugins that should consider this plugin a
	 * soft-dependency.
	 * <ul>
	 * <li>Use the value in the {@link #getName()} of the target plugin to
	 * specify the dependency.
	 * <li>The plugin should load before any other plugins listed here.
	 * <li>Specifying another plugin here is strictly equivalent to having the
	 * specified plugin's {@link #getSoftDepend()} include {@link #getName()
	 * this plugin}.
	 * <li><code>loadbefore</code> must be in <a
	 * href="http://en.wikipedia.org/wiki/YAML#Lists">YAML list format</a>.
	 * </ul>
	 * <p>
	 * In the plugin.yml, this entry is named <code>loadbefore</code>.
	 * <p>
	 * Example: <blockquote>
	 * 
	 * <pre>
	 * loadbefore:
	 * - OnePlugin
	 * - AnotherPlugin
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return immutable list of plugins that should consider this plugin a
	 *         soft-dependency
	 */
	public List<String> getLoadBefore() {
		return loadBefore;
	}

	/**
	 * Gives the token to prefix plugin-specific logging messages with.
	 * <ul>
	 * <li>This includes all messages using {@link Plugin#getLogger()}.
	 * <li>If not specified, the server uses the plugin's {@link #getName()
	 * name}.
	 * <li>This should clearly indicate what plugin is being logged.
	 * </ul>
	 * <p>
	 * In the plugin.yml, this entry is named <code>prefix</code>.
	 * <p>
	 * Example:<blockquote>
	 * 
	 * <pre>
	 * prefix: ex-why-zee
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return the prefixed logging token, or null if not specified
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Gives the map of command-name to command-properties. Each entry in this
	 * map corresponds to a single command and the respective values are the
	 * properties of the command. Each property, <i>with the exception of
	 * aliases</i>, can be defined at runtime using methods in
	 * {@link PluginCommand} and are defined here only as a convenience.
	 * <table border=1>
	 * <tr>
	 * <th>Node</th>
	 * <th>Method</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * <th>Example</th>
	 * </tr>
	 * <tr>
	 * <td><code>description</code></td>
	 * <td>{@link PluginCommand#setDescription(String)}</td>
	 * <td>String</td>
	 * <td>A user-friendly description for a command. It is useful for
	 * documentation purposes as well as in-game help.</td>
	 * <td><blockquote>
	 * 
	 * <pre>
	 * description: Set yourself on fire
	 * </pre>
	 * 
	 * </blockquote></td>
	 * </tr>
	 * <tr>
	 * <td><code>aliases</code></td>
	 * <td>{@link PluginCommand#setAliases(List)}</td>
	 * <td>String or <a href="http://en.wikipedia.org/wiki/YAML#Lists">List</a>
	 * of strings</td>
	 * <td>Alternative command names, with special usefulness for commands that
	 * are already registered. <i>Aliases are not effective when defined at
	 * runtime,</i> so the plugin description file is the only way to have them
	 * properly defined.
	 * <p>
	 * Note: Command aliases may not have a colon in them.</td>
	 * <td>Single alias format: <blockquote>
	 * 
	 * <pre>
	 * aliases: combust_me
	 * </pre>
	 * 
	 * </blockquote> or multiple alias format: <blockquote>
	 * 
	 * <pre>
	 * aliases: [combust_me, combustMe]
	 * </pre>
	 * 
	 * </blockquote></td>
	 * </tr>
	 * <tr>
	 * <td><code>permission</code></td>
	 * <td>{@link PluginCommand#setPermission(String)}</td>
	 * <td>String</td>
	 * <td>The name of the {@link Permission} required to use the command. A
	 * user without the permission will receive the specified message (see
	 * {@linkplain PluginCommand#setPermissionMessage(String) below}), or a
	 * standard one if no specific message is defined. Without the permission
	 * node, no {@link PluginCommand#setExecutor(CommandExecutor)
	 * CommandExecutor} or {@link PluginCommand#setTabCompleter(TabCompleter)
	 * TabCompleter} will be called.</td>
	 * <td><blockquote>
	 * 
	 * <pre>
	 * permission: inferno.flagrate
	 * </pre>
	 * 
	 * </blockquote></td>
	 * </tr>
	 * <tr>
	 * <td><code>permission-message</code></td>
	 * <td>{@link PluginCommand#setPermissionMessage(String)}</td>
	 * <td>String</td>
	 * <td>
	 * <ul>
	 * <li>Displayed to a player that attempts to use a command, but does not
	 * have the required permission. See {@link PluginCommand#getPermission()
	 * above}.
	 * <li>&lt;permission&gt; is a macro that is replaced with the permission
	 * node required to use the command.
	 * <li>Using empty quotes is a valid way to indicate nothing should be
	 * displayed to a player.
	 * </ul>
	 * </td>
	 * <td><blockquote>
	 * 
	 * <pre>
	 * permission-message: You do not have /&lt;permission&gt;
	 * </pre>
	 * 
	 * </blockquote></td>
	 * </tr>
	 * <tr>
	 * <td><code>usage</code></td>
	 * <td>{@link PluginCommand#setUsage(String)}</td>
	 * <td>String</td>
	 * <td>This message is displayed to a player when the
	 * {@link PluginCommand#setExecutor(CommandExecutor)}
	 * {@linkplain CommandExecutor#onCommand(CommandSender,Command,String,String[])
	 * returns false}. &lt;command&gt; is a macro that is replaced the command
	 * issued.</td>
	 * <td><blockquote>
	 * 
	 * <pre>
	 * usage: Syntax error! Perhaps you meant /&lt;command&gt; PlayerName?
	 * </pre>
	 * 
	 * </blockquote> It is worth noting that to use a colon in a yaml, like
	 * <code>`usage: Usage: /god [player]'</code>, you need to <a
	 * href="http://yaml.org/spec/current.html#id2503232">surround the message
	 * with double-quote</a>: <blockquote>
	 * 
	 * <pre>
	 * usage: "Usage: /god [player]"
	 * </pre>
	 * 
	 * </blockquote></td>
	 * </tr>
	 * </table>
	 * The commands are structured as a hiearchy of <a
	 * href="http://yaml.org/spec/current.html#id2502325">nested mappings</a>.
	 * The primary (top-level, no intendentation) node is `<code>commands</code>
	 * ', while each individual command name is indented, indicating it maps to
	 * some value (in our case, the properties of the table above).
	 * <p>
	 * Here is an example bringing together the piecemeal examples above, as
	 * well as few more definitions:<blockquote>
	 * 
	 * <pre>
	 * commands:
	 *  flagrate:
	 *  description: Set yourself on fire.
	 *  aliases: [combust_me, combustMe]
	 *  permission: inferno.flagrate
	 *  permission-message: You do not have /&lt;permission&gt;
	 *  usage: Syntax error! Perhaps you meant /&lt;command&gt; PlayerName?
	 *  burningdeaths:
	 *  description: List how many times you have died by fire.
	 *  aliases:
	 *  - burning_deaths
	 *  - burningDeaths
	 *  permission: inferno.burningdeaths
	 *  usage: |
	 *  /&lt;command&gt; [player]
	 *  Example: /&lt;command&gt; - see how many times you have burned to death
	 *  Example: /&lt;command&gt; CaptainIce - see how many times CaptainIce has burned to death
	 *  # The next command has no description, aliases, etc. defined, but is still valid
	 *  # Having an empty declaration is useful for defining the description, permission, and messages from a configuration dynamically
	 *  apocalypse:
	 * </pre>
	 * 
	 * </blockquote> Note: Command names may not have a colon in their name.
	 * 
	 * @return the commands this plugin will register
	 */
	public Map<String, Map<String, Object>> getCommands() {
		return commands;
	}

	/**
	 * Gives the list of permissions the plugin will register at runtime,
	 * immediately proceding enabling. The format for defining permissions is a
	 * map from permission name to properties. To represent a map without any
	 * specific property, empty <a
	 * href="http://yaml.org/spec/current.html#id2502702">curly-braces</a> (
	 * <code>&#123;&#125;</code> ) may be used (as a null value is not accepted,
	 * unlike the {@link #getCommands() commands} above).
	 * <p>
	 * A list of optional properties for permissions:
	 * <table border=1>
	 * <tr>
	 * <th>Node</th>
	 * <th>Description</th>
	 * <th>Example</th>
	 * </tr>
	 * <tr>
	 * <td><code>description</code></td>
	 * <td>Plaintext (user-friendly) description of what the permission is for.</td>
	 * <td><blockquote>
	 * 
	 * <pre>
	 * description: Allows you to set yourself on fire
	 * </pre>
	 * 
	 * </blockquote></td>
	 * </tr>
	 * <tr>
	 * <td><code>default</code></td>
	 * <td>The default state for the permission, as defined by
	 * {@link Permission#getDefault()}. If not defined, it will be set to the
	 * value of {@link PluginDescriptionFile#getPermissionDefault()}.
	 * <p>
	 * For reference:
	 * <ul>
	 * <li><code>true</code> - Represents a positive assignment to
	 * {@link Permissible permissibles}.
	 * <li><code>false</code> - Represents no assignment to {@link Permissible
	 * permissibles}.
	 * <li><code>op</code> - Represents a positive assignment to
	 * {@link Permissible#isOp() operator permissibles}.
	 * <li><code>notop</code> - Represents a positive assignment to
	 * {@link Permissible#isOp() non-operator permissibiles}.
	 * </ul>
	 * </td>
	 * <td><blockquote>
	 * 
	 * <pre>
	 * default: true
	 * </pre>
	 * 
	 * </blockquote></td>
	 * </tr>
	 * <tr>
	 * <td><code>children</code></td>
	 * <td>Allows other permissions to be set as a
	 * {@linkplain Permission#getChildren() relation} to the parent permission.
	 * When a parent permissions is assigned, child permissions are respectively
	 * assigned as well.
	 * <ul>
	 * <li>When a parent permission is assigned negatively, child permissions
	 * are assigned based on an inversion of their association.
	 * <li>When a parent permission is assigned positively, child permissions
	 * are assigned based on their association.
	 * </ul>
	 * <p>
	 * Child permissions may be defined in a number of ways:
	 * <ul>
	 * <li>Children may be defined as a <a
	 * href="http://en.wikipedia.org/wiki/YAML#Lists">list</a> of names. Using a
	 * list will treat all children associated positively to their parent.
	 * <li>Children may be defined as a map. Each permission name maps to either
	 * a boolean (representing the association), or a nested permission
	 * definition (just as another permission). Using a nested definition treats
	 * the child as a positive association.
	 * <li>A nested permission definition must be a map of these same
	 * properties. To define a valid nested permission without defining any
	 * specific property, empty curly-braces ( <code>&#123;&#125;</code> ) must
	 * be used.
	 * <li>A nested permission may carry it's own nested permissions as
	 * children, as they may also have nested permissions, and so forth. There
	 * is no direct limit to how deep the permission tree is defined.
	 * </ul>
	 * </td>
	 * <td>As a list: <blockquote>
	 * 
	 * <pre>
	 * children: [inferno.flagrate, inferno.burningdeaths]
	 * </pre>
	 * 
	 * </blockquote> Or as a mapping: <blockquote>
	 * 
	 * <pre>
	 * children:
	 * inferno.flagrate: true
	 * inferno.burningdeaths: true
	 * </pre>
	 * 
	 * </blockquote> An additional example showing basic nested values can be
	 * seen <a href="doc-files/permissions-example_plugin.yml">here</a>.</td>
	 * </tr>
	 * </table>
	 * The permissions are structured as a hiearchy of <a
	 * href="http://yaml.org/spec/current.html#id2502325">nested mappings</a>.
	 * The primary (top-level, no intendentation) node is `
	 * <code>permissions</code>', while each individual permission name is
	 * indented, indicating it maps to some value (in our case, the properties
	 * of the table above).
	 * <p>
	 * Here is an example using some of the properties:<blockquote>
	 * 
	 * <pre>
	 * permissions:
	 *  inferno.*:
	 *  description: Gives access to all Inferno commands
	 *  children:
	 *  inferno.flagrate: true
	 *  inferno.burningdeaths: true
	 *  inferno.flagate:
	 *  description: Allows you to ignite yourself
	 *  default: true
	 *  inferno.burningdeaths:
	 *  description: Allows you to see how many times you have burned to death
	 *  default: true
	 * </pre>
	 * 
	 * </blockquote> Another example, with nested definitions, can be found <a
	 * href="doc-files/permissions-example_plugin.yml">here</a>.
	 * 
	 * @return The list of permissions for this plugin
	 */
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

	/**
	 * Gives the default {@link Permission#getDefault() default} state of
	 * {@link #getPermissions() permissions} registered for the plugin.
	 * <ul>
	 * <li>If not specified, it will be {@link PermissionDefault#OP}.
	 * <li>It is matched using {@link PermissionDefault#getByName(String)}
	 * <li>It only affects permissions that do not define the
	 * <code>default</code> node.
	 * <li>It may be any value in {@link PermissionDefault}.
	 * </ul>
	 * <p>
	 * In the plugin.yml, this entry is named <code>default-permission</code>.
	 * <p>
	 * Example:<blockquote>
	 * 
	 * <pre>
	 * default-permission: NOT_OP
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return the default value for the plugin's permissions
	 */
	public DefaultPermissionValue getPermissionDefault() {
		return defaultPerm;
	}

	/**
	 * Returns the name of a plugin, including the version. This method is
	 * provided for convenience; it uses the {@link #getName()} and
	 * {@link #getVersion()} entries.
	 * 
	 * @return a descriptive name of the plugin and respective version
	 */
	public String getFullName() {
		return name + " v" + version;
	}
}
