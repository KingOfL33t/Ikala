package com.ikalagaming.core;

import java.util.HashSet;
import java.util.Set;

import com.ikalagaming.event.Listener;
import com.ikalagaming.packages.PackageSettings;
import com.ikalagaming.packages.PackageState;

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
	private PackageState state = PackageState.DISABLED;

	@Override
	public boolean disable() {
		if (!this.isEnabled()) {
			return false;
		}
		this.setPackageState(PackageState.DISABLING);
		this.onDisable();
		return true;
	}

	@Override
	public boolean enable() {
		if (this.isEnabled()) {
			return false;
		}
		this.setPackageState(PackageState.ENABLING);
		this.onEnable();
		return true;
	}

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
	public PackageState getPackageState() {
		synchronized (this.state) {
			return this.state;
		}
	}

	@Override
	public double getVersion() {
		return this.version;
	}

	@Override
	public boolean isEnabled() {
		if (this.getPackageState() == PackageState.ENABLED) {
			return true;
		}
		return false;
	}

	@Override
	public void onDisable() {
		this.setPackageState(PackageState.DISABLED);
	}

	@Override
	public void onEnable() {
		this.setPackageState(PackageState.ENABLED);
	}

	@Override
	public void onLoad() {
		this.setPackageState(PackageState.LOADING);
		this.setPackageState(PackageState.DISABLED);
		// plugins will be enabled as soon as they load by default
		if (!PackageSettings.ENABLE_ON_LOAD) {
			this.enable();
		}
	}

	@Override
	public void onUnload() {
		this.setPackageState(PackageState.UNLOADING);
		if (this.getPackageState() == PackageState.ENABLED) {
			this.disable();
			this.setPackageState(PackageState.UNLOADING);
		}
		this.setPackageState(PackageState.PENDING_REMOVAL);
	}

	@Override
	public boolean reload() {
		this.setPackageState(PackageState.UNLOADING);
		if (this.isEnabled()) {
			this.disable();
			this.setPackageState(PackageState.UNLOADING);
		}
		this.onLoad();
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

	@Override
	public void setPackageState(PackageState newState) {
		synchronized (this.state) {
			this.state = newState;
		}
	}

}
