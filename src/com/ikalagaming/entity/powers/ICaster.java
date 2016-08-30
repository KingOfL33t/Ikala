package com.ikalagaming.entity.powers;

import com.ikalagaming.entity.Entity;

/**
 * An interface that is supposed to be used to determine what can use a power.
 * This has one method so a lambda can be substituted in its place in a method
 * call.
 * 
 * @author Ches Burks
 *
 */
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
