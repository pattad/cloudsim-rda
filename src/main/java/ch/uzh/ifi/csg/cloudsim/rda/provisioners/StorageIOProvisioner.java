package ch.uzh.ifi.csg.cloudsim.rda.provisioners;

import ch.uzh.ifi.csg.cloudsim.rda.RdaVm;

/**
 * StorageIOProvisioner is an abstract class that represents the provisioning
 * policy of storageIO to virtual machines inside a Host. When extending this
 * class, care must be taken to guarantee that the field availableStorageIO will
 * always contain the amount of free storage IO available for future
 * allocations. 
 * 
 * 
 * @author Patrick A. Taddei
 */
public abstract class StorageIOProvisioner {

	/** The storage IO. */
	private double storageIO;

	/** The available storage IO. */
	private double availableStorageIO;

	/**
	 * Creates the a new StorageIOProvisioner.
	 * 
	 * @param storageIO
	 *            overall capacity of storage IO
	 * 
	 * @pre storageIO >= 0
	 * @post $none
	 */
	public StorageIOProvisioner(double storageIO) {
		setStorageIO(storageIO);
		setAvailableStorageIO(storageIO);
	}

	/**
	 * Allocates storageIO for a given VM.
	 * 
	 * @param vm
	 *            virtual machine for which the storageIO are being allocated
	 * @param storageIO
	 *            the storageIO
	 * 
	 * @return $true if the storageIO could be allocated; $false otherwise
	 * 
	 * @pre $none
	 * @post $none
	 */
	public abstract boolean allocateStorageIOForVm(RdaVm vm, double storageIO);

	/**
	 * Gets the allocated storageIO for VM.
	 * 
	 * @param vm
	 *            the VM
	 * 
	 * @return the allocated storageIO for vm
	 */
	public abstract double getAllocatedStorageIOForVm(RdaVm vm);

	/**
	 * Releases storageIO used by a VM.
	 * 
	 * @param vm
	 *            the vm
	 * 
	 * @pre $none
	 * @post none
	 */
	public abstract void deallocateStorageIOForVm(RdaVm vm);

	/**
	 * Releases storageIO used by a all VMs.
	 * 
	 * @pre $none
	 * @post none
	 */
	public void deallocateStorageIOForAllVms() {
		setAvailableStorageIO(getStorageIO());
	}

	/**
	 * Checks if storageIO is suitable for vm.
	 * 
	 * @param vm
	 *            the vm
	 * @param storageIO
	 *            the storageIO
	 * 
	 * @return true, if storageIO is suitable for vm
	 */
	public abstract boolean isSuitableForVm(RdaVm vm, double storageIO);

	/**
	 * Gets the storageIO.
	 * 
	 * @return the storageIO
	 */
	public double getStorageIO() {
		return storageIO;
	}

	/**
	 * Sets the storageIO.
	 * 
	 * @param storageIO
	 *            the new storageIO
	 */
	protected void setStorageIO(double storageIO) {
		this.storageIO = storageIO;
	}

	/**
	 * Gets the available storageIO in the host.
	 * 
	 * @return available storageIO
	 * 
	 * @pre $none
	 * @post $none
	 */
	public double getAvailableStorageIO() {
		return availableStorageIO;
	}

	/**
	 * Gets the amount of used storageIO in the host.
	 * 
	 * @return used storageIO
	 * 
	 * @pre $none
	 * @post $none
	 */
	public double getUsedStorageIO() {
		return this.storageIO - this.availableStorageIO;
	}

	/**
	 * Sets the available storageIO.
	 * 
	 * @param availablestorageIO
	 *            the new available storageIO
	 */
	protected void setAvailableStorageIO(double availableStorageIO) {
		this.availableStorageIO = availableStorageIO;
	}

}
