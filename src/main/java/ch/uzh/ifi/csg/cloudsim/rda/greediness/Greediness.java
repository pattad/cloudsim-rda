package ch.uzh.ifi.csg.cloudsim.rda.greediness;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.cloudbus.cloudsim.Log;

/**
 * This class delivers the greediness. It works only with the metrics.py script.
 * TODO: Implement this in pure JAVA.
 * 
 * @author Patrick A. Taddei
 * 
 */
public class Greediness {

	private static BufferedWriter out;
	private static Process p = null;
	private static BufferedReader in;

	private static String pythonPath = null;

	public static String getPythonPath() {
		return pythonPath;
	}

	public static void setPythonPath(String pythonPath) {
		Greediness.pythonPath = pythonPath;
	}

	public static void initialize() {
		try {
			p = Runtime.getRuntime().exec(pythonPath + "/getGreediness.py");

			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			out = new BufferedWriter(
					new OutputStreamWriter(p.getOutputStream()));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<String, Float> getGreediness(
			TreeMap<String, double[]> utilizationByUser, int mipsCapacity,
			int ramCapacity, int bwCapacity, int diskCapacity) {

		if (pythonPath == null) {
			return new HashMap<String, Float>();
		}

		Map<String, Float> userPriorities = new HashMap<String, Float>();

		String requestedResources = "";

		for (String customer : new TreeMap<String, double[]>(utilizationByUser)
				.keySet()) {
			double[] util = utilizationByUser.get(customer);
			requestedResources += customer + " " + roundFourPositions(util[0])
					+ " " + roundFourPositions(util[1]) + " "
					+ roundFourPositions(util[2]) + " "
					+ roundFourPositions(util[3]) + " ";
		}
		String supply = mipsCapacity + " " + ramCapacity + " " + bwCapacity
				+ " " + diskCapacity;
		Log.printLine("Determining greediness on DATACENTER level: ");
		try {
			Log.printLine(supply + " " + requestedResources);
			out.write(supply + " " + requestedResources);
			out.flush();
			out.newLine();
			out.flush();

			int i = 0;
			String inLine = in.readLine();
			while (inLine != null) {
				if (i == utilizationByUser.size()) {
					break;
				}
				if (inLine.contains("greed")) {
					Log.printLine(inLine);
					inLine = inLine.substring(3);
					String userName = inLine.substring(0, inLine.indexOf(","));
					inLine = inLine.substring(inLine.indexOf(":") + 1);
					inLine = inLine.substring(0, inLine.indexOf("+"));
					float greediness = Float.valueOf(inLine);

					if (userPriorities.containsKey(userName)) {
						Float currentVal = userPriorities.get(userName);
						userPriorities.put(userName, currentVal + greediness);
					} else {
						userPriorities.put(userName, greediness);
					}
					i++;
				}

				inLine = in.readLine();

			}

		} catch (IOException e) {
			Log.printLine("Error while getting greediness: " + e.getMessage());
		}
		return userPriorities;
	}

	private static double roundFourPositions(double val) {
		return Math.round(val * 10000) / 10000.0d;
	}
}
