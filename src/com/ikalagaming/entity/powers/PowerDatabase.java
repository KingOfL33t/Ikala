package com.ikalagaming.entity.powers;

import java.util.HashSet;
import java.util.Set;

import com.ikalagaming.event.Listener;
import com.ikalagaming.plugins.Plugin;

public class PowerDatabase implements Plugin, Listener {

	/**
	 * The human readable name of the plugin.
	 */
	private static final String pluginName = "Power Database";
	/**
	 * The current iteration of this power database.
	 */
	private static final double version = 0.1;
	private Set<Listener> listeners;

	@Override
	public Set<Listener> getListeners() {
		if (this.listeners == null) {
			this.listeners = new HashSet<>();
			this.listeners.add(this);
		}
		return this.listeners;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return PowerDatabase.pluginName;
	}

	@Override
	public double getVersion() {
		return PowerDatabase.version;
	}

	@Override
	public boolean onDisable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onEnable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onLoad() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUnload() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean powerExistsByName(String name) {
		return false;
	}

}
