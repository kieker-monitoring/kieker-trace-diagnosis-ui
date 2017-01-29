/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.domain;

/**
 * This class represents a concrete operation call within this application. It adds some properties that are only required for concrete operation calls, like
 * the trace ID and the duration. It extends the call tree mechanism (inherited from {@link AbstractOperationCall}) by a parent, allowing to navigate in both
 * directions within the tree.
 *
 * @author Nils Christian Ehmke
 */
public final class OperationCall extends AbstractOperationCall<OperationCall> {

	private final long ivTraceID;

	private OperationCall ivParent;
	private float ivPercent;
	private long ivDuration;
	private long ivTimestamp;

	public OperationCall( final String aContainer, final String aComponent, final String aOperation, final long aTraceID, final long aTimestamp ) {
		this( aContainer, aComponent, aOperation, null, aTraceID, aTimestamp );
	}

	public OperationCall( final String aContainer, final String aComponent, final String aOperation, final String aFailedCause, final long aTraceID,
			final long aTimestamp ) {
		super( aContainer, aComponent, aOperation, aFailedCause );

		this.ivTraceID = aTraceID;
		this.ivTimestamp = aTimestamp;
	}

	@Override
	public void addChild( final OperationCall aChild ) {
		super.addChild( aChild );
		aChild.ivParent = this;
	}

	public OperationCall getParent( ) {
		return this.ivParent;
	}

	public float getPercent( ) {
		return this.ivPercent;
	}

	public void setPercent( final float aPercent ) {
		this.ivPercent = aPercent;
	}

	public long getDuration( ) {
		return this.ivDuration;
	}

	public void setDuration( final long aDuration ) {
		this.ivDuration = aDuration;
	}

	public long getTraceID( ) {
		return this.ivTraceID;
	}

	public long getTimestamp( ) {
		return this.ivTimestamp;
	}

	public void setTimestamp( final long aTimestamp ) {
		this.ivTimestamp = aTimestamp;
	}

}
