

public class CacheSimulator {
	public static void main(String[] args) {
		Cache sharedL3Cache = new Cache(1, 2048, 16, 35);
		CPU cpuOne = new CPU(sharedL3Cache);	
		CPU cpuTwo = new CPU(sharedL3Cache);
		Memory memoryOne = new Memory(16000, 100);
		Memory memoryTwo = new Memory(1000000, 250);
		Bus cacheSimulatorBus = new Bus(cpuOne, cpuTwo, memoryOne, memoryTwo);
		System.out.println(cpuOne.readInstruction(512));
		System.out.println(cpuOne.readInstruction(512));
		System.out.println(cpuOne.readInstruction(1536));
		System.out.println(cpuOne.readInstruction(512));
	}
}
