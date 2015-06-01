package ch.uzh.ifi.csg.cloudsim.rda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.cloudbus.cloudsim.Consts;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.uzh.ifi.csg.cloudsim.rda.RdaCloudlet;

/**
 * The class <code>RdaCloudletTest</code> contains tests for the class
 * <code>{@link RdaCloudlet}</code>.
 *
 * @generatedBy CodePro at 5/11/15 10:59 AM
 * @author pat
 * @version $Revision: 1.0 $
 */
public class RdaCloudletTest {
	/**
	 * Run the RdaCloudlet(int,int,long,long,String,boolean) constructor test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testRdaCloudlet_1() throws Exception {
		int cloudletId = 1;
		int pesNumber = 1;
		long cloudletFileSize = 1L;
		long cloudletOutputSize = 1L;
		String inputPath = "src/test/resources/input1.csv";
		boolean record = false;

		RdaCloudlet result = new RdaCloudlet(cloudletId, pesNumber,
				cloudletFileSize, cloudletOutputSize, inputPath, record);
		assertNotNull(result);
	}

	/**
	 * Run the long getCloudletTotalLength() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetCloudletTotalLength_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);

		long result = cloudlet.getCloudletTotalLength();

		assertEquals(210l, result);
	}

	/**
	 * Run the double getEstimatedNextChangeTime() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetEstimatedNextChangeTime_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);
		cloudlet.setInstructionsFinishedSoFar(0);

		double result = cloudlet.getEstimatedNextChangeTime();

		assertEquals(1.0, result, 0);
	}

	@Test
	public void testGetEstimatedNextChangeTime_2() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);

		long inst = 10000000;

		cloudlet.setInstructionsFinishedSoFar(inst);

		double result = cloudlet.getEstimatedNextChangeTime();

		double pastSpeed = 200;
		double totalInstructions = 210000000; // instructions in timeframe

		double a = (cloudlet.getGradOfCpu() / 2.0 * (result * result) + pastSpeed
				* result);
		double instsToProcess = Math.round(a * (double) Consts.MILLION);

		assertEquals(totalInstructions - inst, instsToProcess, 0);
	}

	/**
	 * Run the double getGradOfBw() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetGradOfBw_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);

		double result = cloudlet.getGradOfBw();

		assertEquals(22.0, result, 0.0);
	}

	/**
	 * Run the double getGradOfCpu() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetGradOfCpu_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);

		double result = cloudlet.getGradOfCpu();

		assertEquals(20, result, 0);
	}

	/**
	 * Run the double getGradOfRam() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetGradOfRam_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);

		double result = cloudlet.getGradOfRam();

		assertEquals(9, result, 0);
	}

	/**
	 * Run the double getGradOfStorageIO() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetGradOfStorageIO_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);

		double result = cloudlet.getGradOfStorageIO();

		assertEquals(55, result, 0.0);
	}

	/**
	 * Run the long getInstructionsFinishedSoFar() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetInstructionsFinishedSoFar_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);

		long instructions = 2654l;
		cloudlet.setInstructionsFinishedSoFar(instructions);

		long result = cloudlet.getInstructionsFinishedSoFar();

		assertEquals(instructions, result);
	}

	/**
	 * Run the long getNextUtilizationChange() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetNextUtilizationChange_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);
		cloudlet.setInstructionsFinishedSoFar(10000000);

		long result = cloudlet.getNextUtilizationChange();

		assertEquals(200000000L, result);
	}

	/**
	 * Run the long getNextUtilizationChange() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetNextUtilizationChange_2() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);
		cloudlet.setInstructionsFinishedSoFar(210000000);

		long result = cloudlet.getNextUtilizationChange();

		assertEquals(0L, result);
	}

	/**
	 * Run the long getRemainingCloudletLength() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetRemainingCloudletLength_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);
		cloudlet.setInstructionsFinishedSoFar(1000000);

		long result = cloudlet.getRemainingCloudletLength();

		assertEquals(209000000l, result);
	}

	/**
	 * Run the double getRequestedUtilizationOfBw(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetRequestedUtilizationOfBw_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);
		cloudlet.setInstructionsFinishedSoFar(0L);

		double timeSpan = 0.0;

		double result = cloudlet.getRequestedUtilizationOfBw(timeSpan);

		assertEquals(0, result, 0);
	}

	@Test
	public void testGetRequestedUtilizationOfBw_2() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);
		long currentInst = 1000000L;
		cloudlet.setInstructionsFinishedSoFar(currentInst);

		double timeSpan = 0;

		double result = cloudlet.getRequestedUtilizationOfBw(timeSpan);

		// calculating expected result -->
		double pastSpeed = 0;
		double pastSpeedCpu = 200;

		double d = (pastSpeedCpu * pastSpeedCpu) + 2 * cloudlet.getGradOfCpu()
				* (currentInst / Consts.MILLION);

		double expectedTime = (-pastSpeedCpu + Math.sqrt(d))
				/ cloudlet.getGradOfCpu();

		double pastRequestedSpeed = cloudlet.getGradOfBw() * expectedTime
				+ pastSpeed;

		double currentRequestedSpeed = cloudlet.getGradOfBw() * timeSpan
				+ pastRequestedSpeed;
		// <--

		assertEquals(currentRequestedSpeed, result, 0);
	}

	/**
	 * Run the double getRequestedUtilizationOfCpu(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetRequestedUtilizationOfCpu_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);
		long currentInst = 1000000L;
		cloudlet.setInstructionsFinishedSoFar(currentInst);

		double timeSpan = 0;

		double result = cloudlet.getRequestedUtilizationOfCpu(timeSpan);

		// calculating expected result -->
		double pastSpeedCpu = 200;
		double d = (pastSpeedCpu * pastSpeedCpu) + 2 * cloudlet.getGradOfCpu()
				* (currentInst / Consts.MILLION);

		double expectedTime = (-pastSpeedCpu + Math.sqrt(d))
				/ cloudlet.getGradOfCpu();

		double pastRequestedSpeed = cloudlet.getGradOfCpu() * expectedTime
				+ pastSpeedCpu;

		double currentRequestedSpeed = cloudlet.getGradOfCpu() * timeSpan
				+ pastRequestedSpeed;
		// <--
		assertEquals(currentRequestedSpeed, result, 0);
	}

	/**
	 * Run the double getRequestedUtilizationOfRam(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetRequestedUtilizationOfRam_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);
		long currentInst = 1000000L;
		cloudlet.setInstructionsFinishedSoFar(currentInst);

		double timeSpan = 0;

		double result = cloudlet.getRequestedUtilizationOfRam(timeSpan);

		// calculating expected result -->
		double pastSpeedCpu = 200;
		double d = (pastSpeedCpu * pastSpeedCpu) + 2 * cloudlet.getGradOfCpu()
				* (currentInst / Consts.MILLION);

		double expectedTime = (-pastSpeedCpu + Math.sqrt(d))
				/ cloudlet.getGradOfCpu();

		double pastSpeedRam = 1;
		double pastRequestedSpeed = cloudlet.getGradOfRam() * expectedTime
				+ pastSpeedRam;

		double currentRequestedSpeed = cloudlet.getGradOfRam() * timeSpan
				+ pastRequestedSpeed;
		// <--
		assertEquals(currentRequestedSpeed, result, 0);
	}

	/**
	 * Run the double getRequestedUtilizationOfStorageIO(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetRequestedUtilizationOfStorageIO_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);
		long currentInst = 1000000L;
		cloudlet.setInstructionsFinishedSoFar(currentInst);

		double timeSpan = 0;

		double result = cloudlet.getRequestedUtilizationOfStorageIO(timeSpan);

		// calculating expected result -->
		double pastSpeedCpu = 200;
		double d = (pastSpeedCpu * pastSpeedCpu) + 2 * cloudlet.getGradOfCpu()
				* (currentInst / Consts.MILLION);

		double expectedTime = (-pastSpeedCpu + Math.sqrt(d))
				/ cloudlet.getGradOfCpu();

		double pastSpeedStorageIO = 0;
		double pastRequestedSpeed = cloudlet.getGradOfStorageIO()
				* expectedTime + pastSpeedStorageIO;

		double currentRequestedSpeed = cloudlet.getGradOfStorageIO() * timeSpan
				+ pastRequestedSpeed;
		// <--
		assertEquals(currentRequestedSpeed, result, 0);
	}

	/**
	 * Run the double getUtilizationOfBw(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetUtilizationOfBw_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);
		long currentInst = 1000000L;
		cloudlet.setInstructionsFinishedSoFar(currentInst);

		double timeSpan = 0;

		double result = cloudlet.getRequestedUtilizationOfBw(timeSpan);

		// calculating expected result -->
		double pastSpeedCpu = 200;
		double d = (pastSpeedCpu * pastSpeedCpu) + 2 * cloudlet.getGradOfCpu()
				* (currentInst / Consts.MILLION);

		double expectedTime = (-pastSpeedCpu + Math.sqrt(d))
				/ cloudlet.getGradOfCpu();

		double pastSpeedBw = 0;
		double pastRequestedSpeed = cloudlet.getGradOfBw() * expectedTime
				+ pastSpeedBw;

		double currentRequestedSpeed = cloudlet.getGradOfBw() * timeSpan
				+ pastRequestedSpeed;
		// <--
		assertEquals(currentRequestedSpeed, result, 0);
	}

	/**
	 * Run the double getUtilizationOfCpu(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetUtilizationOfCpu_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);
		cloudlet.setUtilizationOfCpu(1.0);

		double time = 1.0;

		double result = cloudlet.getUtilizationOfCpu(time);

		assertEquals(1.0, result, 0);
	}

	/**
	 * Run the double getUtilizationOfRam(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testGetUtilizationOfRam_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);
		cloudlet.setUtilizationOfRam(1.0);
		double time = 1.0;

		double result = cloudlet.getUtilizationOfRam(time);

		assertEquals(1.0, result, 0);
	}

	/**
	 * Run the void setUtilizationOfBandwidth(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testSetUtilizationOfBandwidth_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);
		cloudlet.setUtilizationOfBandwidth(1.0);

		double value = 1.0;

		cloudlet.setUtilizationOfBandwidth(value);

		assertEquals(1.0, cloudlet.getUtilizationOfBw(0.0), 0);

	}

	/**
	 * Run the void setUtilizationOfCpu(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testSetUtilizationOfCpu_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);

		cloudlet.setUtilizationOfCpu(1.0);

		double value = 1.0;

		cloudlet.setUtilizationOfCpu(value);
		assertEquals(1.0, cloudlet.getUtilizationOfCpu(0.0), 0);

	}

	/**
	 * Run the void setUtilizationOfRam(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testSetUtilizationOfRam_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);
		cloudlet.setUtilizationOfRam(1.0);

		double value = 1.0;

		cloudlet.setUtilizationOfRam(value);

		assertEquals(1.0, cloudlet.getUtilizationOfRam(0.0), 0);

	}

	/**
	 * Run the void setUtilizationOfStorage(double) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testSetUtilizationOfStorage_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);

		cloudlet.setUtilizationOfStorage(1.0);

		double value = 1.0;

		cloudlet.setUtilizationOfStorage(value);

		assertEquals(1.0, cloudlet.getUtilizationOfStorage(), 0);

	}

	/**
	 * Run the void stopRecording() method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testStopRecording_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);

		cloudlet.stopRecording();

	}

	/**
	 * Run the void updateInstructionsFinishedSoFar(long) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	@Test
	public void testUpdateInstructionsFinishedSoFar_1() throws Exception {
		RdaCloudlet cloudlet = new RdaCloudlet(1, 1, 1L, 1L, "src/test/resources/input1.csv",
				false);
		cloudlet.setInstructionsFinishedSoFar(1L);

		long instructionsFinishedSoFar = 2L;

		cloudlet.updateInstructionsFinishedSoFar(instructionsFinishedSoFar);

		assertEquals(3, cloudlet.getInstructionsFinishedSoFar(), 0);

	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *             if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 5/11/15 10:59 AM
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
	 * @generatedBy CodePro at 5/11/15 10:59 AM
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
	 * @generatedBy CodePro at 5/11/15 10:59 AM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(RdaCloudletTest.class);
	}
}