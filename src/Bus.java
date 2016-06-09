

/**
 * Bus - transfers data between components of a computer
 * @author chris
 *
 */
public class Bus {
	
	private static CPU cpu1;
	
	private static CPU cpu2;
	
	private static Memory oneLM;
	
	private static Memory twoLM;
	
	private static String writeScheme;


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
			// Check if its also in L2. 
			if (secondCPU.getL2Cache().indexOfCache(theAddress) != Cache.NOT_IN_CACHE) {
				// Set state to invalid.
				invalidateLineInCache(secondCPU.getL2Cache(), secondCPU.getL2Cache().indexOfCache(theAddress));
			// If L1 has been modified then save address to memory and invalidate cache lines.
			} else if (secondCPU.getL1dCache().isCacheLineModified(secondCPU.getL1dCache().indexOfCache(theAddress))) {
				time += writeToMemory(theAddress);
				invalidateLineInCache(secondCPU.getL2Cache(), secondCPU.getL2Cache().indexOfCache(theAddress));
			}
				invalidateLineInCache(secondCPU.getL1dCache(),secondCPU.getL1dCache().indexOfCache(theAddress));
				// Write to L3.
				writeL3(firstCPU, CacheLine.MESIState.MODIFIED, theAddress);
				// Write to the first CPU's cache's.
				writeL1andL2(firstCPU, 1, CacheLine.MESIState.MODIFIED, theAddress);
				// Add the latency into the time.
				time += firstCPU.getL1dCache().getLatency() + firstCPU.getL2Cache().getLatency() + firstCPU.getL3Cache().getLatency();
				return time;
		}
		// Check L2 cache.
		if (secondCPU.getL2Cache().indexOfCache(theAddress) != Cache.NOT_IN_CACHE) {
			// If it's modified we need to write into memory and invalidate.
			if (secondCPU.getL2Cache().isCacheLineModified(secondCPU.getL2Cache().indexOfCache(theAddress))) {
				time += writeToMemory(theAddress);
				
			}
				invalidateLineInCache(secondCPU.getL2Cache(), secondCPU.getL2Cache().indexOfCache(theAddress));
				// Write to L3.
				writeL3(firstCPU, CacheLine.MESIState.MODIFIED, theAddress);
				// Write to the first CPU's cache's.
				writeL1andL2(firstCPU, 1, CacheLine.MESIState.MODIFIED, theAddress);
				// Add the latency into the time.
				time += firstCPU.getL1dCache().getLatency() + firstCPU.getL2Cache().getLatency() + firstCPU.getL3Cache().getLatency();
				return time;
		}
		// No need to check L3 because it is being done in the CPU.
		
		/** 
		 * If we get this far then we know its not in the second CPU's
		 * caches, therefore we must just write to memory and to all
		 * of the first CPU's caches (and mark them as exclusive).
		 */
		time += writeToMemory(theAddress);
		// Write to L3.
		writeL3(firstCPU, CacheLine.MESIState.EXCLUSIVE, theAddress);
		// Write to the first CPU's cache's.
		writeL1andL2(firstCPU, 1, CacheLine.MESIState.EXCLUSIVE, theAddress);
		// Mark L3 as exclusive. 
		firstCPU.getL3Cache().setCacheLineState(CacheLine.MESIState.EXCLUSIVE, firstCPU.getL3Cache().indexOfCache(theAddress));
		// Add the latency into the time.
		time += firstCPU.getL1dCache().getLatency() + firstCPU.getL2Cache().getLatency() + firstCPU.getL3Cache().getLatency();
		return time;
	}
	
	/**
	 * Private helper to invalidate a cacheline within a cache.
	 *
	 * @param theCache the cache being inspected
	 * @param theIndex the index for locating the cacheline
     * @return the latency for accessing the particular cache
     */
	private static int invalidateLineInCache(Cache theCache, int theIndex){
		theCache.setCacheLineState(CacheLine.MESIState.INVALID, theIndex);
		return theCache.getLatency();
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
	
	public static void writeL3(CPU theCPU, CacheLine.MESIState mesi, int theAddress) { 
		Cache L3 = theCPU.getL3Cache();
		int index = L3.insertData(theAddress);
		L3.setCacheLineState(mesi, index);
	}
	
	public static void writeL1andL2(CPU theCPU, int flag, CacheLine.MESIState mesi, int theAddress) { // If flag == 1 then use l1d, else then use l1i
		writeL1(theCPU, flag, mesi, theAddress);
		writeL2(theCPU, mesi, theAddress);
	}

	
	public static int writeToMemory(int theAddress) {
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
