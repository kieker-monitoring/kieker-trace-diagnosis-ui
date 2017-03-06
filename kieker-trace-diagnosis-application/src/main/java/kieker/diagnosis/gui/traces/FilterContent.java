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

package kieker.diagnosis.gui.traces;

import java.time.LocalDate;
import java.util.Calendar;

/**
 * @author Nils Christian Ehmke
 */
class FilterContent {

	private String ivFilterComponent;
	private String ivFilterContainer;
	private String ivFilterException;
	private String ivFilterOperation;
	private String ivFilterTraceID;
	private LocalDate ivFilterLowerDate;
	private LocalDate ivFilterUpperDate;
	private Calendar ivFilterLowerTime;
	private Calendar ivFilterUpperTime;
	private boolean ivShowAllButton;
	private boolean ivShowJustFailedButton;
	private boolean ivShowJustSuccessful;
	private boolean ivShowJustFailureContainingButton;

	public String getFilterComponent( ) {
		return ivFilterComponent;
	}

	public void setFilterComponent( final String aFilterComponent ) {
		ivFilterComponent = aFilterComponent;
	}

	public String getFilterContainer( ) {
		return ivFilterContainer;
	}

	public void setFilterContainer( final String aFilterContainer ) {
		ivFilterContainer = aFilterContainer;
	}

	public String getFilterException( ) {
		return ivFilterException;
	}

	public void setFilterException( final String aFilterException ) {
		ivFilterException = aFilterException;
	}

	public String getFilterOperation( ) {
		return ivFilterOperation;
	}

	public void setFilterOperation( final String aFilterOperation ) {
		ivFilterOperation = aFilterOperation;
	}

	public String getFilterTraceID( ) {
		return ivFilterTraceID;
	}

	public void setFilterTraceID( final String aFilterTraceID ) {
		ivFilterTraceID = aFilterTraceID;
	}

	public LocalDate getFilterLowerDate( ) {
		return ivFilterLowerDate;
	}

	public void setFilterLowerDate( final LocalDate aFilterLowerDate ) {
		ivFilterLowerDate = aFilterLowerDate;
	}

	public LocalDate getFilterUpperDate( ) {
		return ivFilterUpperDate;
	}

	public void setFilterUpperDate( final LocalDate aFilterUpperDate ) {
		ivFilterUpperDate = aFilterUpperDate;
	}

	public Calendar getFilterLowerTime( ) {
		return ivFilterLowerTime;
	}

	public void setFilterLowerTime( final Calendar aFilterLowerTime ) {
		ivFilterLowerTime = aFilterLowerTime;
	}

	public Calendar getFilterUpperTime( ) {
		return ivFilterUpperTime;
	}

	public void setFilterUpperTime( final Calendar aFilterUpperTime ) {
		ivFilterUpperTime = aFilterUpperTime;
	}

	public boolean isShowAllButton( ) {
		return ivShowAllButton;
	}

	public void setShowAllButton( final boolean aShowAllButton ) {
		ivShowAllButton = aShowAllButton;
	}

	public boolean isShowJustFailedButton( ) {
		return ivShowJustFailedButton;
	}

	public void setShowJustFailedButton( final boolean aShowJustFailedButton ) {
		ivShowJustFailedButton = aShowJustFailedButton;
	}

	public boolean isShowJustSuccessful( ) {
		return ivShowJustSuccessful;
	}

	public void setShowJustSuccessful( final boolean aShowJustSuccessful ) {
		ivShowJustSuccessful = aShowJustSuccessful;
	}

	public boolean isShowJustFailureContainingButton( ) {
		return ivShowJustFailureContainingButton;
	}

	public void setShowJustFailureContainingButton( final boolean aShowJustFailureContainingButton ) {
		ivShowJustFailureContainingButton = aShowJustFailureContainingButton;
	}

}