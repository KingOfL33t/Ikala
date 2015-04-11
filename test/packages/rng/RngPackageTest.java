package packages.rng;

import org.junit.Assert;
import org.junit.Test;

import com.ikalagaming.packages.rng.RngPackageMain;

/**
 * Tests the RNG package.
 *
 * @author Ches Burks
 *
 */
public class RngPackageTest {

	/**
	 * Creates a new rng package, tests the default getter values and then loads
	 * and unloads it.
	 */
	@Test
	public void testLoadCycle() {
		RngPackageMain pack = new RngPackageMain();
		Assert.assertNotNull("rng is null", pack);
		Assert.assertFalse("boolean is true", pack.getBoolean());
		Assert.assertFalse("boolean is true", pack.getBoolean(1));
		Assert.assertEquals("float not zero", 0, pack.getFloat(), 0.0);
		Assert.assertEquals("float not zero", 0, pack.getInt(), 0.0);
		Assert.assertEquals("float not zero", 0, pack.getIntBetween(1, 9), 0.0);
		Assert.assertEquals("float not zero", 0, pack.getIntBetween(-81, -9),
				0.0);

		pack.onLoad();
		pack.enable();

		pack.disable();
		pack.onUnload();

	}
}
