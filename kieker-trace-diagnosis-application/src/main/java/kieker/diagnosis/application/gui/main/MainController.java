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

package kieker.diagnosis.application.gui.main;

import kieker.diagnosis.application.gui.about.AboutDialogController;
import kieker.diagnosis.application.gui.aggregatedcalls.AggregatedCallsController;
import kieker.diagnosis.application.gui.aggregatedtraces.AggregatedTracesController;
import kieker.diagnosis.application.gui.bugreporting.BugReportingDialogController;
import kieker.diagnosis.application.gui.calls.CallsController;
import kieker.diagnosis.application.gui.monitoringstatistics.MonitoringStatisticsController;
import kieker.diagnosis.application.gui.settings.SettingsDialogController;
import kieker.diagnosis.application.gui.traces.TracesController;
import kieker.diagnosis.application.service.data.DataService;
import kieker.diagnosis.application.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.application.service.data.domain.OperationCall;
import kieker.diagnosis.application.service.export.CSVData;
import kieker.diagnosis.application.service.export.CSVDataCollector;
import kieker.diagnosis.application.service.export.ExportService;
import kieker.diagnosis.application.service.properties.LastExportPathProperty;
import kieker.diagnosis.application.service.properties.LastImportPathProperty;
import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.gui.AbstractController;
import kieker.diagnosis.architecture.gui.GuiLoader;
import kieker.diagnosis.architecture.service.properties.PropertiesService;

import java.io.File;
import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Nils Christian Ehmke
 */
@Component
public class MainController extends AbstractController<MainView> {

	@Autowired
	private GuiLoader ivGuiLoader;

	@Autowired
	private PropertiesService ivPropertiesService;

	@Autowired
	private ExportService ivExportService;

	@Autowired
	private DataService ivDataService;

	@Autowired
	private List<AbstractController<?>> ivAllControllers;

	private Optional<Button> ivDisabledButton = Optional.empty( );
	private int ivFavoritesAvailable;

	@Override
	protected void doInitialize( final boolean aFirstInitialization, final Optional<?> aParameter ) {
		// Nothing to initialize
	}

	@Override
	public void doRefresh( ) {
		// Nothing to refresh
	}

	public void performOpenMonitoringLog( ) {
		final String lastImportPath = ivPropertiesService.loadApplicationProperty( LastImportPathProperty.class );
		final File initialDirectory = new File( lastImportPath );

		final DirectoryChooser directoryChooser = new DirectoryChooser( );
		if ( initialDirectory.exists( ) ) {
			directoryChooser.setInitialDirectory( initialDirectory );
		}
		final File selectedDirectory = directoryChooser.showDialog( getView( ).getWindow( ) );
		if ( selectedDirectory != null ) {
			ivPropertiesService.saveApplicationProperty( LastImportPathProperty.class, selectedDirectory.getAbsolutePath( ) );
			ivDataService.loadMonitoringLogFromFS( selectedDirectory );
		}
	}

	public void performClose( ) {
		getView( ).getWindow( ).hide( );
	}

	public void performShowSettings( ) {
		final long oldPropertiesVersion = ivPropertiesService.getVersion( );

		ivGuiLoader.loadAsDialog( SettingsDialogController.class, getView( ).getWindow( ) );

		final long newPropertiesVersion = ivPropertiesService.getVersion( );

		if ( oldPropertiesVersion != newPropertiesVersion ) {
			refreshAllController( );
		}
	}

	private void refreshAllController( ) {
		for ( final AbstractController<?> controller : ivAllControllers ) {
			controller.doRefresh( );
		}
	}

	public void performShowBugReporting( ) {
		ivGuiLoader.loadAsDialog( BugReportingDialogController.class, getView( ).getWindow( ) );
	}

	public void performShowAbout( ) {
		ivGuiLoader.loadAsDialog( AboutDialogController.class, getView( ).getWindow( ) );
	}

	public void performShowTraces( ) {
		toggleDisabledButton( getView( ).getTraces( ) );
		ivGuiLoader.loadInPane( TracesController.class, getView( ).getContent( ) );
	}

	public void jumpToTrace( final OperationCall aCall ) {
		toggleDisabledButton( getView( ).getTraces( ) );
		ivGuiLoader.loadInPane( TracesController.class, getView( ).getContent( ), Optional.of( aCall ) );
	}

	public void performShowAggregatedTraces( ) {
		toggleDisabledButton( getView( ).getAggregatedtraces( ) );
		ivGuiLoader.loadInPane( AggregatedTracesController.class, getView( ).getContent( ) );
	}

	public void performShowCalls( ) {
		toggleDisabledButton( getView( ).getCalls( ) );
		ivGuiLoader.loadInPane( CallsController.class, getView( ).getContent( ) );
	}

	public void jumpToCalls( final AggregatedOperationCall aCall ) {
		toggleDisabledButton( getView( ).getCalls( ) );
		ivGuiLoader.loadInPane( CallsController.class, getView( ).getContent( ), Optional.of( aCall ) );
	}

	public void performShowAggregatedCalls( ) {
		toggleDisabledButton( getView( ).getAggregatedcalls( ) );
		ivGuiLoader.loadInPane( AggregatedCallsController.class, getView( ).getContent( ) );
	}

	public void performShowStatistics( ) {
		toggleDisabledButton( getView( ).getStatistics( ) );
		ivGuiLoader.loadInPane( MonitoringStatisticsController.class, getView( ).getContent( ) );
	}

	private void toggleDisabledButton( final Button aDisabledButton ) {
		ivDisabledButton.ifPresent( b -> b.setDisable( false ) );
		ivDisabledButton = Optional.of( aDisabledButton );
		aDisabledButton.setDisable( true );
	}

	public <T extends AbstractController<?>> void saveAsFavorite( final Object aFilterContent, final Class<T> aFilterLoader ) throws BusinessException {
		final TextInputDialog textInputDialog = new TextInputDialog( );
		textInputDialog.setTitle( getResourceBundle( ).getString( "newFilterFavorite" ) );
		textInputDialog.setHeaderText( getResourceBundle( ).getString( "newFilterFavoriteName" ) );
		final Optional<String> result = textInputDialog.showAndWait( );

		if ( result.isPresent( ) ) {
			final String text = result.get( );

			if ( !StringUtils.hasText( text ) ) {
				throw new BusinessException( getResourceBundle( ).getString( "errorEmptyFilterName" ) );
			}

			// If necessary, add a horizontal line first
			if ( ivFavoritesAvailable == 0 ) {
				final Separator separator = new Separator( );
				VBox.setMargin( separator, new Insets( 10, 0, 0, 10 ) );
				getView( ).getLeftButtonBox( ).getChildren( ).add( separator );
			}

			// Now we can add the button for the favorite and the button to remove it
			final HBox hbox = new HBox( );
			hbox.setSpacing( 10 );
			VBox.setMargin( hbox, new Insets( 10, 0, 0, 10 ) );

			final Button favoriteButton = new Button( text );
			final Button disabledButton = ivDisabledButton.get( );
			favoriteButton.setPrefWidth( 155 );
			favoriteButton.setOnAction( event -> {
				loadFavorite( aFilterContent, aFilterLoader, disabledButton );
			} );
			hbox.getChildren( ).add( favoriteButton );

			final Button removeButton = new Button( "-" );
			removeButton.setPrefWidth( 20 );
			removeButton.setOnAction( event -> {
				final ObservableList<Node> children = getView( ).getLeftButtonBox( ).getChildren( );
				children.remove( hbox );
				ivFavoritesAvailable--;

				if ( ivFavoritesAvailable == 0 ) {
					children.stream( ).filter( node -> node instanceof Separator ).findFirst( ).ifPresent( children::remove );
				}
			} );
			hbox.getChildren( ).add( removeButton );

			ivFavoritesAvailable++;
			getView( ).getLeftButtonBox( ).getChildren( ).add( hbox );
		}
	}

	private <T extends AbstractController<?>> void loadFavorite( final Object aFilterContent, final Class<T> aFilterLoader, final Button aDisabledButton ) {
		toggleDisabledButton( aDisabledButton );
		ivGuiLoader.loadInPane( aFilterLoader, getView( ).getContent( ), Optional.of( aFilterContent ) );
	}

	public void exportToCSV( final CSVDataCollector aCSVDataCollector ) {
		final String lastExportPath = ivPropertiesService.loadApplicationProperty( LastExportPathProperty.class );

		final File initialDirectory = new File( lastExportPath );

		final FileChooser fileChooser = new FileChooser( );
		if ( initialDirectory.exists( ) ) {
			fileChooser.setInitialDirectory( initialDirectory );
		}

		final File selectedFile = fileChooser.showSaveDialog( getView( ).getWindow( ) );
		if ( selectedFile != null ) {
			ivPropertiesService.saveApplicationProperty( LastExportPathProperty.class, selectedFile.getParent( ) );

			final CSVData data = aCSVDataCollector.collectData( );
			ivExportService.exportToCSV( data, selectedFile );
		}
	}

}
