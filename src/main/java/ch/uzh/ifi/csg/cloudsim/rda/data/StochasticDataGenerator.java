package ch.uzh.ifi.csg.cloudsim.rda.data;

import java.util.ArrayList;

import org.apache.commons.math3.random.RandomDataGenerator;

public class StochasticDataGenerator {

	RandomDataGenerator rd = new RandomDataGenerator();

	public ArrayList<double[]> generateWebServerData() {

		ArrayList<double[]> result = new ArrayList<double[]>();
		double median = 235.0;
		double standardDiv = 10.8;
		
		for (int i = 0; i <= 120; i++) {
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
