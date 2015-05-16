package ch.uzh.ifi.csg.cloudsim.rda.useraware;

import java.util.Map;

/**
 * This interface is intended for hosts that support the user aware mechanisms.
 * The methods allow the transfer of a map with user priorities.
 * 
 * @author Patrick A. Taddei
 * @see UserAwareDatacenter
 * @see RdaUserAwareVmScheduler
 */
public interface UserAwareHost {

	/**
	 * To retrieve the priorities of all users/customers that have a running VM
	 * on this host.
	 * 
	 * @param currentTime
	 *            The current simulation time.
	 * @return A map, that contains the priority associated with each user.
	 */
	public abstract Map<String, Float> getUserPriorities(double currentTime);

	/**
	 * This is the corresponding method for
	 * <code> public double updateVmsProcessing(double currentTime) </code>
	 * Hosts, supporting this interface, should throw an
	 * UnsupportedOperationException when calling this default method.
	 * 
	 * @param currentTime
	 *            The current simulation time.
	 * @param priorities
	 *            A map, that contains the priority associated with each user.
	 * @return the time of the next expected event for the concerning host.
	 */
	public abstract double updateVmsProcessing(double currentTime,
			Map<String, Float> priorities);
}
