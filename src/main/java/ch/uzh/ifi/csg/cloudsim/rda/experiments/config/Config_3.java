package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;

import ch.uzh.ifi.csg.cloudsim.rda.experiments.StochasticDataGenerator;

public class Config_3 implements ExperimentConfig {
	
	
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

			if (i % 2 == 0) {
				// computing intensive workload, lot's of memory and cpu
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateData(400, 50, 20, 500, 10, 0.8, 10, 0.8, 75);
				inputData.add(workloadData);
			} else {
				// network intensive workload, lots of network
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateData(200, 200, 20, 250, 10, 0.8, 10, 0.8, 75);
				inputData.add(workloadData);
			}
		}
		return inputData;
	}
}
