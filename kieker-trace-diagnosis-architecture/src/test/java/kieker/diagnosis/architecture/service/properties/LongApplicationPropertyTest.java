package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractLongApplicationProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.LongApplicationProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class LongApplicationPropertyTest {

	@Test
	public void serializationShouldWork( ) {
		final AbstractLongApplicationProperty property = new LongApplicationProperty( );

		assertThat( property.serialize( 42L ), is( "42" ) );
		assertThat( property.serialize( -10L ), is( "-10" ) );
	}

	@Test
	public void deserializationShouldWork( ) {
		final AbstractLongApplicationProperty property = new LongApplicationProperty( );

		assertThat( property.deserialize( "42" ), is( Long.valueOf( 42L ) ) );
		assertThat( property.deserialize( "-10" ), is( Long.valueOf( -10L ) ) );
	}

}
