package ch.uzh.ifi.csg.cloudsim.rda;

import java.util.List;

import org.cloudbus.cloudsim.Vm;

import ch.uzh.ifi.csg.cloudsim.rda.useraware.RdaUserAwareVmScheduler;

/**
 * VM Schedulers to be used within the RDA module, must either implement this
 * interface or the RdaUserAwareVmScheduler.
 * 
 * @author Patrick A. Taddei
 * @see RdaUserAwareVmScheduler
 */
public interface RdaVmScheduler {

	/**
	 * This method replaces the method
	 * <code>VmScheduler.allocatePesForVm()</code> Because the RDA module
	 * supports multiple resources the resources for all VMs have to be
	 * allocated in one step, as they are all interdependent.
	 * 
	 * VmScheduler that support this interface should throw an
	 * UnsupportedOperationException when calling the
	 * VmScheduler.allocatePesForVm().
	 * 
	 * @param currentTime
	 *            The current simulation time.
	 * @param vms
	 *            The list of VMs running on the host.
	 */
	public abstract void allocateResourcesForAllVms(double currentTime,
			List<Vm> vms);

}
