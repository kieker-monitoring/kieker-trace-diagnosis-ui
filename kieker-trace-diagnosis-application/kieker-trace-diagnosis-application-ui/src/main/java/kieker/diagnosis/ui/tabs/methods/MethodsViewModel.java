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

package kieker.diagnosis.ui.tabs.methods;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Singleton;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Command;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.exception.BusinessRuntimeException;
import kieker.diagnosis.architecture.ui.ViewModelBase;
import kieker.diagnosis.service.data.AggregatedMethodCall;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.export.CSVData;
import kieker.diagnosis.service.methods.MethodsFilter;
import kieker.diagnosis.service.methods.MethodsService;
import kieker.diagnosis.service.methods.SearchType;
import kieker.diagnosis.service.pattern.PatternService;
import kieker.diagnosis.service.settings.SettingsService;
import kieker.diagnosis.ui.main.MainController;
import kieker.diagnosis.ui.scopes.MainScope;

/**
 * The view model of the methods tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class MethodsViewModel extends ViewModelBase<MethodsView> implements ViewModel {

	@InjectScope
	private MainScope ivMainScope;

	private final Command ivSearchCommand = createCommand( this::performSearch );
	private final Command ivSaveAsFavoriteCommand = createCommand( this::performSaveAsFavorite );
	private final Command ivExportToCSVCommand = createCommand( this::performExportToCSV );
	private final Command ivSelectionChangeCommand = createCommand( this::performSelectionChange );
	private final Command ivRefreshCommand = createCommand( this::performRefresh );
	private final Command ivPrepareRefreshCommand = createCommand( this::performPrepareRefresh );
	private final Command ivJumpToTraceCommand = createCommand( this::performJumpToTrace );

	// Filter
	private final StringProperty ivFilterHostProperty = new SimpleStringProperty( );
	private final StringProperty ivFilterClassProperty = new SimpleStringProperty( );
	private final StringProperty ivFilterMethodProperty = new SimpleStringProperty( );
	private final StringProperty ivFilterExceptionProperty = new SimpleStringProperty( );
	private final ObjectProperty<Long> ivFilterTraceIdProperty = new SimpleObjectProperty<>( );
	private final BooleanProperty ivFilterUseRegExprProperty = new SimpleBooleanProperty( );

	private final ObjectProperty<LocalDate> ivFilterLowerDateProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<LocalTime> ivFilterLowerTimeProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<LocalDate> ivFilterUpperDateProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<LocalTime> ivFilterUpperTimeProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<SearchType> ivFilterSearchTypeProperty = new SimpleObjectProperty<>( );

	// Table
	private final ObjectProperty<ObservableList<MethodCall>> ivMethodsProperty = new SimpleObjectProperty<>( );
	private final ObjectProperty<MethodCall> ivSelectedMethodCallProperty = new SimpleObjectProperty<>( );
	private final StringProperty ivDurationColumnHeaderProperty = new SimpleStringProperty( );
	private final List<TableColumn<MethodCall, ?>> ivVisibleColumnsProperty = new ArrayList<>( );

	// Details
	private final StringProperty ivDetailsHostProperty = new SimpleStringProperty( );
	private final StringProperty ivDetailsClassProperty = new SimpleStringProperty( );
	private final StringProperty ivDetailsMethodProperty = new SimpleStringProperty( );
	private final StringProperty ivDetailsExceptionProperty = new SimpleStringProperty( );
	private final StringProperty ivDetailsDurationProperty = new SimpleStringProperty( );
	private final StringProperty ivDetailsTimestampProperty = new SimpleStringProperty( );
	private final StringProperty ivDetailsTraceIdProperty = new SimpleStringProperty( );

	// Status bar
	private final StringProperty ivStatusLabelProperty = new SimpleStringProperty( );

	// Temporary variables
	private List<MethodCall> ivMethods;
	private String ivDurationSuffix;
	private int ivTotalMethods;

	/**
	 * This action is performed once during the application's start.
	 */
	public void initialize( ) {
		updatePresentationDetails( null );
		updatePresentationStatus( 0, 0 );
		updatePresentationFilter( new MethodsFilter( ) );

		ivMainScope.subscribe( MainScope.EVENT_REFRESH, ( aKey, aPayload ) -> ivRefreshCommand.execute( ) );
		ivMainScope.subscribe( MainScope.EVENT_PREPARE_REFRESH, ( aKey, aPayload ) -> ivPrepareRefreshCommand.execute( ) );
	}

	Command getSearchCommand( ) {
		return ivSearchCommand;
	}

	public void performSearch( ) throws BusinessException {
		// Get the filter input from the user
		final MethodsFilter filter = savePresentationFilter( );

		// Find the methods to display
		final MethodsService methodsService = getService( MethodsService.class );
		final List<MethodCall> methods = methodsService.searchMethods( filter );
		final int totalMethods = methodsService.countMethods( );

		// Update the view
		updatePresentationMethods( methods );
		updatePresentationStatus( methods.size( ), totalMethods );

	}

	public void performPrepareRefresh( ) {
		final MethodsService methodsService = getService( MethodsService.class );
		ivMethods = methodsService.searchMethods( new MethodsFilter( ) );
		ivTotalMethods = methodsService.countMethods( );

		final SettingsService settingsService = getService( SettingsService.class );
		ivDurationSuffix = settingsService.getCurrentDurationSuffix( );
	}

	/**
	 * This action is performed, when a refresh of the view is required
	 */
	public void performRefresh( ) {
		// Reset the filter
		final MethodsFilter filter = new MethodsFilter( );
		updatePresentationFilter( filter );

		// Update the table
		updatePresentationMethods( ivMethods );
		updatePresentationStatus( ivMethods.size( ), ivTotalMethods );

		// Update the column header of the table
		updatePresentationDurationColumnHeader( ivDurationSuffix );
	}

	Command getSelectionChangeCommand( ) {
		return ivSelectionChangeCommand;
	}

	/**
	 * This action is performed, when the selection of the table changes
	 */
	public void performSelectionChange( ) {
		final MethodCall methodCall = getSelected( );
		updatePresentationDetails( methodCall );
	}

	Command getJumpToTraceCommand( ) {
		return ivJumpToTraceCommand;
	}

	public void performJumpToTrace( ) {
		final MethodCall methodCall = getSelected( );

		if ( methodCall != null ) {
			getController( MainController.class ).performJumpToTrace( methodCall );
		}
	}

	Command getSaveAsFavoriteCommand( ) {
		return ivSaveAsFavoriteCommand;
	}

	public void performSaveAsFavorite( ) {
		try {
			final MethodsFilter filter = savePresentationFilter( );
			getController( MainController.class ).performSaveAsFavorite( MethodsView.class, filter );
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}
	}

	public void performSetParameter( final Object aParameter ) throws BusinessException {
		if ( aParameter instanceof AggregatedMethodCall ) {
			final AggregatedMethodCall methodCall = ( AggregatedMethodCall ) aParameter;

			// We have to prepare a filter which maches only the method call
			final MethodsFilter filter = new MethodsFilter( );
			filter.setHost( methodCall.getHost( ) );
			filter.setClazz( methodCall.getClazz( ) );
			filter.setMethod( methodCall.getMethod( ) );
			filter.setException( methodCall.getException( ) );
			filter.setSearchType( methodCall.getException( ) != null ? SearchType.ONLY_FAILED : SearchType.ONLY_SUCCESSFUL );

			updatePresentationFilter( filter );

			// Now we can perform the actual search
			performSearch( );
		}

		if ( aParameter instanceof MethodsFilter ) {
			final MethodsFilter filter = ( MethodsFilter ) aParameter;
			updatePresentationFilter( filter );

			performSearch( );
		}
	}

	Command getExportToCSVCommand( ) {
		return ivExportToCSVCommand;
	}

	private void performExportToCSV( ) {
		final CSVData csvData = savePresentationAsCSV( );
		getController( MainController.class ).performExportToCSV( csvData );
	}

	private void updatePresentationMethods( final List<MethodCall> aMethods ) {
		ivMethodsProperty.set( FXCollections.observableArrayList( aMethods ) );
	}

	private void updatePresentationDetails( final MethodCall aMethodCall ) {
		final String noDataAvailable = getLocalizedString( "noDataAvailable" );

		if ( aMethodCall != null ) {
			ivDetailsHostProperty.set( aMethodCall.getHost( ) );
			ivDetailsClassProperty.set( aMethodCall.getClazz( ) );
			ivDetailsMethodProperty.set( aMethodCall.getMethod( ) );
			ivDetailsExceptionProperty.set( aMethodCall.getException( ) != null ? aMethodCall.getException( ) : noDataAvailable );
			ivDetailsDurationProperty.set( String.format( "%d [ns]", aMethodCall.getDuration( ) ) );
			ivDetailsTimestampProperty.set( Long.toString( aMethodCall.getTimestamp( ) ) );
			ivDetailsTraceIdProperty.set( Long.toString( aMethodCall.getTraceId( ) ) );
		} else {
			ivDetailsHostProperty.set( noDataAvailable );
			ivDetailsClassProperty.set( noDataAvailable );
			ivDetailsMethodProperty.set( noDataAvailable );
			ivDetailsExceptionProperty.set( noDataAvailable );
			ivDetailsDurationProperty.set( noDataAvailable );
			ivDetailsTimestampProperty.set( noDataAvailable );
			ivDetailsTraceIdProperty.set( noDataAvailable );
		}
	}

	private void updatePresentationDurationColumnHeader( final String aSuffix ) {
		ivDurationColumnHeaderProperty.set( getLocalizedString( "columnDuration" ) + " " + aSuffix );
	}

	private void updatePresentationStatus( final int aMethods, final int aTotalMethods ) {
		final NumberFormat decimalFormat = DecimalFormat.getInstance( );
		ivStatusLabelProperty.set( String.format( getLocalizedString( "statusLabel" ), decimalFormat.format( aMethods ), decimalFormat.format( aTotalMethods ) ) );
	}

	private void updatePresentationFilter( final MethodsFilter aFilter ) {
		ivFilterHostProperty.set( aFilter.getHost( ) );
		ivFilterClassProperty.set( aFilter.getClazz( ) );
		ivFilterMethodProperty.set( aFilter.getMethod( ) );
		ivFilterExceptionProperty.set( aFilter.getException( ) );
		ivFilterSearchTypeProperty.setValue( aFilter.getSearchType( ) );
		ivFilterTraceIdProperty.setValue( aFilter.getTraceId( ) );
		ivFilterUseRegExprProperty.setValue( aFilter.isUseRegExpr( ) );
		ivFilterLowerDateProperty.setValue( aFilter.getLowerDate( ) );
		ivFilterLowerTimeProperty.setValue( aFilter.getLowerTime( ) );
		ivFilterUpperDateProperty.setValue( aFilter.getUpperDate( ) );
		ivFilterUpperTimeProperty.setValue( aFilter.getUpperTime( ) );

	}

	private MethodsFilter savePresentationFilter( ) throws BusinessException {
		final MethodsFilter filter = new MethodsFilter( );

		filter.setHost( trimToNull( ivFilterHostProperty.get( ) ) );
		filter.setClazz( trimToNull( ivFilterClassProperty.get( ) ) );
		filter.setMethod( trimToNull( ivFilterMethodProperty.get( ) ) );
		filter.setException( trimToNull( ivFilterExceptionProperty.get( ) ) );
		filter.setSearchType( ivFilterSearchTypeProperty.getValue( ) );
		filter.setUseRegExpr( ivFilterUseRegExprProperty.get( ) );
		filter.setLowerDate( ivFilterLowerDateProperty.getValue( ) );
		filter.setLowerTime( ivFilterLowerTimeProperty.get( ) );
		filter.setUpperDate( ivFilterUpperDateProperty.getValue( ) );
		filter.setUpperTime( ivFilterUpperTimeProperty.get( ) );
		filter.setTraceId( ivFilterTraceIdProperty.getValue( ) );

		// If we are using regular expressions, we should check them
		if ( filter.isUseRegExpr( ) ) {
			final PatternService patternService = getService( PatternService.class );

			if ( !( patternService.isValidPattern( filter.getHost( ) ) || filter.getHost( ) == null ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getHost( ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getClazz( ) ) || filter.getClazz( ) == null ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getClazz( ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getMethod( ) ) || filter.getMethod( ) == null ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getMethod( ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getException( ) ) || filter.getException( ) == null ) ) {
				throw new BusinessException( String.format( getLocalizedString( "errorMessageRegExpr" ), filter.getException( ) ) );
			}
		}

		return filter;
	}

	private MethodCall getSelected( ) {
		return ivSelectedMethodCallProperty.get( );
	}

	private CSVData savePresentationAsCSV( ) {
		final List<TableColumn<MethodCall, ?>> visibleColumns = ivVisibleColumnsProperty;

		final int columnSize = visibleColumns.size( );
		final int itemsSize = ivMethodsProperty.get( ).size( );

		final String[] headers = new String[columnSize];
		for ( int columnIndex = 0; columnIndex < columnSize; columnIndex++ ) {
			headers[columnIndex] = visibleColumns.get( columnIndex ).getText( );
		}

		final String[][] values = new String[columnSize][itemsSize];
		for ( int rowIndex = 0; rowIndex < itemsSize; rowIndex++ ) {
			for ( int columnIndex = 0; columnIndex < columnSize; columnIndex++ ) {
				final Object cellData = visibleColumns.get( columnIndex ).getCellData( rowIndex );
				values[columnIndex][rowIndex] = cellData.toString( );
			}
		}

		final CSVData csvData = new CSVData( );
		csvData.setHeader( headers );
		csvData.setValues( values );
		return csvData;
	}

	StringProperty getFilterHostProperty( ) {
		return ivFilterHostProperty;
	}

	StringProperty getFilterClassProperty( ) {
		return ivFilterClassProperty;
	}

	StringProperty getFilterMethodProperty( ) {
		return ivFilterMethodProperty;
	}

	StringProperty getFilterExceptionProperty( ) {
		return ivFilterExceptionProperty;
	}

	ObjectProperty<Long> getFilterTraceIdProperty( ) {
		return ivFilterTraceIdProperty;
	}

	BooleanProperty getFilterUseRegExprProperty( ) {
		return ivFilterUseRegExprProperty;
	}

	ObjectProperty<LocalDate> getFilterLowerDateProperty( ) {
		return ivFilterLowerDateProperty;
	}

	ObjectProperty<LocalTime> getFilterLowerTimeProperty( ) {
		return ivFilterLowerTimeProperty;
	}

	ObjectProperty<LocalDate> getFilterUpperDateProperty( ) {
		return ivFilterUpperDateProperty;
	}

	ObjectProperty<LocalTime> getFilterUpperTimeProperty( ) {
		return ivFilterUpperTimeProperty;
	}

	ObjectProperty<SearchType> getFilterSearchTypeProperty( ) {
		return ivFilterSearchTypeProperty;
	}

	ObjectProperty<ObservableList<MethodCall>> getMethodsProperty( ) {
		return ivMethodsProperty;
	}

	ObjectProperty<MethodCall> getSelectedMethodCallProperty( ) {
		return ivSelectedMethodCallProperty;
	}

	StringProperty getDurationColumnHeaderProperty( ) {
		return ivDurationColumnHeaderProperty;
	}

	StringProperty getDetailsHostProperty( ) {
		return ivDetailsHostProperty;
	}

	StringProperty getDetailsClassProperty( ) {
		return ivDetailsClassProperty;
	}

	StringProperty getDetailsMethodProperty( ) {
		return ivDetailsMethodProperty;
	}

	StringProperty getDetailsExceptionProperty( ) {
		return ivDetailsExceptionProperty;
	}

	StringProperty getDetailsDurationProperty( ) {
		return ivDetailsDurationProperty;
	}

	StringProperty getDetailsTimestampProperty( ) {
		return ivDetailsTimestampProperty;
	}

	StringProperty getDetailsTraceIdProperty( ) {
		return ivDetailsTraceIdProperty;
	}

	StringProperty getStatusLabelProperty( ) {
		return ivStatusLabelProperty;
	}

	List<TableColumn<MethodCall, ?>> getVisibleColumnsProperty( ) {
		return ivVisibleColumnsProperty;
	}

}
