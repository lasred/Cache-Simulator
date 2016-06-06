
public class CacheLine {
	public enum MESIState {
		MODIFIED, EXCLUSIVE, SHARED, INVALID;
	}
	
	private int tag;
	
	private boolean isValid;
	
	private MESIState mesiState;
	
	public CacheLine() {
		this.tag = 0;
		this.isValid = false;
	}
	
	public void setMesiState(MESIState mesiState) {
		this.mesiState = mesiState;
	}
	
	public void setTag(int tag) {
		this.tag = tag;
	}
	
	public int getTag() { 
		return tag;
	}
	
	public void setIsValid(boolean isValid){
		this.isValid =isValid;
	}
	
	public boolean isValid() {
		return isValid;
	}
}
