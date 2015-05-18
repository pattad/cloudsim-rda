package ch.uzh.ifi.csg.cloudsim.rda;

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

	/**
	 * Instantiates a new datacenter.
	 * 
	 * @param name
	 *            the name
	 * @param characteristics
	 *            the res config
	 * @param vmAllocationPolicy
	 *            the vm provisioner
	 * @param storageList
	 *            the storage list
	 * @param schedulingInterval
	 *            the scheduling interval
	 * @throws Exception
	 *             the exception
	 */
	public RdaDatacenter(String name,
			DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList,
			double schedulingInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList,
				schedulingInterval);
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

			setPower(getPower() + timeframePower);

			checkCloudletCompletion();
			removeCompletedVms();

			processMigrations();

			addNextDatacenterEvent(minTime);

			setLastProcessTime(currentTime);
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
