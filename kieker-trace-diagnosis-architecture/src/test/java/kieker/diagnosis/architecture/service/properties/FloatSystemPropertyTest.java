package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractFloatSystemProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.FloatSystemProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class FloatSystemPropertyTest {

	@Test
	public void deserializationShouldWork( ) {
		final AbstractFloatSystemProperty property = new FloatSystemProperty( );

		assertThat( property.deserialize( "42.0" ), is( Float.valueOf( 42f ) ) );
		assertThat( property.deserialize( "-10." ), is( Float.valueOf( -10f ) ) );
	}

}
