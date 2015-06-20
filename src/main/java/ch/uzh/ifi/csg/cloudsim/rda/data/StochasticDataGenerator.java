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
			double ram = 260 + 100 * deviation * stretch;
			double bw = mips / 3.0;
			double storageIO = mips / 2.0;

			checkValidity(mips, ram, bw, storageIO);

			double[] entry = { mips, ram, bw, storageIO };
			result.add(entry);
			degree = degree + random.nextInt(3);

		}

		return result;
	}

	public ArrayList<double[]> generateWebServerDataStepped(double median,
			double standardDiv, double stretch, int periodLength) {

		ArrayList<double[]> result = new ArrayList<double[]>();

		Random random = new Random();
		double deviation = 0d;

		for (int i = 0; i <= sampleLength; i++) {

			if (i % periodLength == 0) {
				deviation = (random.nextInt(100) - 50) / 100.0;
			}

			double mips = Math.round(rd.nextGaussian(median
					+ (median * deviation * stretch), standardDiv));
			double ram = 260 + 10 * deviation * stretch;
			double bw = mips / 3.0;
			double storageIO = mips / 2.0;

			checkValidity(mips, ram, bw, storageIO);

			double[] entry = { mips, ram, bw, storageIO };
			result.add(entry);

		}

		return result;
	}

	/**
	 * 
	 * @param minCpu
	 * @param minBw
	 * @param minStorage
	 * @param medianRam
	 * @param standardDiv
	 * @param verticalStretch
	 * @param periodLength
	 * @param dependencyFactor
	 * @return
	 */
	public ArrayList<double[]> generateData(double minCpu, double minBw,
			double minStorage, double medianRam, double standardDiv,
			double verticalStretch, int periodLength, double dependencyFactor) {

		ArrayList<double[]> result = new ArrayList<double[]>();

		double verticalMean = 75;

		// individual deviations from median
		double deviationCpu = 0d;
		double deviationBw = 0d;
		double deviationStorage = 0d;

		Random r = new Random();
		double ram = medianRam;
		int ramDirection = 0;// change values: 0 down, 1 straight, 2 up

		for (int i = 0; i <= sampleLength; i++) {

			if (i % periodLength == 0) {
				deviationCpu = ((rd.nextExponential(verticalMean))) / 100.0;
				deviationBw = ((rd.nextExponential(verticalMean))) / 100.0;
				deviationStorage = ((rd.nextExponential(verticalMean))) / 100.0;
				ramDirection = r.nextInt(3);
			}

			double mips = Math.round(rd.nextGaussian(minCpu
					+ (minCpu * deviationCpu * verticalStretch), standardDiv));

			if (ramDirection == 0) {
				// down
				ram = ram - verticalStretch * deviationCpu * 3.5; // volatility
																	// of cpu
				// included
			} else if (ramDirection == 2) {
				// go up
				ram = ram + verticalStretch * deviationCpu * 3.5; // volatility
																	// of cpu
				// included
			} // else stay

			double finalDevBw = (dependencyFactor * deviationCpu)
					+ ((1 - dependencyFactor) * deviationBw);

			double bw = Math.round(rd.nextGaussian(minBw
					+ (minBw * finalDevBw * verticalStretch), standardDiv));

			double finalDevStorageIO = (dependencyFactor * deviationCpu)
					+ ((1 - dependencyFactor) * deviationStorage);

			double storageIO = Math.round(rd.nextGaussian(minStorage
					+ (minStorage * finalDevStorageIO * verticalStretch),
					standardDiv));

			checkValidity(mips, ram, bw, storageIO);

			double[] entry = { mips, ram, bw, storageIO };
			result.add(entry);

		}

		return result;
	}

	public void checkValidity(double mips, double ram, double bw,
			double storageIO) {
		if (mips <= 0) {
			throw new RuntimeException(
					"MIPS is smaller than 0 in this data generation approach. Please try to adapt the input parameters.(value: "
							+ mips + ")");
		}
		if (ram < 0) {
			throw new RuntimeException(
					"Ram is smaller than 0 in this data generation approach. Please try to adapt the input parameters.(value: "
							+ ram + ")");
		}
		if (bw < 0) {
			throw new RuntimeException(
					"Bandwidth is smaller than 0 in this data generation approach. Please try to adapt the input parameters.(value: "
							+ bw + ")");
		}
		if (storageIO < 0) {
			throw new RuntimeException(
					"storageIO is smaller than 0 in this data generation approach. Please try to adapt the input parameters. (value: "
							+ storageIO + ")");
		}
	}

	public ArrayList<double[]> generateWebServerData(double median,
			double standardDiv) {

		ArrayList<double[]> result = new ArrayList<double[]>();

		for (int i = 0; i <= sampleLength; i++) {
			double mips = Math.round(rd.nextGaussian(median, standardDiv));
			double ram = 260;
			double bw = mips / 3.0;
			double storageIO = 0.0;

			checkValidity(mips, ram, bw, storageIO);

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

			checkValidity(mips, ram, bw, storageIO);

			double[] entry = { mips, ram, bw, storageIO };
			result.add(entry);
		}

		return result;
	}
}
