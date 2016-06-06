

public class CacheSimulator {
	public static void main(String[] args) {
		Cache sharedL3Cache = new Cache(1, 4096, 16, 35);
		CPU cpuOne = new CPU(sharedL3Cache);	
		CPU cpuTwo = new CPU(sharedL3Cache);
	}
	
}
