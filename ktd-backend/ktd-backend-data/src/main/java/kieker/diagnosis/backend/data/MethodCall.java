/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.diagnosis.backend.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is a data transfer object representing a single method call. However, it contains also all necessary information to represent a part (or the root) of a
 * trace.
 *
 * @author Nils Christian Ehmke
 */
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
