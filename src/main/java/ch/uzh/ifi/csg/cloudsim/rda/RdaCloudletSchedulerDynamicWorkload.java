package ch.uzh.ifi.csg.cloudsim.rda;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * Implements a policy of scheduling performed by a virtual machine. Currently
 * this scheduler only supports one cloudlet at a time. If multiple cloudlets
 * are submitted, they will proportionally degraded, in case of scarcity. A an
 * implementation with the Max-Min Fair-Share algorithm would be appropriate in
 * such situations. Please feel free to contribute such an update.
 * 
 * This cloudlet scheduler, makes sure that the Leontief dependencies are taken
 * care of. This results in an equal percentage drop of the other resources, as
 * soon as one resource experiences scarcity. It checks, which resource is the
 * most scarce resource and down-grades the other resources according to this
 * drop. The central method where the scheduling is processed, is like in the
 * RdaVm class the method updateVmProcessing(). This method also had to be
 * extended to support bandwidth and storageIO, besides the cpu speed.
 * 
 * @author Patrick A. Taddei
 * @see RdaVm
 * @see RdaCloudlet
 * 
 */
public class RdaCloudletSchedulerDynamicWorkload extends
		CloudletSchedulerTimeShared implements RdaCloudletScheduler {

	/** The mips. */
	private double mips;

	/** The number of PEs. */
	private int numberOfPes;

	/** The total mips. */
	private double totalMips;

	public double scarcitySchedulingInterval;

	/**
	 * Instantiates a new vM scheduler time shared.
	 * 
	 * @param mips
	 *            the mips
	 * @param numberOfPes
	 *            the pes number
	 */
	public RdaCloudletSchedulerDynamicWorkload(double mips, int numberOfPes,
			double scarcitySchedulingInterval) {
		super();
		setMips(mips);
		setNumberOfPes(numberOfPes);
		setTotalMips(getNumberOfPes() * getMips());
		this.scarcitySchedulingInterval = scarcitySchedulingInterval;
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
	 * @return time predicted completion time of the earliest finishing
	 *         cloudlet, or 0 if there is no next events
	 */
	public double updateVmProcessing(double currentTime,
			List<Double> mipsShare, double bwShare, double storageIOShare) {
		setCurrentMipsShare(mipsShare);
		double nextEvent = Double.MAX_VALUE;

		// XXX remove this
		if (String.valueOf(currentTime).startsWith("3")) {
			System.out.println("XXX");
		}
		double timeSpan = getTimeSpan(currentTime);

		List<ResCloudlet> cloudletsToFinish = new ArrayList<ResCloudlet>();

		double totalRequestedMips = 0;
		double totalRequestedBandwidth = 0;
		double totalRequestedStorageIO = 0;
		for (ResCloudlet rcl : getCloudletExecList()) {
			RdaCloudlet cloudlet = (RdaCloudlet) rcl.getCloudlet();

			totalRequestedMips += cloudlet
					.getRequestedUtilizationOfCpu(timeSpan);

			totalRequestedBandwidth += cloudlet
					.getRequestedUtilizationOfBw(timeSpan);

			totalRequestedStorageIO += cloudlet
					.getRequestedUtilizationOfStorageIO(timeSpan);
		}
		Log.printLine("totalRequestedCPU: " + totalRequestedMips
				+ ", mipsShare: " + mipsShare);
		Log.printLine("totalRequestedBandwidth: " + totalRequestedBandwidth
				+ ", bwShare: " + bwShare);
		Log.printLine("totalRequestedStorageIO: " + totalRequestedStorageIO
				+ ", storageShare: " + storageIOShare);

		double availableMipsShare = 0;
		for (Double d : mipsShare) {
			availableMipsShare += d.doubleValue();
		}

		double mipsDampingFactor = 1;
		if (availableMipsShare < totalRequestedMips) {
			mipsDampingFactor = totalRequestedMips / availableMipsShare;
			Log.printLine("cpuDampingFactor: " + mipsDampingFactor);
		}

		double bwDampingFactor = 1;
		if (bwShare < totalRequestedBandwidth) {

			bwDampingFactor = totalRequestedBandwidth / bwShare;
			if (bwDampingFactor == 0) {
				bwDampingFactor = 1;
			}
			Log.printLine("bwDampingFactor: " + bwDampingFactor);
		}

		double storageDampingFactor = 1;
		if (storageIOShare < totalRequestedStorageIO) {
			storageDampingFactor = totalRequestedStorageIO / storageIOShare;
			if (storageDampingFactor == 0) {
				storageDampingFactor = 1;
			}
			Log.printLine("storageDampingFactor: " + storageDampingFactor);
		}

		for (ResCloudlet rcl : getCloudletExecList()) {

			RdaCloudlet cloudlet = (RdaCloudlet) rcl.getCloudlet();

			double requestedProcessingSpeed = cloudlet
					.getRequestedUtilizationOfCpu(timeSpan);

			// get the highest damping factor that will influence the cloudlet's
			// performance (see Leontief)
			double effectiveDampingFactor = mipsDampingFactor;
			if (effectiveDampingFactor < bwDampingFactor) {
				effectiveDampingFactor = bwDampingFactor;
			}
			if (effectiveDampingFactor < storageDampingFactor) {
				effectiveDampingFactor = storageDampingFactor;
			}

			double effectiveProcessingSpeed;

			if (effectiveDampingFactor != 1.0d) {
				effectiveProcessingSpeed = new BigDecimal(
						requestedProcessingSpeed).divide(
						new BigDecimal(effectiveDampingFactor),
						MathContext.DECIMAL64).doubleValue();
			} else {
				effectiveProcessingSpeed = requestedProcessingSpeed;
			}

			Log.printLine("Requested CPU: " + requestedProcessingSpeed
					+ ", setUtilization: " + effectiveProcessingSpeed);

			double currentRequestedBw = cloudlet
					.getRequestedUtilizationOfBw(timeSpan);
			cloudlet.setUtilizationOfBandwidth(currentRequestedBw
					/ effectiveDampingFactor);

			double currentRequestedStorageIO = cloudlet
					.getRequestedUtilizationOfStorageIO(timeSpan);
			cloudlet.setUtilizationOfStorage(currentRequestedStorageIO
					/ effectiveDampingFactor);

			double currentRequestedRam = cloudlet
					.getRequestedUtilizationOfRam(timeSpan);
			cloudlet.setUtilizationOfRam(currentRequestedRam);

			double lastCPU = cloudlet.getUtilizationOfCpu(0.0f);

			double effectiveGradient;
			if (effectiveDampingFactor != 1.0d) {
				effectiveGradient = (effectiveProcessingSpeed - lastCPU)
						/ timeSpan;
			} else {
				effectiveGradient = cloudlet.getGradOfCpu();
			}

			// XXX remove this traces
			// Log.printLine("effective Grad: " + effectiveGradient);
			// Log.printLine("last CPU: " + lastCPU);
			// Log.printLine("timeSpan: " + timeSpan);
			// Log.printLine("current_time: " + currentTime);
			long processedInstructions;
			if (effectiveGradient != 0.0) {
				double a = (effectiveGradient / 2.0 * (timeSpan * timeSpan) + lastCPU
						* timeSpan);
				processedInstructions = Math.round(a * (double) Consts.MILLION);
			} else {
				processedInstructions = Math.round(effectiveProcessingSpeed
						* timeSpan * Consts.MILLION);
			}
			// Log.printLine("processedInstr.: "+processedInstructions);
			cloudlet.setUtilizationOfCpu(effectiveProcessingSpeed);
			cloudlet.updateInstructionsFinishedSoFar(processedInstructions);
			rcl.updateCloudletFinishedSoFar(processedInstructions);

			// in Million Instructions for the cloudlet
			cloudlet.setCloudletFinishedSoFar(cloudlet
					.getCloudletFinishedSoFar()
					+ (new BigDecimal(processedInstructions).divide(
							new BigDecimal(Consts.MILLION),
							MathContext.DECIMAL64).longValue()));

			Log.printLine("cloudlet finishedSoFar: "
					+ cloudlet.getInstructionsFinishedSoFar()
					+ ", utilizationOfRam: "
					+ cloudlet.getUtilizationOfRam(0.0)
					+ "; getRemainingCloudletLength() "
					+ cloudlet.getRemainingCloudletLength());

			if (cloudlet.getRemainingCloudletLength() <= 0) {
				// finished
				cloudletsToFinish.add(rcl);
				cloudlet.stopRecording();
				nextEvent = CloudSim.getMinTimeBetweenEvents();
				continue;
			} else { // not finshed get the time of the next utilization change
				double nextChangeTime;
				if (effectiveProcessingSpeed != requestedProcessingSpeed) {
					nextChangeTime = scarcitySchedulingInterval;
				} else {
					nextChangeTime = cloudlet.getEstimatedNextChangeTime();
				}
				Log.printLine("nextChangeTime: " + nextChangeTime); // XXX
																	// remove
																	// this
																	// traces
				if (nextChangeTime < nextEvent) {
					nextEvent = nextChangeTime;
				}
			}
		}

		for (ResCloudlet rgl : cloudletsToFinish) {
			getCloudletExecList().remove(rgl);
			cloudletFinish(rgl);
		}

		setPreviousTime(currentTime);

		if (getCloudletExecList().isEmpty()) {
			return 0;
		}

		return nextEvent;
	}

	private double getTimeSpan(double currentTime) {
		double timeSpan = currentTime - getPreviousTime();
		return timeSpan;
	}

	public double getCurrentRequestedGradCpu() {
		double gradTotal = 0.0;

		for (ResCloudlet rcl : getCloudletExecList()) {
			RdaCloudlet cloudlet = (RdaCloudlet) rcl.getCloudlet();
			gradTotal += cloudlet.getGradOfCpu();
		}

		return gradTotal;
	}

	public double getCurrentRequestedGradBw() {
		double gradTotal = 0.0;

		for (ResCloudlet rcl : getCloudletExecList()) {
			RdaCloudlet cloudlet = (RdaCloudlet) rcl.getCloudlet();
			gradTotal += cloudlet.getGradOfBw();
		}

		return gradTotal;
	}

	public double getCurrentRequestedGradStorageIO() {
		double gradTotal = 0.0;

		for (ResCloudlet rcl : getCloudletExecList()) {
			RdaCloudlet cloudlet = (RdaCloudlet) rcl.getCloudlet();
			gradTotal += cloudlet.getGradOfStorageIO();
		}

		return gradTotal;
	}

	/**
	 * Receives an cloudlet to be executed in the VM managed by this scheduler.
	 * 
	 * @param cl
	 *            the cl
	 * @return predicted completion time
	 * @pre _gl != null
	 * @post $none
	 */
	@Override
	public double cloudletSubmit(Cloudlet cl) {
		return cloudletSubmit(cl, 0);
	}

	/**
	 * Receives an cloudlet to be executed in the VM managed by this scheduler.
	 * 
	 * @param cl
	 *            the cl
	 * @param fileTransferTime
	 *            the file transfer time
	 * @return predicted completion time
	 * @pre _gl != null
	 * @post $none
	 */
	@Override
	public double cloudletSubmit(Cloudlet cl, double fileTransferTime) {
		ResCloudlet rcl = new ResCloudlet(cl);
		rcl.setCloudletStatus(Cloudlet.INEXEC);
		// XXX file transferTime ???

		for (int i = 0; i < cl.getNumberOfPes(); i++) {
			rcl.setMachineAndPeId(0, i);
		}

		getCloudletExecList().add(rcl);
		return getEstimatedFinishTime(rcl, getPreviousTime());
	}

	/**
	 * Processes a finished cloudlet.
	 * 
	 * @param rcl
	 *            finished cloudlet
	 * @pre rgl != $null
	 * @post $none
	 */
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

	public double getCurrentRequestedUtilizationOfRam(double currentTime) {
		double ram = 0;
		double timeSpan = getTimeSpan(currentTime);
		for (ResCloudlet cloudlet : cloudletExecList) {
			ram += ((RdaCloudlet) cloudlet.getCloudlet())
					.getRequestedUtilizationOfRam(timeSpan);
		}
		return ram;
	}

	public double getCurrentRequestedUtilizationOfBw(double currentTime) {
		double bw = 0;
		double timeSpan = getTimeSpan(currentTime);

		for (ResCloudlet resCloudlet : cloudletExecList) {
			RdaCloudlet cloudlet = (RdaCloudlet) resCloudlet.getCloudlet();

			bw += cloudlet.getRequestedUtilizationOfBw(timeSpan);
		}
		return bw;
	}

	/**
	 * 
	 * @return
	 */
	public double getCurrentRequestedUtilizationOfStorageIO(double currentTime) {
		double storageIO = 0;
		double timeSpan = getTimeSpan(currentTime);

		for (ResCloudlet resCloudlet : cloudletExecList) {
			RdaCloudlet cloudlet = (RdaCloudlet) resCloudlet.getCloudlet();

			storageIO += cloudlet.getRequestedUtilizationOfStorageIO(timeSpan);
		}
		return storageIO;
	}

	/**
	 * Get utilization created by all cloudlets.
	 * 
	 * @param time
	 *            the time
	 * @return total utilization
	 */
	@Override
	public double getTotalUtilizationOfCpu(double currentTime) {
		double totalUtilization = 0;
		for (ResCloudlet rcl : getCloudletExecList()) {
			totalUtilization += rcl.getCloudlet().getUtilizationOfCpu(
					currentTime);
		}
		return totalUtilization;
	}

	/**
	 * Gets the current requested mips.
	 * 
	 * @return the current mips
	 */
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
		double totalMips = 0;
		double timeSpan = getTimeSpan(currentTime);

		for (ResCloudlet rcl : getCloudletExecList()) {

			RdaCloudlet cloudlet = (RdaCloudlet) rcl.getCloudlet();
			totalMips += cloudlet.getRequestedUtilizationOfCpu(timeSpan);
		}

		List<Double> currentMips = new ArrayList<Double>();
		double mipsForPe = totalMips / getNumberOfPes();

		for (int i = 0; i < getNumberOfPes(); i++) {
			currentMips.add(mipsForPe);
		}

		return currentMips;
	}

	/**
	 * Gets the current mips.
	 * 
	 * @param rcl
	 *            the rcl
	 * @param time
	 *            the time
	 * @return the current mips
	 */
	@Override
	public double getTotalCurrentRequestedMipsForCloudlet(ResCloudlet rcl,
			double time) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the total current mips for the clouddlet.
	 * 
	 * @param rcl
	 *            the rcl
	 * @param mipsShare
	 *            the mips share
	 * @return the total current mips
	 */
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

	/**
	 * Gets the current mips.
	 * 
	 * @param rcl
	 *            the rcl
	 * @param time
	 *            the time
	 * @return the current mips
	 */
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

}
