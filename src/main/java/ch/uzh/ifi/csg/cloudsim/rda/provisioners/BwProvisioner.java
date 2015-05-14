package ch.uzh.ifi.csg.cloudsim.rda.provisioners;

import org.cloudbus.cloudsim.Vm;

/**
 * This implementation of the bandwidth provisioner allows a more fine
 * allocation of resources. This class is implemented adequate to the
 * org.cloudbus.cloudsim.provisioners.BwProvisioner except that it works with
 * Double values instead of Long values.
 * 
 * @author Patrick A. Taddei
 * @see org.cloudbus.cloudsim.provisioners.BwProvisioner
 */
public abstract class BwProvisioner {

	/** The bw. */
	private double bw;

	/** The available bw. */
	private double availableBw;

	/**
	 * Creates the new BwProvisioner.
	 * 
	 * @param bw
	 *            overall amount of bandwidth available in the host.
	 * 
	 * @pre bw >= 0
	 * @post $none
	 */
	public BwProvisioner(double bw) {
		setBw(bw);
		setAvailableBw(bw);
	}

	/**
	 * Allocates BW for a given VM.
	 * 
	 * @param vm
	 *            virtual machine for which the bw are being allocated
	 * @param bw
	 *            the bw
	 * 
	 * @return $true if the bw could be allocated; $false otherwise
	 * 
	 * @pre $none
	 * @post $none
	 */
	public abstract boolean allocateBwForVm(Vm vm, double bw);

	/**
	 * Gets the allocated BW for VM.
	 * 
	 * @param vm
	 *            the VM
	 * 
	 * @return the allocated BW for vm
	 */
	public abstract double getAllocatedBwForVm(Vm vm);

	/**
	 * Releases BW used by a VM.
	 * 
	 * @param vm
	 *            the vm
	 * 
	 * @pre $none
	 * @post none
	 */
	public abstract void deallocateBwForVm(Vm vm);

	/**
	 * Releases BW used by a all VMs.
	 * 
	 * @pre $none
	 * @post none
	 */
	public void deallocateBwForAllVms() {
		setAvailableBw(getBw());
	}

	/**
	 * Checks if BW is suitable for vm.
	 * 
	 * @param vm
	 *            the vm
	 * @param bw
	 *            the bw
	 * 
	 * @return true, if BW is suitable for vm
	 */
	public abstract boolean isSuitableForVm(Vm vm, double bw);

	/**
	 * Gets the bw.
	 * 
	 * @return the bw
	 */
	public double getBw() {
		return bw;
	}

	/**
	 * Sets the bw.
	 * 
	 * @param bw
	 *            the new bw
	 */
	protected void setBw(double bw) {
		this.bw = bw;
	}

	/**
	 * Gets the available BW in the host.
	 * 
	 * @return available bw
	 * 
	 * @pre $none
	 * @post $none
	 */
	public double getAvailableBw() {
		return availableBw;
	}

	/**
	 * Gets the amount of used BW in the host.
	 * 
	 * @return used bw
	 * 
	 * @pre $none
	 * @post $none
	 */
	public double getUsedBw() {
		return bw - availableBw;
	}

	/**
	 * Sets the available bw.
	 * 
	 * @param availableBw
	 *            the new available bw
	 */
	protected void setAvailableBw(double availableBw) {
		this.availableBw = availableBw;
	}

}
