package com.ikalagaming.entity;

/**
 * An interface for entities that own currency and can perform transactions.
 * Implementations of this should make sure to account for thread safety.
 *
 * @author Ches Burks
 *
 */
public interface ICurrencyHolder {
	/**
	 * Add the given amount to the currency.
	 *
	 * @param amount How much to add
	 */
	public void addCurency(int amount);

	/**
	 * Returns how much currency the entity has.
	 *
	 * @return The amount of currency
	 */
	public int getCurrency();

	/**
	 * Removes the given amount of currency.
	 *
	 * @param amount How much to remove
	 */
	public void removeCurrency(int amount);

	/**
	 * Sets the amount of currency the entity has.
	 *
	 * @param amount How much currency they should have
	 */
	public void setCurrency(int amount);

	/**
	 * Move currency from the given {@link ICurrencyHolder} to this one. This
	 * removes currency from the other and adds it to this one.
	 *
	 * @param other The currencyHolder to remove from
	 * @param amount How much to transfer
	 */
	public void transferCurrencyFrom(ICurrencyHolder other, int amount);

	/**
	 * Move currency from this {@link ICurrencyHolder} to the supplied one. This
	 * removes currency from this and adds it to the other one.
	 *
	 * @param other The currencyHolder to add to
	 * @param amount How much to transfer
	 */
	public void transferCurrencyTo(ICurrencyHolder other, int amount);
}
