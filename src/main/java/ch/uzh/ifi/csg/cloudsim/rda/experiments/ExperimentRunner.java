package ch.uzh.ifi.csg.cloudsim.rda.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ExperimentRunner {

	private static SimpleDateFormat df = new SimpleDateFormat(
			"yyyyMMddhhmmssSSS");

	private static String pythonPath = "python bin";

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

		// number of experiments to conduct
		for (int exp = 0; exp < experimentCnt; exp++) {

			String baseDir = new File("output/" + df.format(new Date()))
					.getAbsolutePath();

			setCurrentDirectory(baseDir);

			// generating input data that can be used for the experiments
			ArrayList<ArrayList<double[]>> inputData = new ArrayList<ArrayList<double[]>>();
			StochasticDataGenerator randomData = new StochasticDataGenerator(60);
			for (int i = 0; i < vmCnt; i++) {

				ArrayList<double[]> workloadData = randomData.generateData(350,
						100, 40, 250, 10, 0.5, 10, 0.5, 75);
				inputData.add(workloadData);
				PrintWriter trace = null;

				try {
					trace = new PrintWriter(new File(df.format(new Date())
							+ "_" + i + "_workload.csv").getAbsoluteFile(),
							"UTF-8");
					trace.println("cpu,ram,bw,storageIO");

					for (double[] line : workloadData) {
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
