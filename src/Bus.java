/**
 * Bus - transfers data between components of a computer
 * @author chris
 *
 */
public class Bus {
	
	private CPU cpu1;
	
	private CPU cpu2;
	
	private Memory oneLM;
	
	private Memory twoLM;
	
	private String writeScheme;


	public Bus(CPU c1, CPU c2, Memory oneLM, Memory twoLM, String theWriteScheme) {
		this.cpu1 = c1;
		this.cpu2 = c2;
		this.oneLM = oneLM;
		this.twoLM  = twoLM;
		this.writeScheme = theWriteScheme;
	}

	/**
	 * Checks the L1 & L2 caches of a CPU for a matching address.
	 */
	public static int snoopReadCPU(final CPU theCPU, final int theAddress){
		int time = 0;

		return time;
	}

	public static int snoopWriteCPU(final CPU theCPU, final int theAddress){
		int time = 0;

		return time;
	}

	public static int requestForOwnernship(CPU theCPU, int theAddress){
		int time = 0;

		return time;
	}
}
