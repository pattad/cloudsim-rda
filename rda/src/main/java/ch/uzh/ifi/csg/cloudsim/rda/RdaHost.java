package ch.uzh.ifi.csg.cloudsim.rda;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G4Xeon3040;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.StorageIOProvisioner;

/**
 * The class of a host supporting dynamic workloads and performance degradation.
 * 
 */
public class RdaHost extends PowerHost {

	public double scarcitySchedulingInterval;

	public StorageIOProvisioner storageIOProvisioner;
	
	/** The ram provisioner. */
	private RamProvisioner ramProvisioner;

	/** The bw provisioner. */
	private BwProvisioner bwProvisioner;

	/**
	 * Instantiates a new host.
	 * 
	 * @param id
	 *            the id
	 * @param ramProvisioner
	 *            the ram provisioner
	 * @param bwProvisioner
	 *            the bw provisioner
	 * @param storage
	 *            the storage
	 * @param peList
	 *            the pe list
	 * @param vmScheduler
	 *            the VM scheduler
	 */
	public RdaHost(int id, RamProvisioner ramProvisioner,
			BwProvisioner bwProvisioner,
			StorageIOProvisioner storageIOProvisioner, long storage,
			List<? extends Pe> peList, VmScheduler vmScheduler,
			double scarcitySchedulingInterval) {
		super(id, null, null, storage, peList, vmScheduler,
				new PowerModelSpecPowerHpProLiantMl110G4Xeon3040());
		this.storageIOProvisioner = storageIOProvisioner;
		this.scarcitySchedulingInterval = scarcitySchedulingInterval;
		this.bwProvisioner = bwProvisioner;
		this.ramProvisioner = ramProvisioner;
	}

	public boolean vmCreate(Vm vm) {
		if (getStorage() < vm.getSize()) {
			Log.printConcatLine("[VmScheduler.vmCreate] Allocation of VM #",
					vm.getId(), " to Host #", getId(), " failed by storage");
			return false;
		}

		if (!this.ramProvisioner.allocateRamForVm(vm,
				vm.getCurrentRequestedRam())) {
			Log.printConcatLine("[VmScheduler.vmCreate] Allocation of VM #",
					vm.getId(), " to Host #", getId(), " failed by RAM");
			return false;
		}

		if (!this.bwProvisioner.allocateBwForVm(vm, vm.getCurrentRequestedBw())) {
			Log.printConcatLine("[VmScheduler.vmCreate] Allocation of VM #",
					vm.getId(), " to Host #", getId(), " failed by BW");
			this.ramProvisioner.deallocateRamForVm(vm);
			return false;
		}

		if (!getVmScheduler()
				.allocatePesForVm(vm, vm.getCurrentRequestedMips())) {
			Log.printConcatLine("[VmScheduler.vmCreate] Allocation of VM #",
					vm.getId(), " to Host #", getId(), " failed by MIPS");
			this.ramProvisioner.deallocateRamForVm(vm);
			this.bwProvisioner.deallocateBwForVm(vm);
			return false;
		}

		setStorage(getStorage() - vm.getSize());
		getVmList().add(vm);
		vm.setHost(this);
		return true;
	}

	/**
	 * Checks if is suitable for vm.
	 * 
	 * @param vm
	 *            the vm
	 * @return true, if is suitable for vm
	 */
	public boolean isSuitableForVm(Vm vm) {
		return (getVmScheduler().getPeCapacity() >= vm
				.getCurrentRequestedMaxMips()
				&& getVmScheduler().getAvailableMips() >= vm
						.getCurrentRequestedTotalMips()
				&& this.ramProvisioner.isSuitableForVm(vm,
						vm.getCurrentRequestedRam()) && this.bwProvisioner
					.isSuitableForVm(vm, vm.getCurrentRequestedBw()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cloudsim.Host#updateVmsProcessing(double)
	 */
	@Override
	public double updateVmsProcessing(double currentTime) {

		if (getVmScheduler() instanceof MultipleResourcesVmScheduler) {
			((MultipleResourcesVmScheduler) getVmScheduler())
					.allocateResourcesForAllVms(currentTime, getVmList());
		} else {
			for (Vm vm : getVmList()) {
				getVmScheduler().deallocatePesForVm(vm);
			}
			for (Vm vm : getVmList()) {
				getVmScheduler().allocatePesForVm(vm,
						((RdaVm) vm).getCurrentRequestedMips(currentTime));
			}
		}

		double smallerTime = Double.MAX_VALUE;
		for (Vm vm : getVmList()) {
			double time = ((RdaVm) vm).updateVmProcessing(currentTime,
					getVmScheduler().getAllocatedMipsForVm(vm),
					((RdaVm) vm).getCurrentAllocatedBwFine(),
					((RdaVm) vm).getCurrentAllocatedStorageIO()); // XXX ram???

			if (time > 0.0 && time < smallerTime) {
				smallerTime = time;
			}
		}

		smallerTime = checkForScarcity(smallerTime, currentTime);

		updateHostState(currentTime);

		return smallerTime;
	}

	protected void updateHostState(double currentTime) {
		double hostTotalRequestedMips = 0;
		setPreviousUtilizationMips(getUtilizationMips());
		setUtilizationMips(0);

		for (Vm vm : getVmList()) {
			double totalRequestedMips = ((RdaVm) vm)
					.getCurrentRequestedTotalMips(currentTime);
			double totalAllocatedMips = getVmScheduler()
					.getTotalAllocatedMipsForVm(vm);

			if (!Log.isDisabled()) {
				Log.formatLine("%.6f: [Host #" + getId()
						+ "] Total allocated MIPS for VM #" + vm.getId()
						+ " (Host #" + vm.getHost().getId()
						+ ") is %.2f, was requested %.2f", CloudSim.clock(),
						totalAllocatedMips, totalRequestedMips);

			}

			if (getVmsMigratingIn().contains(vm)) {
				Log.formatLine(
						"%.6f: [Host #" + getId() + "] VM #" + vm.getId()
								+ " is being migrated to Host #" + getId(),
						CloudSim.clock());
			} else {
				if (totalAllocatedMips + 0.1 < totalRequestedMips) {
					Log.formatLine("%.6f: [Host #" + getId()
							+ "] Under allocated MIPS for VM #" + vm.getId()
							+ ": %.2f", CloudSim.clock(), totalRequestedMips
							- totalAllocatedMips);
				}

				vm.addStateHistoryEntry(currentTime, totalAllocatedMips,
						totalRequestedMips,
						(vm.isInMigration() && !getVmsMigratingIn()
								.contains(vm)));

				if (vm.isInMigration()) {
					Log.formatLine(
							"%.6f: [Host #" + getId() + "] VM #" + vm.getId()
									+ " is in migration", CloudSim.clock());
					totalAllocatedMips /= 0.9; // performance degradation due to
												// migration - 10%
				}
			}

			setUtilizationMips(getUtilizationMips() + totalAllocatedMips);
			hostTotalRequestedMips += totalRequestedMips;
		}

		addStateHistoryEntry(currentTime, getUtilizationMips(),
				hostTotalRequestedMips, (getUtilizationMips() > 0));
	}

	protected double checkForScarcity(double smallerTime, double currentTime) {

		if (String.valueOf(currentTime).startsWith("6.01")) {
			System.out.println("XXX");
		}

		double totalGradCpu = 0.0;
		double totalReqCpu = 0.0;
		double totalGradBw = 0.0;
		double totalReqBw = 0.0;
		double totalGradStorage = 0.0;
		double totalReqStorage = 0.0;
		for (Vm vm : getVmList()) {
			totalGradCpu += ((RdaVm) vm).getCurrentRequestedGradCpu();
			totalReqCpu += ((RdaVm) vm)
					.getCurrentRequestedTotalMips(currentTime);
			totalGradBw += ((RdaVm) vm).getCurrentRequestedGradBw();
			totalReqBw += ((RdaVm) vm).getCurrentRequestedBw(currentTime);
			totalGradStorage += ((RdaVm) vm).getCurrentRequestedGradStorageIO();
			totalReqStorage += ((RdaVm) vm)
					.getCurrentRequestedStorageIO(currentTime);
		}

		// check if time of a shortage will arrive before the next simulation
		// time.
		double max = getVmScheduler().getPeCapacity();
		double current = max - getVmScheduler().getMaxAvailableMips();
		double expectedTime = (max - current) / totalGradCpu;

		if (expectedTime > 0.0d && expectedTime < smallerTime) {
			// next expected shortage with linear increase of the resources
			if (expectedTime > smallerTime) {
				smallerTime = expectedTime;
			} else {
				smallerTime = this.scarcitySchedulingInterval;
			}
		} else if (expectedTime == 0.0d) {
			// max - current = 0 && expectedTime != NaN
			// --> currently there is a shortage
			smallerTime = this.scarcitySchedulingInterval;
		} else if (totalReqCpu > max) {
			// --> currently there is a shortage
			smallerTime = this.scarcitySchedulingInterval;
		}

		double maxBw = this.bwProvisioner.getBw();
		double currentBw = maxBw - this.bwProvisioner.getAvailableBw();
		double expectedTimeBw = (maxBw - currentBw) / totalGradBw;

		if (expectedTimeBw > 0.0d && expectedTimeBw < smallerTime) {
			// next expected shortage with linear increase of the resources
			smallerTime = expectedTimeBw;
			if (expectedTimeBw > smallerTime) {
				smallerTime = expectedTimeBw;
			} else {
				smallerTime = this.scarcitySchedulingInterval;
			}
		} else if (expectedTimeBw == 0.0d) {
			// max - current = 0 && expectedTime != NaN
			// --> currently there is a shortage
			smallerTime = this.scarcitySchedulingInterval;
		} else if (totalReqBw > maxBw) {
			// --> currently there is a shortage
			smallerTime = this.scarcitySchedulingInterval;
		}

		double maxStorageIO = this.storageIOProvisioner.getStorageIO();
		double currentStorageIO = maxStorageIO
				- this.storageIOProvisioner.getAvailableStorageIO();
		double expectedTimeStorageIO = (maxStorageIO - currentStorageIO)
				/ totalGradStorage;

		if (expectedTimeStorageIO > 0.0d && expectedTimeStorageIO < smallerTime) {
			// next expected shortage with linear increase of the resources
			if (expectedTimeStorageIO > smallerTime) {
				smallerTime = expectedTimeStorageIO;
			} else {
				smallerTime = this.scarcitySchedulingInterval;
			}
		} else if (expectedTimeStorageIO == 0.0d) {
			// max - current = 0 && expectedTime != NaN
			// --> currently there is a shortage
			smallerTime = this.scarcitySchedulingInterval;
		} else if (totalReqStorage > maxStorageIO) {
			// --> currently there is a shortage
			smallerTime = this.scarcitySchedulingInterval;
		}
		return smallerTime;
	}

	/**
	 * Gets the completed vms.
	 * 
	 * @return the completed vms
	 */
	public List<Vm> getCompletedVms() {
		List<Vm> vmsToRemove = new ArrayList<Vm>();
		for (Vm vm : getVmList()) {
			if (vm.isInMigration()) {
				continue;
			}
			if (vm.getCurrentRequestedTotalMips() == 0) {
				// vmsToRemove.add(vm); //we don't remove VM's if the mips is 0
				// XXX
			}
		}
		return vmsToRemove;
	}

	public double getScarcityShedulingInterval() {
		return this.scarcitySchedulingInterval;
	}

	public void setScarcityShedulingInterval(double scarcitySchedulingInterval) {
		this.scarcitySchedulingInterval = scarcitySchedulingInterval;
	}

	@Override
	protected void vmDeallocate(Vm vm) {
		this.ramProvisioner.deallocateRamForVm(vm);
		this.bwProvisioner.deallocateBwForVm(vm);
		this.storageIOProvisioner.deallocateStorageIOForVm((RdaVm) vm);
		getVmScheduler().deallocatePesForVm(vm);
		setStorage(getStorage() + vm.getSize());
	}
}
