package ch.uzh.ifi.csg.cloudsim.rda.experiments;

import java.util.ArrayList;

import ch.uzh.ifi.csg.cloudsim.rda.data.StochasticDataGenerator;

public class ExperimentRunner {

	public static void main(String[] args) {

		int vmCnt = 1;
		int hostCnt = 3;
		int userCnt = 3;
		
		// generating input data that can be used for the experiments
		ArrayList<ArrayList<double[]>> inputData = new ArrayList<ArrayList<double[]>>();
		StochasticDataGenerator randomData = new StochasticDataGenerator(120);		
		for (int i = 0; i < vmCnt; i++) {
			inputData.add(randomData.generateData(350, 100, 40, 250, 10, 0.5, 10, 0.5));
		}

		// MMFS policy
		ExperimentalSuite suite = new ExperimentalSuite();
		suite.setInputData(inputData);
		suite.setTrace(true);
		
		// VMs and Hosts and users to create
		suite.simulate(vmCnt, hostCnt, userCnt);

		// greediness policy
		UserAwareExperimentalSuite userAwareSuite = new UserAwareExperimentalSuite();
		userAwareSuite.setInputData(inputData);
		userAwareSuite.setTrace(true);
		
		// VMs and Hosts and users to create
		userAwareSuite.simulate(vmCnt, hostCnt, userCnt);
	}

}
