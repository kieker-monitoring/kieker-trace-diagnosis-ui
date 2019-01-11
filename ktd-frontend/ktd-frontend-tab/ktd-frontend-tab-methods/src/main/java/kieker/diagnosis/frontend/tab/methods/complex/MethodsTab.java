package kieker.diagnosis.frontend.tab.methods.complex;

import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kieker.diagnosis.backend.base.exception.BusinessException;
import kieker.diagnosis.backend.base.exception.BusinessRuntimeException;
import kieker.diagnosis.backend.base.service.ServiceFactory;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.export.CSVData;
import kieker.diagnosis.backend.pattern.PatternService;
import kieker.diagnosis.backend.search.methods.MethodsFilter;
import kieker.diagnosis.backend.search.methods.MethodsService;
import kieker.diagnosis.backend.settings.SettingsService;
import kieker.diagnosis.frontend.base.mixin.ErrorHandlerMixin;
import kieker.diagnosis.frontend.tab.methods.composite.MethodDetailsPane;
import kieker.diagnosis.frontend.tab.methods.composite.MethodFilterPane;
import kieker.diagnosis.frontend.tab.methods.composite.MethodStatusBar;
import kieker.diagnosis.frontend.tab.methods.composite.MethodsTableView;

public final class MethodsTab extends Tab implements ErrorHandlerMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( MethodsTab.class.getName( ) );

	private final MethodFilterPane filterPane = new MethodFilterPane( );
	private final MethodsTableView methodsTableView = new MethodsTableView( );
	private final MethodDetailsPane detailsPane = new MethodDetailsPane( );
	private final MethodStatusBar statusBar = new MethodStatusBar( );

	private Consumer<CSVData> onExportToCSV;
	private Consumer<MethodCall> onJumpToTrace;
	private Consumer<MethodsFilter> onSaveAsFavorite;

	private List<MethodCall> methodsForRefresh;
	private int totalMethodsForRefresh;
	private String durationSuffixForRefresh;

	public MethodsTab( ) {
		final VBox vbox = new VBox( );
		setContent( vbox );

		configureFilterPane( );
		vbox.getChildren( ).add( filterPane );

		configureMethodsTableView( );
		vbox.getChildren( ).add( methodsTableView );

		configureDetailsPane( );
		vbox.getChildren( ).add( detailsPane );

		configureStatusBar( );
		vbox.getChildren( ).add( statusBar );

		performInitialize( );
	}

	private void configureFilterPane( ) {
		filterPane.setOnSearch( e -> executeAction( this::performSearch ) );
		filterPane.setOnSaveAsFavorite( e -> executeAction( this::performSaveAsFavorite ) );
	}

	private void configureMethodsTableView( ) {
		methodsTableView.setId( "tabMethodsTable" );
		methodsTableView.addSelectionChangeListener( ( observable, oldValue, newValue ) -> detailsPane.setValue( newValue ) );

		VBox.setVgrow( methodsTableView, Priority.ALWAYS );
	}

	private void configureDetailsPane( ) {
		detailsPane.setOnJumpToTrace( e -> executeAction( this::performJumpToTrace ) );
	}

	private void configureStatusBar( ) {
		statusBar.setOnExportToCsv( e -> executeAction( this::performExportToCSV ) );

		VBox.setMargin( statusBar, new Insets( 5 ) );
	}

	private void performInitialize( ) {
		detailsPane.setValue( null );
		statusBar.setValue( 0, 0 );
		filterPane.setValue( new MethodsFilter( ) );
	}

	private void performSearch( ) {
		final MethodsFilter filter = filterPane.getValue( );
		checkFilter( filter );

		final MethodsService methodsService = ServiceFactory.getService( MethodsService.class );
		final List<MethodCall> methods = methodsService.searchMethods( filter );
		final int totalMethods = methodsService.countMethods( );

		methodsTableView.setItems( FXCollections.observableList( methods ) );
		methodsTableView.refresh( );

		statusBar.setValue( methods.size( ), totalMethods );
	}

	private void performSaveAsFavorite( ) {
		if ( onSaveAsFavorite != null ) {
			final MethodsFilter filter = filterPane.getValue( );
			checkFilter( filter );
			onSaveAsFavorite.accept( filter );
		}
	}

	private void checkFilter( final MethodsFilter filter ) {
		// If we are using regular expressions, we should check them
		if ( filter.isUseRegExpr( ) ) {
			final PatternService patternService = ServiceFactory.getService( PatternService.class );

			if ( !( patternService.isValidPattern( filter.getHost( ) ) || filter.getHost( ) == null ) ) {
				throw new BusinessRuntimeException( new BusinessException( String.format( RESOURCE_BUNDLE.getString( "errorMessageRegExpr" ), filter.getHost( ) ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getClazz( ) ) || filter.getClazz( ) == null ) ) {
				throw new BusinessRuntimeException( new BusinessException( String.format( RESOURCE_BUNDLE.getString( "errorMessageRegExpr" ), filter.getClazz( ) ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getMethod( ) ) || filter.getMethod( ) == null ) ) {
				throw new BusinessRuntimeException( new BusinessException( String.format( RESOURCE_BUNDLE.getString( "errorMessageRegExpr" ), filter.getMethod( ) ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getException( ) ) || filter.getException( ) == null ) ) {
				throw new BusinessRuntimeException( new BusinessException( String.format( RESOURCE_BUNDLE.getString( "errorMessageRegExpr" ), filter.getException( ) ) ) );
			}
		}
	}

	public void setOnSaveAsFavorite( final Consumer<MethodsFilter> consumer ) {
		onSaveAsFavorite = consumer;
	}

	private void performJumpToTrace( ) {
		if ( onJumpToTrace != null ) {
			final MethodCall methodCall = methodsTableView.getSelectionModel( ).getSelectedItem( );
			if ( methodCall != null ) {
				onJumpToTrace.accept( methodCall );
			}
		}
	}

	public void setOnJumpToTrace( final Consumer<MethodCall> consumer ) {
		onJumpToTrace = consumer;
	}

	private void performExportToCSV( ) {
		if ( onExportToCSV != null ) {
			final CSVData csvData = methodsTableView.getValueAsCsv( );
			onExportToCSV.accept( csvData );
		}
	}

	public void setOnExportToCSV( final Consumer<CSVData> consumer ) {
		onExportToCSV = consumer;
	}

	public void setFilterValue( final MethodsFilter value ) {
		filterPane.setValue( value );
		performSearch( );
	}

	public void setFilterValue( final AggregatedMethodCall value ) {
		filterPane.setValue( value );
		performSearch( );
	}

	public BooleanProperty defaultButtonProperty( ) {
		return filterPane.defaultButtonProperty( );
	}

	public void prepareRefresh( ) {
		final MethodsService methodsService = ServiceFactory.getService( MethodsService.class );
		methodsForRefresh = methodsService.searchMethods( new MethodsFilter( ) );
		totalMethodsForRefresh = methodsService.countMethods( );

		final SettingsService settingsService = ServiceFactory.getService( SettingsService.class );
		durationSuffixForRefresh = settingsService.getCurrentDurationSuffix( );
	}

	public void performRefresh( ) {
		filterPane.setValue( new MethodsFilter( ) );
		methodsTableView.setItems( FXCollections.observableList( methodsForRefresh ) );
		statusBar.setValue( methodsForRefresh.size( ), totalMethodsForRefresh );
		methodsTableView.setDurationSuffix( durationSuffixForRefresh );
	}

}
