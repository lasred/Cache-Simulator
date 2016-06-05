
public class CPU {
	
	private Cache l1d;
	private Cache l1i;
	private Cache l2;
	public CPU() {
		Cache l1d = new Cache(1, 4096, 16);
		Cache l1i = new Cache(1, 4096, 16);
		Cache l2 = new Cache(1, 512, 16);

	}
	
}
