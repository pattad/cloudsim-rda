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

	// current allocated resource values
	private double mips;
	private double bandwidth;
	private double storageIO;
	private double ram;

	private boolean record = true;

	// instructions already processed by this cloudlet
	private long instructionsFinishedSoFar = 0l;

	// input array for requested resources
	// columns: INSTRUCTIONS, CPU, RAM, BW, STORAGE
	// e.g.values: 0, 200, 50, 55, 1
	// 205000000, 210, 40, 22, 2
	// 415000000, 210, 41, 23, 1
	// the INSTRUCTIONS column contains the cumulated processed instructions
	// average between CPU bounds is taken as length to get a linear behavior
	// between 2 bounds.
	private final double[][] data;

	/* index in input data array */
	/* INST_INDEX is the cumulated instruction counter index */
	private static final int INST_INDEX = 0;
	private static final int CPU_INDEX = 1;
	private static final int RAM_INDEX = 2;
	private static final int BW_INDEX = 3;
	private static final int STORAGE_INDEX = 4;

	// for CSV output with processing values.
	private PrintWriter recorder;

	/**
	 * Instantiates the Cloudlet.
	 * 
	 * @param cloudletId
	 *            the unique ID of this Cloudlet
	 * @param cloudletFileSize
	 *            the file size (in byte) of this cloudlet <tt>BEFORE</tt>
	 *            submitting to a PowerDatacenter
	 * @param cloudletOutputSize
	 *            the file size (in byte) of this cloudlet <tt>AFTER</tt> finish
	 *            executing by a PowerDatacenter
	 * @param inputPath
	 *            The path to the CSV file.
	 * @param record
	 *            True, if the output should be written into a CSV file. (File
	 *            name is: yyyyMMddhhmmssSSS.csv)
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public RdaCloudlet(int cloudletId, int pesNumber, long cloudletFileSize,
			long cloudletOutputSize, String inputPath, boolean record)
			throws FileNotFoundException, UnsupportedEncodingException {
		super(cloudletId, 0, pesNumber, cloudletFileSize, cloudletOutputSize,
				null, null, null, false, new LinkedList<String>());

		ArrayList<double[]> inputData = this.readFile(new File(inputPath), ',');
		data = initialize(inputData, record);
	}

	/**
	 * Instantiates the Cloudlet.
	 * 
	 * @param cloudletId
	 *            the unique ID of this Cloudlet
	 * @param cloudletFileSize
	 *            the file size (in byte) of this cloudlet <tt>BEFORE</tt>
	 *            submitting to a PowerDatacenter
	 * @param cloudletOutputSize
	 *            the file size (in byte) of this cloudlet <tt>AFTER</tt> finish
	 *            executing by a PowerDatacenter
	 * @param inputData
	 *            The input utilization data of the cloudlet.
	 * @param record
	 *            True, if the output should be written into a CSV file. (File
	 *            name is: yyyyMMddhhmmssSSS.csv)
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public RdaCloudlet(int cloudletId, int pesNumber, long cloudletFileSize,
			long cloudletOutputSize, ArrayList<double[]> inputData,
			boolean record) throws FileNotFoundException,
			UnsupportedEncodingException {
		super(cloudletId, 0, pesNumber, cloudletFileSize, cloudletOutputSize,
				null, null, null, record, new LinkedList<String>());

		data = initialize(inputData, record);
	}

	private double[][] initialize(ArrayList<double[]> inputData, boolean record)
			throws FileNotFoundException, UnsupportedEncodingException {
		double[][] result = new double[inputData.size()][5];

		int i = 0;
		double instructions = 0;

		double lastMips = 0;
		for (double[] entry : inputData) {
			double mips = entry[INST_INDEX];
			if (i == 0) {
				result[i][INST_INDEX] = 0;
			} else {
				// average instructions in the timeframe
				instructions += (lastMips + mips) / 2.0;
				result[i][INST_INDEX] = Math.round(instructions
						* Consts.MILLION);
			}
			result[i][CPU_INDEX] = entry[0];
			result[i][RAM_INDEX] = entry[1];
			result[i][BW_INDEX] = entry[2];
			result[i][STORAGE_INDEX] = entry[3];

			lastMips = mips;
			i++;
		}

		super.setCloudletLength((long) instructions);

		this.mips = result[0][CPU_INDEX]; // initial mips TODO

		this.record = record;

		if (this.record) {

			recorder = new PrintWriter(new File("workload_trace_"
					+ super.getCloudletId() + ".csv").getAbsoluteFile(),
					"UTF-8");
			recorder.println("time,cpu,memory,bandwidth,storageIO,delay");
		}

		return result;
	}

	@Override
	public long getCloudletTotalLength() {
		return getCloudletLength();
	}

	@Override
	public double getUtilizationOfCpu(final double time) {
		return mips;
	}

	private double getFirstBoundOfCpu() {
		return getFirstBound(this.instructionsFinishedSoFar, CPU_INDEX);
	}

	/**
	 * 
	 * @return Returns the gradient of the CPU.
	 */
	public double getGradOfCpu() {
		double pastCPU = this.getFirstBoundOfCpu();
		double futureProcessingSpeed = this.getFirstBound(
				this.getNextUtilizationChange()
						+ this.instructionsFinishedSoFar, CPU_INDEX);
		return futureProcessingSpeed - pastCPU;
	}

	/**
	 * 
	 * @return Returns the gradient of the bandwidth
	 */
	public double getGradOfBw() {
		double past = this.getFirstBound(this.instructionsFinishedSoFar,
				BW_INDEX);
		double future = this.getFirstBound(this.getNextUtilizationChange()
				+ this.instructionsFinishedSoFar, BW_INDEX);
		return future - past;
	}

	/**
	 * 
	 * @return Returns the gradient of storage I/O.
	 */
	public double getGradOfStorageIO() {
		double past = this.getFirstBound(this.instructionsFinishedSoFar,
				STORAGE_INDEX);
		double future = this.getFirstBound(this.getNextUtilizationChange()
				+ this.instructionsFinishedSoFar, STORAGE_INDEX);
		return future - past;
	}

	/**
	 * 
	 * @return Returns the gradient of ram.
	 */
	public double getGradOfRam() {
		double past = this.getFirstBound(this.instructionsFinishedSoFar,
				RAM_INDEX);
		double future = this.getFirstBound(this.getNextUtilizationChange()
				+ this.instructionsFinishedSoFar, RAM_INDEX);
		return future - past;
	}

	/**
	 * Assesses the current progress of the cloudlet and computes the time to
	 * the next consumption change. This corresponds the given input values.
	 * Generally, a consumption change might be a change of the gradient.
	 * 
	 * @return The estimated time when there is a consumption change.
	 */
	public double getEstimatedNextChangeTime() {

		double grad = this.getGradOfCpu();

		double nextInstructionChange = this.getNextUtilizationChange();

		if (grad == 0.0d) {
			// if the gradient is 0, we can just use the standard formula for
			// distance measurements
			// time=distance/speed
			return new BigDecimal(nextInstructionChange)
					.divide(new BigDecimal(this.getFirstBoundOfCpu()),
							MathContext.DECIMAL64)
					.divide(new BigDecimal(Consts.MILLION),
							MathContext.DECIMAL64).doubleValue();

		} else {
			double past = this.getUtilizationOfCpu(0.0d);
			double instructionsToProcess = (double) nextInstructionChange
					/ (double) Consts.MILLION;

			// calculating the expected time to be finished with the current
			// instruction interval
			// function: grad * x + past
			// integral function: grad/2 * x2 + past*x
			//
			// we know instructionsToProcess, thereafter
			// instructionsToProcess = grad/2 * x2 + past*x
			//
			// resolving after x, with the standard formula for
			// squared equations
			// we get the expected time till the end of the interval.
			// (x = time)

			double discriminant = past * past + 2.0 * grad
					* instructionsToProcess;

			double time = ((-past + Math.sqrt(discriminant)) / grad);
			return time;
		}

	}

	/**
	 * This method computes the requested utilization at the desired time.
	 * 
	 * To retrieve this value the current progress of the Cloudlet is taken into
	 * account. This is done by using the instructionsFinishedSoFar and
	 * calculating from that the expected time to be in the current timeframe.
	 * 
	 * When we have this time, we retrieve the requested value at this time
	 * (pastTime). To get then to the requested value at the desired time span,
	 * the gradient and the pastTime can be set into a simple linear function.
	 * 
	 * @param timeSpan
	 *            Time span since last resource processing.
	 * @param resourceGrad
	 *            The gradient of the resource to be evaluated.
	 * @param resourceIndex
	 *            The resource index in the array.
	 * @return the requested utilization
	 */
	private double getRequestedUtilization(final double timeSpan,
			double resourceGrad, int resourceIndex) {

		double currentRequestedUtilization = 0.0d;

		// we go thru all timeframe starting points
		for (int i = 0; i < data.length; i++) {
			if (data[i][INST_INDEX] == instructionsFinishedSoFar) {
				// we are right on the beginning of an instruction interval
				// f(timeSpan) = grad*timeSpan + past
				currentRequestedUtilization = resourceGrad * timeSpan
						+ data[i][resourceIndex];
				break;
			} else if (data[i][INST_INDEX] >= instructionsFinishedSoFar) {
				// we get to the first instruction interval that we have not
				// finished yet
				if (resourceGrad != 0) {
					double expectedTime;
					double pastRequestedUtilization;

					// calculate the expected point in time, in the interval,
					// where the cloudlet currently is
					if (this.getGradOfCpu() != 0) {
						double pastSpeedCpu = data[i - 1][CPU_INDEX];
						double currentInst = instructionsFinishedSoFar
								- data[i - 1][INST_INDEX];

						// calculating the expected time depending from the
						// already processed instructions
						//
						// function: gradCpu * x + pastSpeedCpu
						// integral function: gradCpu/2 * x2 + pastSpeedCpu*x
						//
						// we know currentInst, thereafter
						// currentInst = gradCpu/2 * x2 + pastSpeedCpu*x
						//
						// resolving after x, with the standard formula for
						// squared equations
						// we get the expected time. (x = expectedTime)
						// where exactly in the current instructions interval we
						// are according to the instruction progress
						//
						double discriminant = (pastSpeedCpu * pastSpeedCpu) + 2
								* this.getGradOfCpu()
								* (currentInst / Consts.MILLION);

						expectedTime = (-pastSpeedCpu + Math.sqrt(discriminant))
								/ this.getGradOfCpu();

					} else {
						double currentInst = instructionsFinishedSoFar
								- data[i - 1][INST_INDEX];
						double instSpan = data[i][INST_INDEX]
								- data[i - 1][INST_INDEX];
						// if the grad of the CPU is 0, we simply take the
						// proportion within the timeframe
						expectedTime = currentInst / instSpan;
					}

					double pastUtilizationResource = data[i - 1][resourceIndex];

					// the requested utilization without the timeSpan
					pastRequestedUtilization = resourceGrad * expectedTime
							+ pastUtilizationResource;

					// the requested utilization with the timeSpan
					currentRequestedUtilization = resourceGrad * timeSpan
							+ pastRequestedUtilization;

				} else {
					// there is a constant value
					currentRequestedUtilization = data[i][resourceIndex];
				}
				break;
			}
		}
		return currentRequestedUtilization;
	}

	/**
	 * Computes the requested utilization at the desired time, depending from
	 * the current processing progress of this cloudlet.
	 * 
	 * @param timeSpan
	 *            Time span since last resource processing.
	 * 
	 * @return the requested utilization of the cpu
	 */
	public double getRequestedUtilizationOfCpu(final double timeSpan) {
		double grad = this.getGradOfCpu();
		double currentRequestedSpeed = 0.0d;

		currentRequestedSpeed = getRequestedUtilization(timeSpan, grad,
				CPU_INDEX);

		return currentRequestedSpeed;
	}

	/**
	 * Computes the requested utilization at the desired time, depending from
	 * the current processing progress of this cloudlet.
	 * 
	 * @param timeSpan
	 *            Time span since last resource processing.
	 * 
	 * @return the requested utilization of the bandwidth
	 */
	public double getRequestedUtilizationOfBw(final double timeSpan) {

		double grad = this.getGradOfBw();
		double currentRequestedSpeed = getRequestedUtilization(timeSpan, grad,
				BW_INDEX);

		return currentRequestedSpeed;
	}

	/**
	 * Computes the requested utilization at the desired time, depending from
	 * the current processing progress of this cloudlet.
	 * 
	 * @param timeSpan
	 *            Time span since last resource processing.
	 * 
	 * @return the requested utilization of the ram
	 */
	public double getRequestedUtilizationOfRam(final double timeSpan) {

		double grad = this.getGradOfRam();
		double currentRequestedSpeed = getRequestedUtilization(timeSpan, grad,
				RAM_INDEX);

		return currentRequestedSpeed;
	}

	/**
	 * Computes the requested utilization at the desired time, depending from
	 * the current processing progress of this cloudlet.
	 * 
	 * @param timeSpan
	 *            Time span since last resource processing.
	 * 
	 * @return the requested utilization of the storage I/O
	 */
	public double getRequestedUtilizationOfStorageIO(final double timeSpan) {
		double grad = this.getGradOfStorageIO();

		double currentRequestedSpeed = getRequestedUtilization(timeSpan, grad,
				STORAGE_INDEX);

		return currentRequestedSpeed;
	}

	/**
	 * Returns the value of the first bound in the instruction frame from the
	 * underlying array.
	 * 
	 * @param instructions
	 *            the instructions processed
	 * @param resource
	 *            the resource index in the array
	 * @return the resource value from the array input
	 */
	private double getFirstBound(long instructions, int resource) {

		for (int i = 0; i < data.length; i++) {
			if (data[i][INST_INDEX] == instructions) {
				return data[i][resource];
			} else if (data[i][INST_INDEX] > instructions) {
				return data[i - 1][resource];
			}
		}

		return data[data.length - 1][resource];
	}

	/**
	 * The actual current utilization value can be set with this method.
	 * 
	 * @param value
	 *            utilization value
	 */
	public void setUtilizationOfCpu(double value) {
		this.mips = value;
	}

	/**
	 * The actual current utilization value can be set with this method.
	 * 
	 * @param value
	 *            utilization value
	 */
	public void setUtilizationOfBandwidth(double value) {
		this.bandwidth = value;
	}

	/**
	 * The actual current utilization value can be set with this method.
	 * 
	 * @param value
	 *            utilization value
	 */
	public void setUtilizationOfStorage(double value) {
		this.storageIO = value;
	}

	/**
	 * The actual current utilization value can be set with this method.
	 * 
	 * @param value
	 *            utilization value
	 */
	public void setUtilizationOfRam(double value) {
		this.ram = value;
	}

	/**
	 * The method looks up the array and determines the number of instructions
	 * to the next bound.
	 * 
	 * 
	 * @return the number of instructions to the next utilization change caused
	 *         by this cloudlet
	 */
	public long getNextUtilizationChange() {

		long dist = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i][INST_INDEX] == instructionsFinishedSoFar) {
				if (data.length - 1 > i) {
					dist = (long) data[i + 1][INST_INDEX]
							- instructionsFinishedSoFar;
					break;
				} else {
					// instructionsFinishedSoFar is right on the last measuring
					// point
					break;
				}
			} else if (data[i][INST_INDEX] > instructionsFinishedSoFar) {
				dist = (long) data[i][INST_INDEX] - instructionsFinishedSoFar;
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

	/**
	 * Returns the utilization of storage I/O.
	 * 
	 * @return the utilization of storage I/O
	 */
	public double getUtilizationOfStorage() {
		return this.storageIO;
	}

	@Override
	public void setCloudletFinishedSoFar(final long length) {
		super.setCloudletFinishedSoFar(length);
		if (record) {

			double delay = 0.0;
			for (int i = 0; i < data.length; i++) {
				if (data[i][INST_INDEX] == this.instructionsFinishedSoFar) {
					if (i != 0) {
						// right on a instruction change
						delay = CloudSim.clock() - super.getExecStartTime()
								- CloudSim.getMinTimeBetweenEvents()
								- (double) i;
					}
					break;

				}
			}

			String trace = CloudSim.clock() + "," + this.mips + ","
					+ this.getUtilizationOfRam(0.0) + "," + this.bandwidth
					+ "," + this.storageIO;

			if (delay != 0.0) {
				trace += "," + delay;
			}
			recorder.println(trace);

		}
	}

	/**
	 * 
	 * @return the instructions already processed by this cloudlet
	 */
	public long getInstructionsFinishedSoFar() {
		return instructionsFinishedSoFar;
	}

	/**
	 * 
	 * @param instructionsFinishedSoFar
	 *            the instructions already processed by this cloudlet
	 */
	public void setInstructionsFinishedSoFar(long instructionsFinishedSoFar) {
		this.instructionsFinishedSoFar = instructionsFinishedSoFar;
	}

	/**
	 * 
	 * @param instructionsFinishedSoFar
	 *            the instructions to add to the already processed instructions
	 *            of this cloudlet
	 */
	public void updateInstructionsFinishedSoFar(long instructionsFinishedSoFar) {
		this.instructionsFinishedSoFar += instructionsFinishedSoFar;
	}

	/**
	 * 
	 * @return the remaining cloudlet length (in instructions) to be processed
	 *         by this cloudlet
	 */
	public long getRemainingCloudletLength() {
		return super.getCloudletLength() * Consts.MILLION
				- instructionsFinishedSoFar;
	}

	/**
	 * Stops the recording for this cloudlet Should be called, when it is
	 * finished.
	 */
	public void stopRecording() {
		if (this.record) {
			recorder.close();
		}
	}

	/* reads the CSV file */
	private ArrayList<double[]> readFile(File file, char delimeter) {
		ArrayList<double[]> entries = new ArrayList<double[]>();
		CsvReader reader = null;
		try {
			Log.printLine("reading csv file: " + file.getAbsolutePath());
			reader = new CsvReader(new FileReader(file), delimeter);
			ArrayList<String[]> result = reader.readAll();

			for (String[] line : result) {
				double[] entry = new double[4];
				entry[0] = Double.valueOf(line[0]);
				entry[1] = Double.valueOf(line[1]);
				entry[2] = Double.valueOf(line[2]);
				entry[3] = Double.valueOf(line[3]);

				entries.add(entry);
			}
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
