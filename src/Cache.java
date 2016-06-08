
public class Cache {
	public static int DIRECT_MAPPED_CACHE = 1;
	public static int NOT_IN_CACHE = -1;
    private CacheLine cache[];
    private int associativity;
	
    private int accesses = 0;
	private int misses = 0;
	private int offsetBits = 0;
	private int indexBits = 0;
	//in nanoseconds
	private int latency;
	
	public Cache(int associativity, int blocks, int blockSize, int latency) {
		this.associativity = associativity;
		this.offsetBits = (int)(Math.log(blockSize)/Math.log(2));
		if(associativity == DIRECT_MAPPED_CACHE) {
			indexBits = (int)(Math.log(blocks)/Math.log(2));
		} else {
			indexBits = (int)(Math.log(blocks/associativity)/Math.log(2));
		}
		cache = new CacheLine[blocks]; 
		for(int i=0; i<cache.length; i++) {
			cache[i] = new CacheLine();
		}
		this.latency = latency;
	}
	
	public int getLatency() {
		return latency;
	}
	
	public int getHits() {
		return accesses - misses;
	}
	
	public CacheLine getCacheLine(int index) {
		return cache[index];
	}

	public int indexOfCache(int address) {
		accesses++;
		//if it were in the cache
		int indexAndTag = address >> offsetBits;
		int indexIfInCache = 0;
		for(int i=0;i<indexBits; i++) {
			indexIfInCache += (Math.pow(2, i)* ((indexAndTag >> i) & 1));
		}
		if(associativity == DIRECT_MAPPED_CACHE) {
			if(cache[indexIfInCache].getTag() == indexAndTag >> indexBits 
						&& cache[indexIfInCache].isValid()){
				return indexIfInCache;
			}	
		} else {
			int setIndex = indexIfInCache * associativity;
			//check each member in that set
			for(int i=0; i<associativity; i++) {
				int toEvaluate = setIndex + i;
				if(cache[toEvaluate].getTag() == indexAndTag >> indexBits &&
					cache[toEvaluate].isValid()) {
						return toEvaluate;
				}
			}
		}
		misses ++;
		return NOT_IN_CACHE;
	}
	
	public void insertData(int address) {
		int indexInCache = indexOfCache(address);
		if(indexInCache == NOT_IN_CACHE) {
			int indexAndTag = address >> offsetBits;
			int indexIfInCache = 0;
			for(int i=0;i<indexBits; i++) {
					indexIfInCache += (Math.pow(2, i)* ((indexAndTag >> i) & 1));
			}
			if(associativity != DIRECT_MAPPED_CACHE) {
				int randomEntry  = (int)(Math.random() * associativity);
				indexIfInCache += randomEntry;
			}
			cache[indexIfInCache].setTag(indexAndTag >> indexBits);
			cache[indexIfInCache].setIsValid(true);			
		}
	}
	
}
