
package com.ikalagaming.core;

import java.io.File;

import com.ikalagaming.event.EventManager;
import com.ikalagaming.packages.rng.RngPackageMain;

/**
 * Contains logic needed for connecting various parts of the engine together.
 * 
 * @author Ches Burks
 * 
 */
public class Game {
	private PackageManager packageMgr;
	private static final String rootFolderName = "KnightsOfIkala";
	// subfolder where plugins are located
	private static final String pluginFolder = "plugins";
	// where configuration files are stored for packages
	private static final String configFolder = "config";

	/**
	 * Returns the folder where configuration files are stored. Plugins as well
	 * as the core system packages store config files here.
	 * 
	 * @return the configuration folder
	 */
	public File getConfigFolder() {
		File f;
		String dir =
				SystemProperties.getHomeDir() + File.separator + rootFolderName
						+ File.separator + configFolder;
		if (!FileManager.fileExists(dir)) {
			createConfigDir();
		}
		f = FileManager.getFile(dir);
		return f;
	}

	/**
	 * Returns the game's package manager.
	 * 
	 * @return The package manager
	 */
	public PackageManager getPackageManager() {
		return this.packageMgr;
	}

	/**
	 * Returns the folder where plugins are stored. Packages in this folder are
	 * loaded by the server.
	 * 
	 * @return the folder where plugins are stored
	 */
	public File getPluginFolder() {
		File f;
		String dir =
				SystemProperties.getHomeDir() + File.separator + rootFolderName
						+ File.separator + pluginFolder;
		if (!FileManager.fileExists(dir)) {
			createPluginDir();
		}
		f = FileManager.getFile(dir);
		return f;
	}

	/**
	 * Returns the root directory for this program. This is usually located
	 * where the system stores application data.
	 * 
	 * @return the home/root directory for the program
	 */
	public File getRootDir() {
		File f;
		String dir =
				SystemProperties.getHomeDir() + File.separator + rootFolderName;
		if (!FileManager.fileExists(dir)) {
			createRootDir();
		}
		f = FileManager.getFile(dir);
		return f;
	}

	/**
	 * Initializes main subsystems.
	 */
	public void init() {
		packageMgr = new PackageManager(this);
		loadCorePackages();
		setupDirectories();
	}

	/**
	 * Loads the main packages used by the game like the event system.
	 */
	private void loadCorePackages() {
		packageMgr.loadPackage(new EventManager());
		packageMgr.loadPackage(new RngPackageMain());
	}

	/**
	 * Sets up the directories for storing files for the server. This includes
	 * plugins, logs, and saved data.
	 */
	public void setupDirectories() {
		createRootDir();
		createPluginDir();
		createConfigDir();

	}

	private void createRootDir() {
		String path =
				SystemProperties.getHomeDir() + File.separator + rootFolderName;
		if (FileManager.fileExists(path)) {
			return;
		}
		if (!FileManager.createFolder(path)) {
			System.out.println("could not create root folder at " + path);
		}
	}

	private void createPluginDir() {
		String path =
				SystemProperties.getHomeDir() + File.separator + rootFolderName
						+ File.separator + pluginFolder;
		if (FileManager.fileExists(path)) {
			return;
		}
		if (!FileManager.createFolder(path)) {
			System.out.println("could not create plugin folder at " + path);
		}
	}

	private void createConfigDir() {
		String path =
				SystemProperties.getHomeDir() + File.separator + rootFolderName
						+ File.separator + configFolder;
		if (FileManager.fileExists(path)) {
			return;
		}
		if (!FileManager.createFolder(path)) {
			System.out.println("could not create config folder at " + path);
		}
	}

}
