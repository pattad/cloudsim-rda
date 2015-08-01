package ch.uzh.ifi.csg.cloudsim.rda;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.core.predicates.PredicateType;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerHost;

/**
 * This data center implementation must be used, when using the RDA module.
 * 
 * @author Patrick A. Taddei
 */
public class RdaDatacenter extends PowerDatacenter {

	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmssSSS");

	private PrintWriter cpuTrace;
	private PrintWriter bwTrace;
	private PrintWriter diskTrace;
	private PrintWriter summaryTrace;
	private PrintWriter utilizationTrace;

	private double pastResourceConsumptionTraceTime = 0.0d;

	/**
	 * contains the current results as shown in this table:
	 * 
	 * cpu | bw | disk<br>
	 * fairness | accumulatedUnfairness
	 * 
	 * 
	 */
	private double[][] unfairness = new double[3][2];

	/** array contains: cpu | bw | disk */
	private TreeMap<String, double[]> fairnessByUser = new TreeMap<String, double[]>();

	/** array contains: cpu | ram | bw | disk */
	private TreeMap<String, double[]> utilizationByUser = new TreeMap<String, double[]>();

	private double resourceUnfairness = 0.0d;
	private double assetUnfairness = 0.0d;

	private double unusedAllocation = 0.0d;

	/**
	 * Instantiates a new RDA datacenter.
	 * 
	 * @param name
	 *            the name of the datacenter
	 * @param characteristics
	 *            an object of DatacenterCharacteristics
	 * @param vmAllocationPolicy
	 *            the vm provisioner
	 * @param storageList
	 *            the storage list
	 * @param schedulingInterval
	 *            the scheduling interval
	 * @throws Exception
	 *             This happens when one of the following scenarios occur:
	 *             <ul>
	 *             <li>creating this entity before initializing CloudSim package
	 *             <li>this entity name is <tt>null</tt> or empty
	 *             <li>this entity has <tt>zero</tt> number of PEs (Processing
	 *             Elements). <br>
	 *             No PEs mean the Cloudlets can't be processed. A CloudResource
	 *             must contain one or more Machines. A Machine must contain one
	 *             or more PEs.
	 *             </ul>
	 */
	public RdaDatacenter(String name,
			DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList,
			double schedulingInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList,
				schedulingInterval);

		cpuTrace = new PrintWriter(
				new File("resourceShare_cpu.csv").getAbsoluteFile(), "UTF-8");
		bwTrace = new PrintWriter(
				new File("resourceShare_bw.csv").getAbsoluteFile(), "UTF-8");
		diskTrace = new PrintWriter(
				new File("resourceShare_disk.csv").getAbsoluteFile(), "UTF-8");
		summaryTrace = new PrintWriter(
				new File("fairness.csv").getAbsoluteFile(), "UTF-8");
		utilizationTrace = new PrintWriter(
				new File("utilization.csv").getAbsoluteFile(), "UTF-8");
	}

	@Override
	public void shutdownEntity() {
		cpuTrace.close();
		bwTrace.close();
		diskTrace.close();

		summaryTrace.println(getEvaluationtString());
		summaryTrace.close();

		utilizationTrace.close();
	}

	public String getEvaluationtString() {
		return "Total asset unfairness: " + roundTwoPositions(assetUnfairness)
				+ ", Total resource unfairness: "
				+ roundTwoPositions(resourceUnfairness)
				+ ", Total unfairness by resource: CPU: "
				+ roundTwoPositions(unfairness[0][1]) + ", BW: "
				+ roundTwoPositions(unfairness[1][1]) + ", Disk I/O: "
				+ roundTwoPositions(unfairness[2][1]) + ", Unused allocation: "
				+ roundTwoPositions(unusedAllocation);
	}

	@Override
	protected void processVmCreate(SimEvent ev, boolean ack) {
		RdaVm vm = (RdaVm) ev.getData();

		boolean result = getVmAllocationPolicy().allocateHostForVm(vm);

		if (ack) {
			int[] data = new int[3];
			data[0] = getId();
			data[1] = vm.getId();

			if (result) {
				data[2] = CloudSimTags.TRUE;
			} else {
				data[2] = CloudSimTags.FALSE;
			}
			send(vm.getUserId(), CloudSim.getMinTimeBetweenEvents(),
					CloudSimTags.VM_CREATE_ACK, data);
		}

		if (result) {
			getVmList().add(vm);

			if (vm.isBeingInstantiated()) {
				vm.setBeingInstantiated(false);
			}

		}

	}

	/**
	 * Updates processing of each cloudlet running in this datacenter. It is
	 * necessary because Hosts and VirtualMachines are simple objects, not
	 * entities. So, they don't receive events and updating cloudlets inside
	 * them must be called from the outside.
	 * 
	 */
	@Override
	protected void updateCloudletProcessing() {
		if (getCloudletSubmitted() == -1
				|| getCloudletSubmitted() == CloudSim.clock()) {
			CloudSim.cancelAll(getId(), new PredicateType(
					CloudSimTags.VM_DATACENTER_EVENT));
			schedule(getId(), getSchedulingInterval(),
					CloudSimTags.VM_DATACENTER_EVENT);
			return;
		}
		double currentTime = CloudSim.clock();

		if (currentTime > getLastProcessTime()) {
			double minTime = Double.MAX_VALUE;

			double timeframePower = logPowerConsumption(currentTime);

			minTime = processHosts(currentTime, minTime);

			logResourceShareByUser(currentTime);

			setPower(getPower() + timeframePower);

			checkCloudletCompletion();
			removeCompletedVms();

			processMigrations();

			addNextDatacenterEvent(minTime);

			setLastProcessTime(currentTime);
		}

	}

	private void logResourceShareByUser(double currentTime) {

		if (currentTime - pastResourceConsumptionTraceTime >= 1.0d) {

			fairnessByUser.clear();
			utilizationByUser.clear();

			traceCpu(currentTime);
			traceRam(currentTime);
			traceBw(currentTime);
			traceDisk(currentTime);

			String line = "";
			double totUnfairness = 0.0d;
			for (String customer : fairnessByUser.keySet()) {
				double[] fairness = fairnessByUser.get(customer);

				double sum = fairness[0] + fairness[1] + fairness[2];

				totUnfairness += Math.abs(sum);

				line += customer + "," + fairness[0] + "," + fairness[1] + ","
						+ fairness[2] + "," + roundTwoPositions(sum) + ",";
			}

			resourceUnfairness += totUnfairness;

			line += ",All users total dev," + roundTwoPositions(totUnfairness);

			summaryTrace.println(line);

			// calculate asset fairness over whole datecenter
			double mipsCapacity = 0.0d;
			double ramCapacity = 0.0d;
			double bwCapacity = 0.0d;
			double diskCapacity = 0.0d;

			for (PowerHost host : this.<PowerHost> getHostList()) {
				mipsCapacity += getMipsCapacity(host.getPeList());
				ramCapacity += host.getRam();
				bwCapacity += host.getBw();
				diskCapacity += ((RdaHost) host).getStorageIOProvisioner()
						.getStorageIO();
			}

			double totalShare = 0.0d;
			for (String customer : new TreeMap<String, double[]>(
					utilizationByUser).keySet()) {

				double[] util = utilizationByUser.get(customer);
				utilizationTrace.print(util[0] + "," + util[1] + "," + util[2]
						+ "," + util[3] + ",");

				double share = util[0] * 100 / mipsCapacity + util[1] * 100
						/ ramCapacity + util[2] * 100 / bwCapacity + util[3]
						* 100 / diskCapacity;

				totalShare += share;
			}

			double avgShare = totalShare / utilizationByUser.size();
			double assetShareDev = 0.0d;

			for (String customer : new TreeMap<String, double[]>(
					utilizationByUser).keySet()) {
				double[] util = utilizationByUser.get(customer);
				double share = util[0] * 100 / mipsCapacity + util[1] * 100
						/ ramCapacity + util[2] * 100 / bwCapacity + util[3]
						* 100 / diskCapacity;

				assetShareDev += Math.abs(avgShare - share);
			}

			assetUnfairness += assetShareDev;

			utilizationTrace.print(System.getProperty("line.separator"));

			pastResourceConsumptionTraceTime = currentTime;
		}
	}

	public void traceRam(double currentTime) {
		HashMap<String, Double> allocated = new HashMap<String, Double>();

		for (PowerHost host : this.<PowerHost> getHostList()) {

			for (Vm vm : host.getVmList()) {
				String customer = ((RdaVm) vm).getCustomer();

				double utilization = ((RdaCloudletScheduler) vm
						.getCloudletScheduler()).getCurrentUtilizationOfRam();

				double[] util = utilizationByUser.get(customer);

				if (util == null) {
					util = new double[4];
					util[1] = utilization;
					utilizationByUser.put(customer, util);
				} else {
					util[1] = util[1] + utilization;
				}

				Double val = allocated.get(customer);

				if (val != null) {
					allocated.put(customer, val + utilization);
				} else {
					allocated.put(customer, utilization);
				}

			}
		}

	}

	public void traceCpu(double currentTime) {
		HashMap<String, Double> requested = new HashMap<String, Double>();
		HashMap<String, Double> allocated = new HashMap<String, Double>();
		HashMap<String, Float> userPriorities = new HashMap<String, Float>();

		double totalAllocated = 0.0d;

		String detail = "";

		for (PowerHost host : this.<PowerHost> getHostList()) {

			for (Vm vm : host.getVmList()) {
				double req = 0;

				List<Double> pes = ((RdaCloudletScheduler) vm
						.getCloudletScheduler()).getCurrentRequestedMips();
				for (Double pe : pes) {
					req += pe;
				}

				String customer = ((RdaVm) vm).getCustomer();

				// store current priority of the user
				userPriorities.put(customer, ((RdaVm) vm).getCurrentPriority());

				Double val = requested.get(customer);
				if (val != null) {
					requested.put(customer, val + req);
				} else {
					requested.put(customer, req);
				}

				double totalAllocatedForUserOnVm = 0.0d;
				List<Double> alloc = ((RdaVm) vm).getCurrentAllocatedMips();
				for (Double pe : alloc) {
					totalAllocatedForUserOnVm += pe;
				}

				double utilization = ((RdaCloudletScheduler) vm
						.getCloudletScheduler()).getCurrentUtilizationOfCpu();
				double unused = 100 - roundTwoPositions(utilization * 100
						/ totalAllocatedForUserOnVm);
				unusedAllocation += unused;

				double[] util = utilizationByUser.get(customer);

				if (util == null) {
					util = new double[4];
					util[0] = utilization;
					utilizationByUser.put(customer, util);
				} else {
					util[0] = util[0] + utilization;
				}

				totalAllocated += utilization;

				val = allocated.get(customer);
				detail += vm.getId() + "," + customer + "," + req + ","
						+ utilization + "," + unused + ",";
				if (val != null) {
					allocated.put(customer, val + utilization);
				} else {
					allocated.put(customer, utilization);
				}

			}
		}

		calculateUnfairness(requested, allocated, userPriorities,
				totalAllocated, detail, cpuTrace, 0);
	}

	public void traceBw(double currentTime) {
		HashMap<String, Double> requested = new HashMap<String, Double>();
		HashMap<String, Double> allocated = new HashMap<String, Double>();
		HashMap<String, Float> userPriorities = new HashMap<String, Float>();

		double totalAllocated = 0.0d;

		String detail = "";

		for (PowerHost host : this.<PowerHost> getHostList()) {

			for (Vm vm : host.getVmList()) {
				double req = ((RdaCloudletScheduler) vm.getCloudletScheduler())
						.getCurrentRequestedUtilizationOfBw();

				String customer = ((RdaVm) vm).getCustomer();

				// store current priority of the user
				userPriorities.put(customer, ((RdaVm) vm).getCurrentPriority());

				Double val = requested.get(customer);
				if (val != null) {
					requested.put(customer, val + req);
				} else {
					requested.put(customer, req);
				}

				double alloc = ((RdaVm) vm).getCurrentAllocatedBwFine();

				double utilization = ((RdaCloudletScheduler) vm
						.getCloudletScheduler()).getCurrentUtilizationOfBw();
				totalAllocated += utilization;

				double[] util = utilizationByUser.get(customer);

				if (util == null) {
					util = new double[4];
					util[2] = utilization;
					utilizationByUser.put(customer, util);
				} else {
					util[2] = util[2] + utilization;
				}

				double unused = 100 - roundTwoPositions(utilization * 100
						/ alloc);
				unusedAllocation += unused;

				val = allocated.get(customer);
				detail += vm.getId() + "," + customer + "," + req + ","
						+ utilization + "," + unused + ",";
				if (val != null) {
					allocated.put(customer, val + utilization);
				} else {
					allocated.put(customer, utilization);
				}

			}
		}

		calculateUnfairness(requested, allocated, userPriorities,
				totalAllocated, detail, bwTrace, 1);
	}

	public void traceDisk(double currentTime) {
		HashMap<String, Double> requested = new HashMap<String, Double>();
		HashMap<String, Double> allocated = new HashMap<String, Double>();
		HashMap<String, Float> userPriorities = new HashMap<String, Float>();

		double totalUtilization = 0.0d;

		String detail = "";

		for (PowerHost host : this.<PowerHost> getHostList()) {

			for (Vm vm : host.getVmList()) {
				double req = ((RdaCloudletScheduler) vm.getCloudletScheduler())
						.getCurrentRequestedUtilizationOfStorageIO();

				String customer = ((RdaVm) vm).getCustomer();

				// store current priority of the user
				userPriorities.put(customer, ((RdaVm) vm).getCurrentPriority());

				Double val = requested.get(customer);
				if (val != null) {
					requested.put(customer, val + req);
				} else {
					requested.put(customer, req);
				}

				double alloc = ((RdaVm) vm).getCurrentAllocatedStorageIO();

				double utilization = ((RdaCloudletScheduler) vm
						.getCloudletScheduler())
						.getCurrentUtilizationOfStorageIO();

				double[] util = utilizationByUser.get(customer);
				if (util == null) {
					util = new double[4];
					util[3] = utilization;
					utilizationByUser.put(customer, util);
				} else {
					util[3] = util[3] + utilization;
				}

				double unused = 100 - roundTwoPositions(utilization * 100
						/ alloc);
				unusedAllocation += unused;

				totalUtilization += utilization;
				val = allocated.get(customer);
				detail += vm.getId() + "," + customer + "," + req + ","
						+ utilization + "," + unused + ",";
				if (val != null) {
					allocated.put(customer, val + utilization);
				} else {
					allocated.put(customer, utilization);
				}

			}
		}

		calculateUnfairness(requested, allocated, userPriorities,
				totalUtilization, detail, diskTrace, 2);
	}

	public void calculateUnfairness(HashMap<String, Double> requested,
			HashMap<String, Double> allocated,
			HashMap<String, Float> userPriorities, double totalAllocated,
			String detail, PrintWriter writer, int resourceId) {

		HashMap<String, Double> fairShare = new MaxMinAlgorithm().evaluate(
				requested, totalAllocated);
		String line = "";
		String unfair = "";

		double totalDev = 0.0d; // over all users
		for (String customer : new TreeMap<String, Double>(requested).keySet()) {
			double req = requested.get(customer);
			double alloc = allocated.get(customer);
			if (req > alloc && alloc < fairShare.get(customer)) {
				// considered as unfair
				double fair = fairShare.get(customer);

				double dev;
				if (fair < req) {
					// fair is higher than requested, take percentage from
					// fair amount
					dev = (fair - alloc) * 100 / fair;
				} else {
					// if fair is less than requested, only take percentage
					// from requested amount
					dev = (fair - alloc) * 100 / req;
				}

				totalDev += dev;
				unfair += roundTwoPositions(dev) + ",";
			} else {
				unfair += ",";
			}

			// double equalShare = totalAllocated / requested.size();
			double fair = fairShare.get(customer);
			double dev = 0;
			if (fair != 0) {
				dev = -((fair - alloc) * 100 / fair);
			}
			double[] fairness = fairnessByUser.get(customer);
			if (fairness == null) {
				fairness = new double[3];
			}

			fairness[resourceId] = fairness[resourceId] + dev;

			fairnessByUser.put(customer, fairness);

			line += req + "," + alloc + ",";

			if (userPriorities.get(customer) != null) {
				line += userPriorities.get(customer) + ",";
			}
		}

		// unfairness
		unfairness[resourceId][0] = totalDev;
		unfairness[resourceId][1] = unfairness[resourceId][1] + totalDev;

		line += unfair + roundTwoPositions(totalDev) + ","
				+ roundTwoPositions(unfairness[resourceId][1]) + "," + detail;
		writer.println(line);
	}

	/**
	 * The generation of the event was slightly adapted from the PowerDatacenter
	 * The RDA datacenter's event creation is optimized to work with very small
	 * time intervals and at the same time not over-firing events
	 */
	protected void addNextDatacenterEvent(double minTime) {
		// schedules an event to the next time
		if (minTime != Double.MAX_VALUE) {
			if (minTime < super.getSchedulingInterval()) {
				minTime = super.getSchedulingInterval();
			}

			minTime = round(minTime);

			CloudSim.cancelAll(getId(), new PredicateType(
					CloudSimTags.VM_DATACENTER_EVENT));
			send(getId(), minTime, CloudSimTags.VM_DATACENTER_EVENT);
		}
	}

	private void removeCompletedVms() {
		/** Remove completed VMs **/
		for (PowerHost host : this.<PowerHost> getHostList()) {
			for (Vm vm : host.getCompletedVms()) {
				getVmAllocationPolicy().deallocateHostForVm(vm);
				getVmList().remove(vm);
				Log.printLine("VM #" + vm.getId()
						+ " has been deallocated from host #" + host.getId());
			}
		}
	}

	protected double processHosts(double currentTime, double minTime) {
		for (PowerHost host : this.<PowerHost> getHostList()) {
			Log.formatLine("\n%.2f: Host #%d", CloudSim.clock(), host.getId());

			double time = host.updateVmsProcessing(currentTime); // inform VMs
																	// to update
																	// processing
			if (time < minTime) {
				minTime = time;
			}
		}
		return minTime;
	}

	private void processMigrations() {
		if (!isDisableMigrations()) {
			List<Map<String, Object>> migrationMap = getVmAllocationPolicy()
					.optimizeAllocation(getVmList());

			if (migrationMap != null) {
				for (Map<String, Object> migrate : migrationMap) {
					Vm vm = (Vm) migrate.get("vm");
					PowerHost targetHost = (PowerHost) migrate.get("host");
					PowerHost oldHost = (PowerHost) vm.getHost();

					if (oldHost == null) {
						Log.formatLine(
								"%.2f: Migration of VM #%d to Host #%d is started",
								CloudSim.clock(), vm.getId(),
								targetHost.getId());
					} else {
						Log.formatLine(
								"%.2f: Migration of VM #%d from Host #%d to Host #%d is started",
								CloudSim.clock(), vm.getId(), oldHost.getId(),
								targetHost.getId());
					}

					targetHost.addMigratingInVm(vm);
					incrementMigrationCount();

					/** VM migration delay = RAM / bandwidth + C (C = 10 sec) **/
					send(getId(), vm.getRam() / ((double) vm.getBw() / 8000)
							+ 10, CloudSimTags.VM_MIGRATE, migrate);
				}
			}
		}
	}

	private double logPowerConsumption(double currentTime) {
		double timeframePower = 0.0;
		double timeDiff = currentTime - getLastProcessTime();

		Log.printLine("\n");

		for (PowerHost host : this.<PowerHost> getHostList()) {
			Log.formatLine("%.2f: Host #%d", CloudSim.clock(), host.getId());

			double hostPower = 0.0;

			try {
				hostPower = host.getMaxPower() * timeDiff;
				timeframePower += hostPower;
			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.formatLine("%.2f: Host #%d utilization is %.2f%%",
					CloudSim.clock(), host.getId(),
					host.getUtilizationOfCpu() * 100);
			Log.formatLine("%.2f: Host #%d energy is %.2f W*sec",
					CloudSim.clock(), host.getId(), hostPower);
		}

		Log.formatLine("\n%.2f: Consumed energy is %.2f W*sec\n",
				CloudSim.clock(), timeframePower);

		Log.printLine("\n\n--------------------------------------------------------------\n\n");
		return timeframePower;
	}

	private double roundTwoPositions(double val) {
		return Math.round(val * 100) / 100.0d;
	}

	/*
	 * rounding up to the 9th position behind the comma.
	 */
	private double round(double d) {

		double a = Math.round(d * 1000000000) / 1000000000.0;

		return a;
	}

	/**
	 * Returns total MIPS among all the PEs.
	 * 
	 * @return mips capacity
	 */
	private double getMipsCapacity(List<Pe> peList) {
		if (peList == null) {
			Log.printLine("Pe list is empty");
			return 0;
		}

		double capacity = 0.0;
		for (Pe pe : peList) {
			capacity += pe.getMips();
		}

		return capacity;
	}
}
