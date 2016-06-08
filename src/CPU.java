import java.util.Map;

public class CPU {
	
	private Cache l1d;
	private Cache l1i;
	private Cache l2;
	private Cache l3;
	
	private int instructionsExecutedThusFar;
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
			timeToExecute += readData(instructionToExecute.getDataAddress());
		} else {
			timeToExecute += writeData(instructionToExecute.getDataAddress());
		}
		return timeToExecute;
		
	}
	
	public int writeData(int dataAddress) {
		return 0;
	}
	
	public int getNumberOfInstructionsExecuted() {
		return instructionsExecutedThusFar;
	}
	
	public int readInstruction(int address) {
		return read(address, l1i);
	}	
	public int readData(int address) {
		return read(address, l1d);
	}
	
	public int read(int address, Cache l1Cache) {
		int timeToRead = 0;
		timeToRead += l1Cache.getLatency();
		if(l1Cache.indexOfCache(address) != Cache.NOT_IN_CACHE) {
			return timeToRead;
		} 
		timeToRead += l2.getLatency();
		//try l2 cache
		if(l2.indexOfCache(address) != Cache.NOT_IN_CACHE) {
			return timeToRead;
		}
		timeToRead += l3.getLatency();
		if(l3.indexOfCache(address) != Cache.NOT_IN_CACHE) {
			return timeToRead;
		}
		return timeToRead;
	}
	
	public void evictCacheLine(CacheLine toEvict) {
		
	}
}
