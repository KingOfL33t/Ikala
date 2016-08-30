package com.ikalagaming.entity.events;

import com.ikalagaming.entity.Entity;
import com.ikalagaming.entity.effects.Effect;
import com.ikalagaming.event.Event;

/**
 * An effect has been applied to an entity.
 *
 * @author Ches Burks
 *
 */
public class EffectActivated extends Event {

	/**
	 * The entity that the effect was applied to.
	 */
	private final Entity theTarget;

	/**
	 * The effect that was applied.
	 */
	private final Effect theEffect;

	/**
	 * An effect was activated on the given target.
	 *
	 * @param effect the effect applied to the entity
	 * @param target the entity to apply the effect to
	 */
	public EffectActivated(Effect effect, Entity target) {
		this.theTarget = target;
		this.theEffect = effect;
	}

	/**
	 * Returns the effect that was activated on the target
	 *
	 * @return the effect
	 */
	public Effect getEffect() {
		return this.theEffect;
	}

	/**
	 * Returns the entity that is the target of the effect
	 *
	 * @return the effect's target
	 */
	public Entity getTarget() {
		return this.theTarget;
	}

}
