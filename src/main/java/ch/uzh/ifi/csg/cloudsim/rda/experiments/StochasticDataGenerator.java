package ch.uzh.ifi.csg.cloudsim.rda.experiments;

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
			double verticalStretch, int periodLength, double dependencyFactor,
			double expMean) {

		ArrayList<double[]> result = new ArrayList<double[]>();

		// individual deviations from median
		double deviationCpu = 0d;
		double deviationBw = 0d;
		double deviationStorage = 0d;

		Random r = new Random();
		double ram = medianRam;
		int ramDirection = 0;// change values: 0 down, 1 straight, 2 up

		for (int i = 0; i <= sampleLength; i++) {

			if (i % periodLength == 0) {
				deviationCpu = ((rd.nextExponential(expMean))) / 100.0;
				deviationBw = ((rd.nextExponential(expMean))) / 100.0;
				deviationStorage = ((rd.nextExponential(expMean))) / 100.0;
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

			// hard limits for ram
			if (ram < medianRam * 0.5) {
				ram = medianRam * 0.5;
			}
			if (ram > medianRam * 1.5) {
				ram = medianRam * 1.5;
			}

			double finalDevBw = (dependencyFactor * deviationCpu)
					+ ((1 - dependencyFactor) * deviationBw);

			double bw = Math.round(rd.nextGaussian(minBw
					+ (minBw * finalDevBw * verticalStretch), standardDiv));

			double finalDevStorageIO = (dependencyFactor * deviationCpu)
					+ ((1 - dependencyFactor) * deviationStorage);

			double storageIO = Math.round(rd.nextGaussian(minStorage
					+ (minStorage * finalDevStorageIO * verticalStretch),
					standardDiv));
			// hard limits for storage
			if (storageIO < minStorage * 0.2) {
				storageIO = minStorage * 0.2;
			}
			if (storageIO > minStorage * 3) {
				storageIO = minStorage * 3;
			}
			
			// hard limits for bw
			if (bw < minBw * 0.2) {
				bw = minBw * 0.2;
			}
			if (bw > minBw * 3) {
				bw = minBw * 3;
			}
			
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

	public ArrayList<double[]> generateWaveingData(double medianMips,
			double standardDivMips, double medianRam, double standardDivRam,
			double bwFactor, double storageFactor) {

		ArrayList<double[]> result = new ArrayList<double[]>();

		double mipsA = Math.round(rd.nextGaussian(medianMips, standardDivMips));
		double ramA = Math.round(rd.nextGaussian(medianRam, standardDivRam));

		double mipsB = Math.round(rd.nextGaussian(medianMips, standardDivMips));
		double ramB = Math.round(rd.nextGaussian(medianRam, standardDivRam));

		double periodLength = 10;

		for (int i = 0; i <= sampleLength; i++) {

			double mips;
			double ram;

			if (i % periodLength == 0) {
				mipsA = mipsB;
				ramA = ramB;

				mipsB = Math
						.round(rd.nextGaussian(medianMips, standardDivMips));
				ramB = Math.round(rd.nextGaussian(medianRam, standardDivRam));

				mips = mipsA;
				ram = ramA;
			} else {
				mips = mipsA + ((mipsB - mipsA) / periodLength)
						* (i % periodLength);
				ram = ramA + ((ramB - ramA) / periodLength)
						* (i % periodLength);
			}

			double bw = mips / bwFactor;
			double storageIO = mips / storageFactor;

			// correlation is about 0.92, when randomizing this way
			double stDivFactor = 33;
			mips = rd.nextGaussian(mips, mips / stDivFactor);
			bw = rd.nextGaussian(bw, bw / stDivFactor);
			storageIO = rd.nextGaussian(storageIO, storageIO / stDivFactor);

			checkValidity(mips, ram, bw, storageIO);

			double[] entry = { mips, ram, bw, storageIO };
			result.add(entry);
		}

		return result;
	}
}
