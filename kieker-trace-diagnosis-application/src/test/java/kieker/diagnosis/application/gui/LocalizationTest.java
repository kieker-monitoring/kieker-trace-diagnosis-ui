package kieker.diagnosis.application.gui;

import static org.hamcrest.collection.IsIn.isIn;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LocalizationTest {

	@Test
	public void aboutDialogViewShouldBeFullyLocalized( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldBeFullyLocalized( "kieker.diagnosis.application.gui.about.AboutDialogView" );
	}

	@Test
	public void aggregatedCallsViewShouldBeFullyLocalized( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldBeFullyLocalized( "kieker.diagnosis.application.gui.aggregatedcalls.AggregatedCallsView" );
	}

	@Test
	public void aggregatedTracesViewShouldBeFullyLocalized( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldBeFullyLocalized( "kieker.diagnosis.application.gui.aggregatedtraces.AggregatedTracesView" );
	}

	@Test
	public void bugReportingDialogViewShouldBeFullyLocalized( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldBeFullyLocalized( "kieker.diagnosis.application.gui.bugreporting.BugReportingDialogView" );
	}

	@Test
	public void callsViewShouldBeFullyLocalized( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldBeFullyLocalized( "kieker.diagnosis.application.gui.calls.CallsView" );
	}

	@Test
	public void mainViewShouldBeFullyLocalized( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldBeFullyLocalized( "kieker.diagnosis.application.gui.main.MainView" );
	}

	@Test
	public void monitoringStatisticsViewShouldBeFullyLocalized( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldBeFullyLocalized( "kieker.diagnosis.application.gui.monitoringstatistics.MonitoringStatisticsView" );
	}

	@Test
	public void settingsDialogViewShouldBeFullyLocalized( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldBeFullyLocalized( "kieker.diagnosis.application.gui.settings.SettingsDialogView" );
	}

	@Test
	public void tracesViewShouldBeFullyLocalized( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldBeFullyLocalized( "kieker.diagnosis.application.gui.traces.TracesView" );
	}

	@Test
	public void aboutDialogViewShouldContainNoUnnecessaryLocalization( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldContainNoUnnecessaryLocalization( "kieker.diagnosis.application.gui.about.AboutDialogView" );
	}

	@Test
	public void aggregatedCallsViewShouldContainNoUnnecessaryLocalization( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldContainNoUnnecessaryLocalization( "kieker.diagnosis.application.gui.aggregatedcalls.AggregatedCallsView" );
	}

	@Test
	public void aggregatedTracesViewShouldContainNoUnnecessaryLocalization( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldContainNoUnnecessaryLocalization( "kieker.diagnosis.application.gui.aggregatedtraces.AggregatedTracesView" );
	}

	@Test
	public void bugReportingDialogViewShouldContainNoUnnecessaryLocalization( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldContainNoUnnecessaryLocalization( "kieker.diagnosis.application.gui.bugreporting.BugReportingDialogView" );
	}

	@Test
	public void callsViewShouldContainNoUnnecessaryLocalization( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldContainNoUnnecessaryLocalization( "kieker.diagnosis.application.gui.calls.CallsView" );
	}

	@Test
	public void mainViewShouldContainNoUnnecessaryLocalization( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldContainNoUnnecessaryLocalization( "kieker.diagnosis.application.gui.main.MainView" );
	}

	@Test
	public void monitoringStatisticsViewShouldContainNoUnnecessaryLocalization( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldContainNoUnnecessaryLocalization( "kieker.diagnosis.application.gui.monitoringstatistics.MonitoringStatisticsView" );
	}

	@Test
	public void settingsDialogViewShouldContainNoUnnecessaryLocalization( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldContainNoUnnecessaryLocalization( "kieker.diagnosis.application.gui.settings.SettingsDialogView" );
	}

	@Test
	public void tracesViewShouldContainNoUnnecessaryLocalization( ) throws ParserConfigurationException, SAXException, IOException {
		viewShouldContainNoUnnecessaryLocalization( "kieker.diagnosis.application.gui.traces.TracesView" );
	}

	private void viewShouldBeFullyLocalized( final String baseName ) throws ParserConfigurationException, SAXException, IOException {
		final ResourceBundle germanResourceBundle = ResourceBundle.getBundle( baseName, Locale.GERMAN );
		final ResourceBundle rootResourceBundle = ResourceBundle.getBundle( baseName, Locale.ROOT );

		final String viewFileName = ResourceUtils.CLASSPATH_URL_PREFIX + baseName.replace( '.', '/' ) + ".fxml";
		final File viewFile = ResourceUtils.getFile( viewFileName );

		final List<String> externalizedStrings = getAllExternalizedStrings( viewFile );

		for ( final String string : externalizedStrings ) {
			assertThat( germanResourceBundle.getString( string ), not( isEmptyOrNullString( ) ) );
			assertThat( rootResourceBundle.getString( string ), not( isEmptyOrNullString( ) ) );
		}
	}

	private void viewShouldContainNoUnnecessaryLocalization( final String baseName ) throws SAXException, IOException, ParserConfigurationException {
		final List<String> exceptions = Arrays.asList( "notAvailable", "title", "errorEmptyFilterName", "newFilterFavorite", "newFilterFavoriteName" );

		final ResourceBundle germanResourceBundle = ResourceBundle.getBundle( baseName, Locale.GERMAN );
		final ResourceBundle rootResourceBundle = ResourceBundle.getBundle( baseName, Locale.ROOT );

		final String viewFileName = ResourceUtils.CLASSPATH_URL_PREFIX + baseName.replace( '.', '/' ) + ".fxml";
		final File viewFile = ResourceUtils.getFile( viewFileName );

		final List<String> externalizedStrings = getAllExternalizedStrings( viewFile );

		final Enumeration<String> germanKeys = germanResourceBundle.getKeys( );
		while ( germanKeys.hasMoreElements( ) ) {
			final String key = germanKeys.nextElement( );

			if ( !exceptions.contains( key ) ) {
				assertThat( key, isIn( externalizedStrings ) );
			}
		}

		final Enumeration<String> rootKeys = rootResourceBundle.getKeys( );
		while ( rootKeys.hasMoreElements( ) ) {
			final String key = rootKeys.nextElement( );

			if ( !exceptions.contains( key ) ) {
				assertThat( key, isIn( externalizedStrings ) );
			}
		}
	}

	private List<String> getAllExternalizedStrings( final File aViewFile ) throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance( );
		final DocumentBuilder builder = factory.newDocumentBuilder( );

		final Document document = builder.parse( aViewFile );
		final Element element = document.getDocumentElement( );

		return getAllExternalizedStrings( element );
	}

	private List<String> getAllExternalizedStrings( final Node aElement ) {
		final List<String> externalizedStrings = new ArrayList<>( );

		final NamedNodeMap attributes = aElement.getAttributes( );
		if ( attributes != null ) {
			Node textAttribute = attributes.getNamedItem( "text" );
			if ( textAttribute == null ) {
				textAttribute = attributes.getNamedItem( "promptText" );
			}

			if ( textAttribute != null ) {
				final String text = textAttribute.getNodeValue( );
				if ( text.startsWith( "%" ) ) {
					externalizedStrings.add( text.replace( "%", "" ) );
				}
			}
		}

		final NodeList nodes = aElement.getChildNodes( );
		for ( int i = 0; i < nodes.getLength( ); i++ ) {
			externalizedStrings.addAll( getAllExternalizedStrings( nodes.item( i ) ) );
		}

		return externalizedStrings;
	}

}
