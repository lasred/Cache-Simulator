
public class CacheLine {
	private int tag;
	private boolean isValid;
	
	public CacheLine() {
		this.tag = 0;
		this.isValid = false;
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
