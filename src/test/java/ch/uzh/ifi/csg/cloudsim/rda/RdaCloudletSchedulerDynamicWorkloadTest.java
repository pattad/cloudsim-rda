package ch.uzh.ifi.csg.cloudsim.rda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.ResCloudlet;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The class <code>RdaCloudletSchedulerDynamicWorkloadTest</code> contains tests
 * for the class <code>{@link RdaCloudletSchedulerDynamicWorkload}</code>.
 *
 * @generatedBy CodePro at 6/1/15 9:28 AM
 * @author pat
 * @version $Revision: 1.0 $
 */
public class RdaCloudletSchedulerDynamicWorkloadTest {
	/**
	 * Run the RdaCloudletSchedulerDynamicWorkload(double,int,double)
	 * constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testRdaCloudletSchedulerDynamicWorkload_1() throws Exception {
		double mips = 1.0;
		int numberOfPes = 1;
		double scarcitySchedulingInterval = 1.0;

		RdaCloudletSchedulerDynamicWorkload result = new RdaCloudletSchedulerDynamicWorkload(
				mips, numberOfPes, scarcitySchedulingInterval);

		assertNotNull(result);
		assertEquals(0.0, result.getCurrentRequestedGradStorageIO(), 1.0);
		assertEquals(0.0, result.getCurrentRequestedGradCpu(), 1.0);
		assertEquals(0.0, result.getCurrentRequestedGradBw(), 1.0);
		assertEquals(1, result.getNumberOfPes());
		assertEquals(1.0, result.getMips(), 1.0);
		assertEquals(1.0, result.getTotalMips(), 1.0);
		assertEquals(null, result.getNextFinishedCloudlet());
		assertEquals(false, result.isFinishedCloudlets());
		assertEquals(0, result.runningCloudlets());
		assertEquals(null, result.getCurrentMipsShare());
		assertEquals(0.0, result.getPreviousTime(), 1.0);
	}

	/**
	 * Run the void cloudletFinish(ResCloudlet) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testCloudletFinish_1() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		ResCloudlet rcl = new ResCloudlet(new Cloudlet(1, 1L, 1, 1L, 1L,
				new UtilizationModelFull(), new UtilizationModelFull(),
				new UtilizationModelFull()));

		fixture.cloudletFinish(rcl);
		assertEquals(Cloudlet.SUCCESS, rcl.getCloudletStatus(), 0);
	}

	/**
	 * Run the double cloudletSubmit(Cloudlet,double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testCloudletSubmit_2() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		RdaCloudlet cl = new RdaCloudlet(1, 1, 1L, 1L,
				"src/test/resources/input1.csv", false);
		double fileTransferTime = 1.0;

		double result = fixture.cloudletSubmit(cl, fileTransferTime);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double cloudletSubmit(Cloudlet,double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testCloudletSubmit_3() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		RdaCloudlet cl = new RdaCloudlet(1, 1, 1L, 1L,
				"src/test/resources/input1.csv", false);
		double fileTransferTime = 1.0;

		double result = fixture.cloudletSubmit(cl, fileTransferTime);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getCurrentRequestedGradBw() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetCurrentRequestedGradBw_1() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double result = fixture.getCurrentRequestedGradBw();

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getCurrentRequestedGradBw() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetCurrentRequestedGradBw_2() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double result = fixture.getCurrentRequestedGradBw();

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getCurrentRequestedGradCpu() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetCurrentRequestedGradCpu_1() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double result = fixture.getCurrentRequestedGradCpu();

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getCurrentRequestedGradCpu() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetCurrentRequestedGradCpu_2() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double result = fixture.getCurrentRequestedGradCpu();

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getCurrentRequestedGradStorageIO() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetCurrentRequestedGradStorageIO_1() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double result = fixture.getCurrentRequestedGradStorageIO();

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getCurrentRequestedGradStorageIO() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetCurrentRequestedGradStorageIO_2() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double result = fixture.getCurrentRequestedGradStorageIO();

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the List<Double> getCurrentRequestedMips() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetCurrentRequestedMips_1() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		List<Double> result = fixture.getCurrentRequestedMips();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.contains(new Double(0.0)));
	}

	/**
	 * Run the List<Double> getCurrentRequestedMips(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetCurrentRequestedMips_2() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double currentTime = 1.0;

		List<Double> result = fixture.getCurrentRequestedMips(currentTime);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertTrue(result.contains(new Double(0.0)));
	}

	/**
	 * Run the List<Double> getCurrentRequestedMips(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetCurrentRequestedMips_3() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		fixture.setNumberOfPes(0);

		double currentTime = 1.0;

		List<Double> result = fixture.getCurrentRequestedMips(currentTime);

		assertNotNull(result);
		assertEquals(0, result.size());
	}

	/**
	 * Run the double getCurrentRequestedUtilizationOfBw() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test(expected = java.lang.UnsupportedOperationException.class)
	public void testGetCurrentRequestedUtilizationOfBw_1() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double result = fixture.getCurrentRequestedUtilizationOfBw();

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getCurrentRequestedUtilizationOfBw(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetCurrentRequestedUtilizationOfBw_2() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double currentTime = 1.0;

		double result = fixture.getCurrentRequestedUtilizationOfBw(currentTime);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getCurrentRequestedUtilizationOfBw(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetCurrentRequestedUtilizationOfBw_3() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double currentTime = 1.0;

		double result = fixture.getCurrentRequestedUtilizationOfBw(currentTime);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getCurrentRequestedUtilizationOfRam() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test(expected = java.lang.UnsupportedOperationException.class)
	public void testGetCurrentRequestedUtilizationOfRam_1() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double result = fixture.getCurrentRequestedUtilizationOfRam();

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getCurrentRequestedUtilizationOfRam(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetCurrentRequestedUtilizationOfRam_2() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double currentTime = 1.0;

		double result = fixture
				.getCurrentRequestedUtilizationOfRam(currentTime);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getCurrentRequestedUtilizationOfRam(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetCurrentRequestedUtilizationOfRam_3() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double currentTime = 1.0;

		double result = fixture
				.getCurrentRequestedUtilizationOfRam(currentTime);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getCurrentRequestedUtilizationOfStorageIO(double) method
	 * test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetCurrentRequestedUtilizationOfStorageIO_1()
			throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double currentTime = 1.0;

		double result = fixture
				.getCurrentRequestedUtilizationOfStorageIO(currentTime);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getCurrentRequestedUtilizationOfStorageIO(double) method
	 * test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetCurrentRequestedUtilizationOfStorageIO_2()
			throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double currentTime = 1.0;

		double result = fixture
				.getCurrentRequestedUtilizationOfStorageIO(currentTime);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getEstimatedFinishTime(ResCloudlet,double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetEstimatedFinishTime_1() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		RdaCloudlet cl = new RdaCloudlet(1, 1, 1L, 1L,
				"src/test/resources/input1.csv", false);
		ResCloudlet rcl = new ResCloudlet(cl);
		double time = 1.0;

		double result = fixture.getEstimatedFinishTime(rcl, time);

		assertEquals(1.0, result, 0.0);
	}

	/**
	 * Run the double getMips() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetMips_1() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double result = fixture.getMips();

		assertEquals(1.0, result, 0);
	}

	/**
	 * Run the int getNumberOfPes() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetNumberOfPes_1() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		int result = fixture.getNumberOfPes();

		assertEquals(1, result);
	}

	/**
	 * Run the double
	 * getTotalCurrentAllocatedMipsForCloudlet(ResCloudlet,double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetTotalCurrentAllocatedMipsForCloudlet_1()
			throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);
		ResCloudlet rcl = new ResCloudlet(new RdaCloudlet(1, 1, 1L, 1L,
				"src/test/resources/input1.csv", true));
		double time = 1.0;

		double result = fixture.getTotalCurrentAllocatedMipsForCloudlet(rcl,
				time);

		assertEquals(200.0, result, 0);
	}

	/**
	 * Run the double
	 * getTotalCurrentAvailableMipsForCloudlet(ResCloudlet,List<Double>) method
	 * test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetTotalCurrentAvailableMipsForCloudlet_1()
			throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		ResCloudlet rcl = new ResCloudlet(new Cloudlet(1, 1L, 1, 1L, 1L,
				new UtilizationModelFull(), new UtilizationModelFull(),
				new UtilizationModelFull()));
		List<Double> mipsShare = new ArrayList<Double>();

		double result = fixture.getTotalCurrentAvailableMipsForCloudlet(rcl,
				mipsShare);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double
	 * getTotalCurrentAvailableMipsForCloudlet(ResCloudlet,List<Double>) method
	 * test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetTotalCurrentAvailableMipsForCloudlet_2()
			throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		ResCloudlet rcl = new ResCloudlet(new Cloudlet(1, 1L, 1, 1L, 1L,
				new UtilizationModelFull(), new UtilizationModelFull(),
				new UtilizationModelFull()));
		List<Double> mipsShare = new ArrayList<Double>();

		double result = fixture.getTotalCurrentAvailableMipsForCloudlet(rcl,
				mipsShare);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double
	 * getTotalCurrentAvailableMipsForCloudlet(ResCloudlet,List<Double>) method
	 * test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetTotalCurrentAvailableMipsForCloudlet_3()
			throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		ResCloudlet rcl = new ResCloudlet(new Cloudlet(1, 1L, 1, 1L, 1L,
				new UtilizationModelFull(), new UtilizationModelFull(),
				new UtilizationModelFull()));
		List<Double> mipsShare = new ArrayList<Double>();

		double result = fixture.getTotalCurrentAvailableMipsForCloudlet(rcl,
				mipsShare);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double
	 * getTotalCurrentAvailableMipsForCloudlet(ResCloudlet,List<Double>) method
	 * test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetTotalCurrentAvailableMipsForCloudlet_4()
			throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		ResCloudlet rcl = new ResCloudlet(new Cloudlet(1, 1L, 1, 1L, 1L,
				new UtilizationModelFull(), new UtilizationModelFull(),
				new UtilizationModelFull()));
		List<Double> mipsShare = null;

		double result = fixture.getTotalCurrentAvailableMipsForCloudlet(rcl,
				mipsShare);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double
	 * getTotalCurrentRequestedMipsForCloudlet(ResCloudlet,double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test(expected = java.lang.UnsupportedOperationException.class)
	public void testGetTotalCurrentRequestedMipsForCloudlet_1()
			throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		ResCloudlet rcl = new ResCloudlet(new Cloudlet(1, 1L, 1, 1L, 1L,
				new UtilizationModelFull(), new UtilizationModelFull(),
				new UtilizationModelFull()));
		double time = 1.0;

		double result = fixture.getTotalCurrentRequestedMipsForCloudlet(rcl,
				time);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getTotalUtilizationOfCpu(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetTotalUtilizationOfCpu_1() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double currentTime = 1.0;

		double result = fixture.getTotalUtilizationOfCpu(currentTime);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double getTotalUtilizationOfCpu(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testGetTotalUtilizationOfCpu_2() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double currentTime = 1.0;

		double result = fixture.getTotalUtilizationOfCpu(currentTime);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the void setMips(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testSetMips_1() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double mips = 1.0;

		fixture.setMips(mips);

		assertEquals(mips, fixture.getMips(), 0);

	}

	/**
	 * Run the void setNumberOfPes(int) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testSetNumberOfPes_1() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		int pesNumber = 1;

		fixture.setNumberOfPes(pesNumber);

		assertEquals(pesNumber, fixture.getNumberOfPes(), 0);
	}

	/**
	 * Run the void setTotalMips(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testSetTotalMips_1() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double mips = 1.0;

		fixture.setTotalMips(mips);

		assertEquals(mips, fixture.getTotalMips(), 0);
	}

	/**
	 * Run the double updateVmProcessing(double,List<Double>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test(expected = java.lang.UnsupportedOperationException.class)
	public void testUpdateVmProcessing_1() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);

		double currentTime = 1.0;
		List<Double> mipsShare = new ArrayList<Double>();

		double result = fixture.updateVmProcessing(currentTime, mipsShare);

		assertEquals(1.0, fixture.getTotalMips(), 0);

		assertEquals(0.0, result, 0);
	}

	/**
	 * Run the double updateVmProcessing(double,List<Double>) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	@Test
	public void testUpdateVmProcessing_2() throws Exception {
		RdaCloudletSchedulerDynamicWorkload fixture = new RdaCloudletSchedulerDynamicWorkload(
				1.0, 1, 1.0);
		double currentTime = 1.0;
		List<Double> mipsShare = new ArrayList<Double>();

		double result = fixture
				.updateVmProcessing(currentTime, mipsShare, 0, 0);

		assertEquals(1.0, fixture.getTotalMips(), 0);
		assertEquals(0.0, result, 0);
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *             if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 6/1/15 9:28 AM
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
	 * @generatedBy CodePro at 6/1/15 9:28 AM
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
	 * @generatedBy CodePro at 6/1/15 9:28 AM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore()
				.run(RdaCloudletSchedulerDynamicWorkloadTest.class);
	}
}