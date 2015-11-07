package com.ikalagaming.entity.effects;

import org.junit.Assert;
import org.junit.Test;

import com.ikalagaming.entity.Entity;
import com.ikalagaming.entity.component.Health;

/**
 * Tests for the basic effects
 *
 * @author Ches Burks
 *
 */
public class TestBasicEffects {

	/**
	 * Tests the damage method
	 */
	@Test
	public void damage() {
		Entity bob = new Entity("Bob");
		bob.addComponent(new Health(0, 100));
		Assert.assertNotNull(bob.getComponent("Health"));
		Health bobsHealth = (Health) bob.getComponent("Health");
		int lastHealth = bobsHealth.getHealth();
		bob.applyEffect(BasicEffect::damage);
		Assert.assertEquals(bobsHealth.getHealth(), lastHealth - 1, 0);
	}

	/**
	 * Tests the Effect returned by the customDamage method
	 */
	public void customDamage() {
		Entity bob = new Entity("Bob");
		bob.addComponent(new Health(0, 100));
		Assert.assertNotNull(bob.getComponent("Health"));
		Health bobsHealth = (Health) bob.getComponent("Health");
		int lastHealth = bobsHealth.getHealth();
		final int damageAmount = 10;
		bob.applyEffect(BasicEffect.customDamage(bob, damageAmount));
		Assert.assertEquals(bobsHealth.getHealth(), lastHealth - damageAmount,
				0);
	}
}
