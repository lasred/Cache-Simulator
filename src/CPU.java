
public class CPU {
	
	private Cache l1d;
	private Cache l1i;
	private Cache l2;
	private Cache l3;
	
	private int instructionsExecutedThusFar;
	public CPU() {
		Cache l1d = new Cache(1, 4096, 16, 1);
		Cache l1i = new Cache(1, 4096, 16, 2);
		Cache l2 = new Cache(1, 512, 16, 10);

	}
	
	public void execute(Instruction instruction) {
		instructionsExecutedThusFar ++;
	}
	
	public int getNumberOfInstructionsExecuted() {
		return instructionsExecutedThusFar;
	}
	
	public void readInstruction(int address) {
		int indexInCache = l1i.indexOfCache(address);
		if(indexInCache == Cache.NOT_IN_CACHE) {
			l1i.insertData(address);
		} else {
			CacheLine data = l1i.getCacheLine(indexInCache);
		}
	}
}
