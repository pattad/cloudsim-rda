package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;

import ch.uzh.ifi.csg.cloudsim.rda.experiments.StochasticDataGenerator;

public class Config_2 extends WorkloadConfig {

	public ArrayList<ArrayList<double[]>> generateWorkload(int vmCnt, int workloadLength) {
		ArrayList<ArrayList<double[]>> inputData = new ArrayList<ArrayList<double[]>>();
		StochasticDataGenerator randomDataGenerator = new StochasticDataGenerator(workloadLength);
		for (int i = 0; i < vmCnt; i++) {

			ArrayList<double[]> workloadData = randomDataGenerator
					.generateWaveingData(350, 40, 150, 10, 1.5, 2);
			inputData.add(workloadData);

		}
		return inputData;
	}
}
