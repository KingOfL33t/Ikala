package com.ikalagaming.entity;

/**
 * An interface for entities that own currency and can perform transactions.
 * 
 * @author Ches Burks
 *
 */
public interface CurrencyHolder {
	/**
	 * Returns how much currency the entity has.
	 * 
	 * @return The amount of currency
	 */
	public int getCurrency();

	/**
	 * Sets the amount of currency the entity has.
	 * 
	 * @param amount How much currency they should have
	 */
	public void setCurrency(int amount);

	/**
	 * Add the given amount to the currency.
	 * 
	 * @param amount How much to add
	 */
	public void addCurency(int amount);

	/**
	 * Removes the given amount of currency.
	 * 
	 * @param amount How much to remove
	 */
	public void removeCurrency(int amount);

	/**
	 * Move currency from the given {@link CurrencyHolder} to this one. This
	 * removes currency from the other and adds it to this one.
	 * 
	 * @param other The currencyHolder to remove from
	 * @param amount How much to transfer
	 */
	public void transferCurrencyFrom(CurrencyHolder other, int amount);

	/**
	 * Move currency from this {@link CurrencyHolder} to the supplied one. This
	 * removes currency from this and adds it to the other one.
	 * 
	 * @param other The currencyHolder to add to
	 * @param amount How much to transfer
	 */
	public void transferCurrencyTo(CurrencyHolder other, int amount);
}
