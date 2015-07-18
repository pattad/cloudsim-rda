package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;

import ch.uzh.ifi.csg.cloudsim.rda.experiments.StochasticDataGenerator;

public class WorkloadConfig {

	public ArrayList<ArrayList<double[]>> generateWorkload(int vmCnt,
			int workloadLength) {

		ArrayList<ArrayList<double[]>> inputData = new ArrayList<ArrayList<double[]>>();
		StochasticDataGenerator randomDataGenerator = new StochasticDataGenerator(
				workloadLength);
		for (int i = 0; i < vmCnt; i++) {

			ArrayList<double[]> workloadData = randomDataGenerator
					.generateData(250, 100, 40, 250, 10, 0.5, 10, 0.5, 75);
			inputData.add(workloadData);

		}
		return inputData;
	}
}
