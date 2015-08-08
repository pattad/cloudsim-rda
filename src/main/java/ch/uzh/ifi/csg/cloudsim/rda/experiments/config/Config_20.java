package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;

import ch.uzh.ifi.csg.cloudsim.rda.experiments.StochasticDataGenerator;

public class Config_20 implements ExperimentConfig {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.uzh.ifi.csg.cloudsim.rda.experiments.config.ExperimentConfig#
	 * generateWorkload(int, int)
	 */
	public ArrayList<ArrayList<double[]>> generateWorkload(int vmCnt,
			int workloadLength) {
		ArrayList<ArrayList<double[]>> inputData = new ArrayList<ArrayList<double[]>>();
		StochasticDataGenerator randomDataGenerator = new StochasticDataGenerator(
				workloadLength);

		for (int i = 0; i < vmCnt; i++) {

			if (i % 4 == 0) {
				// computing intensive workload, lot's of memory and cpu and
				// network
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateWaveingData(600, 20, 500, 10, 0.75, 0.1);
				inputData.add(workloadData);
			} else if (i % 4 == 1) {
				// web-server: network intensive workload
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateWaveingData(400, 10, 250, 10, 1, 0.1);
				inputData.add(workloadData);
			} else {
				// database workload
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateWaveingData(200, 10, 1000, 10, 0.1, 7);
				inputData.add(workloadData);
			}
		}
		return inputData;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.uzh.ifi.csg.cloudsim.rda.experiments.config.ExperimentConfig#
	 * getDescription()
	 */
	public String getDescription() {
		return "CI WS DB DB";
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.experiments.config.ExperimentConfig#getHostConfig
	 * ()
	 */
	public HostConfig getHostConfig() {
		int mips = 2000;
		int peCnt = 1;
		
		int ram = 4096; // host memory (MB)
		long storage = 1000000; // host storage (MB)
		int bw = 1000; // MBit/s
		int storageIO = 4000;

		return new HostConfig(peCnt, mips, ram, storage, bw, storageIO);
	}
}
