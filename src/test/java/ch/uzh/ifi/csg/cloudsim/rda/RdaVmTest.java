package ch.uzh.ifi.csg.cloudsim.rda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The class <code>RdaVmTest</code> contains tests for the class
 * <code>{@link RdaVm}</code>.
 *
 * @generatedBy CodePro at 6/3/15 10:50 AM
 * @author pat
 * @version $Revision: 1.0 $
 */
public class RdaVmTest {
	/**
	 * Run the
	 * RdaVm(int,int,double,int,int,long,long,int,String,CloudletScheduler
	 * ,double) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testRdaVm_1() throws Exception {
		int id = 1;
		int userId = 1;
		double mips = 1.0;
		int pesNumber = 1;
		int ram = 1;
		long bw = 1L;
		long size = 1L;
		int priority = 1;
		String vmm = "";
		CloudletScheduler cloudletScheduler = new CloudletSchedulerTimeShared();
		double schedulingInterval = 1.0;

		RdaVm result = new RdaVm(id, userId, mips, pesNumber, ram, bw, size,
				priority, vmm, cloudletScheduler, schedulingInterval);

		// add additional test code here
		assertNotNull(result);
		assertEquals(null, result.getCustomer());
		assertEquals(0.0, result.getCurrentAllocatedRamFine(), 0);
		assertEquals(0.0, result.getCurrentAllocatedBwFine(), 0);
		assertEquals(1, result.getCurrentRequestedRam());
		assertEquals(1L, result.getCurrentRequestedBw());
		assertEquals(0.0, result.getCurrentAllocatedStorageIO(), 0);
		assertEquals(0, result.getCurrentAllocatedRam());
		assertEquals(0L, result.getCurrentAllocatedBw());
		assertEquals(0.0, result.getPreviousTime(), 0);
		assertEquals(0.0, result.getUtilizationMean(), 0);
		assertEquals(0.0, result.getUtilizationMad(), 0);
		assertEquals(0.0, result.getUtilizationVariance(), 0);
		assertEquals(1.0, result.getSchedulingInterval(), 0);
		assertEquals(1, result.getId());
		assertEquals(1L, result.getSize());
		assertEquals(null, result.getHost());
		assertEquals(1, result.getUserId());
		assertEquals(1.0, result.getMips(), 0);
		assertEquals(1, result.getNumberOfPes());
		assertEquals(1, result.getRam());
		assertEquals("1-1", result.getUid());
		assertEquals("", result.getVmm());
		assertEquals(1L, result.getBw());
		assertEquals(false, result.isInMigration());
		assertEquals(1.0, result.getCurrentRequestedMaxMips(), 0);
		assertEquals(1.0, result.getCurrentRequestedTotalMips(), 0);
		assertEquals(null, result.getCurrentAllocatedMips());
		assertEquals(true, result.isBeingInstantiated());
		assertEquals(0L, result.getCurrentAllocatedSize());
	}

	/**
	 * Run the long getCurrentAllocatedBw() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testGetCurrentAllocatedBw_1() throws Exception {
		RdaVm fixture = createRdaVm();
		fixture.setCurrentAllocatedBw(1.0);
		double result = fixture.getCurrentAllocatedBw();

		assertEquals(1.0, result);
	}

	/**
	 * Run the double getCurrentAllocatedBwFine() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testGetCurrentAllocatedBwFine_1() throws Exception {
		RdaVm fixture = createRdaVm();
		fixture.setCurrentAllocatedBw(1.0);
		double result = fixture.getCurrentAllocatedBwFine();

		assertEquals(1.0, result, 0);
	}

	/**
	 * Run the double getCurrentAllocatedRamFine() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testGetCurrentAllocatedRamFine_1() throws Exception {
		RdaVm fixture = createRdaVm();
		fixture.setCurrentAllocatedRam(1.0d);
		double result = fixture.getCurrentAllocatedRamFine();
		assertEquals(1.0, result, 0);
	}

	/**
	 * Run the double getCurrentRequestedGradBw() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testGetCurrentRequestedGradBw_1() throws Exception {
		RdaVm fixture = createRdaVm();

		double result = fixture.getCurrentRequestedGradBw();

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getCurrentRequestedGradCpu() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testGetCurrentRequestedGradCpu_1() throws Exception {
		RdaVm fixture = createRdaVm();

		double result = fixture.getCurrentRequestedGradCpu();
		assertEquals(0.0, result, 0);
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
	 * Run the double getCurrentRequestedGradStorageIO() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testGetCurrentRequestedGradStorageIO_1() throws Exception {
		RdaVm fixture = createRdaVm();

		double result = fixture.getCurrentRequestedGradStorageIO();
		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the List<Double> getCurrentRequestedMips(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testGetCurrentRequestedMips_1() throws Exception {
		RdaVm fixture = createRdaVm();
		double currentTime = 1.0;

		List<Double> result = fixture.getCurrentRequestedMips(currentTime);
		assertNotNull(result);
	}

	/**
	 * Run the List<Double> getCurrentRequestedMips(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testGetCurrentRequestedMips_2() throws Exception {
		RdaVm fixture = createRdaVm();
		double currentTime = 1.0;

		List<Double> result = fixture.getCurrentRequestedMips(currentTime);
		assertNotNull(result);
	}

	/**
	 * Run the List<Double> getCurrentRequestedMips(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testGetCurrentRequestedMips_3() throws Exception {
		RdaVm fixture = createRdaVm();
		double currentTime = 1.0;

		List<Double> result = fixture.getCurrentRequestedMips(currentTime);
		assertNotNull(result);
	}

	/**
	 * Run the int getCurrentRequestedRam() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testGetCurrentRequestedRam_1() throws Exception {
		RdaVm fixture = createRdaVm();

		int result = fixture.getCurrentRequestedRam();
		assertEquals(512, result);
	}

	/**
	 * Run the double getCurrentRequestedStorageIO(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testGetCurrentRequestedStorageIO_1() throws Exception {
		RdaVm fixture = createRdaVm();
		double currentTime = 1.0;

		double result = fixture.getCurrentRequestedStorageIO(currentTime);

		assertEquals(100.0, result, 0.0);
	}

	/**
	 * Run the double getCurrentRequestedTotalMips(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testGetCurrentRequestedTotalMips_1() throws Exception {
		RdaVm fixture = createRdaVm();
		double currentTime = 1.0;

		double result = fixture.getCurrentRequestedTotalMips(currentTime);

		assertEquals(800, result, 0.0);
	}

	/**
	 * Run the String getCustomer() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testGetCustomer_1() throws Exception {
		RdaVm fixture = createRdaVm();
		fixture.setCustomer("Cust_1");
		String result = fixture.getCustomer();
		assertEquals("Cust_1", result);
	}

	/**
	 * Run the void setCurrentAllocatedBw(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testSetCurrentAllocatedBw_1() throws Exception {
		RdaVm fixture = createRdaVm();
		double currentAllocatedBw = 1.0;

		fixture.setCurrentAllocatedBw(currentAllocatedBw);

		double result = fixture.getCurrentAllocatedBw();

		assertEquals(currentAllocatedBw, result);
	}

	/**
	 * Run the void setCurrentAllocatedRam(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testSetCurrentAllocatedRam_1() throws Exception {
		RdaVm fixture = createRdaVm();
		double currentAllocatedRam = 1.0;

		fixture.setCurrentAllocatedRam(currentAllocatedRam);

		int result = fixture.getCurrentAllocatedRam();

		assertEquals(1, result);
	}

	/**
	 * Run the void setCurrentAllocatedStorageIO(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testSetCurrentAllocatedStorageIO_1() throws Exception {
		RdaVm fixture = createRdaVm();
		double currentAllocatedStorageIO = 1.0;

		fixture.setCurrentAllocatedStorageIO(currentAllocatedStorageIO);

		double result = fixture.getCurrentAllocatedStorageIO();

		assertEquals(currentAllocatedStorageIO, result);
	}

	/**
	 * Run the void setCustomer(String) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testSetCustomer_1() throws Exception {
		RdaVm fixture = createRdaVm();
		String customer = "";

		fixture.setCustomer(customer);

		assertEquals(fixture.getCustomer(), customer);
	}

	/**
	 * Run the double updateVmProcessing(double,List<Double>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test(expected = java.lang.UnsupportedOperationException.class)
	public void testUpdateVmProcessing_1() throws Exception {
		RdaVm fixture = createRdaVm();

		double currentTime = 1.0;
		List<Double> mipsShare = new ArrayList<Double>();

		double result = fixture.updateVmProcessing(currentTime, mipsShare);

	}

	/**
	 * Run the double updateVmProcessing(double,List<Double>,double,double)
	 * method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	@Test
	public void testUpdateVmProcessing_2() throws Exception {
		RdaVm fixture = createRdaVm();

		double currentTime = 1.0;
		List<Double> mipsShare = null;
		double bwShare = 1.0;
		double storageShare = 1.0;

		double result = fixture.updateVmProcessing(currentTime, mipsShare,
				bwShare, storageShare);

		assertEquals(0.0, result, 0.0);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *             if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 6/3/15 10:50 AM
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
	 * @generatedBy CodePro at 6/3/15 10:50 AM
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
	 * @generatedBy CodePro at 6/3/15 10:50 AM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(RdaVmTest.class);
	}
}