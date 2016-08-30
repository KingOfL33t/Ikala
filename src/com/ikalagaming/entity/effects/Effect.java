package com.ikalagaming.entity.effects;

/**
 * A status or power that can be applied to an entity.
 *
 * @author Ches Burks
 *
 */
public class Effect {

	/**
	 * The duration that represents an effect that does not expire. ( {@value} )
	 */
	public static final int PERMANENT_DURATION = -1;

	/**
	 * How long the effect lasts, in seconds.
	 */
	private float theDuration;

	/**
	 * The time in seconds between ticks of the effect. If zero, it is
	 * considered continuous and does not tick.
	 */
	private int tickDelay;

	/**
	 * The name of the effect.
	 */
	private String theName;

	/**
	 * Creates an effect. If name is null, it is set to an empty string. If
	 * duration is negative (but not {@link #PERMANENT_DURATION}) then the
	 * duration will be set to 0 instead. If delay is negative, it will be set
	 * to 0 instead.
	 *
	 * @param name the name of the effect
	 * @param duration how many seconds it lasts
	 * @param delay how many seconds between ticks of the effect
	 */
	public Effect(final String name, final int duration, final int delay) {
		if (name == null) {
			this.theName = "";
		}
		else {
			this.theName = name;
		}
		if (duration < 0 && duration != Effect.PERMANENT_DURATION) {
			this.theDuration = 0;
		}
		else {
			this.theDuration = duration;
		}
		if (delay < 0) {
			this.tickDelay = 0;
		}
		else {
			this.tickDelay = delay;
		}
	}

	/**
	 * Returns how long this effect lasts. If it does not expire,
	 * {@link #PERMANENT_DURATION} is returned.
	 *
	 * @return the duration of the effect
	 */
	public float getDuration() {
		return this.theDuration;
	}

	/**
	 * Returns the name of this effect.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.theName;
	}

	/**
	 * Returns the number of seconds between ticks of the effect.
	 *
	 * @return the tickDelay
	 */
	public int getTickDelay() {
		return this.tickDelay;
	}

	/**
	 * Returns true if this effect does not tick, but instead is just always
	 * applied to the entity while the duration has not expired.
	 *
	 * @return true if the effect is continuous, false if it ticks
	 */
	public boolean isContinuous() {
		return this.tickDelay == 0;
	}

	/**
	 * Returns true if this effect does not expire, but instead remains active
	 * forever or until it is deactivated.
	 * 
	 * @return true if this effect is permanent, false if it has a finite
	 *         duration
	 */
	public boolean isPermanent() {
		return this.theDuration == PERMANENT_DURATION;
	}

}
