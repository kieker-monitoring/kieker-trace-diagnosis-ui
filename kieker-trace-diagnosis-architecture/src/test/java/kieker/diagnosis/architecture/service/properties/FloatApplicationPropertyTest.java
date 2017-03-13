package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractFloatApplicationProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.FloatApplicationProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class FloatApplicationPropertyTest {

	@Test
	public void serializationShouldWork( ) {
		final AbstractFloatApplicationProperty property = new FloatApplicationProperty( );

		assertThat( property.serialize( 42.0f ), is( "42.0" ) );
		assertThat( property.serialize( -10.0f ), is( "-10.0" ) );
	}

	@Test
	public void deserializationShouldWork( ) {
		final AbstractFloatApplicationProperty property = new FloatApplicationProperty( );

		assertThat( property.deserialize( "42.0" ), is( Float.valueOf( 42f ) ) );
		assertThat( property.deserialize( "-10." ), is( Float.valueOf( -10f ) ) );
	}

}
