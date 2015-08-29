package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;

/**
 * Specifies a set of configuration parameters for an experiment/simulation.
 * 
 * @author Patrick A. Taddei
 *
 */
public interface ExperimentConfig {

	/**
	 * Retrieves the generated workloads.
	 * 
	 * @param vmCnt
	 *            the number of VMs
	 * @param workloadLength
	 *            the lenght in seconds of the workloads
	 * @return the generated workloads
	 */
	public abstract ArrayList<ArrayList<double[]>> generateWorkload(int vmCnt,
			int workloadLength);

	/**
	 * 
	 * @return The configuration of hosts.
	 */
	public abstract HostConfig getHostConfig();

	/**
	 * 
	 * @return The configuration of the VMs.
	 */
	public abstract VmConfig getVmConfig();

	/**
	 * 
	 * @return The description of the particular configuration.
	 */
	public abstract String getDescription();
}