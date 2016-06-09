import java.util.Map;

public class CPU {
	
	private Cache l1d;
	private Cache l1i;
	private Cache l2;
	private Cache l3;
	
	private int instructionsExecutedThusFar;
	
	public CPU(int associativity, Cache l3) {
		l1d = new Cache(1, 32, 16, 1);
		l1i = new Cache(1, 32, 16, 2);
		l2 = new Cache(1, 512, 16, 10);
	}
	public CPU(Cache l3, Map<String, String> config) {

		l1d = new Cache(Integer.parseInt(config.get("Cache Associativity")),
						Integer.parseInt(config.get("L1d/L1i size")),
						Integer.parseInt(config.get("Cache Line/Block size")),
						Integer.parseInt(config.get("L1d/L1i latency")));

		l1i = new Cache(Integer.parseInt(config.get("Cache Associativity")),
						Integer.parseInt(config.get("L1d/L1i size")),
						Integer.parseInt(config.get("Cache Line/Block size")),
						Integer.parseInt(config.get("L1d/L1i latency")));

		l2 = new Cache(Integer.parseInt(config.get("Cache Associativity")),
					   Integer.parseInt(config.get("L2 size")),
					   Integer.parseInt(config.get("Cache Line/Block size")),
					   Integer.parseInt(config.get("L2 latency")));
		this.l3 = l3;
	}

	public int execute(Instruction instructionToExecute) {
		int timeToExecute = 0;
		instructionsExecutedThusFar ++;
		timeToExecute += readInstruction(instructionToExecute.getInstructionAddress());
		if(instructionToExecute.getAccessMemory()) {
			timeToExecute += read(instructionToExecute.getDataAddress());
		} else {
			timeToExecute += write(instructionToExecute.getDataAddress());
		}
		return timeToExecute;
	}

	public int getNumberOfInstructionsExecuted() {
		return instructionsExecutedThusFar;
	}

	public int readInstruction(int address) {
		int timeToRead = 0;
		timeToRead += l1i.getLatency();
		// Inspect L1i cache
		if(l1i.indexOfCache(address) != Cache.NOT_IN_CACHE) { // Is in L1i
			return timeToRead;
		}
		timeToRead += l2.getLatency();

		// Inspect L2 cache
		if(l2.indexOfCache(address) != Cache.NOT_IN_CACHE) { // Is in L2
			CacheLine L2_cacheline = l2.getCacheLine(l2.indexOfCache(address));
			CacheLine.MESIState L2_state = L2_cacheline.getState();
			int L1i_index = l1i.insertData(address);
			l1i.setCacheLineState(L2_state,L1i_index); // Set L1i's state -> L2's state
			return timeToRead;
		}
		timeToRead += l3.getLatency();

		// Inspect L3 cache
		if(l3.indexOfCache(address) != Cache.NOT_IN_CACHE) { // Is in L3
			CacheLine L3_cacheline = l3.getCacheLine(l3.indexOfCache(address));
			CacheLine.MESIState L3_state = L3_cacheline.getState();
			int L1i_index = l1i.insertData(address);
			l1i.setCacheLineState(L3_state,L1i_index); // Set L1i's state -> L3's state
			int L2_index = l2.insertData(address);
			l2.setCacheLineState(L3_state,L2_index); // Set L2's state -> L3's state
			return timeToRead;
		}
		timeToRead += Bus.snoopReadCPU(this, address);

		return timeToRead;
	}

	/**
	 * Attempts to read from a given address by inspecting L1d->L2->L3 caches.<br>
	 * Sends a request to snoop the other CPU if missed in all 3 caches.<br>
	 *
	 * @param address the address being located
	 * @return the latency associated with the read
     */
	public int read(int address) {
		int timeToRead = 0;
		timeToRead += l1d.getLatency();
		// Inspect L1d cache
		if(l1d.indexOfCache(address) != Cache.NOT_IN_CACHE) { // Is in L1
			return timeToRead;
		}
		timeToRead += l2.getLatency();

		// Inspect L2 cache
		if(l2.indexOfCache(address) != Cache.NOT_IN_CACHE) { // Is in L2
			CacheLine L2_cacheline = l2.getCacheLine(l2.indexOfCache(address));
			CacheLine.MESIState L2_state = L2_cacheline.getState();
			int L1d_index = l1d.insertData(address);
			l1d.setCacheLineState(L2_state,L1d_index); // Set L1d's state -> L2's state
			return timeToRead;
		}
		timeToRead += l3.getLatency();

		// Inspect L3 cache
		if(l3.indexOfCache(address) != Cache.NOT_IN_CACHE) { // Is in L3
			CacheLine L3_cacheline = l3.getCacheLine(l3.indexOfCache(address));
			CacheLine.MESIState L3_state = L3_cacheline.getState();
			int L1d_index = l1d.insertData(address);
			l1d.setCacheLineState(L3_state,L1d_index); // Set L1d's state -> L3's state
			int L2_index = l2.insertData(address);
			l2.setCacheLineState(L3_state,L2_index); // Set L2's state -> L3's state
			// Set state and increase time.
			return timeToRead;
		}
		timeToRead += Bus.snoopReadCPU(this, address);

		return timeToRead;
	}

	/**
	 * Attempts to perform a write to a given data address by<br>
	 * first inspecting the L1d->L2->L3 caches. If a hit does<br>
	 * not occur then a write request is sent to the bus.
	 *
	 * @param address the data address being located
     * @return the latency associated with the write
     */
	public int write(int address) {
		int timeToWrite = 0;
		timeToWrite += l1d.getLatency();
		if(l1d.indexOfCache(address) != Cache.NOT_IN_CACHE){ // Is in L1
			int L1d_index = l1d.indexOfCache(address);
			if (l1d.isCacheLineModified(L1d_index))
			{
				// cacheline is updated and the state remains MODIFIED
			}
			else if (l1d.isCacheLineExclusive(L1d_index))
			{
				l1d.setCacheLineState(CacheLine.MESIState.MODIFIED, L1d_index);
				int L2_index = l2.indexOfCache(address);
				l2.setCacheLineState(CacheLine.MESIState.MODIFIED, L2_index);

				// TODO: Increment EXCLUSIVE->MODIFIED in bus state change counter
			}
			else if (l1d.isCacheLineShared(L1d_index))
			{
				timeToWrite += Bus.requestForOwnernship(this, address);
				l1d.setCacheLineState(CacheLine.MESIState.MODIFIED, L1d_index);
				int L2_index = l2.indexOfCache(address);
				l2.setCacheLineState(CacheLine.MESIState.MODIFIED, L2_index);

				// TODO: Increment SHARED->MODIFIED in bus state change counter
			}
			return timeToWrite;
			// Inspect L2 cache
		}else if(l2.indexOfCache(address) != Cache.NOT_IN_CACHE){ // Is in L2
			timeToWrite += l2.getLatency();
			int L2_index = l2.indexOfCache(address);
			if (l2.isCacheLineModified(L2_index))
			{
				// cacheline is updated and the state remains MODIFIED
			}
			else if (l2.isCacheLineExclusive(L2_index))
			{
				l2.setCacheLineState(CacheLine.MESIState.MODIFIED, L2_index);
				L2_index = l2.indexOfCache(address);
				l2.setCacheLineState(CacheLine.MESIState.MODIFIED, L2_index);

				// TODO: Increment EXCLUSIVE->MODIFIED in bus state change counter
			}
			else if (l2.isCacheLineShared(L2_index))
			{
				timeToWrite += Bus.requestForOwnernship(this, address);
				L2_index = l2.indexOfCache(address);
				l2.setCacheLineState(CacheLine.MESIState.MODIFIED, L2_index);

				// TODO: Increment SHARED->MODIFIED in bus state change counter
			}

			int L1d_index = l1d.insertData(address);
			l1d.setCacheLineState(CacheLine.MESIState.MODIFIED, L1d_index);
			// TODO: Increment SHARED->MODIFIED in bus state change counter
			timeToWrite += l1d.getLatency();
			return timeToWrite;
		// Inspect L3
		}else if(l3.indexOfCache(address) != Cache.NOT_IN_CACHE){
			timeToWrite += l3.getLatency();
			int L3_index = l3.indexOfCache(address);
			if (l3.isCacheLineModified(L3_index))
			{
				// cacheline is updated and the state remains MODIFIED
			}
			else if (l3.isCacheLineExclusive(L3_index))
			{
				l3.setCacheLineState(CacheLine.MESIState.MODIFIED, L3_index);
				L3_index = l3.indexOfCache(address);
				l3.setCacheLineState(CacheLine.MESIState.MODIFIED, L3_index);

				// TODO: Increment EXCLUSIVE->MODIFIED in bus state change counter
			}
			else if (l3.isCacheLineShared(L3_index))
			{
				timeToWrite += Bus.requestForOwnernship(this, address);
				L3_index = l3.indexOfCache(address);
				l3.setCacheLineState(CacheLine.MESIState.MODIFIED, L3_index);

				// TODO: Increment SHARED->MODIFIED in bus state change counter
			}

			int L1d_index = l1d.insertData(address);
			l1d.setCacheLineState(CacheLine.MESIState.MODIFIED, L1d_index);
			int L2_index = l1d.insertData(address);
			l2.setCacheLineState(CacheLine.MESIState.MODIFIED, L2_index);
			// TODO: Increment SHARED->MODIFIED in bus state change counter
			timeToWrite += l1d.getLatency();
			return timeToWrite;
		}else{
			timeToWrite += Bus.snoopWriteCPU(this, address);
			return timeToWrite;
		}
	}


	public Cache getL1iCache(){
		return l1i;
	}
	public Cache getL2Cache(){
		return l2;
	}


	public void evictCacheLine(CacheLine toEvict) {

	}
}
