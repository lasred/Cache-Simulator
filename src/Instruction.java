
public class Instruction {
	//either read or wite
	private boolean isRead;
	private int instructionAddress;
	private int dataAddress;
	public Instruction(boolean isRead) {
		this.isRead = isRead;
	}
	
	public boolean isRead() {
		return isRead;
	}
	
	public int getInstructionAddress() {
		return instructionAddress;
	}
	
	public int getDataAddress() {
		return dataAddress;
	}
}
