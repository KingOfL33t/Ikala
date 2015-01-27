
package com.ikalagaming.core.packages;

import java.util.HashSet;
import java.util.Set;

import com.ikalagaming.core.config.FileConfiguration;
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
		state = PackageState.DISABLING;
		onDisable();
		return true;
	}

	@Override
	public boolean enable() {
		if (isEnabled()) {
			return false;
		}
		state = PackageState.ENABLING;
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
		if (state == PackageState.ENABLED) {
			return true;
		}
		return false;
	}

	@Override
	public void onDisable() {
		state = PackageState.DISABLED;
	}

	@Override
	public void onEnable() {
		state = PackageState.ENABLED;
	}

	@Override
	public void onLoad() {
		state = PackageState.LOADING;
		state = PackageState.DISABLED;
		// plugins will be enabled as soon as they load by default
		if (!PackageSettings.ENABLE_ON_LOAD) {
			enable();
		}
	}

	@Override
	public void onUnload() {
		state = PackageState.UNLOADING;
		if (state == PackageState.ENABLED) {
			disable();
			state = PackageState.UNLOADING;
		}
		state = PackageState.PENDING_REMOVAL;
	}

	@Override
	public boolean reload() {
		state = PackageState.UNLOADING;
		if (!PackageSettings.DISABLE_ON_UNLOAD) {
			disable();
		}
		if (state == PackageState.ENABLED) {
			disable();
		}
		onLoad();
		enable();// it will restart enabled
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
		config.nop();
	}

	@Override
	public void saveDefaultConfig() {
		config.nop();
	}

	@Override
	public void reloadConfig() {
		config.nop();
	}

	@Override
	public Set<Listener> getListeners() {
		return new HashSet<Listener>();
	}

	@Override
	public PackageState getPackageState() {
		return state;
	}

}
