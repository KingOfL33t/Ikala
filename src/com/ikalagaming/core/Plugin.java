
package com.ikalagaming.core;

import java.util.HashSet;
import java.util.Set;

import com.ikalagaming.packages.FileConfiguration;
import com.ikalagaming.packages.PackageSettings;
import com.ikalagaming.packages.PackageState;
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
	private PackageState state = PackageState.DISABLED;

	@Override
	public boolean disable() {
		if (!isEnabled()) {
			return false;
		}
		setPackageState(PackageState.DISABLING);
		onDisable();
		return true;
	}

	@Override
	public boolean enable() {
		if (isEnabled()) {
			return false;
		}
		setPackageState(PackageState.ENABLING);
		onEnable();
		return true;
	}

	@Override
	public String getName() {
		return packageName;
	}

	@Override
	public double getVersion() {
		return version;
	}

	@Override
	public boolean isEnabled() {
		if (getPackageState() == PackageState.ENABLED) {
			return true;
		}
		return false;
	}

	@Override
	public void onDisable() {
		setPackageState(PackageState.DISABLED);
	}

	@Override
	public void onEnable() {
		setPackageState(PackageState.ENABLED);
	}

	@Override
	public void onLoad() {
		setPackageState(PackageState.LOADING);
		setPackageState(PackageState.DISABLED);
		// plugins will be enabled as soon as they load by default
		if (!PackageSettings.ENABLE_ON_LOAD) {
			enable();
		}
	}

	@Override
	public void onUnload() {
		setPackageState(PackageState.UNLOADING);
		if (getPackageState() == PackageState.ENABLED) {
			disable();
			setPackageState(PackageState.UNLOADING);
		}
		setPackageState(PackageState.PENDING_REMOVAL);
	}

	@Override
	public boolean reload() {
		setPackageState(PackageState.UNLOADING);
		if (isEnabled()) {
			disable();
			setPackageState(PackageState.UNLOADING);
		}
		onLoad();
		return true;
	}

	@Override
	public PluginDescription getDescription() {
		return description;
	}

	@Override
	public FileConfiguration getConfig() {
		return config;
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
	public void reloadConfig() {
		// TODO implement this
	}

	@Override
	public Set<Listener> getListeners() {
		return new HashSet<Listener>();
	}

	@Override
	public PackageState getPackageState() {
		synchronized (state) {
			return state;
		}
	}

	@Override
	public void setPackageState(PackageState newState) {
		synchronized (state) {
			state = newState;
		}
	}

}
