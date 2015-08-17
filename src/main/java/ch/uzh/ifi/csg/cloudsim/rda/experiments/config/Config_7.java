package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;

import ch.uzh.ifi.csg.cloudsim.rda.experiments.StochasticDataGenerator;

public class Config_7 implements ExperimentConfig {

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
				// database workload
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateWaveingData(200, 10, 1500, 10, 0.1, 7);
				inputData.add(workloadData);
			} else {
				// web-server: network intensive workload
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateWaveingData(400, 10, 250, 10, 0.3, 0.1);
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
		return "DB WS WS";
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
