
package com.ikalagaming.core.packages;

import com.ikalagaming.core.PackageManager;
import com.ikalagaming.core.config.FileConfiguration;

/**
 * A package that is loaded from an external source.
 * 
 * @author Ches Burks
 * 
 */
public class Plugin implements IPlugin {

	private String packageName = "empty-package";
	private boolean enabled = false;
	private final double version = 0.1;
	private PackageManager packageManager;
	private FileConfiguration config;
	private PluginDescription description;

	@Override
	public boolean disable() {
		if (!isEnabled()) {
			return false;
		}
		onDisable();
		enabled = false;
		return true;
	}

	@Override
	public boolean enable() {
		if (isEnabled()) {
			return false;
		}
		onEnable();
		enabled = true;
		return true;
	}

	@Override
	public PackageManager getPackageManager() {
		return packageManager;
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
		return enabled;
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}

	@Override
	public void onLoad() {
		if (!PackageSettings.ENABLE_ON_LOAD) {
			enable();
		}
	}

	@Override
	public void onUnload() {
		if (!PackageSettings.DISABLE_ON_UNLOAD) {
			disable();
		}
	}

	@Override
	public boolean reload() {
		onUnload();
		onLoad();
		return true;
	}

	@Override
	public void setPackageManager(PackageManager parent) {
		packageManager = parent;
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

}
