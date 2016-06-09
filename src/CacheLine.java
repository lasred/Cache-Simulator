
public class CacheLine {
	public enum MESIState {
		MODIFIED, EXCLUSIVE, SHARED, INVALID;
	}
	
	private int dirtyBit;
	private int tag;
	
	private boolean isValid;
	
	private MESIState mesiState;
	
	public CacheLine() {
		this.tag = 0;
		this.isValid = false;
		mesiState = MESIState.EXCLUSIVE;
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

	/**
	 * Checks to determine if cacheline is in a MODIFIED state.
	 *
	 * @return true if MODIFIED; false otherwise
     */
	public boolean isModified(){return mesiState == MESIState.MODIFIED? true:false;}

	/**
	 * Checks to determine if cacheline is in a EXCLUSIVE state.
	 *
	 * @return true if EXCLUSIVE; false otherwise
	 */
	public boolean isExclusive(){return mesiState == MESIState.EXCLUSIVE? true:false;}

	/**
	 * Checks to determine if cacheline is in a SHARED state.
	 *
	 * @return true if SHARED; false otherwise
	 */
	public boolean isShared(){return mesiState == MESIState.SHARED? true:false;}


	/**
	 * Sets the cacheline's state.
	 */
	public void setState(MESIState theState){mesiState = theState;}

//	/**
//	 * Sets the cacheline's state to EXCLUSIVE.
//	 */
//	public void setExclusive(){mesiState = MESIState.EXCLUSIVE;}
//
//	/**
//	 * Sets the cacheline's state to SHARED.
//	 */
//	public void setShared(){mesiState = MESIState.SHARED;}
//
//	/**
//	 * Sets the cacheline's state to INVALID.
//	 */
//	public void setInvalid(){mesiState = MESIState.INVALID;}

	/**
	 * Gets the cacheline's state.
	 *
	 * @return the state of the cacheline.
	 */
	public MESIState getState(){return mesiState;}



}
