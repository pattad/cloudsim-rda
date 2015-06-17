package ch.uzh.ifi.csg.cloudsim.rda.experiments;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.cloudbus.cloudsim.Cloudlet;

import ch.uzh.ifi.csg.cloudsim.rda.RdaCloudlet;
import ch.uzh.ifi.csg.cloudsim.rda.data.StochasticDataGenerator;

/**
 * 
 * @author pat
 *
 */
public class Experiment1 extends ExperimentalSuite {

	/** Record output to a CSV file */
	private static boolean record = true;

	/**
	 * Main method to run this experiment
	 *
	 * @param args
	 *            the args
	 */
	public static void main(String[] args) {

		Experiment1 suite = new Experiment1();
		// VMs and Hosts to create
		suite.simulate(2, 5);
	}

	@Override
	public ArrayList<Cloudlet> createCloudlets(int brokerId)
			throws FileNotFoundException, UnsupportedEncodingException {
		
		ArrayList<Cloudlet> cloudletList = new ArrayList<Cloudlet>();

		StochasticDataGenerator randomData = new StochasticDataGenerator(120);
	
		// Cloudlet properties
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;

		RdaCloudlet cloudlet = new RdaCloudlet(0, pesNumber, fileSize, outputSize,
				randomData.generateWebServerData(235.6, 10.85), record);
		cloudlet.setUserId(brokerId);
		cloudlet.setVmId(0);

		cloudletList.add(cloudlet);
		
		
		return cloudletList;
	}
}
