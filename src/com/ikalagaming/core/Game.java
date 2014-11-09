package com.ikalagaming.core;

import com.ikalagaming.event.EventManager;


/**
 * Contains logic needed for connecting various parts of the engine together.
 * @author Ches Burks
 *
 */
public class Game {
	private PackageManager packageMgr;

	/**
	 * Initializes main subsystems.
	 */
	public void init(){
		packageMgr = new PackageManager();
		loadCorePackages();
	}

	/**
	 * Loads the main packages used by the game like the
	 * event system.
	 */
	private void loadCorePackages(){
		packageMgr.loadPackage(new EventManager());
	}

	/**
	 * Returns the game's package manager.
	 *
	 * @return The package manager
	 */
	public PackageManager getPackageManager(){
		return this.packageMgr;
	}


}
