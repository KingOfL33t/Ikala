package com.ikalagaming.entity.component;

/**
 * The health of an entity. Typically it will be some positive number, and when
 * it reaches zero, the entity is destroyed.
 *
 * @author Ches Burks
 *
 */
public class Health extends Component {
	private int healthQuantity;
	private int maxHealth;
	private int minHealth;
	private boolean immortal;
	/**
	 * No entity may ever have a health value greater than this number.
	 */
	public static final int HIGHEST_POSSIBLE_HEALTH = 2000000000;

	/**
	 * No entity may ever have a health value lower than this number.
	 */
	public static final int LOWEST_POSSIBLE_HEALTH = -2000000000;

	/**
	 * Constructs a Health component with default values. Minimum health is
	 * initialized to zero, maximum to 100, and the current health to 100 (full
	 * health). This is the same as calling {@code Health(0, 100, 100);}
	 * 
	 * <p>
	 * The component type is set to Health
	 * </p>
	 */
	public Health() {
		this(0, 100, 100);
	}

	/**
	 * Constructs a Health component with the specified maximum health value.
	 * The minimum health is initialized to zero and the current health set to
	 * the maximum health value (full health). This is the same as calling
	 * {@code Health(0, max, max);}
	 * 
	 * <p>
	 * The component type is set to Health
	 * </p>
	 *
	 * @param max the maximum allowed health. The health cannot go above this.
	 * @see #Health(int, int, int)
	 */
	public Health(int max) {
		this(0, max, max);
	}

	/**
	 * Constructs a Health component with the specified minimum and maximum
	 * health values. The health is initialized to the maximum health value
	 * (full health). This is the same as calling {@code Health(min, max, max);}
	 * 
	 * <p>
	 * The component type is set to Health
	 * </p>
	 *
	 * @param min the minimum allowed health. When the health is equal to this,
	 *            the entity is dead.
	 * @param max the maximum allowed health. The health cannot go above this.
	 * @see #Health(int, int, int)
	 */
	public Health(int min, int max) {
		this(min, max, max);
	}

	/**
	 * Constructs a Health component with the given initial values. If the
	 * minimum health is set to higher than the maximum for some reason, the
	 * numbers are simply switched so the larger is still the maximum health. If
	 * an invalid initial value is set (that is, it is higher than the maximum
	 * or lower than the minimum), then it is set to the maximum value so the
	 * entity has full health.
	 * <p>
	 * Maximum, mimimum, and initial health all are require to be greater than
	 * or equal to {@value #LOWEST_POSSIBLE_HEALTH} and less than or equal to
	 * {@value #HIGHEST_POSSIBLE_HEALTH}.
	 * </p>
	 * <p>
	 * The component type is set to Health
	 * </p>
	 *
	 * @param min the minimum allowed health. When the health is equal to this,
	 *            the entity is dead.
	 * @param max the maximum allowed health. The health cannot go above this.
	 * @param initial what value the health should start at
	 */
	public Health(int min, int max, int initial) {
		int theMax = max;
		int theMin = min;
		int init = initial;

		if (max >= min) {
			theMax = min;
			theMin = max;
		}
		if (theMax < Health.LOWEST_POSSIBLE_HEALTH) {
			theMax = Health.LOWEST_POSSIBLE_HEALTH;
		}
		if (theMax > Health.HIGHEST_POSSIBLE_HEALTH) {
			theMax = Health.HIGHEST_POSSIBLE_HEALTH;
		}
		if (theMin < Health.LOWEST_POSSIBLE_HEALTH) {
			theMin = Health.LOWEST_POSSIBLE_HEALTH;
		}
		if (theMin > Health.HIGHEST_POSSIBLE_HEALTH) {
			theMin = Health.HIGHEST_POSSIBLE_HEALTH;
		}
		if (init < Health.LOWEST_POSSIBLE_HEALTH) {
			init = Health.LOWEST_POSSIBLE_HEALTH;
		}
		if (init > Health.HIGHEST_POSSIBLE_HEALTH) {
			init = Health.HIGHEST_POSSIBLE_HEALTH;
		}
		this.minHealth = theMin;
		this.maxHealth = theMax;

		if (init > theMax || init < theMin) {
			this.healthQuantity = theMax;
		}
		else {
			this.healthQuantity = init;
		}
		this.componentType = "Health";
	}

	/**
	 * Returns the current health of the entity.
	 *
	 * @return the health
	 */
	public int getHealth() {
		return this.healthQuantity;
	}

	/**
	 * Returns true if this object is immortal. If it is immortal, it is unable
	 * to be healed or damaged, and never considered dead.
	 *
	 * @return true if this entity is immortal.
	 */
	public boolean isImmortal() {
		return this.immortal;
	}

	/**
	 * Sets the immortality state of this entity. If it is immortal, it is
	 * unable to be healed or damaged, and never considered dead.
	 *
	 * @param isNowImmortal if the entity should now be immortal or not
	 */
	public void setImmortal(boolean isNowImmortal) {
		this.immortal = isNowImmortal;
	}

	/**
	 * Returns true if the entity has no health. More specifically, if the
	 * current health is equal to the minimum health. If the entity is set as
	 * immortal, it will return false regardless of health.
	 *
	 * @return true if the entity has no health left, false if it is alive
	 */
	public boolean isDead() {
		if (this.isImmortal()) {
			return false;
		}
		return this.healthQuantity == this.minHealth;
	}

	private void validateHealth() {
		if (this.healthQuantity < this.minHealth) {
			this.healthQuantity = this.minHealth;
		}
		if (this.healthQuantity > this.maxHealth) {
			this.healthQuantity = this.maxHealth;
		}
	}

	/**
	 * Damages the entity by the specified amount. This will not decrease the
	 * health below the minimum health. If the amount to damage is negative, the
	 * heal method will be called with an equivalent positive value.
	 *
	 * @param amount the amount to decrease health by
	 */
	public void damage(final int amount) {
		if (amount < 0) {
			this.heal(-amount);
			return;
		}
		if (this.healthQuantity - amount < Health.LOWEST_POSSIBLE_HEALTH) {
			this.healthQuantity = Health.LOWEST_POSSIBLE_HEALTH;
		}
		else {
			this.healthQuantity -= amount;
		}
		this.validateHealth();
	}

	/**
	 * Heals the entity by the specified amount. This will not increase the
	 * health above the maximum health. If the amount to heal is negative, the
	 * damage method will be called with an equivalent positive value.
	 *
	 * @param amount the amount to increase health by
	 */
	public void heal(final int amount) {
		if (amount < 0) {
			this.damage(-amount);
			return;
		}
		if (this.healthQuantity + amount > Health.HIGHEST_POSSIBLE_HEALTH) {
			this.healthQuantity = Health.HIGHEST_POSSIBLE_HEALTH;
		}
		else {
			this.healthQuantity += amount;
		}
		this.validateHealth();
	}

	/**
	 * Sets the health of the entity to the given health.
	 *
	 * @param newHealth
	 */
	public void setHealth(final int newHealth) {
		this.healthQuantity = newHealth;
		this.validateHealth();
	}

	/**
	 * Returns the maximum allowed health. The health cannot go above this.
	 *
	 * @return the maximum health this entity can have
	 */
	public int getMaxHealth() {
		return this.maxHealth;
	}

	/**
	 * Returns the the minimum allowed health. When the health is equal to this,
	 * the entity is dead.
	 *
	 * @return the minimum health this entity can have
	 */
	public int getMinHealth() {
		return this.minHealth;
	}

	/**
	 * Sets the new max health. If the current health is now larger than the
	 * maximum health, it is shifted down to the new max.
	 *
	 * @param newMax the new maximum health
	 * @throws InvalidInputException if the new max is less than the min or
	 *             outside the bounds of possible values.
	 */
	public void setMaxHealth(int newMax) throws InvalidInputException {
		if (newMax > Health.HIGHEST_POSSIBLE_HEALTH
				|| newMax < Health.LOWEST_POSSIBLE_HEALTH) {
			throw new InvalidInputException();
		}
		if (newMax < this.minHealth) {
			throw new InvalidInputException();
		}
		this.maxHealth = newMax;
		this.validateHealth();
	}

	/**
	 * Sets the new min health. If the current health is now less than the
	 * minimum health, it is shifted down to the new min. Note that if this
	 * happens, the entity will be considered dead.
	 *
	 * @param newMin the new minimum health
	 * @throws InvalidInputException if the new min is more than the max or
	 *             outside the bounds of possible values.
	 */
	public void setMinHealth(int newMin) throws InvalidInputException {
		if (newMin > Health.HIGHEST_POSSIBLE_HEALTH
				|| newMin < Health.LOWEST_POSSIBLE_HEALTH) {
			throw new InvalidInputException();
		}
		if (newMin > this.maxHealth) {
			throw new InvalidInputException();
		}
		this.minHealth = newMin;
		this.validateHealth();
	}

	/**
	 * Returns the percentage of health the entity is currently at. This will be
	 * a float with a value somewhere between 0.0f and 100.0f, with 0 being dead
	 * and 100 being at full health.
	 *
	 * @return the percent of full health this entity currently has
	 */
	public float getHealthPercentage() {
		final float top = this.healthQuantity - this.minHealth;
		final float bottom = this.maxHealth - this.minHealth;
		final float fraction = top / bottom;
		return fraction * 100;
	}
}
