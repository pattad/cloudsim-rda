package ch.uzh.ifi.csg.cloudsim.rda;

import java.util.List;

import org.cloudbus.cloudsim.Vm;

public interface MultipleResourcesVmScheduler {
	
	public void allocateResourcesForAllVms(double currentTime, List<Vm> vms);
	
}
