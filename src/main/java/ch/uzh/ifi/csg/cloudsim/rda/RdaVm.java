package ch.uzh.ifi.csg.cloudsim.rda;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.power.PowerVm;

/**
 * The RdaVm particularly supports multiple resources. The central method
 * updateVmProcessing() had to be extended to support the bandwidth and storage
 * I/O, besides CPU speed. Moreover, some methods had also to be adapted to
 * support decimal places for RAM and network bandwidth.
 * 
 * @author Patrick A. Taddei
 * 
 */
public class RdaVm extends PowerVm {

	private double currentAllocatedStorageIO;

	/** The current allocated ram. */
	private double currentAllocatedRam;

	/** The current allocated bw. */
	private double currentAllocatedBw;

	private String customer;

	private float currentPriority;

	public RdaVm(int id, int userId, double mips, int pesNumber, int ram,
			long bw, long size, int priority, String vmm,
			CloudletScheduler cloudletScheduler, double schedulingInterval) {
		super(id, userId, mips, pesNumber, ram, bw, size, priority, vmm,
				cloudletScheduler, schedulingInterval);
	}

	/**
	 * Updates the processing of cloudlets running on this VM.
	 * 
	 * @param currentTime
	 *            current simulation time
	 * @param mipsShare
	 *            array with MIPS share of each Pe available to the scheduler
	 * 
	 * @return time predicted completion time of the earliest finishing
	 *         cloudlet, or 0 if there is no next events
	 * 
	 */
	public double updateVmProcessing(final double currentTime,
			final List<Double> mipsShare, double bwShare, double storageShare) {
		double time = 0.0;
		if (mipsShare != null) {
			time = ((RdaCloudletScheduler) getCloudletScheduler())
					.updateVmProcessing(currentTime, mipsShare, bwShare,
							storageShare);
		} else {
			return time;
		}
		return time;
	}

	@Override
	public double updateVmProcessing(final double currentTime,
			final List<Double> mipsShare) {
		throw new UnsupportedOperationException();
	}

	public double getCurrentAllocatedStorageIO() {
		return this.currentAllocatedStorageIO;
	}

	public List<Double> getCurrentRequestedMips(double currentTime) {
		List<Double> currentRequestedMips = ((RdaCloudletScheduler) getCloudletScheduler())
				.getCurrentRequestedMips(currentTime);
		if (isBeingInstantiated()) {
			currentRequestedMips = new ArrayList<Double>();
			for (int i = 0; i < getNumberOfPes(); i++) {
				currentRequestedMips.add(getMips());
			}
		}
		return currentRequestedMips;
	}

	/**
	 * Gets the current requested total mips.
	 * 
	 * @return the current requested total mips
	 */
	public double getCurrentRequestedTotalMips(double currentTime) {
		double totalRequestedMips = 0;
		for (double mips : getCurrentRequestedMips(currentTime)) {
			totalRequestedMips += mips;
		}
		return totalRequestedMips;
	}

	public double getCurrentRequestedGradCpu() {
		return ((RdaCloudletScheduler) getCloudletScheduler())
				.getCurrentRequestedGradCpu();
	}

	public double getCurrentRequestedGradBw() {
		return ((RdaCloudletScheduler) getCloudletScheduler())
				.getCurrentRequestedGradBw();
	}

	public double getCurrentRequestedGradStorageIO() {
		return ((RdaCloudletScheduler) getCloudletScheduler())
				.getCurrentRequestedGradStorageIO();
	}

	/**
	 * Gets the current requested storage IO.
	 * 
	 * @return the current requested storage IO
	 */
	public double getCurrentRequestedStorageIO(double currentTime) {
		return ((RdaCloudletScheduler) getCloudletScheduler())
				.getCurrentRequestedUtilizationOfStorageIO(currentTime);
	}

	/**
	 * Gets the current requested storage IO.
	 * 
	 * @return the current requested storage IO
	 */
	public double getCurrentRequestedBw(double currentTime) {
		return ((RdaCloudletScheduler) getCloudletScheduler())
				.getCurrentRequestedUtilizationOfBw(currentTime);
	}

	/**
	 * Gets the current requested storage IO.
	 * 
	 * @return the current requested storage IO
	 */
	public double getCurrentRequestedRam(double currentTime) {
		return ((RdaCloudletScheduler) getCloudletScheduler())
				.getCurrentRequestedUtilizationOfRam(currentTime);
	}

	/**
	 * Gets the current requested bw.
	 * 
	 * @return the current requested bw
	 */
	public long getCurrentRequestedBw() {
		if (isBeingInstantiated()) {
			return getBw();
		}
		return (long) getCloudletScheduler()
				.getCurrentRequestedUtilizationOfBw();
	}

	/**
	 * Gets the current requested ram.
	 * 
	 * @return the current requested ram
	 */
	public int getCurrentRequestedRam() {
		if (isBeingInstantiated()) {
			return getRam();
		}
		return (int) getCloudletScheduler()
				.getCurrentRequestedUtilizationOfRam();
	}

	@Override
	public int getCurrentAllocatedRam() {
		return (int) Math.round(currentAllocatedRam);
	}

	public double getCurrentAllocatedRamFine() {
		return currentAllocatedRam;
	}

	public void setCurrentAllocatedRam(double currentAllocatedRam) {
		this.currentAllocatedRam = currentAllocatedRam;
	}

	@Override
	public long getCurrentAllocatedBw() {
		return Math.round(currentAllocatedBw);
	}

	public double getCurrentAllocatedBwFine() {
		return currentAllocatedBw;
	}

	public void setCurrentAllocatedBw(double currentAllocatedBw) {
		this.currentAllocatedBw = currentAllocatedBw;
	}

	public void setCurrentAllocatedStorageIO(double currentAllocatedStorageIO) {
		this.currentAllocatedStorageIO = currentAllocatedStorageIO;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public float getCurrentPriority() {
		return currentPriority;
	}

	public void setCurrentPriority(float currentPriority) {
		this.currentPriority = currentPriority;
	}

}
