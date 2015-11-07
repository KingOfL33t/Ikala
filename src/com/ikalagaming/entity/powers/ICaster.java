package com.ikalagaming.entity.powers;

import com.ikalagaming.entity.Entity;

public interface ICaster {
	/**
	 * Returns true if the given entity is (currently) able to use this ability.
	 * An entity that is dead or stunned for example might not be able to use an
	 * ability.
	 *
	 * @param entity the entity that might cast this ability
	 * @return true if it can use the ability false otherwise
	 */
	public boolean canUse(Entity entity);
}
