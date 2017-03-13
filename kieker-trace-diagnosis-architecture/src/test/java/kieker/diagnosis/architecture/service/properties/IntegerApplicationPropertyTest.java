package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractIntegerApplicationProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.IntegerApplicationProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class IntegerApplicationPropertyTest {

	@Test
	public void serializationShouldWork( ) {
		final AbstractIntegerApplicationProperty property = new IntegerApplicationProperty( );

		assertThat( property.serialize( 42 ), is( "42" ) );
		assertThat( property.serialize( -10 ), is( "-10" ) );
	}

	@Test
	public void deserializationShouldWork( ) {
		final AbstractIntegerApplicationProperty property = new IntegerApplicationProperty( );

		assertThat( property.deserialize( "42" ), is( Integer.valueOf( 42 ) ) );
		assertThat( property.deserialize( "-10" ), is( Integer.valueOf( -10 ) ) );
	}

}
