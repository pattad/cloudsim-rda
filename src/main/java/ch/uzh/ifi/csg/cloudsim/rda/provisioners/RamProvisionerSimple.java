/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation of Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2009-2012, The University of Melbourne, Australia
 */

package ch.uzh.ifi.csg.cloudsim.rda.provisioners;

import java.util.HashMap;
import java.util.Map;

import org.cloudbus.cloudsim.Vm;
import ch.uzh.ifi.csg.cloudsim.rda.RdaVm;

/**
 * RamProvisionerSimple is an extension of RamProvisioner which uses a best-effort policy to
 * allocate memory to a VM.
 * 
 * @author Rodrigo N. Calheiros
 * @author Anton Beloglazov
 * @since CloudSim Toolkit 1.0
 */
public class RamProvisionerSimple extends RamProvisioner {

	/** The RAM table. */
	private Map<String, Double> ramTable;

	/**
	 * Instantiates a new ram provisioner simple.
	 * 
	 * @param availableRam the available ram
	 */
	public RamProvisionerSimple(double availableRam) {
		super(availableRam);
		setRamTable(new HashMap<String, Double>());
	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.provisioners.RamProvisioner#allocateRamForVm(cloudsim.Vm, double)
	 */
	@Override
	public boolean allocateRamForVm(Vm vm, double ram) {
		double maxRam = vm.getRam();

		if (ram >= maxRam) {
			ram = maxRam;
		}

		deallocateRamForVm(vm);

		if (getAvailableRam() >= ram) {
			setAvailableRam((double) (Math.round((getAvailableRam() - ram)*100)/100.0));
			getRamTable().put(vm.getUid(), ram);
			((RdaVm)vm).setCurrentAllocatedRam(getAllocatedRamForVm(vm));
			return true;
		}

		((RdaVm)vm).setCurrentAllocatedRam(getAllocatedRamForVm(vm));

		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.provisioners.RamProvisioner#getAllocatedRamForVm(cloudsim.Vm)
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
	 * @see cloudsim.provisioners.RamProvisioner#deallocateRamForVm(cloudsim.Vm)
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
	 * @see cloudsim.provisioners.RamProvisioner#deallocateRamForVm(cloudsim.Vm)
	 */
	@Override
	public void deallocateRamForAllVms() {
		super.deallocateRamForAllVms();
		getRamTable().clear();
	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.provisioners.RamProvisioner#isSuitableForVm(cloudsim.Vm, double)
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
	 * @param ramTable the ram table
	 */
	protected void setRamTable(Map<String, Double> ramTable) {
		this.ramTable = ramTable;
	}

}
