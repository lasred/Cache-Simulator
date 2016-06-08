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
		
		CPU firstCPU = new CPU(L3, config);
		CPU secondCPU = new CPU(L3, config);
		
		Memory firstMem = new Memory(Integer.parseInt(config.get("1LM size")),
									 Integer.parseInt(config.get("1LM latency")));
		
		Memory secondMem = new Memory(Integer.parseInt(config.get("2LM size")),
									  Integer.parseInt(config.get("2LM latency")));
		
		
		Bus bus = new Bus(firstCPU, secondCPU, firstMem, secondMem, config.get("Write Policy"));

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
			    	if (description.equals("2LM Latency")) {
			    		String numString = line.substring(line.indexOf(':') + 1, line.length());
			    		String[] splited = numString.split("\\s+");
			    		map.put(description, splited[0] + " " + splited[1]);
			    	}
			    	String numString = line.substring(line.indexOf(':') + 1, line.length());
			    	map.put(description, numString);
			    }
		  }
		  catch (Exception e) {
		    System.err.format("Exception occurred trying to read '%s'.", filename);
		    e.printStackTrace();
		  }
	}

}
