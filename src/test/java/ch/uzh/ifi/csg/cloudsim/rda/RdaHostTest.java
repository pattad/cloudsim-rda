package ch.uzh.ifi.csg.cloudsim.rda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
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
 * The class <code>RdaHostTest</code> contains tests for the class
 * <code>{@link RdaHost}</code>.
 *
 * @generatedBy CodePro at 6/3/15 10:49 AM
 * @author pat
 * @version $Revision: 1.0 $
 */
public class RdaHostTest {
	/**
	 * Run the
	 * RdaHost(int,RamProvisioner,BwProvisioner,StorageIOProvisioner,long,List<?
	 * extends Pe>,VmScheduler,double) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:49 AM
	 */
	@Test
	public void testRdaHost_1() throws Exception {
		int id = 1;
		RamProvisioner ramProvisioner = new RamProvisionerSimple(1.0);
		BwProvisioner bwProvisioner = new BwProvisionerSimple(1.0);
		StorageIOProvisioner storageIOProvisioner = new StorageIOProvisionerSimple(
				1.0);
		long storage = 1L;
		List<? extends Pe> peList = new ArrayList<Pe>();
		VmScheduler vmScheduler = new VmSchedulerTimeShared(new ArrayList<Pe>());
		double scarcitySchedulingInterval = 1.0;

		RdaHost result = new RdaHost(id, ramProvisioner, bwProvisioner,
				storageIOProvisioner, storage, peList, vmScheduler,
				scarcitySchedulingInterval);

		assertNotNull(result);
		assertEquals(1.0, result.getScarcityShedulingInterval(), 0);
		assertEquals(117.0, result.getMaxPower(), 0);
		assertEquals(Double.NaN, result.getPower(), 0);
		assertEquals(0.0, result.getUtilizationMips(), 0);
		assertEquals(0.0, result.getPreviousUtilizationMips(), 0);
		assertEquals(0.0, result.getUtilizationOfCpuMips(), 0);
		assertEquals(0.0, result.getMaxUtilization(), 0);
		assertEquals(Double.NaN, result.getUtilizationOfCpu(), 0);
		assertEquals(Double.NaN, result.getPreviousUtilizationOfCpu(), 0);
		assertEquals(1, result.getId());
		assertEquals(1L, result.getStorage());
		assertEquals(0, result.getNumberOfPes());
		assertEquals(0, result.getTotalMips());
		assertEquals(null, result.getBwProvisioner());
		assertEquals(false, result.isFailed());
		assertEquals(null, result.getDatacenter());
		assertEquals(0.0, result.getAvailableMips(), 0);
		assertEquals(null, result.getRamProvisioner());
		assertEquals(0.0, result.getMaxAvailableMips(), 0);
		assertEquals(0, result.getNumberOfFreePes());
	}

	/**
	 * Run the List<Vm> getCompletedVms() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:49 AM
	 */
	@Test
	public void testGetCompletedVms_1() throws Exception {
		RdaHost fixture = new RdaHost(1, new RamProvisionerSimple(1.0),
				new BwProvisionerSimple(1.0), new StorageIOProvisionerSimple(
						1.0), 1L, new ArrayList<Pe>(),
				new VmSchedulerTimeShared(new ArrayList<Pe>()), 1.0);

		List<Vm> result = fixture.getCompletedVms();

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the double getScarcityShedulingInterval() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:49 AM
	 */
	@Test
	public void testGetScarcityShedulingInterval_1() throws Exception {
		RdaHost fixture = new RdaHost(1, new RamProvisionerSimple(1.0),
				new BwProvisionerSimple(1.0), new StorageIOProvisionerSimple(
						1.0), 1L, new ArrayList<Pe>(),
				new VmSchedulerTimeShared(new ArrayList<Pe>()), 1.0);

		double result = fixture.getScarcityShedulingInterval();

		assertEquals(1.0, result, 0.1);
	}

	/**
	 * Run the boolean isSuitableForVm(Vm) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:49 AM
	 */
	@Test
	public void testIsSuitableForVm_1() throws Exception {
		List<Pe> pelist = getPeList();

		RdaHost fixture = new RdaHost(1, new RamProvisionerSimple(1000.0),
				new BwProvisionerSimple(1000.0),
				new StorageIOProvisionerSimple(1000.0), 1L, pelist,
				new VmSchedulerTimeShared(pelist), 1.0);
		RdaVm vm = createRdaVm();

		boolean result = fixture.isSuitableForVm(vm);

		assertTrue(result);
	}

	private List<Pe> getPeList() {
		List<Pe> pelist = new ArrayList<Pe>();
		pelist.add(new Pe(0, new PeProvisionerSimple(1000)));
		return pelist;
	}

	private RdaVm createRdaVm() {
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
		return vm;
	}

	/**
	 * Run the void setScarcityShedulingInterval(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:49 AM
	 */
	@Test
	public void testSetScarcityShedulingInterval_1() throws Exception {
		RdaHost fixture = new RdaHost(1, new RamProvisionerSimple(1.0),
				new BwProvisionerSimple(1.0), new StorageIOProvisionerSimple(
						1.0), 1L, new ArrayList<Pe>(),
				new VmSchedulerTimeShared(new ArrayList<Pe>()), 1.0);
		double scarcitySchedulingInterval = 1.0;

		fixture.setScarcityShedulingInterval(scarcitySchedulingInterval);

		double result = fixture.getScarcityShedulingInterval();

		assertEquals(1.0, result, 0.0);
	}

	/**
	 * Run the double updateVmsProcessing(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:49 AM
	 */
	@Test
	public void testUpdateVmsProcessing_1() throws Exception {

		RdaHost fixture = new RdaHost(1, new RamProvisionerSimple(1000.0),
				new BwProvisionerSimple(1000.0),
				new StorageIOProvisionerSimple(1000.0), 10000L,
				this.getPeList(), createVmScheduler(), 1000.0);
		double currentTime = 0.0;

		RdaVm vm = createRdaVm();

		fixture.vmCreate(vm);

		double result = fixture.updateVmsProcessing(currentTime);

		assertEquals(Double.MAX_VALUE, result, 0.0);
	}

	private VmSchedulerMaxMinFairShare createVmScheduler() {
		VmSchedulerMaxMinFairShare vmScheduler = new VmSchedulerMaxMinFairShare(
				this.getPeList(), new RamProvisionerSimple(500),
				new BwProvisionerSimple(1000), new StorageIOProvisionerSimple(
						1000));
		return vmScheduler;
	}

	/**
	 * Run the boolean vmCreate(Vm) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:49 AM
	 */
	@Test
	public void testVmCreate_1() throws Exception {
		List<Pe> peList = getPeList();

		RdaHost fixture = new RdaHost(1, new RamProvisionerSimple(1000.0),
				new BwProvisionerSimple(1000.0),
				new StorageIOProvisionerSimple(1000.0), 10000L, peList,
				new VmSchedulerTimeShared(peList), 1.0);
		RdaVm vm = createRdaVm();

		boolean result = fixture.vmCreate(vm);

		assertTrue(result);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *             if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 6/3/15 10:49 AM
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
	 * @generatedBy CodePro at 6/3/15 10:49 AM
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
	 * @generatedBy CodePro at 6/3/15 10:49 AM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(RdaHostTest.class);
	}
}