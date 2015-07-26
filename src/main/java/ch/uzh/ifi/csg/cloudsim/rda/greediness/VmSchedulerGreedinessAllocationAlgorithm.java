package ch.uzh.ifi.csg.cloudsim.rda.greediness;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;

import ch.uzh.ifi.csg.cloudsim.rda.RdaVm;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.StorageIOProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.useraware.RdaUserAwareVmScheduler;

/**
 * The greediness scheduling algorithm, was developed to achieve an overall
 * fairness among multiple customers within the same cloud, thereafter, it
 * implements the RdaUserAwareVmScheduler interface, which supports a
 * user/customer aware scenario.
 * 
 * This class solely wraps the python script, where one can find the actual
 * implementation of the greediness metric.
 * 
 * @author Patrick A. Taddei
 *
 */
public class VmSchedulerGreedinessAllocationAlgorithm extends
		VmSchedulerTimeShared implements RdaUserAwareVmScheduler {

	RamProvisioner ramProvisioner;
	BwProvisioner bwProvisioner;
	StorageIOProvisioner sProvisioner;
	String pythonPath;

	BufferedWriter out;
	Process p;
	BufferedReader in;

	BufferedWriter out2;
	Process p2;
	BufferedReader in2;

	public VmSchedulerGreedinessAllocationAlgorithm(List<? extends Pe> pelist) {
		super(pelist);
		throw new UnsupportedOperationException(
				"This constructor is not supported by this scheduler.");
	}

	public VmSchedulerGreedinessAllocationAlgorithm(List<? extends Pe> pelist,
			RamProvisioner ramProvisioner, BwProvisioner bwProvisioner,
			StorageIOProvisioner sProvisioner, String pythonPath) {
		super(pelist);
		this.pythonPath = pythonPath;
		this.ramProvisioner = ramProvisioner;
		this.bwProvisioner = bwProvisioner;
		this.sProvisioner = sProvisioner;
		try {
			p = Runtime.getRuntime().exec(pythonPath + "/getGreediness.py");
		} catch (IOException e) {
			e.printStackTrace();
		}
		in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

		try {
			p2 = Runtime.getRuntime().exec(
					pythonPath + "/getAllocationUserAware.py");
		} catch (IOException e) {
			e.printStackTrace();
		}
		in2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
		out2 = new BufferedWriter(new OutputStreamWriter(p2.getOutputStream()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.greediness.UserAwareVmScheduler#getUserPriorities
	 * (double, java.util.List)
	 */
	public Map<String, Float> getUserPriorities(double currentTime, List<Vm> vms) {

		String requestedResources = "";
		int vmCnt = vms.size();

		Map<String, Float> userPriorities = new HashMap<String, Float>();

		if (vmCnt == 0) {
			return userPriorities;
		}

		for (Vm vm : vms) {
			double reqRam = ((RdaVm) vm).getCurrentAllocatedRamFine();
			double reqBw = ((RdaVm) vm).getCurrentAllocatedBwFine();
			double reqStorage = ((RdaVm) vm).getCurrentAllocatedStorageIO();
			List<Double> reqCpuList = ((RdaVm) vm).getCurrentAllocatedMips();
			double reqCpu = 0.0d;

			if (reqCpuList != null) {
				for (Double pe : reqCpuList) {
					reqCpu += pe.doubleValue();
				}
			}

			reqCpu = roundUpToZero(reqCpu);
			reqRam = roundUpToZero(reqRam);
			reqBw = roundUpToZero(reqBw);
			reqStorage = roundUpToZero(reqStorage);

			requestedResources += ((RdaVm) vm).getCustomer() + " " + reqCpu
					+ " " + reqRam + " " + reqBw + " " + reqStorage + " ";
		}

		String supply = (int) getMipsCapacity() + " " + ramProvisioner.getRam()
				+ " " + bwProvisioner.getBw() + " "
				+ sProvisioner.getStorageIO();
		Log.printLine("Determining greediness: ");
		try {
			Log.printLine(supply + " " + requestedResources);
			out.write(supply + " " + requestedResources);
			out.flush();
			out.newLine();
			out.flush();

			int i = 0;
			String line = in.readLine();
			while (line != null) {
				if (i == vmCnt) {
					break;
				}
				if (line.contains("greed")) {
					Log.printLine(line);
					line = line.substring(3);
					String userName = line.substring(0, line.indexOf(","));
					line = line.substring(line.indexOf(":") + 1);
					line = line.substring(0, line.indexOf("+"));
					float greediness = Float.valueOf(line);

					if (userPriorities.containsKey(userName)) {
						Float currentVal = userPriorities.get(userName);
						userPriorities.put(userName, currentVal + greediness);
					} else {
						userPriorities.put(userName, greediness);
					}
					i++;
				}

				line = in.readLine();

			}

		} catch (IOException e) {
			Log.printLine("Error while getting greediness: " + e.getMessage());
		}

		return userPriorities;
	}

	public double roundUpToZero(double value) {
		if (value < 0.0) {
			value = 0.0;
		}
		return value;
	}

	/*
	 * rounding up to the 8th position behind the comma. If there is some minor
	 * number behind the 8th position the result will be rounded up. e.g.
	 * 0.0000000001235 will be rounded up to 0.00000001
	 */
	private double round(double d) {

		double a = Math.round(d * 100000000) / 100000000.0;

		if (d - a > 0.0d) {
			// still some very small number left
			return a + 0.00000001;
		}
		return a;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.uzh.ifi.csg.cloudsim.rda.greediness.UserAwareVmScheduler#
	 * allocateResourcesForAllVms(double, java.util.List, java.util.Map)
	 */
	public void allocateResourcesForAllVms(double currentTime, List<Vm> vms,
			Map<String, Float> userPriorities) {

		super.getMipsMap().clear();
		setAvailableMips(getMipsCapacity());

		bwProvisioner.deallocateBwForAllVms();
		ramProvisioner.deallocateRamForAllVms();
		sProvisioner.deallocateStorageIOForAllVms();

		String requestedResources = "";
		int vmCnt = vms.size();

		if (vmCnt == 0) {
			return;
		}

		for (Vm vm : vms) {
			double reqRam = ((RdaVm) vm).getCurrentRequestedRam(currentTime);
			double reqBw = ((RdaVm) vm).getCurrentRequestedBw(currentTime);
			double reqStorage = ((RdaVm) vm)
					.getCurrentRequestedStorageIO(currentTime);
			double reqCpu = ((RdaVm) vm)
					.getCurrentRequestedTotalMips(currentTime);

			// rounding the values up, so that the python script gets a more
			// optimistic value
			// this measure is taken, that no resource is damped, if it's
			// because of some
			// very small number behind the comma.
			reqCpu = round(reqCpu);
			reqStorage = round(reqStorage);
			reqBw = round(reqBw);
			reqRam = round(reqRam);

			reqCpu = roundUpToZero(reqCpu);
			reqRam = roundUpToZero(reqRam);
			reqBw = roundUpToZero(reqBw);
			reqStorage = roundUpToZero(reqStorage);

			String owner = ((RdaVm) vm).getCustomer();

			Float greediness = userPriorities.get(owner);
			if (greediness == null) {
				greediness = 0.0f;
			}
			requestedResources += owner + " " + greediness + " " + reqCpu + " "
					+ reqRam + " " + reqBw + " " + reqStorage + " ";
		}

		String supply = (int) getMipsCapacity() + " " + ramProvisioner.getRam()
				+ " " + bwProvisioner.getBw() + " "
				+ sProvisioner.getStorageIO();

		try {
			Log.printLine(supply + " " + requestedResources);
			out2.write(supply + " " + requestedResources);
			out2.flush();
			out2.newLine();
			out2.flush();

			int i = 0;
			double totalAllocatedMips = 0d;
			String line = in2.readLine();
			while (line != null) {
				Log.printLine(line);
				if (i == vmCnt) {
					break;
				}
				if (line.contains("gets")) {
					line = line.substring(line.indexOf("'") + 1);
					double mips = Double.valueOf(line.substring(0,
							line.indexOf("'")));
					line = line.substring(line.indexOf("'") + 1);
					line = line.substring(line.indexOf("'") + 1);
					double memory = Double.valueOf(line.substring(0,
							line.indexOf("'")));
					line = line.substring(line.indexOf("'") + 1);
					line = line.substring(line.indexOf("'") + 1);
					double bw = Double.valueOf(line.substring(0,
							line.indexOf("'")));
					line = line.substring(line.indexOf("'") + 1);
					line = line.substring(line.indexOf("'") + 1);
					double storageIO = Double.valueOf(line.substring(0,
							line.indexOf("'")));

					Vm vm = vms.get(i);
					List<Double> mipsMapCapped = new ArrayList<Double>();
					// split the mips equally between all processor units
					int peCnt = vm.getNumberOfPes();
					for (int n = 0; n < peCnt; n++) {
						mipsMapCapped.add(mips / peCnt);
						totalAllocatedMips += (mips / peCnt);
					}

					getMipsMap().put(vm.getUid(), mipsMapCapped);
					setAvailableMips(super.getAvailableMips() - mips);

					((RdaVm) vm).setCurrentAllocatedMips(mipsMapCapped);

					bwProvisioner.allocateBwForVm(vm, bw);
					ramProvisioner.allocateRamForVm(vm, memory);
					sProvisioner.allocateStorageIOForVm((RdaVm) vm, storageIO);

					i++;

				}

				line = in2.readLine();

			}

			// 0.001 is the safety margin
			if (totalAllocatedMips > super.getPeCapacity() + 0.001) {
				throw new RuntimeException(
						"Too much MIPS assignment by python script. Allocation is higher than supply.");
			}

		} catch (IOException e) {
			Log.printLine("Error while allocating resources: " + e.getMessage());
		}
	}

	/**
	 * Returns total MIPS among all the PEs.
	 * 
	 * @return mips capacity
	 */
	private double getMipsCapacity() {
		if (getPeList() == null) {
			Log.printLine("Pe list is empty");
			return 0;
		}

		double capacity = 0.0;
		for (Pe pe : getPeList()) {
			capacity += pe.getMips();
		}

		return capacity;
	}
}
