package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

/**
 * Encapsulates the different configuration parameters to setup a host.
 * 
 * @author Patrick A. Taddei
 *
 */
public class HostConfig {

	private int mips = 1000;
	private int ram = 2048; // host memory (MB)
	private long storage = 1000000; // host storage (MB)
	private int bw = 1000; // MBit/s
	private int storageIO = 4000;
	private int peCnt = 1;

	public HostConfig() {
		super();
	}

	public HostConfig(int peCnt, int mips, int ram, long storage, int bw,
			int storageIO) {
		super();
		this.mips = mips;
		this.ram = ram;
		this.storage = storage;
		this.bw = bw;
		this.storageIO = storageIO;
		this.peCnt = peCnt;
	}

	public List<Pe> getPeList() {
		List<Pe> peList = new ArrayList<Pe>();
		for (int i = 0; i < peCnt; i++) {
			peList.add(new Pe(0, new PeProvisionerSimple(mips)));
		}
		return peList;
	}

	public int getMips() {
		return mips;
	}

	public int getRam() {
		return ram;
	}

	public long getStorage() {
		return storage;
	}

	public int getBw() {
		return bw;
	}

	public int getStorageIO() {
		return storageIO;
	}

	@Override
	public String toString() {
		return "HostConfig [mips=" + mips + ", ram=" + ram + ", storage="
				+ storage + ", bw=" + bw + ", storageIO=" + storageIO
				+ ", peCnt=" + peCnt + "]";
	}
}
