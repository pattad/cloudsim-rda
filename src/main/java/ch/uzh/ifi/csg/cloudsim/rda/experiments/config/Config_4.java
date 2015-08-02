package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;

import ch.uzh.ifi.csg.cloudsim.rda.experiments.StochasticDataGenerator;

public class Config_4 implements ExperimentConfig {

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
				// batch: computing intensive workload, lot's of memory and cpu
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateData(400, 50, 20, 500, 10, 0.8, 10, 0.8, 25);
				inputData.add(workloadData);
			} else if (i % 3 == 2) {
				// web-server: network intensive workload
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateData(200, 200, 20, 250, 10, 0.8, 10, 0.8, 25);
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
		return "CI WS DS";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.experiments.config.ExperimentConfig#getHostConfig
	 * ()
	 */
	public HostConfig getHostConfig() {
		return new HostConfig();
	}
}
