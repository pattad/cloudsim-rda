package ch.uzh.ifi.csg.cloudsim.rda;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.ResCloudlet;

/**
 * A simple mock implementation of a rda cloudlet scheduler.
 * 
 * @author Patrick A. Taddei
 * @see RdaVm
 * @see RdaCloudlet
 * 
 */
public class MockRdaCloudletScheduler extends CloudletSchedulerTimeShared
		implements RdaCloudletScheduler {

	/** The mips. */
	private double mips;
	private double ram;
	private double bw;
	private double storageIO;

	/** The number of PEs. */
	private int numberOfPes;

	/** The total mips. */
	private double totalMips;

	/** the event time interval, when scarcity occurs */
	public double scarcitySchedulingInterval;

	/**
	 * Instantiates a new VM scheduler
	 * 
	 * @param mips
	 *            the mips
	 * @param numberOfPes
	 *            the pes number
	 * @param scarcitySchedulingInterval
	 *            the event time interval, when scarcity occurs
	 */
	public MockRdaCloudletScheduler(double mips, int numberOfPes,
			double scarcitySchedulingInterval) {
		super();
		setMips(mips);
		setNumberOfPes(numberOfPes);
		setTotalMips(getNumberOfPes() * getMips());
		this.scarcitySchedulingInterval = scarcitySchedulingInterval;
	}

	/**
	 * set requested ram for testing purposes
	 * 
	 * @param ram
	 */
	public void setRam(double ram) {
		this.ram = ram;
	}

	/**
	 * set requested bandwidth for testing purposes
	 * 
	 * @param bw
	 */
	public void setBw(double bw) {
		this.bw = bw;
	}

	/**
	 * set requested storage IO for testing purposes
	 * 
	 * @param storageIO
	 */
	public void setStorageIO(double storageIO) {
		this.storageIO = storageIO;
	}

	/**
	 * Updates the processing of cloudlets running under management of this
	 * scheduler.
	 * 
	 * @param currentTime
	 *            current simulation time
	 * @param mipsShare
	 *            list with MIPS share of each Pe available to the scheduler
	 * @param bwShare
	 *            bandwidth share available
	 * @param storageIOShare
	 *            storage IO share available
	 * 
	 * @return time predicted completion time of the earliest finishing cloudlet
	 */
	public double updateVmProcessing(double currentTime,
			List<Double> mipsShare, double bwShare, double storageIOShare) {

		return 0.0;
	}

	/**
	 * 
	 * @see RdaCloudletScheduler interface
	 */
	public double getCurrentRequestedGradCpu() {

		return 0.0;
	}

	/**
	 * 
	 * @see RdaCloudletScheduler interface
	 */
	public double getCurrentRequestedGradBw() {

		return 0.0;
	}

	/**
	 * 
	 * @see RdaCloudletScheduler interface
	 */
	public double getCurrentRequestedGradStorageIO() {

		return 0.0;
	}

	@Override
	public double cloudletSubmit(Cloudlet cl) {
		return cloudletSubmit(cl, 0);
	}

	@Override
	public double cloudletSubmit(Cloudlet cl, double fileTransferTime) {
		ResCloudlet rcl = new ResCloudlet(cl);
		rcl.setCloudletStatus(Cloudlet.INEXEC);

		for (int i = 0; i < cl.getNumberOfPes(); i++) {
			rcl.setMachineAndPeId(0, i);
		}

		getCloudletExecList().add(rcl);
		return getEstimatedFinishTime(rcl, getPreviousTime());
	}

	@Override
	public void cloudletFinish(ResCloudlet rcl) {
		rcl.setCloudletStatus(Cloudlet.SUCCESS);
		rcl.finalizeCloudlet();
		getCloudletFinishedList().add(rcl);
	}

	@Override
	public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getCurrentRequestedUtilizationOfRam() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getCurrentRequestedUtilizationOfBw() {
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @see RdaCloudletScheduler interface
	 */
	public double getCurrentRequestedUtilizationOfRam(double currentTime) {
		return ram;
	}

	/**
	 * 
	 * @see RdaCloudletScheduler interface
	 */
	public double getCurrentRequestedUtilizationOfBw(double currentTime) {
		return bw;
	}

	/**
	 * 
	 * @see RdaCloudletScheduler interface
	 */
	public double getCurrentRequestedUtilizationOfStorageIO(double currentTime) {
		return storageIO;
	}

	@Override
	public double getTotalUtilizationOfCpu(double currentTime) {
		double totalUtilization = 0;
		for (ResCloudlet rcl : getCloudletExecList()) {
			totalUtilization += rcl.getCloudlet().getUtilizationOfCpu(
					currentTime);
		}
		return totalUtilization;
	}

	@Override
	public List<Double> getCurrentRequestedMips() {
		// this method is called, when allocating a VM on the host to check
		return getCurrentRequestedMips(0.0);
	}

	/**
	 * Gets the current requested mips.
	 * 
	 * @return the current mips
	 */
	public List<Double> getCurrentRequestedMips(double currentTime) {

		List<Double> currentMips = new ArrayList<Double>();
		double mipsForPe = this.mips / getNumberOfPes();

		for (int i = 0; i < getNumberOfPes(); i++) {
			currentMips.add(mipsForPe);
		}

		return currentMips;
	}

	@Override
	public double getTotalCurrentRequestedMipsForCloudlet(ResCloudlet rcl,
			double time) {
		throw new UnsupportedOperationException();
	}

	@Override
	public double getTotalCurrentAvailableMipsForCloudlet(ResCloudlet rcl,
			List<Double> mipsShare) {
		double totalCurrentMips = 0.0;
		if (mipsShare != null) {
			int neededPEs = rcl.getNumberOfPes();
			for (double mips : mipsShare) {
				totalCurrentMips += mips;
				neededPEs--;
				if (neededPEs <= 0) {
					break;
				}
			}
		}
		return totalCurrentMips;
	}

	@Override
	public double getTotalCurrentAllocatedMipsForCloudlet(ResCloudlet rcl,
			double time) {
		return ((RdaCloudlet) rcl.getCloudlet()).getUtilizationOfCpu(time);
	}

	/**
	 * Get estimated cloudlet completion time.
	 * 
	 * @param rcl
	 *            the rcl
	 * @param time
	 *            the time
	 * @return the estimated finish time
	 */
	public double getEstimatedFinishTime(ResCloudlet rcl, double time) {
		return time
				+ ((rcl.getRemainingCloudletLength()) / getTotalCurrentAllocatedMipsForCloudlet(
						rcl, time));
	}

	/**
	 * Gets the total current mips.
	 * 
	 * @return the total current mips
	 */
	public int getTotalCurrentMips() {
		int totalCurrentMips = 0;
		for (double mips : getCurrentMipsShare()) {
			totalCurrentMips += mips;
		}
		return totalCurrentMips;
	}

	/**
	 * Sets the total mips.
	 * 
	 * @param mips
	 *            the new total mips
	 */
	public void setTotalMips(double mips) {
		totalMips = mips;
	}

	/**
	 * Gets the total mips.
	 * 
	 * @return the total mips
	 */
	public double getTotalMips() {
		return totalMips;
	}

	/**
	 * Sets the pes number.
	 * 
	 * @param pesNumber
	 *            the new pes number
	 */
	public void setNumberOfPes(int pesNumber) {
		numberOfPes = pesNumber;
	}

	/**
	 * Gets the pes number.
	 * 
	 * @return the pes number
	 */
	public int getNumberOfPes() {
		return numberOfPes;
	}

	/**
	 * Sets the mips.
	 * 
	 * @param mips
	 *            the new mips
	 */
	public void setMips(double mips) {
		this.mips = mips;
	}

	/**
	 * Gets the mips.
	 * 
	 * @return the mips
	 */
	public double getMips() {
		return mips;
	}

	public double getCurrentUtilizationOfStorageIO() {
		return storageIO;
	}

	public double getCurrentRequestedUtilizationOfStorageIO() {
		return storageIO;
	}

	public double getCurrentUtilizationOfBw() {
		return bw;
	}

	public double getCurrentUtilizationOfRam() {
		return ram;
	}

	public double getCurrentUtilizationOfCpu() {
		return mips;
	}

}
