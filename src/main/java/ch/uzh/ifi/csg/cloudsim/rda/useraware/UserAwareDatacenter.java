package ch.uzh.ifi.csg.cloudsim.rda.useraware;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;

import ch.uzh.ifi.csg.cloudsim.rda.RdaDatacenter;

/**
 * 
 * @author Patrick A. Taddei
 */
public class UserAwareDatacenter extends RdaDatacenter {

	public UserAwareDatacenter(String name,
			DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy, List<Storage> storageList,
			double schedulingInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList,
				schedulingInterval);
	}

	@Override
	protected double processHosts(double currentTime, double minTime) {
		Map<String, Float> userPriorities = new HashMap<String, Float>();

		for (PowerHost host : this.<PowerHost> getHostList()) {

			Map<String, Float> updatedUsers = ((UserAwareHost) host)
					.getUserPriorities(currentTime);

			for (String userName : updatedUsers.keySet()) {
				if (userPriorities.containsKey(userName)) {
					Float currentVal = userPriorities.get(userName);
					userPriorities.put(userName,
							currentVal + updatedUsers.get(userName));
				} else {
					userPriorities.put(userName, updatedUsers.get(userName));
				}
			}
		}

		for (PowerHost host : this.<PowerHost> getHostList()) {
			Log.formatLine("\n%.2f: Host #%d", CloudSim.clock(), host.getId());

			double time = ((UserAwareHost) host).updateVmsProcessing(
					currentTime, userPriorities);

			if (time < minTime) {
				minTime = time;
			}
		}

		return minTime;
	}
}
