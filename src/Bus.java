

/**
 * Bus - transfers data between components of a computer
 * @author chris
 *
 */
public class Bus {
	
	private static CPU cpu1;
	
	private static CPU cpu2;
	
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
		CPU firstCPU;
		CPU secondCPU;
		
		if (theCPU.getName().equals("cpu1")) {
			firstCPU = theCPU;
			secondCPU = cpu2;
		} else {
			secondCPU = cpu1;
			firstCPU = theCPU;
		}

		return time;
	}

	public static int snoopWriteCPU(final CPU theCPU, final int theAddress){
		int time = 0;
		CPU firstCPU;
		CPU secondCPU;
		
		if (theCPU.getName().equals("cpu1")) {
			firstCPU = theCPU;
			secondCPU = cpu2;
		} else {
			secondCPU = cpu1;
			firstCPU = theCPU;
		}
		
		// Check if second cpu's L1 has the address. 
		if (secondCPU.getL1dCache().indexOfCache(theAddress) != Cache.NOT_IN_CACHE) {
			//firstCPU.get
		}

		return time;
	}

	public static int requestForOwnernship(CPU theCPU, int theAddress){
		int time = 0;
		CPU firstCPU;
		CPU secondCPU;
		
		if (theCPU.getName().equals("cpu1")) {
			firstCPU = theCPU;
			secondCPU = cpu2;
		} else {
			secondCPU = cpu1;
			firstCPU = theCPU;
		}

		return time;
	}
	
	public static void writeL1(CPU theCPU, int flag, CacheLine.MESIState mesi, int theAddress) { // If flag == 1 then use l1d, else then use l1i
		Cache L1 ;
		if (flag == 1) {
			L1 = theCPU.getL1dCache();
		} else {
			L1 = theCPU.getL1iCache();
		}
		
		int index = L1.insertData(theAddress);
		L1.setCacheLineState(mesi, index);
	}
	
	public static void writeL2(CPU theCPU, CacheLine.MESIState mesi, int theAddress) { 
		Cache L2 = theCPU.getL2Cache();
		int index = L2.insertData(theAddress);
		L2.setCacheLineState(mesi, index);
	}
	
	public static void writeL1andL2(CPU theCPU, int flag, CacheLine.MESIState mesi, int theAddress) { // If flag == 1 then use l1d, else then use l1i
		writeL1(theCPU, flag, mesi, theAddress);
		writeL2(theCPU, mesi, theAddress);
	}

	
	public int writeToMemory(int theAddress) {
		int time = 0;
		
		// If write back then we know that memory has already been written to.
		if (writeScheme.equals("Writethrough")) {
			// Try to write to 1LM because its faster. 
			if (!oneLM.isFull()) {
				oneLM.writeToMemory(theAddress);
				time += oneLM.getLatency();
			} else if (!twoLM.isFull()) {
				twoLM.writeToMemory(theAddress);
				time += twoLM.getLatency();
			} else {
				System.out.println("Memory is full!!");
			}
		}
		
		return time;
	}
}
