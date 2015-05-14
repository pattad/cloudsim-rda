package ch.uzh.ifi.csg.cloudsim.rda;

import java.util.List;

public interface RdaCloudletScheduler {
	
	public double updateVmProcessing(double currentTime, List<Double> mipsShare, double bwShare, double storageIOShare);
	public List<Double> getCurrentRequestedMips(double currentTime);
	public double getCurrentRequestedUtilizationOfRam(double currentTime);
	public double getCurrentRequestedUtilizationOfBw(double currentTime);
	public double getCurrentRequestedUtilizationOfStorageIO(double currentTime);
	public double getCurrentRequestedGradCpu();
	public double getCurrentRequestedGradBw();
	public double getCurrentRequestedGradStorageIO();

}
