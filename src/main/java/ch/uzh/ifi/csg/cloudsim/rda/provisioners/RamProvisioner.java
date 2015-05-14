package ch.uzh.ifi.csg.cloudsim.rda.provisioners;

import org.cloudbus.cloudsim.Vm;

/**
 * This implementation of the bandwidth provisioner allows a more fine
 * allocation of resources. This class is implemented adequate to the
 * org.cloudbus.cloudsim.provisioners.RamProvisioner except that it works with
 * Double values instead of Long values.
 * 
 * @author Patrick A. Taddei
 * @see org.cloudbus.cloudsim.provisioners.RamProvisioner
 */
public abstract class RamProvisioner {

	/** The ram. */
	private double ram;

	/** The available ram. */
	private double availableRam;

	/**
	 * Creates the new RamProvisioner.
	 * 
	 * @param ram the ram
	 * 
	 * @pre ram>=0
	 * @post $none
	 */
	public RamProvisioner(double ram) {
		setRam(ram);
		setAvailableRam(ram);
	}

	/**
	 * Allocates RAM for a given VM.
	 * 
	 * @param vm virtual machine for which the RAM are being allocated
	 * @param ram the RAM
	 * 
	 * @return $true if the RAM could be allocated; $false otherwise
	 * 
	 * @pre $none
	 * @post $none
	 */
	public abstract boolean allocateRamForVm(Vm vm, double ram);

	/**
	 * Gets the allocated RAM for VM.
	 * 
	 * @param vm the VM
	 * 
	 * @return the allocated RAM for vm
	 */
	public abstract double getAllocatedRamForVm(Vm vm);

	/**
	 * Releases BW used by a VM.
	 * 
	 * @param vm the vm
	 * 
	 * @pre $none
	 * @post none
	 */
	public abstract void deallocateRamForVm(Vm vm);

	/**
	 * Releases BW used by a all VMs.
	 * 
	 * @pre $none
	 * @post none
	 */
	public void deallocateRamForAllVms() {
		setAvailableRam(getRam());
	}

	/**
	 * Checks if is suitable for vm.
	 * 
	 * @param vm the vm
	 * @param ram the ram
	 * 
	 * @return true, if is suitable for vm
	 */
	public abstract boolean isSuitableForVm(Vm vm, double ram);

	/**
	 * Gets the ram.
	 * 
	 * @return the ram
	 */
	public double getRam() {
		return ram;
	}

	/**
	 * Sets the ram.
	 * 
	 * @param ram the ram to set
	 */
	protected void setRam(double ram) {
		this.ram = ram;
	}

	/**
	 * Gets the amount of used RAM in the host.
	 * 
	 * @return used ram
	 * 
	 * @pre $none
	 * @post $none
	 */
	public double getUsedRam() {
		return ram - availableRam;
	}

	/**
	 * Gets the available RAM in the host.
	 * 
	 * @return available ram
	 * 
	 * @pre $none
	 * @post $none
	 */
	public double getAvailableRam() {
		return availableRam;
	}

	/**
	 * Sets the available ram.
	 * 
	 * @param availableRam the availableRam to set
	 */
	protected void setAvailableRam(double availableRam) {
		this.availableRam = availableRam;
	}

}
