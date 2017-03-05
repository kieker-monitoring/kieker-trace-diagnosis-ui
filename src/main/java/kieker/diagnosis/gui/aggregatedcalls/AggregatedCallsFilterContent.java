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

package kieker.diagnosis.gui.aggregatedcalls;

class AggregatedCallsFilterContent {

	private boolean ivShowAllButton;
	private boolean ivShowJustSuccessful;
	private boolean ivShowJustFailedButton;

	private String ivFilterContainer;
	private String ivFilterComponent;
	private String ivFilterOperation;
	private String ivFilterException;

	public boolean isShowAllButton( ) {
		return ivShowAllButton;
	}

	public void setShowAllButton( final boolean showAllButton ) {
		ivShowAllButton = showAllButton;
	}

	public boolean isShowJustSuccessful( ) {
		return ivShowJustSuccessful;
	}

	public void setShowJustSuccessful( final boolean showJustSuccessful ) {
		ivShowJustSuccessful = showJustSuccessful;
	}

	public boolean isShowJustFailedButton( ) {
		return ivShowJustFailedButton;
	}

	public void setShowJustFailedButton( final boolean showJustFailedButton ) {
		ivShowJustFailedButton = showJustFailedButton;
	}

	public String getFilterContainer( ) {
		return ivFilterContainer;
	}

	public void setFilterContainer( final String filterContainer ) {
		ivFilterContainer = filterContainer;
	}

	public String getFilterComponent( ) {
		return ivFilterComponent;
	}

	public void setFilterComponent( final String filterComponent ) {
		ivFilterComponent = filterComponent;
	}

	public String getFilterOperation( ) {
		return ivFilterOperation;
	}

	public void setFilterOperation( final String filterOperation ) {
		ivFilterOperation = filterOperation;
	}

	public String getFilterException( ) {
		return ivFilterException;
	}

	public void setFilterException( final String filterException ) {
		ivFilterException = filterException;
	}

}