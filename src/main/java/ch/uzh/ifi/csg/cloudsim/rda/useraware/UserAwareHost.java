package ch.uzh.ifi.csg.cloudsim.rda.useraware;

import java.util.Map;

public interface UserAwareHost {

	public Map<String, Float> getUserPriorities(double currentTime);

	public double updateVmsProcessing(double currentTime,
			Map<String, Float> priorities);
}
