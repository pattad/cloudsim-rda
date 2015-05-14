package ch.uzh.ifi.csg.cloudsim.rda.provisioners;

import java.util.HashMap;
import java.util.Map;

import org.cloudbus.cloudsim.Vm;

import ch.uzh.ifi.csg.cloudsim.rda.RdaVm;

/**
 * BwProvisionerSimple is a class that implements a simple best effort
 * allocation policy: if there is bw available to request, it allocates,
 * otherwise, it fails.
 * 
 * @author Patrick A. Taddei
 */
public class BwProvisionerSimple extends BwProvisioner {

	/** The bw table. */
	private Map<String, Double> bwTable;

	/**
	 * Instantiates a new bw provisioner simple.
	 * 
	 * @param bw
	 *            the bw
	 */
	public BwProvisionerSimple(double bw) {
		super(bw);
		setBwTable(new HashMap<String, Double>());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisioner#allocateBwForVm
	 * (org.cloudbus.cloudsim.Vm, double)
	 */
	@Override
	public boolean allocateBwForVm(Vm vm, double bw) {
		deallocateBwForVm(vm);

		double roundedBw = (double) (Math
				.round(((getAvailableBw() - bw) * 100)) / 100.0);
		if (getAvailableBw() >= roundedBw) {
			setAvailableBw(roundedBw);
			getBwTable().put(vm.getUid(), bw);
			((RdaVm) vm).setCurrentAllocatedBw(getAllocatedBwForVm(vm));
			return true;
		}

		((RdaVm) vm).setCurrentAllocatedBw(getAllocatedBwForVm(vm));
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisioner#getAllocatedBwForVm
	 * (org.cloudbus.cloudsim.Vm)
	 */
	@Override
	public double getAllocatedBwForVm(Vm vm) {
		if (getBwTable().containsKey(vm.getUid())) {
			return getBwTable().get(vm.getUid());
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisioner#deallocateBwForVm
	 * (org.cloudbus.cloudsim.Vm)
	 */
	@Override
	public void deallocateBwForVm(Vm vm) {
		if (getBwTable().containsKey(vm.getUid())) {
			double amountFreed = getBwTable().remove(vm.getUid());
			setAvailableBw(getAvailableBw() + amountFreed);
			vm.setCurrentAllocatedBw(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisioner#deallocateBwForAllVms
	 * ()
	 */
	@Override
	public void deallocateBwForAllVms() {
		super.deallocateBwForAllVms();
		getBwTable().clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisioner#isSuitableForVm
	 * (org.cloudbus.cloudsim.Vm, double)
	 */
	@Override
	public boolean isSuitableForVm(Vm vm, double bw) {
		double allocatedBw = getAllocatedBwForVm(vm);
		boolean result = allocateBwForVm(vm, bw);
		deallocateBwForVm(vm);
		if (allocatedBw > 0) {
			allocateBwForVm(vm, allocatedBw);
		}
		return result;
	}

	/**
	 * Gets the bw table.
	 * 
	 * @return the bw table
	 */
	protected Map<String, Double> getBwTable() {
		return bwTable;
	}

	/**
	 * Sets the bw table.
	 * 
	 * @param bwTable
	 *            the bw table
	 */
	protected void setBwTable(Map<String, Double> bwTable) {
		this.bwTable = bwTable;
	}

}
