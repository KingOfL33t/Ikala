package com.ikalagaming.entity.powers;

import java.util.ArrayList;
import java.util.TreeMap;

import com.ikalagaming.entity.Resource;

/**
 * A list of {@link CostPair CostPairs} and some helper methods for determining
 * info about the list. This list is final, as are all items in the list, and
 * hence cannot be altered.
 *
 * @author Ches Burks
 *
 */
public class CostList {

	private final CostPair[] costs;

	/**
	 * A list of costs that are needed for a power. Any resources that appear
	 * multiple times are simply summed up. Resources with a zero cost are
	 * ignored.
	 *
	 * @param pairs the pairs of resources and associated quantities
	 */
	public CostList(final CostPair... pairs) {
		TreeMap<Resource, Integer> resources = new TreeMap<>();
		for (CostPair pair : pairs) {
			if (pair.getCost() == 0) {
				continue;
			}
			if (resources.containsKey(pair.getResource())) {
				// increment by cost if it already exists
				resources.put(pair.getResource(),
						resources.get(pair.getResource()) + pair.getCost());
			}
			else {
				resources.put(pair.getResource(), pair.getCost());
			}
		}
		ArrayList<CostPair> costList = new ArrayList<>();
		for (Resource res : resources.keySet()) {
			costList.add(new CostPair(resources.get(res), res));
		}
		resources.clear();
		this.costs = (CostPair[]) costList.toArray();
		costList.clear();
	}

	/**
	 * Returns true if the list does include an entry for the resource
	 *
	 * @param resource the resource to test for
	 * @return true if that resource is needed, false otherwise
	 */
	public boolean doesRequire(final Resource resource) {
		for (CostPair pair : this.costs) {
			if (pair.getResource().equals(resource)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the list includes entries for all of the given resources.
	 *
	 * @param resources the resources to test for
	 * @return true if all resources are needed, false otherwise
	 */
	public boolean doesRequireAll(final Resource... resources) {
		for (Resource res : resources) {
			if (!this.doesRequire(res)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns true if the list includes entries for any number of the given
	 * resources.
	 *
	 * @param resources the resources to test for
	 * @return true if any of the resources are needed, false otherwise
	 */
	public boolean doesRequireAny(final Resource... resources) {
		for (Resource res : resources) {
			if (this.doesRequire(res)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the cost associated with the given resource. If the resource is
	 * not in the cost list, 0 is returned as that is technically how much of
	 * that resource is needed.
	 *
	 * @param resource the resource to fetch info on
	 * @return the quantity of the specified resource needed
	 */
	public int getCost(final Resource resource) {
		for (CostPair pair : this.costs) {
			if (pair.getResource().equals(resource)) {
				return pair.getCost();
			}
		}
		return 0;
	}

	/**
	 * Returns the internal list of cost pairs
	 *
	 * @return the list of cost pairs
	 */
	public CostPair[] getCosts() {
		return this.costs;
	}
}
