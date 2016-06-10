

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
	
	public void executeOnFirstCPU(Instruction theInstruction) {
		cpu1.execute(theInstruction);
	}
	
	public void executeOnSecondCPU(Instruction theInstruction) {
		cpu2.execute(theInstruction);
	}

	/**
	 * Snoops the read request; the non-caller CPU checks its L1 and L2 caches for a cachline<br>
	 * matchin the request.
	 *
	 * @param theCPU the requesting CPU
	 * @param theAddress the address that is being located
     * @return the latency associated with the read request
     */
	public static int snoopReadCPU(final CPU theCPU, final int theAddress, final char instr_type){
		int time = 0;
		CPU firstCPU, secondCPU;
		Cache firstCPU_Cache, secondCPU_Cache;
		
		if (theCPU.getName().equals("cpu1")) {
			firstCPU = theCPU;
			secondCPU = cpu2;
		} else {
			secondCPU = cpu1;
			firstCPU = theCPU;
		}

		if(instr_type == 'i'){
			firstCPU_Cache = firstCPU.getL1iCache();
			secondCPU_Cache = secondCPU.getL1iCache();
		}else{
			firstCPU_Cache = firstCPU.getL1dCache();
			secondCPU_Cache = secondCPU.getL1dCache();
		}

		int secondCPU_L1index = secondCPU_Cache.indexOfCache(theAddress);
		time += secondCPU_Cache.getLatency();
		// Inspect CPU2's L1 cache
		if(secondCPU_L1index != -1){
			time += writeToMemory(theAddress);
			// TODO: state change
			firstCPU_Cache.insertData(theAddress);
			firstCPU.getL2Cache().insertData(theAddress);
			firstCPU.getL3Cache().insertData(theAddress);
			

			if(secondCPU_Cache.isCacheLineModified(secondCPU_L1index)){
				Bus.updateCachesState(firstCPU, secondCPU, firstCPU_Cache,
									  secondCPU_Cache,CacheLine.MESIState.SHARED,theAddress, true);
				// TODO: ESCLUSIVE->SHARED
				secondCPU.getL2Cache().insertData(theAddress);
			}else if(secondCPU_Cache.isCacheLineExclusive(secondCPU_L1index)){
				// TODO: ESCLUSIVE->SHARED
				Bus.updateCachesState(firstCPU, secondCPU, firstCPU_Cache,
									  secondCPU_Cache,CacheLine.MESIState.SHARED,theAddress, true);
				secondCPU.getL2Cache().insertData(theAddress);
			}
			return time;
		}

		int secondCPU_L2index = secondCPU.getL2Cache().indexOfCache(theAddress);
		time += secondCPU.getL2Cache().getLatency();
		// Inspect CPU2's L2 cache
		if(secondCPU_L2index != -1){
			writeToMemory(theAddress);
			//TODO: MODIFIED->SHARED
			firstCPU_Cache.insertData(theAddress);
			firstCPU.getL2Cache().insertData(theAddress);
			firstCPU.getL3Cache().insertData(theAddress);

			if(secondCPU.getL2Cache().isCacheLineModified(secondCPU_L2index)){
				Bus.updateCachesState(firstCPU, secondCPU, firstCPU_Cache,
									  secondCPU_Cache,CacheLine.MESIState.SHARED,theAddress, false);
				// TODO: ESCLUSIVE->SHARED

			}else if(secondCPU.getL2Cache().isCacheLineExclusive(secondCPU_L2index)){
				// TODO: ESCLUSIVE->SHARED
				Bus.updateCachesState(firstCPU, secondCPU, firstCPU_Cache,
									  secondCPU_Cache,CacheLine.MESIState.SHARED,theAddress, false);

			}
			return time;
		}
		time += oneLM.getLatency();
		if(oneLM.readToMemory(theAddress)){
			int firstCPU_L1index = firstCPU_Cache.insertData(theAddress);
			int firstCPU_L2index = firstCPU.getL2Cache().insertData(theAddress);
			int indexL3 = firstCPU.getL3Cache().insertData(theAddress);

			// INVALID->EXCLUSIVE
			firstCPU.getL3Cache().setCacheLineState(CacheLine.MESIState.INVALID,indexL3);
			firstCPU_Cache.setCacheLineState(CacheLine.MESIState.INVALID,firstCPU_L1index);
			firstCPU.getL2Cache().setCacheLineState(CacheLine.MESIState.INVALID,firstCPU_L2index);
		}

		time += twoLM.getLatency();
		if(twoLM.readToMemory(theAddress)){
			int firstCPU_L1index = firstCPU_Cache.insertData(theAddress);
			int firstCPU_L2index = firstCPU.getL2Cache().insertData(theAddress);
			int indexL3 = firstCPU.getL3Cache().insertData(theAddress);

			// INVALID->EXCLUSIVE
			firstCPU.getL3Cache().setCacheLineState(CacheLine.MESIState.INVALID,indexL3);
			firstCPU_Cache.setCacheLineState(CacheLine.MESIState.INVALID,firstCPU_L1index);
			firstCPU.getL2Cache().setCacheLineState(CacheLine.MESIState.INVALID,firstCPU_L2index);
		}
		return time;
	}

	private static void updateCachesState(final CPU CPU1,final CPU CPU2, final Cache CPU1_L1, final Cache CPU2_L1,
									      final CacheLine.MESIState theState, final int theAddress, boolean theFlag){
		int index;

		if(theFlag){
			index = CPU2_L1.indexOfCache(theAddress);
			CPU2_L1.setCacheLineState(theState,index);
			index = CPU2.getL2Cache().indexOfCache(theAddress);
			CPU2.getL2Cache().setCacheLineState(theState,index);
		}else{
			CPU1_L1.insertData(theAddress);
			index = CPU2_L1.indexOfCache(theAddress);
			CPU2_L1.setCacheLineState(theState,index);
			index = CPU2.getL2Cache().indexOfCache(theAddress);
			CPU2.getL2Cache().setCacheLineState(theState,index);
			index = CPU2.getL3Cache().indexOfCache(theAddress);
			CPU2.getL3Cache().setCacheLineState(theState,index);
		}

		index = CPU1_L1.indexOfCache(theAddress);
		CPU1_L1.setCacheLineState(theState,index);
		index = CPU1.getL2Cache().indexOfCache(theAddress);
		CPU1.getL2Cache().setCacheLineState(theState,index);
		index = CPU1.getL3Cache().indexOfCache(theAddress);
		CPU1.getL3Cache().setCacheLineState(theState,index);

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
//		firstCPU.getL3Cache().setCacheLineState(CacheLine.MESIState.EXCLUSIVE, firstCPU.getL3Cache().indexOfCache(theAddress));
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
		int L1i_index,L1d_index,L2_index;
		CPU secondCPU;
		
		if (theCPU.getName().equals("cpu1")) {
			secondCPU = cpu2;
		} else {
			secondCPU = cpu1;
		}

		L1i_index = secondCPU.getL1iCache().indexOfCache(theAddress);
		L1d_index = secondCPU.getL1dCache().indexOfCache(theAddress);
		L2_index = secondCPU.getL2Cache().indexOfCache(theAddress);

		// Inspect L1i
		if(L1i_index != -1){
			time += Bus.invalidateLineInCache(secondCPU.getL1iCache(), L1i_index);
			// TODO: SHARED->INVALID
		}
		// Inspect L1d
		if(L1d_index != -1){
			time += Bus.invalidateLineInCache(secondCPU.getL1dCache(), L1d_index);
			// TODO: SHARED->INVALID
		}
		// Inspect L2
		if(L2_index != -1){
			time += Bus.invalidateLineInCache(secondCPU.getL1iCache(), L2_index);
			// TODO: SHARED->INVALID
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
		System.out.println("Index of L3 cachline: " + index);
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
