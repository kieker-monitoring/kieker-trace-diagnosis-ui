package kieker.diagnosis.service.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class MethodCall {

	private ArrayList<MethodCall> ivChildren;

	private long ivTimestamp;
	private long ivDuration;
	private long ivTraceId;
	private String ivHost;
	private String ivClazz;
	private String ivMethod;
	private String ivException;

	private int ivTraceDepth;
	private int ivTraceSize;
	private float ivPercent;

	public List<MethodCall> getChildren( ) {
		if ( ivChildren == null ) {
			return Collections.emptyList( );
		}
		return ivChildren;
	}

	public void addChild( final MethodCall aMethod ) {
		if ( ivChildren == null ) {
			ivChildren = new ArrayList<>( );
		}
		ivChildren.add( aMethod );
	}

	public void trimToSize( ) {
		if ( ivChildren != null ) {
			ivChildren.trimToSize( );
		}
	}

	public long getTimestamp( ) {
		return ivTimestamp;
	}

	public void setTimestamp( final long aTimestamp ) {
		ivTimestamp = aTimestamp;
	}

	public long getDuration( ) {
		return ivDuration;
	}

	public void setDuration( final long aDuration ) {
		ivDuration = aDuration;
	}

	public long getTraceId( ) {
		return ivTraceId;
	}

	public void setTraceId( final long aTraceId ) {
		ivTraceId = aTraceId;
	}

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

	public int getTraceDepth( ) {
		return ivTraceDepth;
	}

	public void setTraceDepth( final int aTraceDepth ) {
		ivTraceDepth = aTraceDepth;
	}

	public int getTraceSize( ) {
		return ivTraceSize;
	}

	public void setTraceSize( final int aTraceSize ) {
		ivTraceSize = aTraceSize;
	}

	public float getPercent( ) {
		return ivPercent;
	}

	public void setPercent( final float aPercent ) {
		ivPercent = aPercent;
	}

	public void addToTraceSize( final int aTraceSize ) {
		ivTraceSize += aTraceSize;
	}

}
