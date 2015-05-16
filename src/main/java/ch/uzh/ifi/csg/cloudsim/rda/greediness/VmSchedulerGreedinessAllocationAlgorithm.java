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

	public VmSchedulerGreedinessAllocationAlgorithm(
			List<? extends Pe> pelist) {
		super(pelist);
		throw new UnsupportedOperationException(
				"This constructor is not supported by this scheduler.");
	}

	public VmSchedulerGreedinessAllocationAlgorithm(
			List<? extends Pe> pelist, RamProvisioner ramProvisioner,
			BwProvisioner bwProvisioner, StorageIOProvisioner sProvisioner,
			String pythonPath) {
		super(pelist);
		this.pythonPath = pythonPath;
		this.ramProvisioner = ramProvisioner;
		this.bwProvisioner = bwProvisioner;
		this.sProvisioner = sProvisioner;
		try {
			p = Runtime.getRuntime().exec(
					pythonPath
							+ " src\\main\\resources\\python\\getGreediness.py");
		} catch (IOException e) {
			e.printStackTrace();
		}
		in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

		try {
			p2 = Runtime
					.getRuntime()
					.exec(pythonPath
							+ " src\\main\\resources\\python\\getAllocationUserAware.py");
		} catch (IOException e) {
			e.printStackTrace();
		}
		in2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
		out2 = new BufferedWriter(new OutputStreamWriter(p2.getOutputStream()));
	}

	/* (non-Javadoc)
	 * @see ch.uzh.ifi.csg.cloudsim.rda.greediness.UserAwareVmScheduler#getUserPriorities(double, java.util.List)
	 */
	public Map<String, Float> getUserPriorities(double currentTime, List<Vm> vms) {

		String requestedResources = "";
		for (Vm vm : vms) {
			double reqRam = ((RdaVm) vm).getCurrentRequestedRam(currentTime);
			double reqBw = ((RdaVm) vm).getCurrentRequestedBw(currentTime);
			double reqStorage = ((RdaVm) vm)
					.getCurrentRequestedStorageIO(currentTime);
			double reqCpu = ((RdaVm) vm)
					.getCurrentRequestedTotalMips(currentTime);

			requestedResources += ((RdaVm) vm).getCustomer() + " " + reqCpu + " "
					+ reqRam + " " + reqBw + " " + reqStorage + " ";

		}

		String supply = (int) super.getPeCapacity() + " "
				+ ramProvisioner.getRam() + " " + bwProvisioner.getBw() + " "
				+ sProvisioner.getStorageIO();

		Map<String, Float> userPriorities = new HashMap<String, Float>();

		try {
			out.write(supply + " " + requestedResources);
			out.flush();
			out.newLine();
			out.flush();

			int i = 0;
			int vmCnt = 2;
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

	/*
	 * rounding up to the 9th position behind the comma. If there is some minor
	 * number behind the 9th position the result will be rounded up. e.g.
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

	/* (non-Javadoc)
	 * @see ch.uzh.ifi.csg.cloudsim.rda.greediness.UserAwareVmScheduler#allocateResourcesForAllVms(double, java.util.List, java.util.Map)
	 */
	public void allocateResourcesForAllVms(double currentTime, List<Vm> vms,
			Map<String, Float> userPriorities) {

		super.getMipsMap().clear();
		setAvailableMips(super.getPeCapacity());

		bwProvisioner.deallocateBwForAllVms();
		ramProvisioner.deallocateRamForAllVms();
		sProvisioner.deallocateStorageIOForAllVms();

		String requestedResources = "";
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

			String owner = ((RdaVm) vm).getCustomer();
			requestedResources += owner + " " + userPriorities.get(owner) + " "
					+ reqCpu + " " + reqRam + " " + reqBw + " " + reqStorage
					+ " ";

		}

		String supply = (int) super.getPeCapacity() + " "
				+ ramProvisioner.getRam() + " " + bwProvisioner.getBw() + " "
				+ sProvisioner.getStorageIO();

		try {
			out2.write(supply + " " + requestedResources);
			out2.flush();
			out2.newLine();
			out2.flush();

			int i = 0;
			int vmCnt = 2;
			String line = in2.readLine();
			while (line != null) {
				System.out.println(line);
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
		} catch (IOException e) {
			Log.printLine("Error while allocating resources: " + e.getMessage());
		}
	}

}
