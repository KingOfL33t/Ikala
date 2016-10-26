package com.ikalagaming.entity.component;

import org.junit.Assert;
import org.junit.Test;

import com.ikalagaming.entity.component.Health;
import com.ikalagaming.entity.component.InvalidInputException;

/**
 * Tests for the Health component
 *
 * @author Ches Burks
 *
 */
public class HealthTest {

	/**
	 * Sample valid values for use in testing constructor initialization values
	 */
	private final int[] constructorVals = {Health.LOWEST_POSSIBLE_HEALTH, -100,
			-70, -45, 0, 55, 99, 205, Health.HIGHEST_POSSIBLE_HEALTH};

	private boolean constructorSetsValidValues(final int max) {
		return this.constructorSetsValidValues(max > 0 ? 0 : max, max > 0 ? max
				: 0, max > 0 ? max : 0);
	}

	private boolean constructorSetsValidValues(final int min, final int max) {
		return this
				.constructorSetsValidValues(min, max, max >= min ? max : min);
	}

	private boolean constructorSetsValidValues(final int min, final int max,
			final int init) {
		Health test = new Health(min, max, init);
		int high;
		int low;
		int initial;
		if (max >= min) {
			high = max;
			low = min;
		}
		else {
			high = min;
			low = max;
		}
		initial = init;
		if (initial < low) {
			initial = low;
		}
		if (initial > high) {
			initial = high;
		}

		if (test.getMaxHealth() != high) {
			return false;
		}
		if (test.getMinHealth() != low) {
			return false;
		}
		if (test.getHealth() != initial) {
			return false;
		}
		return true;
	}

	/**
	 * Tests that damage works correctly, stays in bounds, and heals when
	 * negative.
	 */
	@Test
	public void testDamage() {
		Health health = new Health(0, 100);
		health.damage(1);
		Assert.assertEquals(99, health.getHealth());
		health.setHealth(50);
		Assert.assertEquals(50, health.getHealth());
		health.damage(25);
		Assert.assertEquals(25, health.getHealth());
		// should not go past min health
		health.damage(500);
		Assert.assertEquals(0, health.getHealth());

		// this will call damage
		health.damage(-1);
		Assert.assertEquals(1, health.getHealth());

		Health negHealth = new Health(-100, -1);
		Assert.assertEquals(-1, negHealth.getHealth());
		negHealth.damage(10);
		Assert.assertEquals(-11, negHealth.getHealth());
	}

	/**
	 * Ensures that the health value is set and returned properly and it stays
	 * inside the given min and max bounds for the health object.
	 */
	@Test
	public void testGetHealth() {
		Health health = new Health();
		Assert.assertEquals(health.getMaxHealth(), health.getHealth());
		health.setHealth(50);
		Assert.assertEquals(50, health.getHealth());
		health.setHealth(health.getMinHealth() - 5);
		Assert.assertEquals(health.getMinHealth(), health.getHealth());
		health.setHealth(health.getMaxHealth() + 3);
		Assert.assertEquals(health.getMaxHealth(), health.getHealth());
	}

	/**
	 * Finds the percentage health of random values in several different health
	 * ranges and ensures they match the reported percentage values.
	 */
	@Test
	public void testGetHealthPercentage() {
		// not using constructor vals because max-min is larger than INT_MAX
		final int[] validVals = {-100, -70, -45, 0, 55, 99, 205};
		Health health = new Health();
		Assert.assertEquals(1.0f, health.getHealthPercentage(), 0);

		int i, j;
		int value;
		float percent;
		for (i = 0; i < validVals.length; ++i) {
			for (j = 0; j < validVals.length; ++j) {

				Health partial = new Health(validVals[i], validVals[j]);
				value =
						(int) (Math.random()
								* (partial.getMaxHealth() - partial
										.getMinHealth()) + partial
								.getMinHealth());
				partial.setHealth(value);
				// cast to float to prevent integer division
				percent =
						(value - partial.getMinHealth())
								/ ((float) partial.getMaxHealth() - partial
										.getMinHealth());
				Assert.assertEquals("value " + partial.getHealth()
						+ " in the range [" + partial.getMinHealth() + ", "
						+ partial.getMaxHealth() + "] is invalid.", percent,
						partial.getHealthPercentage(), 0.001f);
			}
		}
	}

	/**
	 * Tests the max health is returned correctly
	 */
	@Test
	public void testGetMaxHealth() {
		Health health = new Health();
		Assert.assertEquals(Health.DEFAULT_MAX_HEALTH, health.getMaxHealth());

		Health health2 = new Health(10);
		Assert.assertEquals(10, health2.getMaxHealth());

		try {
			health.setMaxHealth(500);
		}
		catch (InvalidInputException e) {
			Assert.fail("Invalid max health");
		}
		Assert.assertEquals(500, health.getMaxHealth());

	}

	/**
	 * Tests the min health is returned correctly
	 */
	@Test
	public void testGetMinHealth() {
		Health health = new Health();
		Assert.assertEquals(Health.DEFAULT_MIN_HEALTH, health.getMinHealth());

		Health health2 = new Health(10);
		Assert.assertEquals(10, health2.getMaxHealth());

		try {
			health.setMinHealth(-25);
		}
		catch (InvalidInputException e) {
			Assert.fail("Invalid min health");
		}
		Assert.assertEquals(-25, health.getMinHealth());
	}

	/**
	 * Ensures the type of Health is "Health"
	 */
	@Test
	public void testGetType() {
		Health health = new Health();
		Assert.assertEquals("Health", health.getType());
	}

	/**
	 * Tests that healing works, stays inside bounds, and damages when negative.
	 */
	@Test
	public void testHeal() {
		Health health = new Health(0, 100);
		health.heal(1);
		Assert.assertEquals(health.getMaxHealth(), health.getHealth());
		health.setHealth(50);
		Assert.assertEquals(50, health.getHealth());
		health.heal(25);
		Assert.assertEquals(75, health.getHealth());
		// should not go past max health
		health.heal(500);
		Assert.assertEquals(100, health.getHealth());

		// this will call damage
		health.heal(-1);
		Assert.assertEquals(99, health.getHealth());

		Health negHealth = new Health(-100, -1);
		Assert.assertEquals(-1, negHealth.getHealth());
		negHealth.setHealth(-50);
		negHealth.heal(10);
		Assert.assertEquals(-40, negHealth.getHealth());
	}

	/**
	 * Tests that the default constructor has the correct values as specified in
	 * the documentation (that is, minimum of 0, maximum of 100, current of
	 * 100).
	 */
	@Test
	public void testHealth() {
		Health test = new Health();
		Assert.assertEquals(test.getMinHealth(), 0, 0);
		Assert.assertEquals(test.getMaxHealth(), 100, 0);
		Assert.assertEquals(test.getHealth(), test.getMaxHealth(), 0);
	}

	/**
	 * Tests the constructor with one parameter for maximum value, using a small
	 * subset of values from the lowest to highest possible values and ensures
	 * they work.
	 */
	@Test
	public void testHealthInt() {
		int i;

		for (i = 0; i < this.constructorVals.length; ++i) {
			Assert.assertTrue(this.constructorVals[i] + " failed",
					this.constructorSetsValidValues(this.constructorVals[i]));
		}
	}

	/**
	 * Tests the constructor with two parameters for min and max values, using a
	 * small subset of values from the lowest to highest possible values and
	 * ensures they work.
	 */
	@Test
	public void testHealthIntInt() {
		int i, j;

		for (i = 0; i < this.constructorVals.length; ++i) {
			for (j = 0; j < this.constructorVals.length; ++j) {
				Assert.assertTrue(this.constructorVals[i] + ", "
						+ this.constructorVals[j] + " failed", this
						.constructorSetsValidValues(this.constructorVals[i],
								this.constructorVals[j]));
			}
		}
	}

	/**
	 * Tests the constructor with three parameters for min, max and initial
	 * values, using a small subset of values from the lowest to highest
	 * possible values and ensures they work.
	 */
	@Test
	public void testHealthIntIntInt() {
		int i, j, k;

		for (i = 0; i < this.constructorVals.length; ++i) {
			for (j = 0; j < this.constructorVals.length; ++j) {
				for (k = 0; k < this.constructorVals.length; ++k) {
					Assert.assertTrue(this.constructorVals[i] + ", "
							+ this.constructorVals[j] + ", "
							+ this.constructorVals[k] + " failed", this
							.constructorSetsValidValues(
									this.constructorVals[i],
									this.constructorVals[j],
									this.constructorVals[k]));
				}
			}
		}
	}

	/**
	 * Ensures health reports death correctly and kills/revives while testing
	 */
	@Test
	public void testIsDead() {
		Health health = new Health(-30, 312);
		// don't start dead
		Assert.assertFalse(health.isDead());
		health.setHealth(-30);
		Assert.assertTrue(health.isDead());
		health.heal(10);
		Assert.assertFalse(health.isDead());
		health.damage(50);
		Assert.assertTrue(health.isDead());

		health = new Health(30, 30);
		Assert.assertTrue(health.isDead());

	}

	/**
	 * Tests that health defaults to not immortal and sets it to immortal then
	 * back to mortal, testing after both changes.
	 */
	@Test
	public void testIsImmortal() {
		Health health = new Health();
		Assert.assertFalse(health.isImmortal());

		health.setImmortal(true);
		Assert.assertTrue(health.isImmortal());

		health.setImmortal(false);
		Assert.assertFalse(health.isImmortal());
	}

	/**
	 * Tests that health is set correctly. This is essentially the same test as
	 * the unit test for the getter.
	 */
	@Test
	public void testSetHealth() {
		Health health = new Health();
		Assert.assertEquals(health.getMaxHealth(), health.getHealth());
		health.setHealth(33);
		Assert.assertEquals(33, health.getHealth());
		health.setHealth(health.getMinHealth() - 1);
		Assert.assertEquals(health.getMinHealth(), health.getHealth());
		health.setHealth(health.getMaxHealth() + 1);
		Assert.assertEquals(health.getMaxHealth(), health.getHealth());
	}

	/**
	 * Tries a couple of patterns of setting health to immortal and not, testing
	 * that it worked each time.
	 */
	@Test
	public void testSetImmortal() {
		Health health = new Health();
		// double toggle
		health.setImmortal(true);
		Assert.assertTrue(health.isImmortal());
		health.setImmortal(true);
		Assert.assertTrue(health.isImmortal());
		health.setImmortal(false);
		Assert.assertFalse(health.isImmortal());
		health.setImmortal(false);
		Assert.assertFalse(health.isImmortal());
		// start false, toggle
		health = new Health();
		health.setImmortal(false);
		Assert.assertFalse(health.isImmortal());
		health.setImmortal(true);
		Assert.assertTrue(health.isImmortal());
		health.setImmortal(false);
		Assert.assertFalse(health.isImmortal());
	}

	/**
	 * Tests several max healths and ensures they were changed right
	 */
	@Test
	public void testSetMaxHealth() {
		int i;
		/*
		 * Note that these must remain in sorted, ascending order because max
		 * has to always be set to larger than min before min is set.
		 */
		final int[] validVals =
				{-100, -98, -70, -68, -47, -45, -2, 0, 2, 55, 57, 97, 99, 203,
						205};
		Health health = new Health(validVals[0]);

		for (i = 0; i < validVals.length; ++i) {
			try {
				health.setMaxHealth(validVals[i]);
				health.setMinHealth(validVals[i] - 2);
			}
			catch (InvalidInputException e) {
				Assert.fail("Invalid max health (" + validVals[i]
						+ ") reported");
			}
			Assert.assertEquals(validVals[i], health.getMaxHealth());
		}

		Health invalid = new Health(0, 100);
		boolean threwError = false;
		try {
			invalid.setMaxHealth(-1);
		}
		catch (InvalidInputException e) {
			threwError = true;
		}
		Assert.assertTrue(threwError);

	}

	/**
	 * Tests several min healths and ensures they were changed right
	 */
	@Test
	public void testSetMinHealth() {
		int i;
		/*
		 * Note that these must remain in sorted, ascending order because max
		 * has to always be set to larger than min before min is set.
		 */
		final int[] validVals =
				{-100, -98, -70, -68, -47, -45, -2, 0, 2, 55, 57, 97, 99, 203,
						205};
		Health health = new Health(validVals[0]);

		for (i = 0; i < validVals.length; ++i) {
			try {
				health.setMaxHealth(validVals[i] + 2);
				health.setMinHealth(validVals[i]);
			}
			catch (InvalidInputException e) {
				Assert.fail("Invalid min health (" + validVals[i]
						+ ") reported");
			}
			Assert.assertEquals(validVals[i], health.getMinHealth());
		}

		Health invalid = new Health(0, 100);
		boolean threwError = false;
		try {
			invalid.setMinHealth(200);
		}
		catch (InvalidInputException e) {
			threwError = true;
		}
		Assert.assertTrue(threwError);
	}

}
