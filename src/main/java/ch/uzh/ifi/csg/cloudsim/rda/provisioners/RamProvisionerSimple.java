package ch.uzh.ifi.csg.cloudsim.rda.provisioners;

import java.util.HashMap;
import java.util.Map;

import org.cloudbus.cloudsim.Vm;
import ch.uzh.ifi.csg.cloudsim.rda.RdaVm;

/**
 * RamProvisionerSimple is an extension of RamProvisioner which uses a
 * best-effort policy to allocate memory to a VM.
 * 
 * @author Patrick A. Taddei
 */
public class RamProvisionerSimple extends RamProvisioner {

	/** The RAM table. */
	private Map<String, Double> ramTable;

	/**
	 * Instantiates a new ram provisioner simple.
	 * 
	 * @param availableRam
	 *            the available ram
	 */
	public RamProvisionerSimple(double availableRam) {
		super(availableRam);
		setRamTable(new HashMap<String, Double>());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisioner#allocateRamForVm
	 * (org.cloudbus.cloudsim.Vm, double)
	 */
	@Override
	public boolean allocateRamForVm(Vm vm, double ram) {

		deallocateRamForVm(vm);

		if (getAvailableRam() >= ram) {
			setAvailableRam((double) (Math
					.round((getAvailableRam() - ram) * 100) / 100.0));
			getRamTable().put(vm.getUid(), ram);
			((RdaVm) vm).setCurrentAllocatedRam(getAllocatedRamForVm(vm));
			return true;
		}

		((RdaVm) vm).setCurrentAllocatedRam(getAllocatedRamForVm(vm));

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisioner#getAllocatedRamForVm
	 * (org.cloudbus.cloudsim.Vm)
	 */
	@Override
	public double getAllocatedRamForVm(Vm vm) {
		if (getRamTable().containsKey(vm.getUid())) {
			return getRamTable().get(vm.getUid());
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisioner#deallocateRamForVm
	 * (org.cloudbus.cloudsim.Vm)
	 */
	@Override
	public void deallocateRamForVm(Vm vm) {
		if (getRamTable().containsKey(vm.getUid())) {
			double amountFreed = getRamTable().remove(vm.getUid());
			setAvailableRam(getAvailableRam() + amountFreed);
			vm.setCurrentAllocatedRam(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisioner#
	 * deallocateRamForAllVms()
	 */
	@Override
	public void deallocateRamForAllVms() {
		super.deallocateRamForAllVms();
		getRamTable().clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisioner#isSuitableForVm
	 * (org.cloudbus.cloudsim.Vm, double)
	 */
	@Override
	public boolean isSuitableForVm(Vm vm, double ram) {
		double allocatedRam = getAllocatedRamForVm(vm);
		boolean result = allocateRamForVm(vm, ram);
		deallocateRamForVm(vm);
		if (allocatedRam > 0) {
			allocateRamForVm(vm, allocatedRam);
		}
		return result;
	}

	/**
	 * Gets the ram table.
	 * 
	 * @return the ram table
	 */
	protected Map<String, Double> getRamTable() {
		return ramTable;
	}

	/**
	 * Sets the ram table.
	 * 
	 * @param ramTable
	 *            the ram table
	 */
	protected void setRamTable(Map<String, Double> ramTable) {
		this.ramTable = ramTable;
	}

}
