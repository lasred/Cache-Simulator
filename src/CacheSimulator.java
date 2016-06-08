

public class CacheSimulator {
	public static void main(String[] args) {
		Cache sharedL3Cache = new Cache(1, 2048, 16, 35);
		CPU cpuOne = new CPU(1, sharedL3Cache);	
		CPU cpuTwo = new CPU(1, sharedL3Cache);
		Memory memoryOne = new Memory(16000, 100);
		Memory memoryTwo = new Memory(1000000, 250);
		Bus cacheSimulatorBus = new Bus(cpuOne, cpuTwo, memoryOne, memoryTwo);
		System.out.println(cpuOne.readInstruction(512));
		System.out.println(cpuOne.readInstruction(512));
		System.out.println(cpuOne.readInstruction(1536));
		System.out.println(cpuOne.readInstruction(512));
		System.out.println("4 Way Associative");
		Cache sharedL12Cache = new Cache(4, 2048, 16, 35);
		CPU cpu1 = new CPU(4, sharedL12Cache);	
		CPU cpu2 = new CPU(4, sharedL12Cache);
		System.out.println(cpu1.readInstruction(512));
		System.out.println(cpu1.readInstruction(528));
		System.out.println(cpu1.readInstruction(544));
		System.out.println(cpu1.readInstruction(560));
		System.out.println(cpu1.readInstruction(512));
		System.out.println(cpu1.readInstruction(528));
		System.out.println(cpu1.readInstruction(544));
		System.out.println(cpu1.readInstruction(560));
	}
}
