package com.ikalagaming.entity.effects;

import java.util.function.Consumer;

import com.ikalagaming.entity.Entity;
import com.ikalagaming.entity.component.Health;

/**
 * Some simple effects
 *
 * @author Ches Burks
 *
 */
public class BasicEffect {
	/**
	 * Does one damage to an entity if it has a Health component.
	 *
	 * @param target the target entity
	 */
	public static void damage(Entity target) {
		target.applyEffect((entity) -> {
			if (!entity.hasComponent("Health")) {
				return;
			}
			Health health = (Health) entity.getComponent("Health");
			health.damage(1);
		});
	}

	/**
	 * Creates a new Effect that applies the given damage to an entity, and
	 * returns it.
	 *
	 * @param target the target to damage
	 * @param damage the damage to do to the entity
	 * @return the newly created Effect
	 */
	public static Effect customDamage(Entity target, int damage) {
		Consumer<Entity> newEffect = (entity) -> {
			if (!entity.hasComponent("Health")) {
				return;
			}
			Health health = (Health) entity.getComponent("Health");
			health.damage(damage);
		};
		return newEffect::accept;
	}
}
