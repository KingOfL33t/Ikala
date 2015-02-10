
package com.ikalagaming.core.packages;

import java.util.Set;

import com.ikalagaming.event.Listener;

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
	 * Deactivates the package and halts all of its operations. The package is
	 * still loaded in memory but not active. Calls {@link #onDisable()}. This
	 * should change the packages state to {@link PackageState#DISABLING
	 * DISABLING}. If the disabling were done here and not in the
	 * {@link #onDisable()} method, the package state should be changed to
	 * {@link PackageState#DISABLED DISABLED} after completion. Otherwise the
	 * change to DISABLED is handled in the onDisable method.
	 * 
	 * @return true if the package has been successfully disabled
	 */
	public boolean disable();

	/**
	 * Activates the package and enables it to perform its normal functions.
	 * Calls {@link #onEnable()}. This should change the package state to
	 * {@link PackageState#ENABLING ENABLING}. If the enabling were done here
	 * and not in the {@link #onEnable()} method, the package state should be
	 * changed to {@link PackageState#ENABLED ENABLED} after completion.
	 * Otherwise the change to ENABLED is handled in the onEnable method.
	 * 
	 * @return true if the package was successfully enabled
	 */
	public boolean enable();

	/**
	 * Returns the type of package this is. This is a string that describes the
	 * package, such as "Graphics" or "AI".
	 * 
	 * @return a string descriptor of the package
	 */
	public String getName();

	/**
	 * Returns this classes version number. This changes periodically for each
	 * package subclass as they are changed and updated.
	 * 
	 * @return the version
	 */
	public double getVersion();

	/**
	 * Returns true if the package is enabled, and false otherwise. A state of
	 * {@link PackageState#ENABLED ENABLED} returns true, any other states will
	 * return false.
	 * 
	 * @return true if the package is fully ready to operate
	 */
	public boolean isEnabled();

	/**
	 * This method is called when the package is disabled, and gives the package
	 * the chance to shut itself down and save any changes made in memory to
	 * disk if necessary. After the package is done disabling, the package state
	 * should be changed to {@link PackageState#DISABLED DISABLED}.
	 */
	public void onDisable();

	/**
	 * This method is called when the package is enabled. Initialization should
	 * be performed here, and configuration and data should be loaded from disk
	 * if necessary. After the package is done setting up and is ready to be
	 * used, the package state should be changed to {@link PackageState#ENABLED
	 * ENABLED}.
	 */
	public void onEnable();

	/**
	 * Called when the package is loaded into memory. The package may or may not
	 * be enabled at this time. The Package State should be set to
	 * {@link PackageState#LOADING} just before loading has started. After it is
	 * fully loaded, it should be enabled based on the value of
	 * {@link PackageSettings#ENABLE_ON_LOAD}. If it is not enabled then the
	 * package state should be set to {@link PackageState#DISABLED}.
	 */
	public void onLoad();

	/**
	 * Called when the package is unloaded from memory. If
	 * {@link PackageSettings#DISABLE_ON_UNLOAD} is true, then the package
	 * should disable itself now. Otherwise, it should already have been
	 * disabled, but this is not guaranteed. The Package State should be set to
	 * {@link PackageState#UNLOADING UNLOADING} just before unloading has
	 * started. Any memory that can reasonably be dereferenced by the package
	 * itself, should be. Files may be saved to disk if needed. After completion
	 * of the unloading, the Package State should be set to
	 * {@link PackageState#PENDING_REMOVAL PENDING_REMOVAL}.
	 */
	public void onUnload();

	/**
	 * Returns the {@link PackageState current state} of the package. This
	 * should be updated as the package is interacted with, and is recommended
	 * to default to {@link PackageState#DISABLED DISABLED} if the state is not
	 * set yet. This is to prevent the package from being accessed
	 * inappropriately.
	 * 
	 * @return a PackageState representing the status of this package
	 */
	public PackageState getPackageState();
	
	/**
	 * Sets the {@link PackageState current state} of the package. This
	 * should be used to update the package state as it is interacted with,
	 * This exists to assist with thread safety.
	 * @param newState The state the package should now be
	 * 
	 * @return a PackageState representing the status of this package
	 */
	public void setPackageState(PackageState newState);

	/**
	 * This is essentially restarting the package. The general flow is as
	 * follows: <br>
	 * <ol>
	 * <li>If the package is enabled and
	 * {@link PackageSettings#DISABLE_ON_UNLOAD} is true, disable it by calling
	 * {@link #disable()}. If DISABLE_ON_UNLOAD is false then the disabling may
	 * be done just before unloading instead.</li>
	 * <li>Set the Package State to {@link PackageState#UNLOADING UNLOADING} and
	 * perform the operations {@link #onUnload()} carries out. A Package state
	 * of {@link PackageState#PENDING_REMOVAL PENDING_REMOVAL} is not used as
	 * the framework should stay loaded. This should return the package to a
	 * fresh state, as it would appear during the first time loading just before
	 * {@link #onLoad()} is called</li>
	 * <li>Loading the package by calling {@link #onLoad()} or performing the
	 * operations it carries out which are applicable at this time</li>
	 * <li>The package is enabled using {@link #enable()}</li>
	 * </ol>
	 * 
	 * @return true if the package reloaded successfully
	 */
	public boolean reload();

	/**
	 * Returns a list of listeners for this package. These listeners will be
	 * used with the event system.
	 * 
	 * @return a list of listeners for the package.
	 */
	public Set<Listener> getListeners();

}
