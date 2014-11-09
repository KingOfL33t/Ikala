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
		assertNotNull("Package manager is null", g.getPackageManager());
		assertNotNull("Event manager is null",
				g.getPackageManager().getPackage("event-manager"));
		assertNotNull("Logging is null",
				g.getPackageManager().getPackage("logging"));
	}

}
