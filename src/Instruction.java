public class Instruction {
	
	/** Used to store the instruction address. */
	private int instructionAddress; 
	
	/** 1 is for read, 0 is for write. */
	private char ReadOrWrite;
	
	/** True if instruction accesses memory, false if not. */
	private boolean AccessMemory;
	
	/** Used to store the data address. */
	private int dataAddress;
	
	public Instruction(boolean isRead) {
		this.AccessMemory = isRead;
	}
	
	public Instruction(int theInstruction) {
		instructionAddress = theInstruction;
		AccessMemory = false;
		dataAddress = -1;
	}
	
	public Instruction(int theInstruction, char RorW, int theData) {
		instructionAddress = theInstruction;
		ReadOrWrite = RorW;
		dataAddress = theData;
		AccessMemory = true;
	}
	
	public void setInstructionAddress(int theInstruction) {
		instructionAddress = theInstruction;
	}
	
	public void setAccessMemory(boolean theMemAccess) {
		AccessMemory = theMemAccess;
	}
	
	public void setReadOrWrite(char RorW) {
		ReadOrWrite = RorW;
	}
	
	public void setDataAddress(int theData) {
		dataAddress = theData;
	}
	
	public int getInstructionAddress() {
		return instructionAddress;
	}
	
	public char getReadOrWrite() {
		return ReadOrWrite;
	}
	
	public boolean getAccessMemory() {
		return AccessMemory; 
	}
	
	public int getDataAddress() {
		return dataAddress;
	}
	
	
	public String toString() {
		return instructionAddress + " " + ReadOrWrite + " " + AccessMemory + " " + dataAddress;
	}

}
