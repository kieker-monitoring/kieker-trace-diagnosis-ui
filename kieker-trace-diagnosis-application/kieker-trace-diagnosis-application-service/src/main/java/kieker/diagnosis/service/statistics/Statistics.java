/*************************************************************************** 
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)         
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

package kieker.diagnosis.service.statistics;

/**
 * This is a data transfer object holding the statistics for the statistics service.
 *
 * @author Nils Christian Ehmke
 */
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
