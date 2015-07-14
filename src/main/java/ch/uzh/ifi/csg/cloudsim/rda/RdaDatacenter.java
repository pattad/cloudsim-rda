package ch.uzh.ifi.csg.cloudsim.rda;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
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
 * This datacenter implementation must be used, when using the RDA module.
 * 
 * @author Patrick A. Taddei
 */
public class RdaDatacenter extends PowerDatacenter {

	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmssSSS");

	private PrintWriter resourceTrace;

	private double pastResourceConsumptionTraceTime = 0.0d;

	private double accumulatedUnfairness = 0.0d;

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

		resourceTrace = new PrintWriter(new File(df.format(new Date())
				+ "_resourceShare.csv").getAbsoluteFile(), "UTF-8");
	}

	public double getAccumulatedUnfairness() {
		return Math.round(accumulatedUnfairness * 100) / 100.0;
	}

	@Override
	public void shutdownEntity() {
		resourceTrace.close();
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
			HashMap<String, Double> reqMips = new HashMap<String, Double>();
			HashMap<String, Double> allocatedMips = new HashMap<String, Double>();
			HashMap<String, Float> userPriorities = new HashMap<String, Float>();

			double totalMipsSupply = 0.0d;
			double totalMipsReq = 0.0d;

			String detail = "";

			for (PowerHost host : this.<PowerHost> getHostList()) {

				// only add to total, if host has some VMs running on it
				if (host.getVmList().size() > 0) {
					totalMipsSupply += host.getTotalMips();
				}

				for (Vm vm : host.getVmList()) {
					double req = ((RdaVm) vm)
							.getCurrentRequestedTotalMips(currentTime);
					totalMipsReq += req;
					String customer = ((RdaVm) vm).getCustomer();

					// store current priority of the user
					userPriorities.put(customer,
							((RdaVm) vm).getCurrentPriority());

					Double val = reqMips.get(customer);
					if (val != null) {
						reqMips.put(customer, val + req);
					} else {
						reqMips.put(customer, req);
					}
					double totalAllocated = 0.0d;
					List<Double> allocated = ((RdaVm) vm)
							.getCurrentAllocatedMips();
					for (Double pe : allocated) {
						totalAllocated += pe;
					}
					val = allocatedMips.get(customer);
					detail += vm.getId() + "," + customer + "," + req + ","
							+ totalAllocated + ",";
					if (val != null) {
						allocatedMips.put(customer, val + totalAllocated);
					} else {
						allocatedMips.put(customer, totalAllocated);
					}

				}
			}

			HashMap<String, Double> fairShare = new MaxMinAlgorithm().evaluate(
					reqMips, totalMipsSupply);
			String line = "";
			String unfair = "";
			for (String customer : reqMips.keySet()) {
				double req = reqMips.get(customer);
				double allocated = allocatedMips.get(customer);
				if (req > allocated && allocated < fairShare.get(customer)) {
					// considered as unfair
					double fair = fairShare.get(customer);

					double dev;
					if (fair < req) {
						dev = Math
								.round(((fair - allocated) * 100 / fair) * 100) / 100.0;
					} else {
						dev = Math
								.round(((fair - allocated) * 100 / req) * 100) / 100.0;
					}

					dev = (100 - (req * 100 / totalMipsReq)) / 100
							* dev;

					accumulatedUnfairness += dev;
					unfair += dev + ",";
				} else {
					unfair += ",";
				}
				line += req + "," + allocated + ",";

				if (userPriorities.get(customer) != null) {
					line += userPriorities.get(customer) + ",";
				}
			}

			line += unfair + detail;
			resourceTrace.println(line);

			pastResourceConsumptionTraceTime = currentTime;
		}
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

	/*
	 * rounding up to the 9th position behind the comma.
	 */
	private double round(double d) {

		double a = Math.round(d * 1000000000) / 1000000000.0;

		return a;
	}
}
