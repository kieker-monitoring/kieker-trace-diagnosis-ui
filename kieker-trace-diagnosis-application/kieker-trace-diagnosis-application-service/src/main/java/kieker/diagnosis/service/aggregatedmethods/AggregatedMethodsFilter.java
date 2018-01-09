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

package kieker.diagnosis.service.aggregatedmethods;

/**
 * This is a data transfer object holding the filter for the aggregated methods service.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedMethodsFilter {

	private String ivHost;
	private String ivClazz;
	private String ivMethod;
	private String ivException;
	private boolean ivUseRegExpr;
	private SearchType ivSearchType = SearchType.ALL;

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

	public boolean isUseRegExpr( ) {
		return ivUseRegExpr;
	}

	public void setUseRegExpr( final boolean aUseRegExpr ) {
		ivUseRegExpr = aUseRegExpr;
	}

	public SearchType getSearchType( ) {
		return ivSearchType;
	}

	public void setSearchType( final SearchType aSearchType ) {
		ivSearchType = aSearchType;
	}

}
