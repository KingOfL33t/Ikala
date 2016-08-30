package com.ikalagaming.entity.powers;

import com.ikalagaming.entity.Entity;

/**
 * A minimum representation of a power that can be owned by or applied to an
 * entity.
 * 
 * @author Ches Burks
 *
 */
public interface Power {
	/**
	 * Applies this effect to the given entity. May be called repeatedly.
	 * Effects may include damaging or healing the entity, affecting their
	 * movement, blinding them, etc. This may or may not do anything depending
	 * on the entity and power.
	 *
	 * @param target the entity that may be affected by this power
	 */
	public void applyEffect(Entity target);

	/**
	 * Returns true if the power is applied over time (that is, repeatedly every
	 * so often). Returns false if the power only does something once.
	 * 
	 * @return true if the power ticks over time or false if it is a one-time
	 *         effect
	 */
	public boolean doesTick();

	/**
	 * Returns the (unlocalized) name of the power
	 * 
	 * @return the name of the power
	 */
	public String getName();

	/**
	 * Returns the length of time between ticks of the power in milliseconds. If
	 * X is returned, and the power {@link #doesTick() ticks} then every X ms
	 * the effect is applied. If the power does not tick then the return value
	 * is useless.
	 * 
	 * @return the time between ticks in ms
	 */
	public long getTickTime();

	/**
	 * Performs an action associated with the start of the power. This is the
	 * first thing the power does when applied, and is allowed to do nothing.
	 */
	public void onActivate();

	/**
	 * Performs an action associated with a power finishing. This is the last
	 * thing the power does when applied, and is allowed to do nothing.
	 */
	public void onDeactivate();

}
