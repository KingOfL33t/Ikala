package com.ikalagaming.entity.powers;

import java.util.HashSet;
import java.util.Set;

import com.ikalagaming.event.Listener;
import com.ikalagaming.packages.Package;

public class PowerDatabase implements Package, Listener {

	/**
	 * The human readable name of the package.
	 */
	private static final String packageName = "Power Database";
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
		return PowerDatabase.packageName;
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
