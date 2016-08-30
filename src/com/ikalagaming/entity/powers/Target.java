package com.ikalagaming.entity.powers;

/**
 * What a power allows the user to target.
 * 
 * @author Ches Burks
 *
 */
public enum Target {
	/**
	 * The power cannot target anything.
	 */
	NONE,
	/**
	 * Targets the user.
	 */
	SELF,
	/**
	 * Targets an ally or some sentient entity that is friendly to the user.
	 */
	FRIENDLY,
	/**
	 * Targets an enemy or some sentient entity that is hostile to the user.
	 */
	HOSTILE,
	/**
	 * Targets a particular location, such as the place a projectile is launched
	 * at, the center of an area of effect power that can be aimed, or where a
	 * device is placed.
	 */
	LOCATION,
	/**
	 * Targets a particular direction, such as where the user is facing.
	 */
	DIRECTON,
	/**
	 * Targets a sentient entity that is neutral towards the player, such as an
	 * impartial NPC.
	 */
	NEUTRAL,
	/**
	 * Targets any player, regardless of whether or not that player is aligned
	 * with the user.
	 */
	PLAYER,
	/**
	 * Targets an inanimate, or at least non-sentient, object in the world.
	 */
	OBJECT;
}