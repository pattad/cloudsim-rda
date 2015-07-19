package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;

public interface ExperimentConfig {

	public abstract ArrayList<ArrayList<double[]>> generateWorkload(int vmCnt,
			int workloadLength);

}