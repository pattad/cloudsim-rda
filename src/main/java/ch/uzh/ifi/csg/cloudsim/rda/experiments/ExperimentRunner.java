package ch.uzh.ifi.csg.cloudsim.rda.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.uzh.ifi.csg.cloudsim.rda.experiments.config.WorkloadConfig;

public class ExperimentRunner {

	private static SimpleDateFormat df = new SimpleDateFormat(
			"yyyyMMddhhmmssSSS");

	private static String pythonPath = "python bin";

	private static WorkloadConfig workloadConfig = new WorkloadConfig();

	public static void main(String[] args) {

		int vmCnt = 9;
		int hostCnt = 3;
		int userCnt = 3;

		int experimentCnt = 1;

		if (args.length > 2) {
			vmCnt = Integer.valueOf(args[0]);
			hostCnt = Integer.valueOf(args[1]);
			userCnt = Integer.valueOf(args[2]);
		}

		if (args.length > 3) {
			experimentCnt = Integer.valueOf(args[3]);
		}

		if (args.length > 4) {
			pythonPath = args[4];
		}

		if (args.length > 5) {
			try {
				workloadConfig = (WorkloadConfig) Class.forName(args[5])
						.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		// number of experiments to conduct
		for (int exp = 0; exp < experimentCnt; exp++) {

			String baseDir = new File("output/" + df.format(new Date()))
					.getAbsolutePath();

			setCurrentDirectory(baseDir);

			// generating input data that can be used for the experiments
			ArrayList<ArrayList<double[]>> inputData = workloadConfig
					.generateWorkload(vmCnt);

			int i = 0;
			for (ArrayList<double[]> wl : inputData) {

				PrintWriter trace = null;

				try {
					trace = new PrintWriter(
							new File(+i + "_workload.csv").getAbsoluteFile(),
							"UTF-8");
					trace.println("cpu,ram,bw,storageIO");

					for (double[] line : wl) {
						trace.println(line[0] + "," + line[1] + "," + line[2]
								+ "," + line[3]);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} finally {
					trace.close();
				}
				i++;
			}

			setCurrentDirectory(baseDir + "/mmfs");

			// MMFS policy
			ExperimentalSuite suite = new ExperimentalSuite();
			suite.setInputData(inputData);
			suite.setTrace(true);

			// VMs and Hosts and users to create
			suite.simulate(vmCnt, hostCnt, userCnt);

			setCurrentDirectory(baseDir + "/greediness");

			// greediness policy
			UserAwareExperimentalSuite userAwareSuite = new UserAwareExperimentalSuite(
					pythonPath);
			userAwareSuite.setInputData(inputData);
			userAwareSuite.setTrace(true);

			// VMs and Hosts and users to create
			userAwareSuite.simulate(vmCnt, hostCnt, userCnt);
		}
	}

	public static boolean setCurrentDirectory(String directory_name) {
		boolean result = false;
		File directory;

		directory = new File(directory_name).getAbsoluteFile();
		if (directory.exists() || directory.mkdirs()) {
			result = (System.setProperty("user.dir",
					directory.getAbsolutePath()) != null);
		}

		return result;
	}

}
