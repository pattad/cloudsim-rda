package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;

import ch.uzh.ifi.csg.cloudsim.rda.experiments.StochasticDataGenerator;

public class Config_9 implements ExperimentConfig {

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

			if (i % 3 == 0) {
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateData(700, 500, 300, 150, 10, 0.0, 10, 0.0, 75);
				inputData.add(workloadData);
			} else if (i % 3 == 1) {
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateData(150, 700, 500, 300, 10, 0.0, 10, 0.0, 75);
				inputData.add(workloadData);
			} else {
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateData(300, 150, 700, 500, 10, 0.0, 10, 0.0, 75);
				inputData.add(workloadData);
			}
		}
		return inputData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.experiments.config.ExperimentConfig#getHostConfig
	 * ()
	 */
	public HostConfig getHostConfig() {

		int mips = 1000;
		int peCnt = 1;

		int ram = 2048; // host memory (MB)
		long storage = 1000000; // host storage (MB)
		int bw = 1000; // MBit/s
		int storageIO = 10000;

		return new HostConfig(peCnt, mips, ram, storage, bw, storageIO);
	}
}
