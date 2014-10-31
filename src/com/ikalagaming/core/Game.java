package com.ikalagaming.core;

import com.ikalagaming.event.EventManager;
import com.ikalagaming.logging.LoggingNode;


/**
 * Contains logic needed for connecting various parts of the engine together.
 * @author Ches Burks
 *
 */
public class Game {
	private NodeManager nodeMgr;

	/**
	 * Initializes main subsystems.
	 */
	public void init(){
		nodeMgr = new NodeManager();
		loadCoreNodes();
	}

	/**
	 * Loads the main nodes used by the game like the
	 * event system.
	 */
	private void loadCoreNodes(){
		nodeMgr.loadNode(new LoggingNode());
		nodeMgr.loadNode(new EventManager());
	}

	/**
	 * Returns the game's node manager.
	 *
	 * @return The node manager
	 */
	public NodeManager getNodeManager(){
		return this.nodeMgr;
	}


}
