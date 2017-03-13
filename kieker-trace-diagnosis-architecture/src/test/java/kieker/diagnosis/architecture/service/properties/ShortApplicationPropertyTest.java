package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractShortApplicationProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.ShortApplicationProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class ShortApplicationPropertyTest {

	@Test
	public void serializationShouldWork( ) {
		final AbstractShortApplicationProperty property = new ShortApplicationProperty( );

		assertThat( property.serialize( (short) 42 ), is( "42" ) );
		assertThat( property.serialize( (short) -10 ), is( "-10" ) );
	}

	@Test
	public void deserializationShouldWork( ) {
		final AbstractShortApplicationProperty property = new ShortApplicationProperty( );

		assertThat( property.deserialize( "42" ), is( Short.valueOf( (short) 42 ) ) );
		assertThat( property.deserialize( "-10" ), is( Short.valueOf( (short) -10 ) ) );
	}

}
