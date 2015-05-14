/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package ch.uzh.ifi.csg.cloudsim.rda.greediness;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmScheduler;
import ch.uzh.ifi.csg.cloudsim.rda.RdaHost;
import ch.uzh.ifi.csg.cloudsim.rda.RdaVm;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.StorageIOProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.useraware.UserAwareHost;

/**
 * The class of a host supporting dynamic workloads and performance degradation.
 * 
 */
public class RdaHostGreedinessUserAware extends RdaHost implements
		UserAwareHost {

	/**
	 * Instantiates a new host.
	 * 
	 * @param id
	 *            the id
	 * @param ramProvisioner
	 *            the ram provisioner
	 * @param bwProvisioner
	 *            the bw provisioner
	 * @param storage
	 *            the storage
	 * @param peList
	 *            the pe list
	 * @param vmScheduler
	 *            the VM scheduler
	 */
	public RdaHostGreedinessUserAware(int id, RamProvisioner ramProvisioner,
			BwProvisioner bwProvisioner, StorageIOProvisioner sProvisioner,
			long storage, List<? extends Pe> peList, VmScheduler vmScheduler,
			double scarcitySchedulingInterval) {
		super(id, ramProvisioner, bwProvisioner, sProvisioner, storage, peList,
				vmScheduler, scarcitySchedulingInterval);
	}

	public Map<String, Float> getUserPriorities(double currentTime) {

		return ((VmSchedulerGreedinessAllocationAlgorithmUserAware) getVmScheduler())
				.getUserGreediness(currentTime, getVmList());

	}

	public double updateVmsProcessing(double currentTime,
			Map<String, Float> priorities) {
		((VmSchedulerGreedinessAllocationAlgorithmUserAware) getVmScheduler())
				.allocateResourcesForAllVms(currentTime, getVmList(),
						priorities);

		double smallerTime = Double.MAX_VALUE;
		for (Vm vm : getVmList()) {
			double time = ((RdaVm) vm).updateVmProcessing(currentTime,
					getVmScheduler().getAllocatedMipsForVm(vm),
					((RdaVm) vm).getCurrentAllocatedBwFine(),
					((RdaVm) vm).getCurrentAllocatedStorageIO());
			if (time > 0.0 && time < smallerTime) {
				smallerTime = time;
			}
		}

		smallerTime = checkForScarcity(smallerTime, currentTime);

		updateHostState(currentTime);

		return smallerTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.uzh.ifi.csg.cloudsim.rda.RdaHost#updateVmsProcessing(double)
	 */
	@Override
	public double updateVmsProcessing(double currentTime) {
		throw new UnsupportedOperationException();
	}

}
