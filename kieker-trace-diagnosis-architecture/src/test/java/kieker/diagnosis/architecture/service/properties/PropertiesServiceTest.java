package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.LogoProperty;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.architecture.service.properties.SplashscreenProperty;
import kieker.diagnosis.architecture.service.properties.TitleProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.ApplicationProperty1;
import kieker.diagnosis.architecture.service.properties.testclasses.ApplicationProperty2;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class PropertiesServiceTest {

	@Autowired
	private PropertiesService ivPropertiesService;

	@Test
	public void systemPropertiesShouldBeLoadedCorrectly( ) {
		assertThat( ivPropertiesService.loadSystemProperty( LogoProperty.class ), is( "test-logo" ) );
		assertThat( ivPropertiesService.loadSystemProperty( SplashscreenProperty.class ), is( "test-splashscreen" ) );
		assertThat( ivPropertiesService.loadSystemProperty( TitleProperty.class ), is( "test-title" ) );
	}

	@Test
	public void defaultValueOfApplicationPropertiesShouldBeLoaded( ) {
		assertThat( ivPropertiesService.loadApplicationProperty( ApplicationProperty1.class ), is( Boolean.TRUE ) );
	}

	@Test
	public void applicationPropertiesShouldBeSavedAndLoadedCorrectly( ) {
		ivPropertiesService.saveApplicationProperty( ApplicationProperty2.class, Boolean.FALSE );
		assertThat( ivPropertiesService.loadApplicationProperty( ApplicationProperty2.class ), is( Boolean.FALSE ) );

		ivPropertiesService.saveApplicationProperty( ApplicationProperty2.class, Boolean.TRUE );
		assertThat( ivPropertiesService.loadApplicationProperty( ApplicationProperty2.class ), is( Boolean.TRUE ) );
	}

	@Test
	public void propertiesVersionShouldBeIncrementedAfterSave( ) {
		final long preVersion = ivPropertiesService.getVersion( );
		ivPropertiesService.saveApplicationProperty( ApplicationProperty2.class, Boolean.FALSE );
		final long postVersion = ivPropertiesService.getVersion( );

		assertThat( postVersion, is( preVersion + 1 ) );
	}

}
