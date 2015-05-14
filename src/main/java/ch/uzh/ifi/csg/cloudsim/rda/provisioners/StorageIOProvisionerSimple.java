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

import ch.uzh.ifi.csg.cloudsim.rda.RdaVm;

/**
 * storageIOProvisionerSimple is a class that implements a simple best effort
 * allocation policy: if there is storageIO available to request, it allocates;
 * otherwise, it fails.
 * 
 * @author P. Taddei
 */
public class StorageIOProvisionerSimple extends StorageIOProvisioner {

	/** The storageIO table. */
	private Map<String, Double> storageIOTable;

	/**
	 * Instantiates a new storageIO provisioner simple.
	 * 
	 * @param storageIO
	 *            the storageIO
	 */
	public StorageIOProvisionerSimple(double storageIO) {
		super(storageIO);
		setstorageIOTable(new HashMap<String, Double>());
	}

	public boolean allocateStorageIOForVm(RdaVm vm, double storageIO) {
		deallocateStorageIOForVm(vm);

		double storageIOrounded = (double) (Math.round((getAvailableStorageIO() - storageIO) * 100) / 100.0);
		
		if (getAvailableStorageIO() >= storageIOrounded) {
			setAvailableStorageIO(storageIOrounded);
			getStorageIOTable().put(vm.getUid(), storageIO);
			vm.setCurrentAllocatedStorageIO(getAllocatedStorageIOForVm(vm));
			return true;
		}

		vm.setCurrentAllocatedStorageIO(getAllocatedStorageIOForVm(vm));
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.StorageIOProvisioner#getAllocatedStorageIOForVm
	 * (ch.uzh.ifi.csg.cloudsim.rda.RdaVm)
	 */
	@Override
	public double getAllocatedStorageIOForVm(RdaVm vm) {
		if (getStorageIOTable().containsKey(vm.getUid())) {
			return getStorageIOTable().get(vm.getUid());
		}
		return 0;
	}

	public void deallocateStorageIOForVm(RdaVm vm) {
		if (getStorageIOTable().containsKey(vm.getUid())) {
			double amountFreed = getStorageIOTable().remove(vm.getUid());
			setAvailableStorageIO(getAvailableStorageIO() + amountFreed);
			vm.setCurrentAllocatedStorageIO(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.StorageIOProvisioner#deallocateStorageIOForAllVms
	 * ()
	 */
	@Override
	public void deallocateStorageIOForAllVms() {
		super.deallocateStorageIOForAllVms();
		getStorageIOTable().clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.StorageIOProvisioner#isSuitableForVm(org.cloudbus
	 * .cloudsim.rda.RdaVm, double)
	 */
	@Override
	public boolean isSuitableForVm(RdaVm vm, double storageIO) {
		double allocatedstorageIO = getAllocatedStorageIOForVm(vm);
		boolean result = allocateStorageIOForVm(vm, storageIO);
		deallocateStorageIOForVm(vm);
		if (allocatedstorageIO > 0) {
			allocateStorageIOForVm(vm, allocatedstorageIO);
		}
		return result;
	}

	/**
	 * Gets the storageIO table.
	 * 
	 * @return the storageIO table
	 */
	protected Map<String, Double> getStorageIOTable() {
		return storageIOTable;
	}

	/**
	 * Sets the storageIO table.
	 * 
	 * @param storageIOTable
	 *            the storageIO table
	 */
	protected void setstorageIOTable(Map<String, Double> storageIOTable) {
		this.storageIOTable = storageIOTable;
	}

}
