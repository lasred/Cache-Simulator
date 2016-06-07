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
	
	public Bus(CPU c1, CPU c2, Memory oneLM, Memory twoLM) {
		cpu1 = c1;
		cpu2 = c2;
		this.oneLM = oneLM;
		this.twoLM  = twoLM;
	}

}
