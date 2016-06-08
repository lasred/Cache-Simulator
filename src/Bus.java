/**
 * Bus - transfers data between components of a computer
 * @author chris
 *
 */
public class Bus {
	
	private CPU cpu1;
	
	private CPU cpu2;
	
	private Memory oneLM;
	
	private Memory twoLM;
	
	private String writeScheme;
	
	public Bus(CPU c1, CPU c2, Memory oneLM, Memory twoLM, String theWriteScheme) {
		this.cpu1 = c1;
		this.cpu2 = c2;
		this.oneLM = oneLM;
		this.twoLM  = twoLM;
		this.writeScheme = theWriteScheme;
	}

	/**
	 * Checks the L1 & L2 caches of a CPU for a matching address.
	 */
	public void snoopCPU(final CPU theCPU, final String theAddress){

	}

}
