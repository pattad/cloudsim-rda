package ch.uzh.ifi.csg.cloudsim.rda.experiments;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicySimple;

import ch.uzh.ifi.csg.cloudsim.rda.RdaHost;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisionerSimple;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisionerSimple;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.StorageIOProvisionerSimple;
import ch.uzh.ifi.csg.cloudsim.rda.useraware.RdaHostUserAware;
import ch.uzh.ifi.csg.cloudsim.rda.useraware.UserAwareDatacenter;
import ch.uzh.ifi.csg.cloudsim.rda.useraware.VmSchedulerDRFMH;

/**
 * This experimental suite applies the VmSchedulerDRFMH.
 * 
 * @author pat
 *
 */
public class DRFMHExperimentalSuite extends ExperimentalSuite {

	private double priorityUpdateInterval = 1.0;

	public DRFMHExperimentalSuite() {
		super();
	}

	/**
	 * Main method to run this experiment
	 *
	 * @param args
	 *            the args
	 */
	public static void main(String[] args) {

		DRFMHExperimentalSuite suite = new DRFMHExperimentalSuite();
		// VMs and Hosts to create
		suite.simulate(2, 3, 3);

	}

	@Override
	public Datacenter createDatacenter(String name, int hostCnt) {

		List<Host> hostList = new ArrayList<Host>();

		for (int i = 0; i < hostCnt; i++) {
			hostList.add(createHost(i)); // This
		}

		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are
																		// not
																		// adding
																		// SAN
		// devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		UserAwareDatacenter datacenter = null;
		try {
			datacenter = new UserAwareDatacenter(name, characteristics,
					new PowerVmAllocationPolicySimple(hostList), storageList,
					schedulingInterval);
		} catch (Exception e) {
			e.printStackTrace();
		}

		datacenter.setPriorityUpdateInterval(priorityUpdateInterval);

		return datacenter;
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
		return new RdaHostUserAware(hostId, ramProvisioner, bwProvisioner,
				storageIOProvisioner, storage, peList, new VmSchedulerDRFMH(
						peList, ramProvisioner, bwProvisioner,
						storageIOProvisioner), scarcitySchedulingInterval);

	}

	public double getPriorityUpdateInterval() {
		return priorityUpdateInterval;
	}

	public void setPriorityUpdateInterval(double priorityUpdateInterval) {
		this.priorityUpdateInterval = priorityUpdateInterval;
	}

}
