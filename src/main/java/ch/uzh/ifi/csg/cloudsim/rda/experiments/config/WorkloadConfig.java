package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;

import ch.uzh.ifi.csg.cloudsim.rda.experiments.StochasticDataGenerator;

public class WorkloadConfig {
	StochasticDataGenerator randomData = new StochasticDataGenerator(60);

	public ArrayList<ArrayList<double[]>> generateWorkload(int cnt) {
		ArrayList<ArrayList<double[]>> inputData = new ArrayList<ArrayList<double[]>>();
		StochasticDataGenerator randomData = new StochasticDataGenerator(60);
		for (int i = 0; i < cnt; i++) {

			ArrayList<double[]> workloadData = randomData.generateData(250,
					100, 40, 250, 10, 0.5, 10, 0.5, 75);
			inputData.add(workloadData);

		}
		return inputData;
	}
}
