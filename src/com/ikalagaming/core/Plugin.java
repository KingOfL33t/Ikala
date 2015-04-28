package com.ikalagaming.core;

import java.util.HashSet;
import java.util.Set;

import com.ikalagaming.event.Listener;

/**
 * A package that is loaded from an external source.
 *
 * @author Ches Burks
 *
 */
public class Plugin implements IPlugin {

	private String packageName = "null-plugin";
	private final double version = 0.1;
	private FileConfiguration config;
	private PluginDescription description;

	@Override
	public FileConfiguration getConfig() {
		return this.config;
	}

	@Override
	public PluginDescription getDescription() {
		return this.description;
	}

	@Override
	public Set<Listener> getListeners() {
		return new HashSet<>();
	}

	@Override
	public String getName() {
		return this.packageName;
	}

	@Override
	public double getVersion() {
		return this.version;
	}

	@Override
	public boolean onDisable() {
		return true;
	}

	@Override
	public boolean onEnable() {
		return true;
	}

	@Override
	public boolean onLoad() {
		return true;
	}

	@Override
	public boolean onUnload() {
		return true;
	}

	@Override
	public void reloadConfig() {
		// TODO implement this
	}

	@Override
	public void saveConfig() {
		// TODO implement this
	}

	@Override
	public void saveDefaultConfig() {
		// TODO implement this
	}

}
