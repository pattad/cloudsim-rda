package ch.uzh.ifi.csg.cloudsim.rda.data;

import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.math3.random.RandomDataGenerator;

public class StochasticDataGenerator {

	RandomDataGenerator rd = new RandomDataGenerator();

	private int sampleLength = 120;

	public StochasticDataGenerator() {
		super();
	}

	public StochasticDataGenerator(int sampleLength) {
		super();
		this.sampleLength = sampleLength;
	}

	public ArrayList<double[]> generateWebServerDataSinusCurved(double median,
			double standardDiv, double stretch) {

		ArrayList<double[]> result = new ArrayList<double[]>();

		Random random = new Random();
		int degree = random.nextInt(360); // start at a random point

		for (int i = 0; i <= sampleLength; i++) {

			double deviation = Math.sin(Math.toRadians(degree));

			double mips = Math.round(rd.nextGaussian(median
					+ (median * deviation * stretch), standardDiv));
			if (mips <= 0) {
				throw new RuntimeException(
						"MIPS is smaller than 0 in this data generation approach. Please try to adapt the input parameters.");
			}
			double ram = 260 + 100 * deviation * stretch;
			double bw = mips / 3.0;
			double storageIO = mips / 2.0;
			double[] entry = { mips, ram, bw, storageIO };
			result.add(entry);
			degree = degree + random.nextInt(3);

		}

		return result;
	}

	public ArrayList<double[]> generateWebServerData(double median,
			double standardDiv) {

		ArrayList<double[]> result = new ArrayList<double[]>();

		for (int i = 0; i <= sampleLength; i++) {
			double mips = Math.round(rd.nextGaussian(median, standardDiv));
			double ram = 260;
			double bw = mips / 3.0;
			double storageIO = 0.0;
			double[] entry = { mips, ram, bw, storageIO };
			result.add(entry);
		}

		return result;
	}

	public ArrayList<double[]> generateDatabaseServerData(double median,
			double standardDiv) {

		ArrayList<double[]> result = new ArrayList<double[]>();

		for (int i = 0; i <= sampleLength; i++) {
			double mips = Math.round(rd.nextGaussian(median, standardDiv));
			double ram = 260;
			double bw = mips / 3.0;
			double storageIO = 0.0;
			double[] entry = { mips, ram, bw, storageIO };
			result.add(entry);
		}

		return result;
	}
}
