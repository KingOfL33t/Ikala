package com.ikalagaming.packages.rng;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

import com.ikalagaming.event.Listener;
import com.ikalagaming.packages.Package;
import com.ikalagaming.packages.PackageState;

/**
 * The main interface for the rng package.
 * 
 * @author Ches Burks
 * 
 */
public class RngPackageMain implements Package, Listener {

	private Generator gen;
	private final double version = 0.1;
	private final String packageName = "rng";
	private PackageState state = PackageState.DISABLED;

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

	/**
	 * Returns a random {@link Boolean boolean} if the package is enabled.
	 * Returns false if it is not enabled.
	 * 
	 * @return a boolean
	 */
	public boolean getBoolean() {
		if (!isEnabled()) {
			return false;
		}
		return gen.getBoolean();
	}

	/**
	 * If the package is not enabled, returns false. If it is enabled, returns a
	 * {@link Boolean boolean} with a given probability of being true. The
	 * probability is a float from 0.0f to 1.0f, with 0 being no chance of
	 * returning true and 1 being a 100% chance of returning true.
	 * 
	 * @param probablilty The chance of returning true
	 * @return a boolean
	 */
	public boolean getBoolean(float probablilty) {
		if (!isEnabled()) {
			return false;
		}
		return gen.getBoolean(probablilty);
	}

	/**
	 * If the package is not enabled, returns 0.
	 * 
	 * Returns a random {@link Float float}.
	 * 
	 * @return a random float
	 */
	public float getFloat() {
		if (!isEnabled()) {
			return 0;
		}
		return gen.getFloat();
	}

	/**
	 * 
	 * Returns a random integer if the package is enabled. If it is not enabled,
	 * returns 0.
	 * 
	 * @return a random int
	 */
	public int getInt() {
		if (!isEnabled()) {
			return 0;
		}
		return gen.getInt();
	}

	/**
	 * If the package is not enabled, returns 0. Returns a random
	 * {@link Integer int} between the given values, inclusive. <br>
	 * For example: a call {@code getIntBetween(2,6)} will return either
	 * {@code 2, 3, 4, 5 or 6}.
	 * 
	 * @param min The minimum number
	 * @param max The maximum number
	 * @return a random integer
	 */
	public int getIntBetween(int min, int max) {
		if (!isEnabled()) {
			return 0;
		}
		return gen.getIntBetween(min, max);
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
		gen = null;
		setPackageState(PackageState.DISABLED);
	}

	@Override
	public void onEnable() {
		SecureRandom sec = new SecureRandom();
		byte[] sbuf = sec.generateSeed(8);
		ByteBuffer bb = ByteBuffer.wrap(sbuf);
		long seed = bb.getLong();
		gen = new Generator((int) seed);
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
		if (isEnabled()) {
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
		return false;
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
