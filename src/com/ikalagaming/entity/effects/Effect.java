package com.ikalagaming.entity.effects;

import com.ikalagaming.entity.Entity;

/**
 * A status or power that can be applied to an entity.
 *
 * @author Ches Burks
 *
 */
public interface Effect {

	/**
	 * Does one tick of whatever this effect does. Some effects are only called
	 * once, and others are applied over time.
	 *
	 * @param target the entity to affect
	 */
	public void tick(Entity target);
}
