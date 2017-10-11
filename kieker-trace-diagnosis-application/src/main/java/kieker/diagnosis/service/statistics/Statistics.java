package kieker.diagnosis.service.statistics;

public final class Statistics {

	private long ivProcessedBytes;
	private long ivProcessDuration;
	private long ivProcessSpeed;
	private int ivIgnoredRecords;
	private int ivDanglingRecords;
	private int ivIncompleteTraces;
	private int ivMethods;
	private int ivAggregatedMethods;
	private int ivTraces;
	private String ivBeginnOfMonitoring;
	private String ivEndOfMonitoring;
	private String ivDirectory;

	public long getProcessedBytes( ) {
		return ivProcessedBytes;
	}

	public void setProcessedBytes( final long aProcessedBytes ) {
		ivProcessedBytes = aProcessedBytes;
	}

	public long getProcessDuration( ) {
		return ivProcessDuration;
	}

	public void setProcessDuration( final long aProcessDuration ) {
		ivProcessDuration = aProcessDuration;
	}

	public long getProcessSpeed( ) {
		return ivProcessSpeed;
	}

	public void setProcessSpeed( final long aProcessSpeed ) {
		ivProcessSpeed = aProcessSpeed;
	}

	public int getIgnoredRecords( ) {
		return ivIgnoredRecords;
	}

	public void setIgnoredRecords( final int aIgnoredRecords ) {
		ivIgnoredRecords = aIgnoredRecords;
	}

	public int getDanglingRecords( ) {
		return ivDanglingRecords;
	}

	public void setDanglingRecords( final int aDanglingRecords ) {
		ivDanglingRecords = aDanglingRecords;
	}

	public int getIncompleteTraces( ) {
		return ivIncompleteTraces;
	}

	public void setIncompleteTraces( final int aIncompleteTraces ) {
		ivIncompleteTraces = aIncompleteTraces;
	}

	public int getMethods( ) {
		return ivMethods;
	}

	public void setMethods( final int aMethods ) {
		ivMethods = aMethods;
	}

	public int getAggregatedMethods( ) {
		return ivAggregatedMethods;
	}

	public void setAggregatedMethods( final int aAggregatedMethods ) {
		ivAggregatedMethods = aAggregatedMethods;
	}

	public int getTraces( ) {
		return ivTraces;
	}

	public void setTraces( final int aTraces ) {
		ivTraces = aTraces;
	}

	public String getBeginnOfMonitoring( ) {
		return ivBeginnOfMonitoring;
	}

	public void setBeginnOfMonitoring( final String aBeginnOfMonitoring ) {
		ivBeginnOfMonitoring = aBeginnOfMonitoring;
	}

	public String getEndOfMonitoring( ) {
		return ivEndOfMonitoring;
	}

	public void setEndOfMonitoring( final String aEndOfMonitoring ) {
		ivEndOfMonitoring = aEndOfMonitoring;
	}

	public String getDirectory( ) {
		return ivDirectory;
	}

	public void setDirectory( final String aDirectory ) {
		ivDirectory = aDirectory;
	}

}
