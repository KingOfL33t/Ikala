package com.ikalagaming.entity.powers;

import com.ikalagaming.entity.Resource;

/**
 * A resource, and how much of that resource, that is needed to use a power.
 *
 * @author Ches Burks
 *
 */
public class CostPair {
	private final int theCost;
	private final Resource resourceType;

	/**
	 * Constructs a cost structure given an amount and the associated resource.
	 * 
	 * @param amount how much of the resource to use
	 * @param resource the resource associated with that cost
	 */
	public CostPair(final int amount, final Resource resource) {
		this.theCost = amount;
		this.resourceType = resource;
	}

	/**
	 * Returns the amount of the resource needed.
	 * 
	 * @return the cost of the resource
	 */
	public int getCost() {
		return this.theCost;
	}

	/**
	 * Returns the type of the resource needed.
	 * 
	 * @return the type of the resource
	 */
	public Resource getResource() {
		return this.resourceType;
	}
}
