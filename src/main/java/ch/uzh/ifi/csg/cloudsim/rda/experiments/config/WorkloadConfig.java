package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;

import ch.uzh.ifi.csg.cloudsim.rda.experiments.StochasticDataGenerator;

public class WorkloadConfig {
	StochasticDataGenerator randomData = new StochasticDataGenerator(60);

	public ArrayList<double[]> generateWorkload() {
		return randomData.generateData(350, 100, 40, 250, 10, 0.5, 10, 0.5, 75);
	}
}
