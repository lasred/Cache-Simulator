
public class Memory {
	
	private int size;
	private int latency;
	private int numOfWrites;
	private int accesses;
	private int memoryLeft;
	
	public Memory(int size, int latency) {
		this.size = size;
		this.latency = latency;
		this.numOfWrites = 0;
		memoryLeft = size;
	}
	
	public boolean writeToMemory(int address) {
		boolean result = false;
		if (address < size && memoryLeft >= 4) { // 4 bytes for each address.
			memoryLeft -= 4;
			result = true;
			numOfWrites++;
		}
		return result;
	}
	
	public boolean isFull() {
		return !(memoryLeft >= 4);
	}
	
	public int getLatency() {
		return latency;
	}
	
	public int getAccess() {
		return accesses;
	}
}
