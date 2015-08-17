package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;

import ch.uzh.ifi.csg.cloudsim.rda.experiments.StochasticDataGenerator;

public class Config_18 implements ExperimentConfig {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.experiments.config.ExperimentConfig#getVmConfig
	 * ()
	 */
	public VmConfig getVmConfig() {
		return new VmConfig();
	}
	
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
				// web-server: network intensive workload
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateData(400, 130, 20, 250, 10, 0.5, 10, 0.8, 35);
				inputData.add(workloadData);
			} else {
				// CI workload
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateData(500, 50, 20, 500, 10, 0.5, 10, 0.8, 35);
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
		return "CI CI WS dependent";
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
		int storageIO = 4000;

		return new HostConfig(peCnt, mips, ram, storage, bw, storageIO);
	}
}
