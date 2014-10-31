package com.ikalagaming.core;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runners.Suite.SuiteClasses;
/**
 * Used for testing the Game class.
 * @author Ches Burks
 *
 */
@SuiteClasses({Game.class})
public class GameTest {

	/**
	 * Ensures the game can be set up properly
	 */
	@Test
	public void testStartup(){
		Game g = new Game();
		g.init();
		assertNotNull("Game is null", g);
		assertNotNull("Node manager is null", g.getNodeManager());
		assertNotNull("Event manager is null",
				g.getNodeManager().getNode("event-manager"));
		assertNotNull("Logging is null",
				g.getNodeManager().getNode("logging"));
	}

}
