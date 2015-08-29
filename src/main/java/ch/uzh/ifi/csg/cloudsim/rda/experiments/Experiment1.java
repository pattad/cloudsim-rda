package ch.uzh.ifi.csg.cloudsim.rda.experiments;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.cloudbus.cloudsim.Cloudlet;

import ch.uzh.ifi.csg.cloudsim.rda.RdaCloudlet;

/**
 * A sample experiment.
 * 
 * @author pat
 *
 */
public class Experiment1 extends ExperimentalSuite {

	/**
	 * Main method to run this experiment
	 *
	 * @param args
	 *            the args
	 */
	public static void main(String[] args) {

		Experiment1 suite = new Experiment1();
		// VMs and Hosts to create
		suite.simulate(1, 3, 3);
	}

	@Override
	public ArrayList<Cloudlet> createCloudlets(int brokerId, int vmCnt)
			throws FileNotFoundException, UnsupportedEncodingException {
		ArrayList<Cloudlet> cloudletList = new ArrayList<Cloudlet>();

		StochasticDataGenerator randomData = new StochasticDataGenerator(120);
		// Cloudlet properties
		long fileSize = 300;
		long outputSize = 300;
		int pesNumber = 1;

		int vmId = 0;
		int cloudletId = 0;

		while (vmId < vmCnt) {

			// ArrayList<double[]> workloadData = randomData.generateData(350,
			// 100, 40,
			// 250, 10, 0.5, 10, 0.5, 75);
			ArrayList<double[]> workloadData = randomData.generateWaveingData(
					224, 36, 72, 3, 11, 1.5);

			RdaCloudlet cloudlet = new RdaCloudlet(cloudletId++, pesNumber,
					fileSize, outputSize, workloadData, this.isRecord());
			cloudlet.setUserId(brokerId);
			cloudlet.setVmId(vmId++);
			cloudletList.add(cloudlet);
		}

		return cloudletList;
	}
}
