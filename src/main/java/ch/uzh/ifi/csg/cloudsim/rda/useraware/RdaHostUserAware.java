package ch.uzh.ifi.csg.cloudsim.rda.useraware;

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

/**
 * This is an implementation of the UserAwareHost interface. This host must be
 * used, when setting up a scheduling policy with a user aware context.
 * 
 * @author Patrick A. Taddei
 * @see UserAwareHost
 */
public class RdaHostUserAware extends RdaHost implements UserAwareHost {

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
	 * @param scarcitySchedulingInterval
	 *            The scheduling interval, if a resource is scarce.
	 */
	public RdaHostUserAware(int id, RamProvisioner ramProvisioner,
			BwProvisioner bwProvisioner, StorageIOProvisioner sProvisioner,
			long storage, List<? extends Pe> peList, VmScheduler vmScheduler,
			double scarcitySchedulingInterval) {
		super(id, ramProvisioner, bwProvisioner, sProvisioner, storage, peList,
				vmScheduler, scarcitySchedulingInterval);
	}

	/**
	 * @see UserAwareHost
	 */
	public Map<String, Float> getUserPriorities(double currentTime) {

		return ((RdaUserAwareVmScheduler) getVmScheduler()).getUserPriorities(
				currentTime, getVmList());

	}

	/**
	 * @see UserAwareHost
	 */
	public double updateVmsProcessing(double currentTime,
			Map<String, Float> priorities) {
		((RdaUserAwareVmScheduler) getVmScheduler())
				.allocateResourcesForAllVms(currentTime, getVmList(),
						priorities);

		double smallerTime = Double.MAX_VALUE;
		for (Vm vm : getVmList()) {

			if (priorities.containsKey(((RdaVm) vm).getCustomer())) {
				// set the current priority of the VM according to the user's
				// priority
				((RdaVm) vm).setCurrentPriority(priorities.get(((RdaVm) vm)
						.getCustomer()));
			}
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

	@Override
	public double updateVmsProcessing(double currentTime) {
		throw new UnsupportedOperationException();
	}

}
