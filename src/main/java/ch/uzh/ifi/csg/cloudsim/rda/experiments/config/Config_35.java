package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;

import ch.uzh.ifi.csg.cloudsim.rda.experiments.StochasticDataGenerator;

public class Config_35 implements ExperimentConfig {

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
	 * (non-Javadoc)3 customer mit je einer VM [8/14/2015 7:58:43 PM] Patrick
	 * Pou: 1 host [8/14/2015 7:58:51 PM] Patrick Pou: alle VMs wollen mehr CPU
	 * [8/14/2015 7:59:02 PM] Patrick Pou: VM1 will auch mehr RAM als FS
	 * [8/14/2015 7:59:12 PM] Patrick Pou: VM2 verbraucht bei RAM genau FS
	 * [8/14/2015 7:59:26 PM] Patrick Pou: VM3 braucht weniger RAM
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
				// web-server: network intensive workload (35% more RAM) 921
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateWaveingData(400, 10, 921, 0.001, 0.3, 0.1);
				inputData.add(workloadData);
			} else if (i % 3 == 1) {
				// web-server: network intensive workload (RAM = equal share)
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateWaveingData(400, 10, 628, 0.001, 0.3, 0.1);
				inputData.add(workloadData);
			} else {
				// web-server: network intensive workload
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateWaveingData(400, 10, 477, 0.001, 0.3, 0.1);
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
		return "Only WS, RAM differentiates";
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
