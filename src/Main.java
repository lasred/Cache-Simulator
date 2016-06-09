import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
	
	public static void main(String[] args) {
		
		List<Instruction> instructions = new ArrayList<Instruction>();
		Map<String, String> config = new HashMap<String, String>();
		
		readTrace("trace-2k.csv", instructions);
		readConfig("config.txt", config);
		
		Cache L3 = new Cache(Integer.parseInt(config.get("Cache Associativity")),
							 Integer.parseInt(config.get("L3 size")),
							 Integer.parseInt(config.get("Cache Line/Block size")),
							 Integer.parseInt(config.get("L3 latency")));
		
		CPU firstCPU = new CPU(L3, config, "cpu1");
		CPU secondCPU = new CPU(L3, config, "cpu2");
		
		Memory firstMem = new Memory(Integer.parseInt(config.get("1LM size")),
									 Integer.parseInt(config.get("1LM latency")));
		
		Memory secondMem = new Memory(Integer.parseInt(config.get("2LM size")),
									  Integer.parseInt(config.get("2LM latency")));
		
		
		Bus bus = new Bus(firstCPU, secondCPU, firstMem, secondMem, config.get("Write Policy"));
		int[][] mesiStates =    { {1,2,3,4},{5,6,7,8}, {1,2,3,4}, {5,6,7,8}};
		writeMesiTransation(mesiStates);
	}
	
	private static void readTrace(String filename, List<Instruction> list) {
	  try {
	    BufferedReader reader = new BufferedReader(new FileReader(filename));
	    String line;
	    while ((line = reader.readLine()) != null) {
	    	List<String> splited = Arrays.asList(line.split(","));
	    	if (splited.size() == 1) { // No memory access.
	    		int instructAddress = Integer.parseInt(splited.get(0));
	    		Instruction inst = new Instruction(instructAddress);
	    		list.add(inst);
	    	} else { 
	    		int instructAddress = Integer.parseInt(splited.get(0));
	    		char RorW = splited.get(1).charAt(0);
	    		int dataAddress = Integer.parseInt(splited.get(2));
	    		Instruction inst = new Instruction(instructAddress, RorW, dataAddress);
	    		list.add(inst);
	    	}
	    }
	    reader.close();
	  }
	  catch (Exception e) {
	    System.err.format("Exception occurred trying to read '%s'.", filename);
	    e.printStackTrace();
	  }
	}
	
	private static void readConfig(String filename, Map<String, String> map) {
		  try {
			    BufferedReader reader = new BufferedReader(new FileReader(filename));
			    String line;
			    while ((line = reader.readLine()) != null) {
			    	String description = line.substring(0, line.indexOf(':'));
			    	if (description.equals("2LM latency")) {
			    		String numString = line.substring(line.indexOf(':') + 1, line.length());
			    		String[] splited = numString.split("\\s+");
			    		map.put(description, splited[0]);
			    	} else {
				    	String numString = line.substring(line.indexOf(':') + 1, line.length());
				    	map.put(description, numString);
			    	}
			    }
		  }
		  catch (Exception e) {
		    System.err.format("Exception occurred trying to read '%s'.", filename);
		    e.printStackTrace();
		  }
	}
	public static void wrintStatistics(Cache L1i, Cache L1d, Cache  L2, Cache L3, Memory mem1, Memory mem2 ){
		int l1iAccess =  L1i.getHits() + L1i.getMiss();
		int l1dAccess = L1d.getHits()  + L1d.getMiss();
		int l2Access = L2.getHits()  + L2.getMiss();
		int l3Access = L3.getHits()  + L3.getMiss();
		System.out.println("L1i Access Count: " + l1iAccess);
		System.out.println("L1i Miss: " + L1i.getMiss() + " L1i  Hit: " + L1i.getHits());
		System.out.println("L1d Access Count: " + l1dAccess);
		System.out.println("L1d Miss: " + L1d.getMiss() + " L1d  Hit: " + L1d.getHits());
		System.out.println("L2 Access Count: " + l2Access);
		System.out.println("L2 Miss: " + L2.getMiss() + " L2  Hit: " + L2.getHits());
		System.out.println("L3 Access Count: " + l3Access);
		System.out.println("L3 Miss: " + L3.getMiss() + " L3  Hit: " + L3.getHits());
		System.out.println("Memory Access Count: " + mem1.getAccess());
		System.out.println("Memory Access Count: " + mem2.getAccess());
	}
	public static void writeMesiTransation(int[][] mesiStat){
		char [] mesilebel = {'M', 'E', 'S', 'I'};
		System.out.println("  M   E  S  I");
		for(int i = 0 ;i < 4; i++){
			System.out.print(mesilebel[i]);
			for(int j = 0; j < 4; j++){
				System.out.print(" "+ mesiStat[i][j]+" ");
			}
			System.out.println();
		}
	}
}
