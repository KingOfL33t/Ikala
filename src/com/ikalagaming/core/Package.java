package com.ikalagaming.core;

/**
 * A distinct chunk of the program with a specific purpose and methods for
 * managing its state and interacting with the main program.
 *
 * @author Ches Burks
 *
 */
public interface Package {
	/**
	 * Which version of the package this is. This changes periodically for each
	 * package subclass as they are changed and updated.
	 */
	final double version = 0.0;

	/**
	 * Deactivates the package and halts all of its operations. The package is still
	 * loaded in memory but not active. Calls {@link #onDisable()}.
	 *
	 * @return true if the package has been successfully disabled
	 */
	public boolean disable();

	/**
	 * Activates the package and enables it to perform its normal functions. Calls
	 * {@link #onEnable()}.
	 *
	 * @return true if the package was successfully enabled
	 */
	public boolean enable();

	/**
	 * Returns the current package manager reference, if it exists.
	 * @return the parent package manager
	 */
	public PackageManager getPackageManager();

	/**
	 * Returns the type of package this is. This is a string that describes the
	 * package, such as "Graphics" or "AI".
	 *
	 * @return a string descriptor of the package
	 */
	public String getType();

	/**
	 * Returns this classes version number. This changes periodically for each
	 * package subclass as they are changed and updated.
	 *
	 * @return the version
	 */
	public double getVersion();

	/**
	 * Returns true if the package is enabled, and false otherwise.
	 *
	 * @return true if the package is enabled
	 */
	public boolean isEnabled();

	/**
	 * This method is called when the package is disabled.
	 */
	public void onDisable();

	/**
	 * This method is called when the package is enabled. Initialization tends to
	 * be performed here.
	 */
	public void onEnable();

	/**
	 * Called when the package is loaded into memory. The package may or may not be
	 * enabled at this time.
	 */
	public void onLoad();

	/**
	 * Called when the package is unloaded from memory.
	 */
	public void onUnload();

	/**
	 * Disables and then enables the package.
	 *
	 * @return true if the package restarted successfully
	 */
	public boolean reload();

	/**
	 * Stores a reference to the PackageManager that is handling this package.
	 *
	 * @param parent the parent package manager
	 */
	public void setPackageManager(PackageManager parent);
}
