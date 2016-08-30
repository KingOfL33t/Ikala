package com.ikalagaming.entity.events;

import com.ikalagaming.entity.Entity;
import com.ikalagaming.entity.effects.Effect;
import com.ikalagaming.event.Event;

/**
 * An effect has been removed from an entity.
 *
 * @author Ches Burks
 *
 */
public class EffectDeactivated extends Event {
	/**
	 * The entity that the effect was removed from.
	 */
	private final Entity theTarget;

	/**
	 * The effect that was removed.
	 */
	private final Effect theEffect;

	/**
	 * An effect was removed from the given target.
	 *
	 * @param effect the effect removed from the entity
	 * @param target the entity the effect was removed from
	 */
	public EffectDeactivated(Effect effect, Entity target) {
		this.theTarget = target;
		this.theEffect = effect;
	}

	/**
	 * Returns the effect that was removed from the target.
	 *
	 * @return the effect
	 */
	public Effect getEffect() {
		return this.theEffect;
	}

	/**
	 * Returns the entity that was the target of the effect.
	 *
	 * @return the effect's target
	 */
	public Entity getTarget() {
		return this.theTarget;
	}

}
