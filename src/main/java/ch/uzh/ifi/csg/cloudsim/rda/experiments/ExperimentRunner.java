package ch.uzh.ifi.csg.cloudsim.rda.experiments;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.uzh.ifi.csg.cloudsim.rda.data.StochasticDataGenerator;

public class ExperimentRunner {

	private static SimpleDateFormat df = new SimpleDateFormat(
			"yyyyMMddhhmmssSSS");

	public static void main(String[] args) {

		int vmCnt = 9;
		int hostCnt = 3;
		int userCnt = 3;

		// generating input data that can be used for the experiments
		ArrayList<ArrayList<double[]>> inputData = new ArrayList<ArrayList<double[]>>();
		StochasticDataGenerator randomData = new StochasticDataGenerator(60);
		for (int i = 0; i < vmCnt; i++) {

			ArrayList<double[]> workloadData = randomData.generateData(350,
					100, 40, 250, 10, 0.5, 10, 0.5, 75);
			inputData.add(workloadData);
			PrintWriter trace = null;
			try {
				trace = new PrintWriter(df.format(new Date()) + "_" + i
						+ "_workload.csv", "UTF-8");
				trace.println("cpu,ram,bw,storageIO");

				for (double[] line : workloadData) {
					trace.println(line[0] + "," + line[1] + "," + line[2] + ","
							+ line[3]);
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
				trace.close();
			}

		}

		// MMFS policy
		ExperimentalSuite suite = new ExperimentalSuite();
		suite.setInputData(inputData);
		suite.setTrace(true);

		// VMs and Hosts and users to create
		suite.simulate(vmCnt, hostCnt, userCnt);

//		// greediness policy
//		UserAwareExperimentalSuite userAwareSuite = new UserAwareExperimentalSuite();
//		userAwareSuite.setInputData(inputData);
//		userAwareSuite.setTrace(true);
//
//		// VMs and Hosts and users to create
//		userAwareSuite.simulate(vmCnt, hostCnt, userCnt);
	}

}
