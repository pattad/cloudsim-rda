package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;

import ch.uzh.ifi.csg.cloudsim.rda.experiments.StochasticDataGenerator;

public class Config_31 implements ExperimentConfig {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.uzh.ifi.csg.cloudsim.rda.experiments.config.ExperimentConfig#getVmConfig
	 * ()
	 */
	public VmConfig getVmConfig() {

		String vmm = "Xen"; // VMM name
		int mips = 500;
		int ram = 512; // host memory (MB)
		int bw = 1000; // MBit/s
		int peCnt = 1;
		long size = 10000; // image size (MB)

		VmConfig vmConfig = new VmConfig(vmm, peCnt, mips, ram, size, bw);
		return vmConfig;
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

			if (i % 4 == 0) {
				// web-server: network intensive workload
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateWaveingData(550, 20, 250, 10, 0.3, 0.05);
				inputData.add(workloadData);
			} else if (i % 4 == 1) {
				// web-server: network intensive workload
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateWaveingData(550, 20, 250, 10, 0.3, 0.05);
				inputData.add(workloadData);
			} else if (i % 4 == 2) {
				// web-server: network intensive workload
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateWaveingData(450, 20, 250, 10, 0.3, 0.05);
				inputData.add(workloadData);
			} else {
				// web-server: network intensive workload
				ArrayList<double[]> workloadData = randomDataGenerator
						.generateWaveingData(550, 20, 250, 10, 0.3, 0.05);
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
		return "Only WS";
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
