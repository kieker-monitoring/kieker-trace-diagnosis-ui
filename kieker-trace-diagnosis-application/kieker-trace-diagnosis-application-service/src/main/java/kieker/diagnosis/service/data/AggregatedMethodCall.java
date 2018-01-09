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

package kieker.diagnosis.service.data;

/**
 * This is a data transfer object representing a single aggregaed method call.
 *
 * @author Nils Christian Ehmke
 */
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
