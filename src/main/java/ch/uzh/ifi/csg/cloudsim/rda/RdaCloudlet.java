package ch.uzh.ifi.csg.cloudsim.rda;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Consts;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;

import ch.uzh.ifi.csg.cloudsim.rda.util.CsvReader;

/**
 * This Cloudlet works in a progress aware fashion. This means that it requests
 * computing resources, dependent on the already progressed workload. This
 * behavior becomes visible, as soon as there is a time of scarcity and it
 * didn't got all requested resources. This is resulting into a down-graded
 * processing speed for the time, where scarcity exists. Naturally, this is also
 * reflected in the overall processing time of the Cloudlet that will increase.
 * 
 * It must be instantiated with a CSV input file that contains the requested
 * resources for cpu, ram, bandwidth & storage I/O. <br/>
 * Example CSV file:<br/>
 * <code>
 * cpu,ram,network,storage
 * 150,50,0,0
 * 280,300,0,0
 * </code> <br/>
 * cpu in MIPS (million instructions per second)<br/>
 * ram in MB<br/>
 * network in MB/s<br/>
 * storage in MB/s<br/>
 * 
 * The fist line of the input values represents the requested values at 0
 * second, second line 1 second and so on.
 * 
 * @author Patrick A. Taddei
 * @see RdaCloudletSchedulerDynamicWorkload
 *
 */
public class RdaCloudlet extends Cloudlet {

	private double mips;
	private double bandwidth;
	private double storageIO;
	private double ram;
	private boolean record = true;

	private long instructionsFinishedSoFar = 0l;

	private final long[][] data;

	/* index in input data array */
	/* INST_INDEX is the cumulated instruction counter index */
	private static final int INST_INDEX = 0;
	private static final int CPU_INDEX = 1;
	private static final int RAM_INDEX = 2;
	private static final int BW_INDEX = 3;
	private static final int STORAGE_INDEX = 4;

	private PrintWriter recorder;

	public RdaCloudlet(int cloudletId, int pesNumber, long cloudletFileSize,
			long cloudletOutputSize, String inputPath, boolean record)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(cloudletId, 0, pesNumber, cloudletFileSize, cloudletOutputSize,
				null, null, null, record, new LinkedList<String>());

		ArrayList<String[]> entries = this.readFile(new File(inputPath), ',');

		data = new long[entries.size()][5];

		int i = 0;
		long instructions = 0;

		int lastMips = 0;
		for (String[] entry : entries) {
			int mips = Integer.valueOf(entry[INST_INDEX]);
			if (i == 0) {
				data[i][INST_INDEX] = 0;
			} else {
				// average instructions in the timeframe
				instructions += (lastMips + mips) / 2;
				data[i][INST_INDEX] = instructions * Consts.MILLION;
			}
			data[i][CPU_INDEX] = Integer.valueOf(entry[0]);
			data[i][RAM_INDEX] = Integer.valueOf(entry[1]);
			data[i][BW_INDEX] = Integer.valueOf(entry[2]);
			data[i][STORAGE_INDEX] = Integer.valueOf(entry[3]);

			lastMips = mips;
			i++;
		}
		Log.printLine("Resource utilization data added from file: " + inputPath
				+ ", entries: " + entries.size());

		super.setCloudletLength(instructions);

		this.mips = data[0][CPU_INDEX]; // initial mips

		this.record = record;

		if (this.record) {

			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");

			recorder = new PrintWriter(df.format(new Date()) + "_"
					+ super.getCloudletId() + ".csv", "UTF-8");
			recorder.println("time,cpu,memory,bandwidth,storageIO");
		}
	}

	@Override
	public long getCloudletTotalLength() {
		return getCloudletLength();
	}

	@Override
	public double getUtilizationOfCpu(final double time) {
		return mips;
	}

	private long getFirstBoundOfCpu() {
		return getFirstBound(this.instructionsFinishedSoFar, CPU_INDEX);
	}

	public double getGradOfCpu() {
		double pastCPU = this.getFirstBoundOfCpu();
		double futureProcessingSpeed = this.getFirstBound(
				this.getNextUtilizationChange()
						+ this.instructionsFinishedSoFar, CPU_INDEX);
		return futureProcessingSpeed - pastCPU;
	}

	public double getGradOfBw() {
		double past = this.getFirstBound(this.instructionsFinishedSoFar,
				BW_INDEX);
		double future = this.getFirstBound(this.getNextUtilizationChange()
				+ this.instructionsFinishedSoFar, BW_INDEX);
		return future - past;
	}

	public double getGradOfStorageIO() {
		double past = this.getFirstBound(this.instructionsFinishedSoFar,
				STORAGE_INDEX);
		double future = this.getFirstBound(this.getNextUtilizationChange()
				+ this.instructionsFinishedSoFar, STORAGE_INDEX);
		return future - past;
	}

	public double getGradOfRam() {
		double past = this.getFirstBound(this.instructionsFinishedSoFar,
				RAM_INDEX);
		double future = this.getFirstBound(this.getNextUtilizationChange()
				+ this.instructionsFinishedSoFar, RAM_INDEX);
		return future - past;
	}

	public double getEstimatedNextChangeTime() {

		double grad = this.getGradOfCpu();

		long nextInstructionChange = this.getNextUtilizationChange();

		if (grad == 0.0d) {
			return new BigDecimal(nextInstructionChange)
					.divide(new BigDecimal(this.getFirstBoundOfCpu()),
							MathContext.DECIMAL64)
					.divide(new BigDecimal(Consts.MILLION),
							MathContext.DECIMAL64).doubleValue();

		} else {
			double past = this.getUtilizationOfCpu(0.0d);

			double a = (double) nextInstructionChange / (double) Consts.MILLION;

			double d = past * past + 2.0 * grad * a;
			double time = ((-past + Math.sqrt(d)) / grad);
			return time;
		}

	}

	private double getRequestedUtilization(final double timeSpan,
			double resourceGrad, double currentRequestedSpeed, int resource) {
		for (int i = 0; i < data.length; i++) {
			if (data[i][INST_INDEX] == instructionsFinishedSoFar) {
				currentRequestedSpeed = resourceGrad * timeSpan
						+ data[i][resource];
				break;
			} else if (data[i][INST_INDEX] >= instructionsFinishedSoFar) {

				if (resourceGrad != 0) {
					double expectedTime;
					double pastRequestedSpeed;

					// calculate the expected point in time, in the interval,
					// where the cloudlet currently is
					if (this.getGradOfCpu() != 0) {
						double pastSpeedCpu = data[i - 1][CPU_INDEX];
						double currentInst = instructionsFinishedSoFar
								- data[i - 1][INST_INDEX];

						double d = (pastSpeedCpu * pastSpeedCpu) + 2
								* this.getGradOfCpu()
								* (currentInst / Consts.MILLION);

						expectedTime = (-pastSpeedCpu + Math.sqrt(d))
								/ this.getGradOfCpu();

					} else {
						double currentInst = instructionsFinishedSoFar
								- data[i - 1][INST_INDEX];
						double instSpan = data[i][INST_INDEX]
								- data[i - 1][INST_INDEX];
						expectedTime = currentInst / instSpan;
					}

					double pastSpeedResource = data[i - 1][resource];

					pastRequestedSpeed = resourceGrad * expectedTime
							+ pastSpeedResource;
					currentRequestedSpeed = resourceGrad * timeSpan
							+ pastRequestedSpeed;

				} else {
					// there is a constant value
					currentRequestedSpeed = data[i][resource];
				}
				break;
			}
		}
		return currentRequestedSpeed;
	}

	public double getRequestedUtilizationOfCpu(final double timeSpan) {
		double grad = this.getGradOfCpu();
		double currentRequestedSpeed = 0.0d;

		currentRequestedSpeed = getRequestedUtilization(timeSpan, grad,
				currentRequestedSpeed, CPU_INDEX);

		return currentRequestedSpeed;
	}

	public double getRequestedUtilizationOfBw(final double timeSpan) {

		double currentRequestedSpeed = 0.0d;
		double grad = this.getGradOfBw();

		currentRequestedSpeed = getRequestedUtilization(timeSpan, grad,
				currentRequestedSpeed, BW_INDEX);

		return currentRequestedSpeed;
	}

	public double getRequestedUtilizationOfRam(final double timeSpan) {
		double currentRequestedSpeed = 0.0d;
		double grad = this.getGradOfRam();

		currentRequestedSpeed = getRequestedUtilization(timeSpan, grad,
				currentRequestedSpeed, RAM_INDEX);

		return currentRequestedSpeed;
	}

	public double getRequestedUtilizationOfStorageIO(final double timeSpan) {
		double currentRequestedSpeed = 0.0d;
		double grad = this.getGradOfStorageIO();

		currentRequestedSpeed = getRequestedUtilization(timeSpan, grad,
				currentRequestedSpeed, STORAGE_INDEX);

		return currentRequestedSpeed;
	}

	private long getFirstBound(long instructions, int resource) {

		for (int i = 0; i < data.length; i++) {
			if (data[i][INST_INDEX] == instructions) {
				return data[i][resource];
			} else if (data[i][INST_INDEX] > instructions) {
				return data[i - 1][resource];
			}
		}

		return data[data.length - 1][resource];
	}

	public void setUtilizationOfCpu(double value) {
		this.mips = value;
	}

	public void setUtilizationOfBandwidth(double value) {
		this.bandwidth = value;
	}

	public void setUtilizationOfStorage(double value) {
		this.storageIO = value;
	}

	public void setUtilizationOfRam(double value) {
		this.ram = value;
	}

	/**
	 * 
	 * @return the number of instructions to the next utilization change caused
	 *         by this cloudlet
	 */
	public long getNextUtilizationChange() {

		long dist = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i][INST_INDEX] == instructionsFinishedSoFar) {
				if (data.length - 1 > i) {
					dist = data[i + 1][INST_INDEX] - instructionsFinishedSoFar;
					break;
				} else {
					// instructionsFinishedSoFar is right on the last measuring
					// point
					break;
				}
			} else if (data[i][INST_INDEX] > instructionsFinishedSoFar) {
				dist = data[i][INST_INDEX] - instructionsFinishedSoFar;
				break;
			}
		}

		return dist;
	}

	@Override
	public double getUtilizationOfRam(final double time) {
		return this.ram;
	}

	@Override
	public double getUtilizationOfBw(final double time) {
		return this.bandwidth;
	}

	@Override
	public void setCloudletFinishedSoFar(final long length) {
		super.setCloudletFinishedSoFar(length);
		if (record) {
			recorder.println(CloudSim.clock() + "," + this.mips + ","
					+ this.getUtilizationOfRam(0.0) + "," + this.bandwidth
					+ "," + this.storageIO);
		}
	}

	public long getInstructionsFinishedSoFar() {
		return instructionsFinishedSoFar;
	}

	public void setInstructionsFinishedSoFar(long instructionsFinishedSoFar) {
		this.instructionsFinishedSoFar = instructionsFinishedSoFar;
	}

	public void updateInstructionsFinishedSoFar(long instructionsFinishedSoFar) {
		this.instructionsFinishedSoFar += instructionsFinishedSoFar;
	}

	public long getRemainingCloudletLength() {
		return super.getCloudletLength() * Consts.MILLION
				- instructionsFinishedSoFar;
	}

	public void stopRecording() {
		if (this.record) {
			recorder.close();
		}
	}

	/* reads the CSV file */
	private ArrayList<String[]> readFile(File file, char delimeter) {
		ArrayList<String[]> entries = null;
		CsvReader reader = null;
		try {
			Log.printLine("reading csv file: " + file.getAbsolutePath());
			reader = new CsvReader(new FileReader(file), delimeter);
			entries = reader.readAll();
		} catch (Exception e) {
			Log.printLine("There was an error while reading the CSV file: "
					+ e.getMessage());
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				// do nothing
			}
		}
		if (entries.size() < 1) {
			Log.printLine("No entries found in csv file.");
		}
		return entries;
	}
}
