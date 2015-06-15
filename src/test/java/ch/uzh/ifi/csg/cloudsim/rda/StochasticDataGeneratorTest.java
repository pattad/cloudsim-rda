package ch.uzh.ifi.csg.cloudsim.rda;

import static org.junit.Assert.assertNotNull;

import org.cloudbus.cloudsim.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.uzh.ifi.csg.cloudsim.rda.data.StochasticDataGenerator;

/**
 * The class <code>StochasticDataGeneratorTest</code> contains tests for the
 * class <code>{@link StochasticDataGenerator}</code>.
 *
 * @generatedBy CodePro at 6/15/15 5:23 PM
 * @author pat
 * @version $Revision: 1.0 $
 */
public class StochasticDataGeneratorTest {
	/**
	 * Run the StochasticDataGenerator() constructor test.
	 *
	 * @generatedBy CodePro at 6/15/15 5:23 PM
	 */
	@Test
	public void testStochasticDataGenerator_1() throws Exception {
		StochasticDataGenerator result = new StochasticDataGenerator();
		assertNotNull(result);
		
		assertNotNull(result.generateWebServerData());
		
	}

	/**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *             if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 6/15/15 5:23 PM
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
	 * @generatedBy CodePro at 6/15/15 5:23 PM
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
	 * @generatedBy CodePro at 6/15/15 5:23 PM
	 */
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(StochasticDataGeneratorTest.class);
	}
}