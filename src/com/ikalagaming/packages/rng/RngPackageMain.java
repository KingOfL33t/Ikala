package com.ikalagaming.packages.rng;

import com.ikalagaming.core.Package;
import com.ikalagaming.core.PackageManager;

/**
 * The main interface for the rng package.
 * @author Ches Burks
 *
 */
public class RngPackageMain implements Package{

	private Generator gen;
	private PackageManager parent;
	private final double version = 0.1;
	private final String packageName = "rng";
	private boolean enabled = false;

	@Override
	public boolean disable() {
		onDisable();
		enabled = false;
		return true;
	}

	@Override
	public boolean enable() {
		onEnable();
		enabled = true;
		return true;
	}

	/**
	 * Returns a random {@link Boolean boolean} if the
	 * package is enabled. Returns false if it is not enabled.
	 *
	 * @return a boolean
	 */
	public boolean getBoolean(){
		if (!enabled){
			return false;
		}
		return gen.getBoolean();
	}

	/**
	 * If the package is not enabled, returns false.
	 * If it is enabled,
	 * returns a {@link Boolean boolean} with a
	 * given probability of being true.
	 * The probability is a float from 0.0f to 1.0f,
	 * with 0 being no chance of returning true and
	 * 1 being a 100% chance of returning true.
	 *
	 * @param probablilty The chance of returning true
	 * @return a boolean
	 */
	public boolean getBoolean(float probablilty){
		if (!enabled){
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
		if (!enabled){
			return 0;
		}
		return gen.getFloat();
	}

	/**
	 *
	 * Returns a random integer if the package is enabled.
	 * If it is not enabled, returns 0.
	 * @return a random int
	 */
	public int getInt(){
		if (!enabled){
			return 0;
		}
		return gen.getInt();
	}

	/**
	 * If the package is not enabled, returns 0.
	 * Returns a random {@link Integer int} between the given values,
	 * inclusive.
	 * <br>
	 * For example: a call {@code getIntBetween(2,6)} will return
	 * either {@code 2, 3, 4, 5 or 6}.
	 *
	 * @param min The minimum number
	 * @param max The maximum number
	 * @return a random integer
	 */
	public int getIntBetween(int min, int max){
		if (!enabled){
			return 0;
		}
		return gen.getIntBetween(min, max);
	}

	@Override
	public PackageManager getPackageManager() {
		return parent;
	}

	@Override
	public String getType() {
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
	public void onDisable() {
		gen = null;
	}

	@Override
	public void onEnable() {
		gen = new Generator();
		//get the current time of the system
		long time1 = System.nanoTime();
		/*
		 * construct a random number of integers
		 * based on the last 3 digits of the system time
		 * this will take a different amount of time based
		 * on what value the hash was
		 */
		for (int i = 0; i <= time1 % 1000; ++i){
			@SuppressWarnings("unused")//not supposed to be used
			int x = i+2;//its value is trashed
		}
		//get the current time again
		long time2 = System.nanoTime();

		long deltaTime = time2-time1;

		int timeAsInt;
		//make sure the long time can fit into an integer
		while (deltaTime > Integer.MAX_VALUE){
			deltaTime = deltaTime - Integer.MAX_VALUE;
		}
		timeAsInt = (int) deltaTime;

		gen.initializeGenerator(timeAsInt);

	}

	@Override
	public void onLoad() {
	}
	@Override
	public void onUnload() {
	}
	@Override
	public boolean reload() {
		enable();
		disable();
		return false;
	}

	@Override
	public void setPackageManager(PackageManager parent) {
		this.parent = parent;
	}
}
