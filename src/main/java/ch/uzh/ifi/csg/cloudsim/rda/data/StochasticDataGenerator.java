package ch.uzh.ifi.csg.cloudsim.rda.data;

import java.util.ArrayList;

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

	public ArrayList<double[]> generateWebServerData(double median, double standardDiv) {

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
	
	public ArrayList<double[]> generateDatabaseServerData(double median, double standardDiv) {

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
