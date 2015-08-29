package ch.uzh.ifi.csg.cloudsim.rda.useraware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;

import ch.uzh.ifi.csg.cloudsim.rda.MaxMinAlgorithm;
import ch.uzh.ifi.csg.cloudsim.rda.RdaCloudletScheduler;
import ch.uzh.ifi.csg.cloudsim.rda.RdaVm;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.StorageIOProvisioner;

/**
 * This VM scheduler is the Dominan Resource Fairness (DRF) VM scheduler to be
 * used within the RDA module.
 * 
 * TODO: This is only a example implementation. Currently there are no
 * differences observable from the VmSchedulerDRF. The concept for such an
 * algorithm has to be developed and than implemented in this class.
 * 
 * @author Patrick A. Taddei
 * @see MaxMinAlgorithm
 */
public class VmSchedulerDRFMH extends VmSchedulerTimeShared implements
		RdaUserAwareVmScheduler {

	private MaxMinAlgorithm maxMin = new MaxMinAlgorithm();

	private RamProvisioner ramProvisioner;
	private BwProvisioner bwProvisioner;
	private StorageIOProvisioner sProvisioner;

	/**
	 * Instantiates a new vm scheduler time shared.
	 * 
	 * @param pelist
	 *            the pelist
	 * @param ramProvisioner
	 *            the ram provisioner
	 * @param bwProvisioner
	 *            the bandwidth provisioner
	 * @param sProvisioner
	 *            the storage I/O provisioner
	 */
	public VmSchedulerDRFMH(List<? extends Pe> pelist,
			RamProvisioner ramProvisioner, BwProvisioner bwProvisioner,
			StorageIOProvisioner storageIOProvisioner) {
		super(pelist);
		this.ramProvisioner = ramProvisioner;
		this.bwProvisioner = bwProvisioner;
		this.sProvisioner = storageIOProvisioner;
	}

	/**
	 * Retrieves the percentages of the Dominant Resource Fairness of the
	 * customers.
	 */
	public Map<String, Float> getUserPriorities(double currentTime, List<Vm> vms) {
		int vmCnt = vms.size();

		Map<String, Float> userPriorities = new HashMap<String, Float>();

		if (vmCnt == 0) {
			return userPriorities;
		}

		HashMap<String, Double> requestedCpu = new HashMap<String, Double>();
		HashMap<String, Double> requestedRam = new HashMap<String, Double>();
		HashMap<String, Double> requestedBw = new HashMap<String, Double>();
		HashMap<String, Double> requestedStorageIO = new HashMap<String, Double>();

		for (Vm vm : vms) {
			double reqRam = ((RdaCloudletScheduler) vm.getCloudletScheduler())
					.getCurrentUtilizationOfRam();
			double reqBw = ((RdaCloudletScheduler) vm.getCloudletScheduler())
					.getCurrentUtilizationOfBw();
			double reqStorage = ((RdaCloudletScheduler) vm
					.getCloudletScheduler()).getCurrentUtilizationOfStorageIO();
			double reqCpu = ((RdaCloudletScheduler) vm.getCloudletScheduler())
					.getCurrentUtilizationOfCpu();

			String uid = (String) vm.getUid();

			requestedCpu.put(uid, reqCpu);
			requestedRam.put(uid, reqRam);
			requestedBw.put(uid, reqBw);
			requestedStorageIO.put(uid, reqStorage);

		}

		// determine max capacity of resources
		double cpuCapacity = getMipsCapacity();
		double bwCapacity = bwProvisioner.getBw();
		double storageCapacity = sProvisioner.getStorageIO();
		double ramCapacity = ramProvisioner.getRam();

		// determine the dominant resource for each user and put it into the
		// result map
		for (String customer : requestedCpu.keySet()) {
			double cpu = requestedCpu.get(customer) * 100 / cpuCapacity / 100;
			double bw = requestedBw.get(customer) * 100 / bwCapacity / 100;
			double ram = requestedRam.get(customer) * 100 / ramCapacity / 100;
			double storage = requestedStorageIO.get(customer) * 100
					/ storageCapacity / 100;

			float dominantShare = (float) Math.max(Math.max(cpu, bw),
					Math.max(ram, storage));

			userPriorities.put(customer, dominantShare);
		}

		return userPriorities;
	}

	/**
	 * Please read class description above for further details.
	 * 
	 * @param currentTime
	 *            the simulation time
	 * @param vms
	 *            the VMs to allocate the resources to
	 */
	public void allocateResourcesForAllVms(double currentTime, List<Vm> vms,
			Map<String, Float> userPriorities) {

		super.getMipsMap().clear();
		setAvailableMips(getMipsCapacity());

		bwProvisioner.deallocateBwForAllVms();
		ramProvisioner.deallocateRamForAllVms();
		sProvisioner.deallocateStorageIOForAllVms();

		HashMap<String, Double> requestedCpu = new HashMap<String, Double>();
		HashMap<String, Double> requestedRam = new HashMap<String, Double>();
		HashMap<String, Double> requestedBw = new HashMap<String, Double>();
		HashMap<String, Double> requestedStorageIO = new HashMap<String, Double>();

		double totReqRam = 0.0;

		for (Vm vm : vms) {
			double reqRam = ((RdaVm) vm).getCurrentRequestedRam(currentTime);
			double reqBw = ((RdaVm) vm).getCurrentRequestedBw(currentTime);
			double reqStorage = ((RdaVm) vm)
					.getCurrentRequestedStorageIO(currentTime);
			double reqCpu = ((RdaVm) vm)
					.getCurrentRequestedTotalMips(currentTime);

			String uid = (String) vm.getUid();

			requestedCpu.put(uid, reqCpu);
			requestedRam.put(uid, reqRam);
			requestedBw.put(uid, reqBw);
			requestedStorageIO.put(uid, reqStorage);

			totReqRam += reqRam;

		}

		if (totReqRam > ramProvisioner.getRam()) {
			throw new RuntimeException(
					"Requested RAM is more than available RAM. ");
		}

		// determine max capacity of resources
		double cpuCapacity = getMipsCapacity();
		double bwCapacity = bwProvisioner.getBw();
		double storageCapacity = sProvisioner.getStorageIO();
		double ramCapacity = ramProvisioner.getRam();

		double[] dominantResources = new double[4];

		// determine the dominant resource for each user and put it into the
		// array
		for (String customer : requestedCpu.keySet()) {

			float priority = 0f;

			if (userPriorities.containsKey(customer)) {
				priority = userPriorities.get(customer);
			}

			double cpu = requestedCpu.get(customer) * 100 / cpuCapacity
					* (1 - priority);
			double bw = requestedBw.get(customer) * 100 / bwCapacity
					* (1 - priority);
			double ram = requestedRam.get(customer) * 100 / ramCapacity
					* (1 - priority);
			double storage = requestedStorageIO.get(customer) * 100
					/ storageCapacity * (1 - priority);

			if (cpu >= storage && cpu >= bw && cpu >= ram) {
				dominantResources[0] = Math.min(dominantResources[0], cpu);
			} else if (bw >= storage && bw >= cpu && bw >= ram) {
				dominantResources[1] = Math.min(dominantResources[1], bw);
			} else if (ram >= storage && ram >= cpu && ram >= bw) {
				dominantResources[2] = Math.min(dominantResources[2], ram);
			} else {
				dominantResources[3] = Math.min(dominantResources[3], storage);
			}

		}

		Map<String, Double> allocatedCpu = null;
		Map<String, Double> allocatedBw = null;
		Map<String, Double> allocatedRam = null;
		Map<String, Double> allocatedStorageIO = null;

		for (int i = 0; i < 4; i++) {
			// start with the smallest dominant resource
			if (dominantResources[0] <= dominantResources[1]
					&& dominantResources[0] <= dominantResources[2]
					&& dominantResources[0] <= dominantResources[3]) {
				allocatedCpu = maxMin.evaluate(requestedCpu, cpuCapacity);
				dominantResources[0] = Double.MAX_VALUE;

			} else if (dominantResources[1] <= dominantResources[0]
					&& dominantResources[1] <= dominantResources[2]
					&& dominantResources[1] <= dominantResources[3]) {
				allocatedBw = maxMin.evaluate(requestedBw,
						bwProvisioner.getBw());

				dominantResources[1] = Double.MAX_VALUE;
			} else if (dominantResources[2] <= dominantResources[0]
					&& dominantResources[2] <= dominantResources[1]
					&& dominantResources[2] <= dominantResources[3]) {

				// no over-demand supported. therefore allocated equals
				// requested and no MMFS has to be applied.
				allocatedRam = requestedRam;
				dominantResources[2] = Double.MAX_VALUE;
			} else {
				allocatedStorageIO = maxMin.evaluate(requestedStorageIO,
						sProvisioner.getStorageIO());
				dominantResources[3] = Double.MAX_VALUE;
			}
		}

		if (allocatedRam == null) {
			allocatedRam = requestedRam;
		}
		if (allocatedCpu == null) {
			allocatedCpu = maxMin.evaluate(requestedCpu, cpuCapacity);
		}
		if (allocatedBw == null) {
			allocatedBw = maxMin.evaluate(requestedBw, bwProvisioner.getBw());
		}
		if (allocatedStorageIO == null) {
			allocatedStorageIO = maxMin.evaluate(requestedStorageIO,
					sProvisioner.getStorageIO());
		}

		for (Vm vm : vms) {

			double mips = allocatedCpu.get(vm.getUid());

			if (super.getAvailableMips() - mips < -0.1) {
				throw new RuntimeException(
						"Trying to allocate more MIPS than available.");
			}

			List<Double> mipsMapCapped = new ArrayList<Double>();
			// split the mips equally between all processor units
			int peCnt = vm.getNumberOfPes();
			for (int n = 0; n < peCnt; n++) {
				mipsMapCapped.add((double) (mips / peCnt));
			}

			getMipsMap().put(vm.getUid(), mipsMapCapped);
			setAvailableMips(super.getAvailableMips() - mips);

			String uid = (String) vm.getUid();
			((RdaVm) vm).setCurrentAllocatedMips(mipsMapCapped);

			double ram = allocatedRam.get(uid);
			double bw = allocatedBw.get(uid);
			double storageIO = allocatedStorageIO.get(uid);

			bwProvisioner.allocateBwForVm(vm, bw);
			ramProvisioner.allocateRamForVm(vm, ram);
			sProvisioner.allocateStorageIOForVm((RdaVm) vm, storageIO);
		}
	}

	/**
	 * Returns total MIPS among all the PEs.
	 * 
	 * @return mips capacity
	 */
	private double getMipsCapacity() {
		if (getPeList() == null) {
			Log.printLine("Pe list is empty");
			return 0;
		}

		double capacity = 0.0;
		for (Pe pe : getPeList()) {
			capacity += pe.getMips();
		}

		return capacity;
	}

}
