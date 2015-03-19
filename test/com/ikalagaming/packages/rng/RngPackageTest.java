
package com.ikalagaming.packages.rng;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

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
		assertNotNull("rng is null", pack);
		assertFalse("boolean is true", pack.getBoolean());
		assertFalse("boolean is true", pack.getBoolean(1));
		assertEquals("float not zero", 0, pack.getFloat(), 0.0);
		assertEquals("float not zero", 0, pack.getInt(), 0.0);
		assertEquals("float not zero", 0, pack.getIntBetween(1, 9), 0.0);
		assertEquals("float not zero", 0, pack.getIntBetween(-81, -9), 0.0);

		pack.onLoad();
		pack.enable();

		pack.disable();
		pack.onUnload();

	}
}
