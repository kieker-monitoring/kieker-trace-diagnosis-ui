package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractDoubleApplicationProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.DoubleApplicationProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class DoubleApplicationPropertyTest {

	@Test
	public void serializationShouldWork( ) {
		final AbstractDoubleApplicationProperty property = new DoubleApplicationProperty( );

		assertThat( property.serialize( 42.0 ), is( "42.0" ) );
		assertThat( property.serialize( -10.0 ), is( "-10.0" ) );
	}

	@Test
	public void deserializationShouldWork( ) {
		final AbstractDoubleApplicationProperty property = new DoubleApplicationProperty( );

		assertThat( property.deserialize( "42.0" ), is( Double.valueOf( 42 ) ) );
		assertThat( property.deserialize( "-10." ), is( Double.valueOf( -10 ) ) );
	}

}
