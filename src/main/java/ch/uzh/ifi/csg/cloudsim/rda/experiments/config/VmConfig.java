package ch.uzh.ifi.csg.cloudsim.rda.experiments.config;

public class VmConfig {

	private String vmm = "Xen"; // VMM name
	private int mips = 300;
	private int ram = 512; // host memory (MB)
	private int bw = 1000; // MBit/s
	private int peCnt = 1;
	private long size = 10000; // image size (MB)

	public VmConfig(String vmm, int peCnt, int mips, int ram, long size, int bw) {
		super();
		this.vmm = vmm;
		this.mips = mips;
		this.ram = ram;
		this.size = size;
		this.bw = bw;
		this.peCnt = peCnt;
	}
	public VmConfig() {
		super();
	}
	
	public int getPeCnt() {
		return peCnt;
	}

	public int getMips() {
		return mips;
	}

	public int getRam() {
		return ram;
	}

	public int getBw() {
		return bw;
	}

	public String getVmm() {
		return vmm;
	}

	public long getSize() {
		return size;
	}

	@Override
	public String toString() {
		return "VmConfig [vmm=" + vmm + ", mips=" + mips + ", ram=" + ram
				+ ", bw=" + bw + ", peCnt=" + peCnt + ", size=" + size + "]";
	}

}
