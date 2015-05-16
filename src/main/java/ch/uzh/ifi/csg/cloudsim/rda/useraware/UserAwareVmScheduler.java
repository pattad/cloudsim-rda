package ch.uzh.ifi.csg.cloudsim.rda.useraware;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Vm;

public interface UserAwareVmScheduler {

	public abstract Map<String, Float> getUserPriorities(double currentTime,
			List<Vm> vms);

	public abstract void allocateResourcesForAllVms(double currentTime,
			List<Vm> vms, Map<String, Float> userPriorities);

}