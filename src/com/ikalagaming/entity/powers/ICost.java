package com.ikalagaming.entity.powers;

/**
 * An interface that is supposed to be used to determine what a power costs to
 * use. This has one method so a lambda can be substituted in its place in a
 * method call.
 * 
 * @author Ches Burks
 *
 */
public interface ICost {
	/**
	 * Returns a {@link CostList list of costs} for a power. This lists
	 * resources and their associated amount required.
	 * 
	 * @return a CostList for what is required to use a power
	 */
	public CostList getCost();
}
