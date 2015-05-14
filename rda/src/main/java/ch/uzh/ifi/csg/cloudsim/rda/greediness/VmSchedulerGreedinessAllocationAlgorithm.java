package ch.uzh.ifi.csg.cloudsim.rda.greediness;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import ch.uzh.ifi.csg.cloudsim.rda.*;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.StorageIOProvisioner;

public class VmSchedulerGreedinessAllocationAlgorithm extends
		VmSchedulerTimeShared implements MultipleResourcesVmScheduler {

	RamProvisioner ramProvisioner;
	BwProvisioner bwProvisioner;
	StorageIOProvisioner sProvisioner;
	String pythonPath;
	
	BufferedWriter out;
	Process p;
	BufferedReader in;

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
			p = Runtime.getRuntime().exec(
					pythonPath+" src\\main\\resources\\python\\getAllocationSimple.py");
		} catch (IOException e) {
			e.printStackTrace();
		}
		in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
	}

	public void allocateResourcesForAllVms(double currentTime, List<Vm> vms) {

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
			reqCpu = Math.round(Math.ceil(reqCpu * 10000000)) / 10000000.0;
			reqStorage = Math.round(Math.ceil(reqStorage * 10000000)) / 10000000.0;
			reqBw = Math.round(Math.ceil(reqBw * 10000000)) / 10000000.0;
			reqRam = Math.round(Math.ceil(reqRam * 10000000)) / 10000000.0;
			
			requestedResources += reqCpu
					+ " "
					+ reqRam
					+ " " + reqBw + " " + reqStorage + " ";

		}

		String supply = (int) super.getPeCapacity() + " "
				+ ramProvisioner.getRam() + " " + bwProvisioner.getBw() + " "
				+ sProvisioner.getStorageIO();

		try {
			out.write(supply + " " + requestedResources);
			out.flush();
			out.newLine();
			out.flush();

			int i = 0;
			int vmCnt = 2;
			String line = in.readLine();
			while (line != null) {
				System.out.println("value is : " + line);
				if (i == vmCnt) {
					break;
				}
				if (line.contains("gets")) {
					line = line.substring(line.indexOf("'") + 1);
					double mips = Double.valueOf(line.substring(0,
							line.indexOf("'")));
					line = line.substring(line.indexOf("'") + 1);
					line = line.substring(line.indexOf("'") + 1);
					float memory = Float.valueOf(line.substring(0,
							line.indexOf("'")));
					line = line.substring(line.indexOf("'") + 1);
					line = line.substring(line.indexOf("'") + 1);
					float bw = Float.valueOf(line.substring(0,
							line.indexOf("'")));
					line = line.substring(line.indexOf("'") + 1);
					line = line.substring(line.indexOf("'") + 1);
					float storageIO = Float.valueOf(line.substring(0,
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
				
				line = in.readLine();

			}
		} catch (IOException e) {
			Log.printLine("Error while allocating resources: " + e.getMessage());
		}

	}

}
