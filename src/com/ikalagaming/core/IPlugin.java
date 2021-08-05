package com.ikalagaming.core;

/**
 * Contains methods for handling descriptions and configuration of plugins.
 *
 * @author Ches Burks
 *
 */
// TODO should this be it's own plugin? In Ikala-Core?
public interface IPlugin {
	/**
	 * Returns the FileConfiguration for the plugin.
	 *
	 * @return the FileConfiguration for the plugin
	 */
	public FileConfiguration getConfig();

	/**
	 * Returns a PluginDescription for the plugin.
	 * 
	 * @deprecated Replaced by Plugin Info.
	 *
	 * @return the description for the plugin
	 */
	@Deprecated
	public PluginDescription getDescription();

	/**
	 * Clears out the configuration in memory and reloads it from the disk.
	 */
	public void reloadConfig();

	/**
	 * Saves the configuration to disk. Any changes made in memory will
	 * overwrite the file on disk.
	 */
	public void saveConfig();

	/**
	 * Saves the default configuration. If a configuration already exists on
	 * disk, nothing happens. If no default configuration file is in contained
	 * in the plugin, an empty configuration file is saved.
	 */
	public void saveDefaultConfig();
}
