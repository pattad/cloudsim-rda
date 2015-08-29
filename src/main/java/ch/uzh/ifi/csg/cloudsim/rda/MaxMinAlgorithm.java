package ch.uzh.ifi.csg.cloudsim.rda;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is an implementation of the max-min fair share (MMFS) algorithm.
 * 
 * The available capacity is split among the customers in a fair manner. So that
 * no customer gets a share larger than its demand and the remaining customers
 * obtain an equal share of the resource.
 * 
 * @see http://en.wikipedia.org/wiki/Max-min_fairness
 * @see http://www.ece.rutgers.edu/~marsic/Teaching/CCN/minmax-fairsh.html
 * @author Patrick A. Taddei
 */
public class MaxMinAlgorithm {

	/**
	 * Allocates the resources according to the Max-Min fair share (MMFS)
	 * algorithm among the customers.
	 * 
	 * @param requestedResources
	 *            A map that contains the customers with their demands.
	 * @param maxCapacity
	 *            The maximal available capacity for the resource.
	 * @return a map with the resources allocated among the customers.
	 */
	public HashMap<String, Double> evaluate(
			HashMap<String, Double> requestedResources, double maxCapacity) {

		// cloning, so that no modifications are visible to outside
		@SuppressWarnings("unchecked")
		HashMap<String, Double> reqResources = (HashMap<String, Double>) ((HashMap<String, Double>) requestedResources)
				.clone();

		// no shortage of the requested resources
		if (!isResourceScarce(reqResources, maxCapacity)) {
			return requestedResources;
		}

		HashMap<String, Double> allocatedResources = new HashMap<String, Double>();

		int customerCnt = reqResources.size();
		double fairShare = maxCapacity / customerCnt;
		double remainingCapacity = maxCapacity;

		List<String> toRemove = null;

		// provision resources as long as the requested share of a customer is
		// smaller or equal to the remaining fair share of at least one customer
		while (toRemove == null || toRemove.size() != 0) {
			toRemove = new ArrayList<String>();
			for (String customer : reqResources.keySet()) {
				double requested = reqResources.get(customer);
				if (requested <= fairShare) {
					allocatedResources.put(customer, requested);
					toRemove.add(customer);
					remainingCapacity -= requested;
				}
			}
			for (String customer : toRemove) {
				reqResources.remove(customer);
			}
			customerCnt = reqResources.size();
			fairShare = remainingCapacity / customerCnt;
		}

		// splitting up leftover between the remaining customers
		fairShare = remainingCapacity / customerCnt;
		for (String s : reqResources.keySet()) {
			allocatedResources.put(s, fairShare);
		}

		return allocatedResources;
	}

	/**
	 * Checks if the resource is scarce.
	 * 
	 * @param requestedResources
	 *            A map that contains the customers with their demands.
	 * @param maxCapacity
	 *            The maximal available capacity for the resource.
	 * @return true, if the resource is scarce, otherwise return false
	 */
	public boolean isResourceScarce(HashMap<String, Double> requestedResources,
			double maxCapacity) {

		double totalRequested = 0.0f;
		for (Double s : requestedResources.values()) {
			totalRequested += s;
		}

		// no shortage of the requested resources
		if (totalRequested <= maxCapacity) {
			return false;
		}
		return true;
	}

	/**
	 * Returns the percentage of over-demand.
	 * 
	 * @param requestedResources
	 *            A map that contains the customers with their demands.
	 * @param maxCapacity
	 *            The maximal available capacity for the resource.
	 * @return >100 if over-demand, <100 if under-demand, 100 if demand is as
	 *         capacity
	 */
	public double getResourceDemand(HashMap<String, Double> requestedResources,
			double maxCapacity) {

		double totalRequested = 0.0f;
		for (Double s : requestedResources.values()) {
			totalRequested += s;
		}

		return totalRequested * 100 / maxCapacity;
	}
}
