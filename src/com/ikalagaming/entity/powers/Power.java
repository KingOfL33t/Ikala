package com.ikalagaming.entity.powers;

import com.ikalagaming.entity.Entity;

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

	public boolean doesTick();

	public String getName();

	public long getTickTime();

	public void onActivate();

	public void onDeactivate();

}
