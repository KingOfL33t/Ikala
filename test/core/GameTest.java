
package core;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runners.Suite.SuiteClasses;

import com.ikalagaming.core.Game;

/**
 * Used for testing the Game class.
 * 
 * @author Ches Burks
 * 
 */
@SuiteClasses({Game.class})
public class GameTest {

	/**
	 * Ensures the game can be set up properly
	 */
	@Test
	public void testStartup() {
		Game g = new Game();
		g.init();
		assertNotNull("Game is null", g);
		assertNotNull("Package manager is null", Game.getPackageManager());
		assertNotNull("Event manager is null", Game.getEventManager());
		assertNotNull("Logging is null",
				Game.getPackageManager().getPackage("logging"));
	}

}
