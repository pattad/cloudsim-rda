package ch.uzh.ifi.csg.cloudsim.rda.experiments;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

import ch.uzh.ifi.csg.cloudsim.rda.RdaHost;
import ch.uzh.ifi.csg.cloudsim.rda.VmSchedulerDRF;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisionerSimple;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisionerSimple;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.StorageIOProvisionerSimple;

/**
 * 
 * @author pat
 *
 */
public class DRFExperimentalSuite extends ExperimentalSuite {


	/**
	 * Main method to run this experiment
	 *
	 * @param args
	 *            the args
	 */
	public static void main(String[] args) {

		DRFExperimentalSuite suite = new DRFExperimentalSuite();
		// VMs and Hosts to create
		suite.simulate(2, 3, 3);

	}

	@Override
	public RdaHost createHost(int hostId) {
		List<Pe> peList = getHostConfig().getPeList();
		int ram = getHostConfig().getRam(); // host memory (MB)
		long storage = getHostConfig().getStorage(); // host storage (MB)
		int bw = getHostConfig().getBw(); // MBit/s
		int storageIO = getHostConfig().getStorageIO();

		RamProvisionerSimple ramProvisioner = new RamProvisionerSimple(ram);
		BwProvisionerSimple bwProvisioner = new BwProvisionerSimple(bw);
		StorageIOProvisionerSimple storageIOProvisioner = new StorageIOProvisionerSimple(
				storageIO);
		return new RdaHost(hostId, ramProvisioner, bwProvisioner,
				storageIOProvisioner, storage, peList, new VmSchedulerDRF(
						peList, ramProvisioner, bwProvisioner,
						storageIOProvisioner), scarcitySchedulingInterval);

	}
}
