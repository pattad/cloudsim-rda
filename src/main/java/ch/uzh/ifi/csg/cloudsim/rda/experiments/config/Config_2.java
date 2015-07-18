package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;

import ch.uzh.ifi.csg.cloudsim.rda.experiments.StochasticDataGenerator;

public class Config_2 extends WorkloadConfig {

	private ArrayList<ArrayList<double[]>> inputData = new ArrayList<ArrayList<double[]>>();

	public ArrayList<ArrayList<double[]>> generateWorkload(int cnt, int workloadLength) {
		StochasticDataGenerator randomDataGenerator = new StochasticDataGenerator(workloadLength);
		for (int i = 0; i < cnt; i++) {

			ArrayList<double[]> workloadData = randomDataGenerator
					.generateWaveingData(350, 40, 150, 10, 1.5, 2);
			inputData.add(workloadData);

		}
		return inputData;
	}
}
