/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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
 * This is a data transfer object representing a single method call. However, it contains also all necessary information
 * to represent a part (or the root) of a trace.
 *
 * @author Nils Christian Ehmke
 */
public final class MethodCall {

	private ArrayList<MethodCall> children;

	private long timestamp;
	private long duration;
	private long traceId;
	private String host;
	private String clazz;
	private String method;
	private String exception;

	private int traceDepth;
	private int traceSize;
	private float percent;

	public List<MethodCall> getChildren( ) {
		if ( children == null ) {
			return Collections.emptyList( );
		}
		return children;
	}

	public void addChild( final MethodCall method ) {
		if ( children == null ) {
			children = new ArrayList<>( );
		}
		children.add( method );
	}

	public void trimToSize( ) {
		if ( children != null ) {
			children.trimToSize( );
		}
	}

	public long getTimestamp( ) {
		return timestamp;
	}

	public void setTimestamp( final long timestamp ) {
		this.timestamp = timestamp;
	}

	public long getDuration( ) {
		return duration;
	}

	public void setDuration( final long duration ) {
		this.duration = duration;
	}

	public long getTraceId( ) {
		return traceId;
	}

	public void setTraceId( final long traceId ) {
		this.traceId = traceId;
	}

	public String getHost( ) {
		return host;
	}

	public void setHost( final String host ) {
		this.host = host;
	}

	public String getClazz( ) {
		return clazz;
	}

	public void setClazz( final String clazz ) {
		this.clazz = clazz;
	}

	public String getMethod( ) {
		return method;
	}

	public void setMethod( final String method ) {
		this.method = method;
	}

	public String getException( ) {
		return exception;
	}

	public void setException( final String exception ) {
		this.exception = exception;
	}

	public int getTraceDepth( ) {
		return traceDepth;
	}

	public void setTraceDepth( final int traceDepth ) {
		this.traceDepth = traceDepth;
	}

	public int getTraceSize( ) {
		return traceSize;
	}

	public void setTraceSize( final int traceSize ) {
		this.traceSize = traceSize;
	}

	public float getPercent( ) {
		return percent;
	}

	public void setPercent( final float percent ) {
		this.percent = percent;
	}

	public void addToTraceSize( final int traceSize ) {
		this.traceSize += traceSize;
	}

}
