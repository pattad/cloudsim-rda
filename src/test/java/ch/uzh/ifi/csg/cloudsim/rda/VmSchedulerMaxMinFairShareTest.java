package ch.uzh.ifi.csg.cloudsim.rda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.BwProvisionerSimple;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.RamProvisionerSimple;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.StorageIOProvisioner;
import ch.uzh.ifi.csg.cloudsim.rda.provisioners.StorageIOProvisionerSimple;

/**
 * The class <code>VmSchedulerMaxMinFairShareTest</code> contains tests for the
 * class <code>{@link VmSchedulerMaxMinFairShare}</code>.
 *
 * @generatedBy CodePro at 6/3/15 8:12 AM
 * @author pat
 * @version $Revision: 1.0 $
 */
public class VmSchedulerMaxMinFairShareTest {

	/**
	 * Run the VmSchedulerMaxMinFairShare(List<? extends
	 * Pe>,RamProvisioner,BwProvisioner,StorageIOProvisioner) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 8:12 AM
	 */
	@Test
	public void testVmSchedulerMaxMinFairShare_1() throws Exception {
		List<? extends Pe> pelist = new ArrayList<Pe>();
		RamProvisioner ramProvisioner = new RamProvisionerSimple(500);
		BwProvisioner bwProvisioner = new BwProvisionerSimple(1000);
		StorageIOProvisioner storageIOProvisioner = new StorageIOProvisionerSimple(
				1000);

		VmSchedulerMaxMinFairShare result = new VmSchedulerMaxMinFairShare(
				pelist, ramProvisioner, bwProvisioner, storageIOProvisioner);

		assertNotNull(result);
		assertEquals(0.0, result.getMaxAvailableMips(), 0);
		assertEquals(0.0, result.getAvailableMips(), 0);
	}

	/**
	 * Run the void allocateResourcesForAllVms(double,List<Vm>) method test. no
	 * 
	 * Testing no over-demand
	 * 
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 8:12 AM
	 */
	@Test
	public void testAllocateResourcesForAllVms_1() throws Exception {
		List<Pe> pelist = new ArrayList<Pe>();
		pelist.add(new Pe(0, new PeProvisionerSimple(1000)));

		VmSchedulerMaxMinFairShare fixture = new VmSchedulerMaxMinFairShare(
				pelist, new RamProvisionerSimple(500), new BwProvisionerSimple(
						1000), new StorageIOProvisionerSimple(1000));
		double currentTime = 1.0;
		List<Vm> vms = new ArrayList<Vm>();

		int mips = 800;
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name
		double schedulingInterval = 0.000000001; // nano second
		double scarcitySchedulingInterval = 0.01; // milli second
		int brokerId = 0;

		MockRdaCloudletScheduler mockScheduler = new MockRdaCloudletScheduler(
				mips, pesNumber, scarcitySchedulingInterval);
		mockScheduler.setMips(mips);
		mockScheduler.setBw(200);
		mockScheduler.setRam(123);
		mockScheduler.setStorageIO(100);

		// create VM
		RdaVm vm = new RdaVm(0, brokerId, mips, pesNumber, ram, bw, size, 1,
				vmm, mockScheduler, schedulingInterval);
		// add the VM to the vmList
		vms.add(vm);

		fixture.allocateResourcesForAllVms(currentTime, vms);

		assertEquals(200, vm.getCurrentAllocatedBw(), 0);
		assertEquals(123, vm.getCurrentAllocatedRam(), 0);
		assertEquals(100, vm.getCurrentAllocatedStorageIO(), 0);
		assertEquals(800, vm.getCurrentAllocatedMips().get(0), 0);

	}

	/**
	 * Run the void allocateResourcesForAllVms(double,List<Vm>) method test.
	 * 
	 * Testing over-demand of CPU
	 * 
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 8:12 AM
	 */
	@Test
	public void testAllocateResourcesForAllVms_2() throws Exception {
		List<Pe> pelist = new ArrayList<Pe>();
		pelist.add(new Pe(0, new PeProvisionerSimple(1000)));

		VmSchedulerMaxMinFairShare fixture = new VmSchedulerMaxMinFairShare(
				pelist, new RamProvisionerSimple(500), new BwProvisionerSimple(
						1000), new StorageIOProvisionerSimple(1000));
		double currentTime = 1.0;
		List<Vm> vms = new ArrayList<Vm>();

		int mips = 1100;
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name
		double schedulingInterval = 0.000000001; // nano second
		double scarcitySchedulingInterval = 0.01; // milli second
		int brokerId = 0;

		MockRdaCloudletScheduler mockScheduler = new MockRdaCloudletScheduler(
				mips, pesNumber, scarcitySchedulingInterval);
		mockScheduler.setMips(mips);
		mockScheduler.setBw(200);
		mockScheduler.setRam(123);
		mockScheduler.setStorageIO(100);

		// create VM
		RdaVm vm = new RdaVm(0, brokerId, mips, pesNumber, ram, bw, size, 1,
				vmm, mockScheduler, schedulingInterval);
		// add the VM to the vmList
		vms.add(vm);

		fixture.allocateResourcesForAllVms(currentTime, vms);

		assertEquals(182, vm.getCurrentAllocatedBw(), 0);
		assertEquals(123, vm.getCurrentAllocatedRam(), 0);
		assertEquals(90.9, vm.getCurrentAllocatedStorageIO(), 0.1);
		assertEquals(1000, vm.getCurrentAllocatedMips().get(0), 0);

	}

	/**
	 * Run the void allocateResourcesForAllVms(double,List<Vm>) method test.
	 * 
	 * Testing over-demand of BW
	 * 
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 8:12 AM
	 */
	@Test
	public void testAllocateResourcesForAllVms_3() throws Exception {
		List<Pe> pelist = new ArrayList<Pe>();
		pelist.add(new Pe(0, new PeProvisionerSimple(1000)));

		VmSchedulerMaxMinFairShare fixture = new VmSchedulerMaxMinFairShare(
				pelist, new RamProvisionerSimple(500), new BwProvisionerSimple(
						1000), new StorageIOProvisionerSimple(1000));
		double currentTime = 1.0;
		List<Vm> vms = new ArrayList<Vm>();

		int mips = 1000;
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name
		double schedulingInterval = 0.000000001; // nano second
		double scarcitySchedulingInterval = 0.01; // milli second
		int brokerId = 0;

		MockRdaCloudletScheduler mockScheduler = new MockRdaCloudletScheduler(
				mips, pesNumber, scarcitySchedulingInterval);
		mockScheduler.setMips(mips);
		mockScheduler.setBw(1100);
		mockScheduler.setRam(123);
		mockScheduler.setStorageIO(100);

		// create VM
		RdaVm vm = new RdaVm(0, brokerId, mips, pesNumber, ram, bw, size, 1,
				vmm, mockScheduler, schedulingInterval);
		// add the VM to the vmList
		vms.add(vm);

		fixture.allocateResourcesForAllVms(currentTime, vms);

		assertEquals(1000, vm.getCurrentAllocatedBw(), 0);
		assertEquals(123, vm.getCurrentAllocatedRam(), 0);
		assertEquals(90.9, vm.getCurrentAllocatedStorageIO(), 0.1);
		assertEquals(909, vm.getCurrentAllocatedMips().get(0), 0.1);

	}

	/**
	 * Run the void allocateResourcesForAllVms(double,List<Vm>) method test.
	 * 
	 * Testing over-demand of StorageIO
	 * 
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 8:12 AM
	 */
	@Test
	public void testAllocateResourcesForAllVms_4() throws Exception {
		List<Pe> pelist = new ArrayList<Pe>();
		pelist.add(new Pe(0, new PeProvisionerSimple(1000)));

		VmSchedulerMaxMinFairShare fixture = new VmSchedulerMaxMinFairShare(
				pelist, new RamProvisionerSimple(500), new BwProvisionerSimple(
						1000), new StorageIOProvisionerSimple(1000));
		double currentTime = 1.0;
		List<Vm> vms = new ArrayList<Vm>();

		int mips = 1000;
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name
		double schedulingInterval = 0.000000001; // nano second
		double scarcitySchedulingInterval = 0.01; // milli second
		int brokerId = 0;

		MockRdaCloudletScheduler mockScheduler = new MockRdaCloudletScheduler(
				mips, pesNumber, scarcitySchedulingInterval);
		mockScheduler.setMips(mips);
		mockScheduler.setBw(1000);
		mockScheduler.setRam(123);
		mockScheduler.setStorageIO(1100);

		// create VM
		RdaVm vm = new RdaVm(0, brokerId, mips, pesNumber, ram, bw, size, 1,
				vmm, mockScheduler, schedulingInterval);
		// add the VM to the vmList
		vms.add(vm);

		fixture.allocateResourcesForAllVms(currentTime, vms);

		assertEquals(909, vm.getCurrentAllocatedBw(), 0.1);
		assertEquals(123, vm.getCurrentAllocatedRam(), 0);
		assertEquals(1000, vm.getCurrentAllocatedStorageIO(), 0.1);
		assertEquals(909, vm.getCurrentAllocatedMips().get(0), 0.1);

	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *             if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 6/3/15 8:12 AM
	 */
	@Before
	public void setUp() throws Exception {
		// add additional set up code here
	}

	/**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *             if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 6/3/15 8:12 AM
	 */
	@After
	public void tearDown() throws Exception {
		// Add additional tear down code here
	}

	/**
	 * Launch the test.
	 *
	 * @param args
	 *            the command line arguments
	 *
	 * @generatedBy CodePro at 6/3/15 8:12 AM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore()
				.run(VmSchedulerMaxMinFairShareTest.class);
	}
}