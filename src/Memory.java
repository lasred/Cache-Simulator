
public class Memory {
	
	private int size;
	
	private int latency;
	
	private int numOfWrites;
	
	private int memoryLeft;
	
	public Memory(int size, int latency) {
		this.size = size;
		this.latency = latency;
		this.numOfWrites = 0;
		memoryLeft = size;
	}
	
	public boolean writeToMemory(int address) {
		boolean result = false;
		if (address < size && memoryLeft >= 32) { // 32 bits for each address.
			memoryLeft -= 32;
			result = true;
			numOfWrites++;
		}
		return result;
	}
	
	public boolean isFull() {
		return !(memoryLeft >= 32);
	}
	
	public int getLatency() {
		return latency;
	}
}
