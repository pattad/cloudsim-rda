package ch.uzh.ifi.csg.cloudsim.rda.experiments;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicySimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

import ch.uzh.ifi.csg.cloudsim.rda.RdaCloudlet;
import ch.uzh.ifi.csg.cloudsim.rda.RdaCloudletSchedulerDynamicWorkload;
import ch.uzh.ifi.csg.cloudsim.rda.RdaDatacenter;
import ch.uzh.ifi.csg.cloudsim.rda.RdaHost;
import ch.uzh.ifi.csg.cloudsim.rda.RdaVm;
import ch.uzh.ifi.csg.cloudsim.rda.VmSchedulerMaxMinFairShare;
import ch.uzh.ifi.csg.cloudsim.rda.data.StochasticDataGenerator;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisionerSimple;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisionerSimple;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.StorageIOProvisionerSimple;

/**
 * 
 */
public class ExperimentalSuite {

	/** the minimal scheduling interval between events */
	public static double schedulingInterval = 0.000000001; // nano second

	/** the maximal scheduling interval if scarcity occurs on a host */
	public static double scarcitySchedulingInterval = 0.01; // milli second

	/** Record output to a CSV file */
	public static boolean record = true;

	/**
	 * Main method to run this example as an application.
	 *
	 * @param args
	 *            the args
	 */
	public static void main(String[] args) {

		ExperimentalSuite suite = new ExperimentalSuite();
		// VMs and Hosts to create
		suite.simulate(2, 5);
	}

	/**
	 * 
	 * @param vmCnt
	 * @param hostCnt
	 */
	public void simulate(int vmCnt, int hostCnt) {

		List<Cloudlet> cloudletList;
		List<Vm> vmlist;

		try {
			int num_user = 1; // number of cloud users
			boolean trace_flag = false; // trace events

			CloudSim.init(num_user, Calendar.getInstance(), trace_flag,
					schedulingInterval);

			createDatacenter("Datacenter_01", hostCnt);
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			vmlist = createVms(vmCnt, brokerId);

			// submit vm list to the broker
			broker.submitVmList(vmlist);

			cloudletList = createCloudlets(brokerId);

			// submit cloudlet list to the broker
			broker.submitCloudletList(cloudletList);

			// Sixth step: Starts the simulation
			CloudSim.startSimulation();

			CloudSim.stopSimulation();

			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			printCloudletList(newList);

		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Error occured during simulation: " + e.getMessage());
		}
	}

	public List<Vm> createVms(int vmCnt, int brokerId) {
		List<Vm> vmlist;
		vmlist = new ArrayList<Vm>();

		for (int i = 0; i < vmCnt; i++) {
			vmlist.add(createVm(i, brokerId));
		}
		return vmlist;
	}

	/**
	 * 
	 * @param brokerId
	 * @return
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public ArrayList<Cloudlet> createCloudlets(int brokerId)
			throws FileNotFoundException, UnsupportedEncodingException {
		ArrayList<Cloudlet> cloudletList = new ArrayList<Cloudlet>();

		StochasticDataGenerator randomData = new StochasticDataGenerator(120);
		// Cloudlet properties
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;

		Cloudlet cloudlet = new RdaCloudlet(0, pesNumber, fileSize, outputSize,
				randomData.generateWebServerData(235.6, 10.85), record);
		cloudlet.setUserId(brokerId);
		cloudlet.setVmId(0);

		cloudletList.add(cloudlet);
		return cloudletList;
	}

	/**
	 * 
	 * @param vmId
	 * @param brokerId
	 * @return
	 */
	public RdaVm createVm(int vmId, int brokerId) {
		// VM description, this resources will be checked, when allocating
		// it to a host
		int mips = 200;
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name

		// create VM
		return new RdaVm(vmId, brokerId, mips, pesNumber, ram, bw, size, 1,
				vmm, new RdaCloudletSchedulerDynamicWorkload(mips, pesNumber,
						scarcitySchedulingInterval), schedulingInterval);
	}

	/**
	 * Creates the RDA datacenter.
	 *
	 * @param name
	 *            the name
	 *
	 * @return the datacenter
	 */
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

		Datacenter datacenter = null;
		try {
			datacenter = new RdaDatacenter(name, characteristics,
					new PowerVmAllocationPolicySimple(hostList), storageList,
					schedulingInterval);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

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
				storageIOProvisioner, storage, peList,
				new VmSchedulerMaxMinFairShare(peList, ramProvisioner,
						bwProvisioner, storageIOProvisioner),
				scarcitySchedulingInterval);
	}

	/**
	 * Creates the broker.
	 *
	 * @return the datacenter broker
	 */
	private DatacenterBroker createBroker() {
		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects.
	 *
	 * @param list
	 *            list of Cloudlets
	 */
	private void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Time"
				+ indent + indent + indent + indent + "Start Time" + indent
				+ "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ Math.round(cloudlet.getActualCPUTime() * 1000000)
						/ 1000000.0 + indent + indent
						+ dft.format(cloudlet.getExecStartTime()) + indent
						+ indent + dft.format(cloudlet.getFinishTime()));
			}

			// Log.printLine(cloudlet.getCloudletHistory());

		}
	}
}