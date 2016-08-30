package com.ikalagaming.entity;

/**
 * A list of resources that can be owned by an entity or used as a cost for
 * something.
 * 
 * @author Ches Burks
 *
 */
public enum Resource {
	/**
	 * How much life an entity has. Generally, having none kills or destroys the
	 * entity.
	 */
	HEALTH,
	/**
	 * Magical power. Generally, having none means magical spells and the like
	 * cannot be used.
	 */
	MANA,
	/**
	 * Physical exhaustion. Used to determine things like how fast living
	 * entities can move or whether they can perform some physical task.
	 */
	STAMINA,
	/**
	 * Experience. Typically used with some sort of leveling system.
	 */
	EXP;
}
