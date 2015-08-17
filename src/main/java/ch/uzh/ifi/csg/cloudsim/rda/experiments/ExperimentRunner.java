package ch.uzh.ifi.csg.cloudsim.rda.experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ch.uzh.ifi.csg.cloudsim.rda.experiments.config.Config_1;
import ch.uzh.ifi.csg.cloudsim.rda.experiments.config.ExperimentConfig;
import ch.uzh.ifi.csg.cloudsim.rda.greediness.Greediness;

public class ExperimentRunner {

	private static SimpleDateFormat df = new SimpleDateFormat(
			"yyyyMMddhhmmssSSS");

	private static String pythonPath = "python bin";

	private static ExperimentConfig config = new Config_1();

	private static int vmCnt = 9;
	private static int hostCnt = 3;
	private static int userCnt = 3;

	private static int workloadLength = 60;

	private static int experimentCnt = 1;

	private static boolean logTrace = true;

	private static double priorityUpdateInterval = 1;

	public static void main(String[] args) {

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
				config = (ExperimentConfig) Class.forName(args[5])
						.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		if (args.length > 6) {
			workloadLength = Integer.valueOf(args[6]);
		}

		if (args.length > 7) {
			logTrace = Boolean.valueOf(args[7]);
		}

		if (args.length > 8) {
			priorityUpdateInterval = Double.valueOf(args[8]);
		}

		Greediness.setPythonPath(pythonPath);
		Greediness.initialize();

		System.out.println("Running experiments with parameters:");
		String params = "vmCnt: " + vmCnt + ", hostCnt: " + hostCnt
				+ ", userCnt: " + userCnt + ", workloadLength: "
				+ workloadLength + ", experimentCnt: " + experimentCnt
				+ ", workloadConfig: " + config.getClass().getSimpleName()
				+ ", priorityUpdateInterval: " + priorityUpdateInterval + ", "
				+ config.getDescription() + ",";
		System.out.println(params);

		String homeDir = new File("output/").getAbsolutePath();

		PrintWriter master = null;
		try {
			master = new PrintWriter(new BufferedWriter(new FileWriter(
					"output/experimentResults.csv", true)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String paramsMaster = vmCnt + "," + userCnt + "," + hostCnt + ","
				+ workloadLength + "," + config.getClass().getSimpleName()
				+ "," + config.getDescription() + "," + priorityUpdateInterval
				+ "," + config.getHostConfig().getMips() + ","
				+ config.getHostConfig().getRam() + ","
				+ config.getHostConfig().getBw() + ","
				+ config.getHostConfig().getStorageIO() + ","
				+ config.getVmConfig().getMips() + ","
				+ config.getVmConfig().getRam() + ","
				+ config.getVmConfig().getBw() + ",";

		// number of experiments to conduct
		for (int exp = 0; exp < experimentCnt; exp++) {

			String dirString = config.getClass().getSimpleName() + "_"
					+ df.format(new Date());
			String baseDir = new File(homeDir + "/" + dirString)
					.getAbsolutePath();

			setCurrentDirectory(baseDir);

			PrintWriter paramsLog = null;

			try {
				paramsLog = new PrintWriter(
						new File("experimentParams.log").getAbsoluteFile(),
						"UTF-8");
				paramsLog.println(params);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			// generating input data that can be used for the experiments
			ArrayList<ArrayList<double[]>> inputData = config.generateWorkload(
					vmCnt, workloadLength);

			int i = 0;
			for (ArrayList<double[]> wl : inputData) {

				PrintWriter trace = null;

				try {
					trace = new PrintWriter(
							new File("workload_" + i + ".csv")
									.getAbsoluteFile(),
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

			String resultString = "";

			// ---------------- DRF
			setCurrentDirectory(baseDir + "/drf");

			System.out.println();
			System.out.println("DRF (Dominant Resource Fairness)...");

			// DRF policy
			DRFExperimentalSuite drfSuite = new DRFExperimentalSuite();
			drfSuite.setInputData(inputData);
			drfSuite.setTrace(logTrace);
			drfSuite.setHostConfig(config.getHostConfig());
			drfSuite.setVmConfig(config.getVmConfig());

			// VMs and Hosts and users to create
			drfSuite.simulate(vmCnt, hostCnt, userCnt);

			resultString += drfSuite.getResultStringCsv();

			// ---------------- DRF Multi Host
			setCurrentDirectory(baseDir + "/drf_mh");

			System.out.println();
			System.out
					.println("DRF MH (Dominant Resource Fairness Multi Host Aware)...");

			// DRF policy
			DRFMHExperimentalSuite drfMhSuite = new DRFMHExperimentalSuite();
			drfMhSuite.setInputData(inputData);
			drfMhSuite.setTrace(logTrace);
			drfMhSuite.setPriorityUpdateInterval(priorityUpdateInterval);
			drfMhSuite.setHostConfig(config.getHostConfig());
			drfMhSuite.setVmConfig(config.getVmConfig());

			// VMs and Hosts and users to create
			drfMhSuite.simulate(vmCnt, hostCnt, userCnt);

			resultString += drfMhSuite.getResultStringCsv();
			// ---------------- MMFS
			setCurrentDirectory(baseDir + "/mmfs");
			System.out.println();
			System.out.println("MMFS (Max Min Fair Share)...");

			// MMFS policy
			ExperimentalSuite suite = new ExperimentalSuite();
			suite.setInputData(inputData);
			suite.setTrace(logTrace);
			suite.setHostConfig(config.getHostConfig());
			suite.setVmConfig(config.getVmConfig());

			// VMs and Hosts and users to create
			suite.simulate(vmCnt, hostCnt, userCnt);

			resultString += suite.getResultStringCsv();

			// ---------------- Greediness
			setCurrentDirectory(baseDir + "/greediness");

			System.out.println();
			System.out.println("Greediness Allocation Algorithm...");

			// greediness policy
			GreedinessExperimentalSuite userAwareSuite = new GreedinessExperimentalSuite(
					pythonPath);
			userAwareSuite.setInputData(inputData);
			userAwareSuite.setTrace(logTrace);
			userAwareSuite.setPriorityUpdateInterval(priorityUpdateInterval);
			userAwareSuite.setHostConfig(config.getHostConfig());
			userAwareSuite.setVmConfig(config.getVmConfig());

			// VMs and Hosts and users to create
			userAwareSuite.simulate(vmCnt, hostCnt, userCnt);
			resultString += userAwareSuite.getResultStringCsv();

			master.println(dirString + "," + paramsMaster + resultString);
			paramsLog.println("," + dirString + "," + paramsMaster
					+ resultString);
			paramsLog.close();
		}
		master.close();
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
