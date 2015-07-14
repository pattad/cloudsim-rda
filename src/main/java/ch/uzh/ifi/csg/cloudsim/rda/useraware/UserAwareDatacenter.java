package ch.uzh.ifi.csg.cloudsim.rda.useraware;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;

import ch.uzh.ifi.csg.cloudsim.rda.RdaDatacenter;

/**
 * This datacenter supports a user aware VM scheduling policy. Before calling
 * the method updateVmsProcessing() on the hosts, this datacenter calls
 * getUserPriorities() on all hosts. This gathers all priorities of the users,
 * so that they can be taken into account when updating the resources for the
 * VMs, running on the different hosts.
 * 
 * @author Patrick A. Taddei
 */
public class UserAwareDatacenter extends RdaDatacenter {

	private Map<String, Float> userPriorities;

	private double lastUpdateTime;

	private double priorityUpdateInterval = 1;

	/**
	 * 
	 * Instantiates a new user aware datacenter.
	 * 
	 * @param name
	 *            the name of the datacenter
	 * @param characteristics
	 *            an object of DatacenterCharacteristics
	 * @param vmAllocationPolicy
	 *            the vm provisioner
	 * @param storageList
	 *            the storage list
	 * @param schedulingInterval
	 *            the scheduling interval
	 * @throws Exception
	 *             This happens when one of the following scenarios occur:
	 *             <ul>
	 *             <li>creating this entity before initializing CloudSim package
	 *             <li>this entity name is <tt>null</tt> or empty
	 *             <li>this entity has <tt>zero</tt> number of PEs (Processing
	 *             Elements). <br>
	 *             No PEs mean the Cloudlets can't be processed. A CloudResource
	 *             must contain one or more Machines. A Machine must contain one
	 *             or more PEs.
	 *             </ul>
	 */
	public UserAwareDatacenter(String name,
			DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList,
			double schedulingInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList,
				schedulingInterval);
	}

	@Override
	protected double processHosts(double currentTime, double minTime) {

		// only update user priority every X seconds
		if (userPriorities == null
				|| currentTime - lastUpdateTime >= priorityUpdateInterval) {

			userPriorities = new HashMap<String, Float>();

			for (PowerHost host : this.<PowerHost> getHostList()) {

				Map<String, Float> updatedUsers = ((UserAwareHost) host)
						.getUserPriorities(currentTime);

				for (String userName : updatedUsers.keySet()) {
					if (userPriorities.containsKey(userName)) {
						Float currentVal = userPriorities.get(userName);
						userPriorities.put(userName,
								currentVal + updatedUsers.get(userName));
					} else {
						userPriorities
								.put(userName, updatedUsers.get(userName));
					}
				}
			}
			lastUpdateTime = currentTime;
		}

		for (PowerHost host : this.<PowerHost> getHostList()) {
			Log.formatLine("\n%.2f: Host #%d", CloudSim.clock(), host.getId());

			double time = ((UserAwareHost) host).updateVmsProcessing(
					currentTime, userPriorities);

			if (time < minTime) {
				minTime = time;
			}
		}

		return minTime;
	}

	/**
	 * 
	 * @return the current update interval of the priorities
	 */
	public double getPriorityUpdateInterval() {
		return priorityUpdateInterval;
	}

	/**
	 * Specifies the interval to update the priorities. (in seconds)
	 * 
	 * @param priorityUpdateInterval
	 */
	public void setPriorityUpdateInterval(double priorityUpdateInterval) {
		this.priorityUpdateInterval = priorityUpdateInterval;
	}

}
