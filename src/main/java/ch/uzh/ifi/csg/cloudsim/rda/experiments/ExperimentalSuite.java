package ch.uzh.ifi.csg.cloudsim.rda.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

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

import ch.uzh.ifi.csg.cloudsim.rda.RdaCloudlet;
import ch.uzh.ifi.csg.cloudsim.rda.RdaCloudletSchedulerDynamicWorkload;
import ch.uzh.ifi.csg.cloudsim.rda.RdaDatacenter;
import ch.uzh.ifi.csg.cloudsim.rda.RdaHost;
import ch.uzh.ifi.csg.cloudsim.rda.RdaVm;
import ch.uzh.ifi.csg.cloudsim.rda.VmSchedulerMaxMinFairShare;
import ch.uzh.ifi.csg.cloudsim.rda.experiments.config.HostConfig;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisionerSimple;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisionerSimple;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.StorageIOProvisionerSimple;

/**
 * 
 */
public class ExperimentalSuite {

	/** the minimal scheduling interval between events */
	public static double schedulingInterval = 0.01; // nano second

	/** the maximal scheduling interval if scarcity occurs on a host */
	public static double scarcitySchedulingInterval = 0.01;

	/** Record output to a CSV file */
	private boolean record = true;

	/** Trace log */
	private boolean trace = false;

	/** The input data for the cloudlets */
	private ArrayList<ArrayList<double[]>> inputData;

	private Datacenter datacenter;

	private double timeTotal;

	public void setHostConfig(HostConfig hostConfig) {
		this.hostConfig = hostConfig;
	}

	public HostConfig getHostConfig() {
		return hostConfig;
	}

	private HostConfig hostConfig = new HostConfig();

	/**
	 * Main method to run this example as an application.
	 *
	 * @param args
	 *            the args
	 */
	public static void main(String[] args) {

		ExperimentalSuite suite = new ExperimentalSuite();
		// VMs and Hosts to create
		suite.simulate(2, 5, 3);
	}

	/**
	 * 
	 * @param vmCnt
	 * @param hostCnt
	 */
	public void simulate(int vmCnt, int hostCnt, int userCnt) {

		List<Cloudlet> cloudletList;
		List<Vm> vmlist;
		try {
			if (trace) {
				try {
					SimpleDateFormat df = new SimpleDateFormat(
							"yyyyMMddhhmmssSSS");

					Log.setOutput(new FileOutputStream(new File(df
							.format(new Date()) + "_trace.log")
							.getAbsoluteFile()));

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				Log.setDisabled(true);
			}

			int num_user = 1; // number of cloud users
			boolean trace_flag = false; // trace events

			CloudSim.init(num_user, Calendar.getInstance(), trace_flag,
					schedulingInterval);

			datacenter = createDatacenter("Datacenter_01", hostCnt);
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			vmlist = createVms(vmCnt, brokerId, userCnt);

			// submit vm list to the broker
			broker.submitVmList(vmlist);

			cloudletList = createCloudlets(brokerId, vmCnt);

			// submit cloudlet list to the broker
			broker.submitCloudletList(cloudletList);

			// Sixth step: Starts the simulation
			CloudSim.startSimulation();

			CloudSim.stopSimulation();

			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			List<Vm> vms = broker.getVmList();

			printCloudletList(newList, vms, userCnt);

		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Error occured during simulation: " + e.getMessage());
		}
	}

	public List<Vm> createVms(int vmCnt, int brokerId, int userCnt) {
		List<Vm> vmlist;
		vmlist = new ArrayList<Vm>();

		for (int i = 0; i < vmCnt; i++) {
			vmlist.add(createVm(i, brokerId, "user_" + i % userCnt));
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
	public ArrayList<Cloudlet> createCloudlets(int brokerId, int vmCnt)
			throws FileNotFoundException, UnsupportedEncodingException {
		ArrayList<Cloudlet> cloudletList = new ArrayList<Cloudlet>();

		// Cloudlet properties
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;

		int vmId = 0;
		int cloudletId = 0;

		while (vmId < vmCnt) {

			RdaCloudlet cloudlet = new RdaCloudlet(cloudletId, pesNumber,
					fileSize, outputSize, this.inputData.get(cloudletId),
					record);
			cloudlet.setUserId(brokerId);
			cloudlet.setVmId(vmId);
			cloudletList.add(cloudlet);

			cloudletId++;
			vmId++;
		}

		return cloudletList;
	}

	/**
	 * 
	 * @param vmId
	 * @param brokerId
	 * @param userName
	 * @return
	 */
	public RdaVm createVm(int vmId, int brokerId, String userName) {
		// VM description, this resources will be checked, when allocating
		// it to a host
		int mips = 300;
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name
		// create VM
		RdaVm vm = new RdaVm(vmId, brokerId, mips, pesNumber, ram, bw, size, 1,
				vmm, new RdaCloudletSchedulerDynamicWorkload(mips, pesNumber,
						scarcitySchedulingInterval), schedulingInterval);

		((RdaVm) vm).setCustomer(userName); // specify the user/owner of the VM
		return vm;
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
		List<Pe> peList = hostConfig.getPeList();
		int ram = hostConfig.getRam(); // host memory (MB)
		long storage = hostConfig.getStorage(); // host storage (MB)
		int bw = hostConfig.getBw(); // MBit/s
		int storageIO = hostConfig.getStorageIO();

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
	private void printCloudletList(List<Cloudlet> cloudlets, List<Vm> vms,
			int userCnt) {
		int size = cloudlets.size();
		double[][] userTotals = new double[userCnt][4];

		Cloudlet cloudlet;

		StringBuilder result = new StringBuilder();

		String indent = "    ";
		TreeMap<String, Double> totalTime = new TreeMap<String, Double>();

		result.append(System.getProperty("line.separator")
				+ " ========== RESULT ========== "
				+ System.getProperty("line.separator"));
		result.append("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Time"
				+ indent + "Start Time" + indent + "Finish Time" + indent
				+ "VM customer " + System.getProperty("line.separator"));

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = cloudlets.get(i);
			result.append(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				result.append("SUCCESS");

				RdaVm currentVm = null;
				for (Vm vm : vms) {
					if (vm.getId() == cloudlet.getVmId())
						currentVm = (RdaVm) vm;
				}
				String customer = currentVm.getCustomer();

				if (totalTime.get(customer) == null) {
					totalTime.put(customer, cloudlet.getActualCPUTime());
				} else {
					totalTime.put(customer, cloudlet.getActualCPUTime()
							+ totalTime.get(customer));
				}

				result.append(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + indent
						+ cloudlet.getVmId() + indent + indent
						+ Math.round(cloudlet.getActualCPUTime() * 1000000)
						/ 1000000.0 + indent + indent
						+ dft.format(cloudlet.getExecStartTime()) + indent
						+ indent + dft.format(cloudlet.getFinishTime())
						+ indent + indent + indent + customer);
			}

			ArrayList<double[]> workload = this.getInputData().get(
					cloudlet.getCloudletId());

			double totalCpu = 0.0d;
			double totalBw = 0.0d;
			double totalStorageIO = 0.0d;
			for (double[] line : workload) {
				totalCpu += line[0];
				totalBw += line[2];
				totalStorageIO += line[3];
			}

			userTotals[i % userCnt][0] = userTotals[i % userCnt][0] + totalCpu;
			userTotals[i % userCnt][1] = userTotals[i % userCnt][1] + totalBw;
			userTotals[i % userCnt][2] = userTotals[i % userCnt][2]
					+ totalStorageIO;

			result.append(System.getProperty("line.separator"));
			// Log.printLine(cloudlet.getCloudletHistory());

		}

		result.append("Evaluation result: "
				+ ((RdaDatacenter) datacenter).getEvaluationtString());

		result.append(System.getProperty("line.separator") + "By customers "
				+ System.getProperty("line.separator"));
		timeTotal = 0;

		int n = 0;
		for (String cust : totalTime.keySet()) {

			double time = round(totalTime.get(cust));
			result.append(cust + " time: " + time
					+ System.getProperty("line.separator"));
			timeTotal += totalTime.get(cust);

			userTotals[n % userCnt][3] = time;
			n++;
		}

		result.append("sum time: " + round(timeTotal)
				+ System.getProperty("line.separator"));

		result.append("Resources: " + System.getProperty("line.separator"));

		int i = 0;
		double totalCpu = 0.0d;
		double totalBw = 0.0d;
		double totalStorageIO = 0.0d;
		for (double[] entry : userTotals) {
			result.append("mips: " + round(entry[0]) + ", bw: "
					+ round(entry[1]) + ", disk I/O: " + round(entry[2])
					+ System.getProperty("line.separator"));
			totalCpu += entry[0];
			totalBw += entry[1];
			totalStorageIO += entry[2];
			i++;
		}

		result.append("sum mips: " + round(totalCpu) + ", bw: "
				+ round(totalBw) + ", disk I/O: " + round(totalStorageIO)
				+ System.getProperty("line.separator"));

		result.append("Percentages: " + System.getProperty("line.separator"));

		for (double[] entry : userTotals) {
			result.append("mips: " + roundFine(entry[0] * 100 / totalCpu / 100)
					+ ", bw: " + roundFine(entry[1] * 100 / totalBw / 100)
					+ ", disk I/O: "
					+ roundFine(entry[2] * 100 / totalStorageIO / 100)
					+ ", time: " + roundFine(entry[3] * 100 / timeTotal / 100)
					+ System.getProperty("line.separator"));
		}

		result.append("Offsets: " + System.getProperty("line.separator"));

		double mipsTotal = 0;
		double bwTotal = 0;
		double diskTotal = 0;
		for (double[] entry : userTotals) {
			double time = round(entry[3] * 100 / timeTotal / 100);

			double mips = Math.abs(time - (entry[0] * 100 / totalCpu / 100));
			double bw = Math.abs(time - (entry[1] * 100 / totalBw / 100));
			double disk = Math.abs(time
					- (entry[2] * 100 / totalStorageIO / 100));

			result.append("mips: " + roundFine(mips) + ", bw: " + roundFine(bw)
					+ ", disk I/O: " + roundFine(disk)
					+ System.getProperty("line.separator"));
			mipsTotal += mips;
			bwTotal += bw;
			diskTotal += disk;
		}

		result.append("sum mips: " + roundFine(mipsTotal) + ", bw: "
				+ roundFine(bwTotal) + ", disk I/O: " + roundFine(diskTotal)
				+ System.getProperty("line.separator"));
		System.out.print(result);

		PrintWriter summary = null;
		try {
			if (trace) {
				Log.getOutput().write(result.toString().getBytes());
			}
			summary = new PrintWriter(
					new File("summary.log").getAbsoluteFile(), "UTF-8");
			summary.append(result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			summary.close();
		}

	}

	private double round(double val) {
		return Math.round(val * 100) / 100.0;
	}

	private double roundFine(double val) {
		return Math.round(val * 10000) / 10000.0;
	}

	public void setRecord(boolean record) {
		this.record = record;
	}

	public void setTrace(boolean trace) {
		this.trace = trace;
	}

	public boolean isRecord() {
		return record;
	}

	public boolean isTrace() {
		return trace;
	}

	public ArrayList<ArrayList<double[]>> getInputData() {
		return inputData;
	}

	public void setInputData(ArrayList<ArrayList<double[]>> inputData) {
		this.inputData = inputData;
	}

	public String getResultStringCsv() {
		return ((RdaDatacenter) datacenter).getEvaluationtStringCsv() + ","
				+ this.timeTotal + ",";
	}
}