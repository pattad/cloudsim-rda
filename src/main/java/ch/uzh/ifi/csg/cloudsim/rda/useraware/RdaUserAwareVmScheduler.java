package ch.uzh.ifi.csg.cloudsim.rda.useraware;

import java.util.List;

import java.util.Map;

import org.cloudbus.cloudsim.Vm;

import ch.uzh.ifi.csg.cloudsim.rda.RdaVmScheduler;

/**
 * VM Schedulers to be used within the RDA module, must either implement this
 * interface or the RdaVmScheduler.
 * 
 * @author Patrick A. Taddei
 * @see RdaVmScheduler
 */
public interface RdaUserAwareVmScheduler {

	/**
	 * To retrieve the priorities of all users/customers that have a running VM
	 * on this host.
	 * 
	 * @param currentTime
	 *            The current simulation time.
	 * @param vms
	 *            the VMs to be taken into account
	 * @return A map, that contains the priority associated with each user.
	 */
	public abstract Map<String, Float> getUserPriorities(double currentTime,
			List<Vm> vms);

	/**
	 * This is the corresponding method for
	 * <code>VmScheduler.allocatePesForVm()</code> Hosts, supporting this
	 * interface, should throw an UnsupportedOperationException when calling
	 * this default method.
	 * 
	 * @param currentTime
	 *            The current simulation time.
	 * @param priorities
	 * @param vm
	 *            A map, that contains the priority associated with each user.
	 * @return the time of the next expected event for the concerning host.
	 */
	public abstract void allocateResourcesForAllVms(double currentTime,
			List<Vm> vms, Map<String, Float> userPriorities);

}