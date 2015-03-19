
package com.ikalagaming.core.packages;

import java.util.HashSet;
import java.util.Set;

import com.ikalagaming.event.Listener;

/**
 * A basic implementation of the {@link Package} interface. This allows you to
 * only implement some of the methods yourself.
 * 
 * @author Ches
 *
 */
public class BasePackage implements Package{

	private String packageName = "package-manager";
	private final double version = 0.1;
	private PackageState state = PackageState.DISABLED;
	private Set<Listener> listeners;
	
	@Override
	public boolean disable() {
		setPackageState(PackageState.DISABLING);
		onDisable();
		return true;
	}

	@Override
	public boolean enable() {
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
		if (getPackageState() == PackageState.ENABLED) {
			disable();
			setPackageState(PackageState.UNLOADING);
		}
		onLoad();
		return false;
	}

	@Override
	public Set<Listener> getListeners() {
		if (listeners == null){
			listeners = new HashSet<Listener>();
		}
		return listeners;
	}

	@Override
	public PackageState getPackageState() {
		synchronized (state) {
			return state;
		}
	}

	@Override
	public void setPackageState(PackageState newState) {
		synchronized (state){
			state = newState;
		}
	}

}
