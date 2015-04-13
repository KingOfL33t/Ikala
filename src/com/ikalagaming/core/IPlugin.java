package com.ikalagaming.core;

import com.ikalagaming.packages.Package;

/**
 * An interface that extends the package interface. Contains methods for
 * handling descriptions and configuration of plugins.
 *
 * @author Ches Burks
 *
 */
public interface IPlugin extends Package {
	/**
	 * Returns the FileConfiguration for the plugin.
	 *
	 * @return the FileConfiguration for the plugin
	 */
	public FileConfiguration getConfig();

	/**
	 * Returns a PluginDescription for the plugin.
	 *
	 * @return the description for the plugin
	 */
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
