package com.ikalagaming.entity;

import java.util.concurrent.locks.ReentrantLock;

import com.ikalagaming.entity.component.Health;
import com.ikalagaming.entity.component.Inventory;

/**
 * A player in the game which might be controlled by a human. Humanoid NPC's
 * should probably use this as the base class by adding and removing components
 * as necessary.
 * 
 * @author Ches Burks
 *
 */
public class Player extends Entity implements ICurrencyHolder {

	private int currency;
	private ReentrantLock currencyLock = new ReentrantLock();
	private final static int INVENTORY_SIZE = 32;

	/**
	 * Constructs a player and gives it some essential components.
	 */
	public Player() {
		this.addComponent(new Health());
		this.addComponent(new Inventory(INVENTORY_SIZE));
	}

	@Override
	public void addCurency(int amount) {
		if (amount <= 0) {
			return;
		}
		currencyLock.lock();
		try {
			currency += amount;
		}
		finally {
			currencyLock.unlock();
		}

	}

	@Override
	public int getCurrency() {
		int amt = 0;
		currencyLock.lock();
		try {
			amt = currency;
		}
		finally {
			currencyLock.unlock();
		}
		return amt;
	}

	@Override
	public void removeCurrency(int amount) {
		if (amount <= 0) {
			return;
		}
		currencyLock.lock();
		try {
			currency -= amount;
		}
		finally {
			currencyLock.unlock();
		}
	}

	@Override
	public void setCurrency(int amount) {
		currencyLock.lock();
		try {
			currency = amount;
		}
		finally {
			currencyLock.unlock();
		}
	}

	@Override
	public void transferCurrencyFrom(ICurrencyHolder other, int amount) {
		other.removeCurrency(amount);// hopefully this is thread-safe
		this.addCurency(amount);
	}

	@Override
	public void transferCurrencyTo(ICurrencyHolder other, int amount) {
		this.removeCurrency(amount);// hopefully this is thread-safe
		other.addCurency(amount);
	}

}
