package ch.uzh.ifi.csg.cloudsim.rda.experiments;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;

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

		Log.setDisabled(false);
		
		Experiment1 suite = new Experiment1();
		// VMs and Hosts to create
		suite.simulate(9, 3);
	}

	@Override
	public ArrayList<Cloudlet> createCloudlets(int brokerId)
			throws FileNotFoundException, UnsupportedEncodingException {

		ArrayList<Cloudlet> cloudletList = new ArrayList<Cloudlet>();

		StochasticDataGenerator randomData = new StochasticDataGenerator(60);

		// Cloudlet properties
		long fileSize = 350;
		long outputSize = 350;
		int pesNumber = 1;
		int vmId = 0;
		int cloudletId = 0;
		
		RdaCloudlet cloudlet = new RdaCloudlet(cloudletId++, pesNumber,
				fileSize, outputSize, randomData.generateWebServerDataStepped(
						350, 10.85, 0, 10), record);
		cloudlet.setUserId(brokerId);
		cloudlet.setVmId(vmId++);
		cloudletList.add(cloudlet);

		cloudlet = new RdaCloudlet(cloudletId++, pesNumber, fileSize,
				outputSize, randomData.generateWebServerDataStepped(350,
						10.85, 0, 10), record);
		cloudlet.setUserId(brokerId);
		cloudlet.setVmId(vmId++);
		cloudletList.add(cloudlet);

		cloudlet = new RdaCloudlet(cloudletId++, pesNumber, fileSize,
				outputSize, randomData.generateWebServerDataStepped(350,
						10.85, 0, 10), record);
		cloudlet.setUserId(brokerId);
		cloudlet.setVmId(vmId++);
		cloudletList.add(cloudlet);

		cloudlet = new RdaCloudlet(cloudletId++, pesNumber, fileSize,
				outputSize, randomData.generateWebServerDataStepped(350,
						10.85, 0, 10), record);
		cloudlet.setUserId(brokerId);
		cloudlet.setVmId(vmId++);
		cloudletList.add(cloudlet);

		cloudlet = new RdaCloudlet(cloudletId++, pesNumber, fileSize,
				outputSize, randomData.generateWebServerDataStepped(350,
						10.85, 0, 10), record);
		cloudlet.setUserId(brokerId);
		cloudlet.setVmId(vmId++);
		cloudletList.add(cloudlet);

		cloudlet = new RdaCloudlet(cloudletId++, pesNumber, fileSize,
				outputSize, randomData.generateWebServerDataStepped(350,
						10.85, 0, 10), record);
		cloudlet.setUserId(brokerId);
		cloudlet.setVmId(vmId++);
		cloudletList.add(cloudlet);

		cloudlet = new RdaCloudlet(cloudletId++, pesNumber, fileSize,
				outputSize, randomData.generateWebServerDataStepped(350,
						10.85, 0, 10), record);
		cloudlet.setUserId(brokerId);
		cloudlet.setVmId(vmId++);
		cloudletList.add(cloudlet);

		cloudlet = new RdaCloudlet(cloudletId++, pesNumber, fileSize,
				outputSize, randomData.generateWebServerDataStepped(350,
						10.85, 0, 10), record);
		cloudlet.setUserId(brokerId);
		cloudlet.setVmId(vmId++);
		cloudletList.add(cloudlet);

		cloudlet = new RdaCloudlet(cloudletId++, pesNumber, fileSize,
				outputSize, randomData.generateWebServerDataStepped(350,
						10.85, 0, 10), record);
		cloudlet.setUserId(brokerId);
		cloudlet.setVmId(vmId++);
		cloudletList.add(cloudlet);

		return cloudletList;
	}
}
