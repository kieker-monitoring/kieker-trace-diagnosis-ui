package kieker.diagnosis.service.data;

public final class AggregatedMethodCall {

	private String ivHost;
	private String ivClazz;
	private String ivMethod;
	private String ivException;

	private int ivCount;
	private long ivAvgDuration;
	private long ivTotalDuration;
	private long ivMedianDuration;
	private long ivMinDuration;
	private long ivMaxDuration;

	public String getHost( ) {
		return ivHost;
	}

	public void setHost( final String aHost ) {
		ivHost = aHost;
	}

	public String getClazz( ) {
		return ivClazz;
	}

	public void setClazz( final String aClazz ) {
		ivClazz = aClazz;
	}

	public String getMethod( ) {
		return ivMethod;
	}

	public void setMethod( final String aMethod ) {
		ivMethod = aMethod;
	}

	public String getException( ) {
		return ivException;
	}

	public void setException( final String aException ) {
		ivException = aException;
	}

	public int getCount( ) {
		return ivCount;
	}

	public void setCount( final int aCount ) {
		ivCount = aCount;
	}

	public long getAvgDuration( ) {
		return ivAvgDuration;
	}

	public void setAvgDuration( final long aAvgDuration ) {
		ivAvgDuration = aAvgDuration;
	}

	public long getTotalDuration( ) {
		return ivTotalDuration;
	}

	public void setTotalDuration( final long aTotalDuration ) {
		ivTotalDuration = aTotalDuration;
	}

	public long getMedianDuration( ) {
		return ivMedianDuration;
	}

	public void setMedianDuration( final long aMedianDuration ) {
		ivMedianDuration = aMedianDuration;
	}

	public long getMinDuration( ) {
		return ivMinDuration;
	}

	public void setMinDuration( final long aMinDuration ) {
		ivMinDuration = aMinDuration;
	}

	public long getMaxDuration( ) {
		return ivMaxDuration;
	}

	public void setMaxDuration( final long aMaxDuration ) {
		ivMaxDuration = aMaxDuration;
	}

}
