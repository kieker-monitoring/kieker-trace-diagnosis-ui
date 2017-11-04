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

package kieker.diagnosis.service.traces;

import java.time.LocalDate;
import java.util.Calendar;

/**
 * This is a data transfer object holding the filter for the traces service.
 *
 * @author Nils Christian Ehmke
 */
public final class TracesFilter {

	private String ivHost;
	private String ivClazz;
	private String ivMethod;
	private String ivException;
	private Long ivTraceId;
	private SearchType ivSearchType = SearchType.ALL;
	private boolean ivUseRegExpr;
	private boolean ivSearchWholeTrace;
	private Calendar ivLowerTime;
	private LocalDate ivUpperDate;
	private Calendar ivUpperTime;
	private LocalDate ivLowerDate;

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

	public Long getTraceId( ) {
		return ivTraceId;
	}

	public void setTraceId( final Long aTraceId ) {
		ivTraceId = aTraceId;
	}

	public boolean isUseRegExpr( ) {
		return ivUseRegExpr;
	}

	public void setUseRegExpr( final boolean aUseRegExpr ) {
		ivUseRegExpr = aUseRegExpr;
	}

	public boolean isSearchWholeTrace( ) {
		return ivSearchWholeTrace;
	}

	public void setSearchWholeTrace( final boolean aSearchWholeTrace ) {
		ivSearchWholeTrace = aSearchWholeTrace;
	}

	public Calendar getLowerTime( ) {
		return ivLowerTime;
	}

	public void setLowerTime( final Calendar aLowerTime ) {
		ivLowerTime = aLowerTime;
	}

	public LocalDate getUpperDate( ) {
		return ivUpperDate;
	}

	public void setUpperDate( final LocalDate aUpperDate ) {
		ivUpperDate = aUpperDate;
	}

	public Calendar getUpperTime( ) {
		return ivUpperTime;
	}

	public void setUpperTime( final Calendar aUpperTime ) {
		ivUpperTime = aUpperTime;
	}

	public LocalDate getLowerDate( ) {
		return ivLowerDate;
	}

	public void setLowerDate( final LocalDate aLowerDate ) {
		ivLowerDate = aLowerDate;
	}

	public SearchType getSearchType( ) {
		return ivSearchType;
	}

	public void setSearchType( final SearchType aSearchType ) {
		ivSearchType = aSearchType;
	}

}
