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
		List<Pe> peList = new ArrayList<Pe>();
		int mips = 1000;
		peList.add(new Pe(0, new PeProvisionerSimple(mips)));
		int ram = 2048; // host memory (MB)
		long storage = 1000000; // host storage (MB)
		int bw = 1000; // MBit/s
		int storageIO = 10000;

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
