package persistence.configuration;

public class C3P0Configuration {
	
	private int minSize;
	private int maxSize;
	private long timeout;
	private int maxStatements;
	
	public int getMinSize() {
		return minSize;
	}
	
	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}
	
	public int getMaxSize() {
		return maxSize;
	}
	
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public long getTimeout() {
		return timeout;
	}
	
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	public int getMaxStatements() {
		return maxStatements;
	}
	
	public void setMaxStatements(int maxStatements) {
		this.maxStatements = maxStatements;
	}

}
