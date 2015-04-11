package core;

import org.junit.Assert;
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
		Assert.assertNotNull("Game is null", g);
		Assert.assertNotNull("Package manager is null",
				Game.getPackageManager());
		Assert.assertNotNull("Event manager is null", Game.getEventManager());
		Assert.assertNotNull("Logging is null", Game.getPackageManager()
				.getPackage("logging"));
	}

}
