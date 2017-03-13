package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractBooleanApplicationProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.BooleanApplicationProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class BooleanApplicationPropertyTest {

	@Test
	public void serializationShouldWork( ) {
		final AbstractBooleanApplicationProperty property = new BooleanApplicationProperty( );

		assertThat( property.serialize( Boolean.FALSE ), is( "false" ) );
		assertThat( property.serialize( Boolean.TRUE ), is( "true" ) );
	}

	@Test
	public void deserializationShouldWork( ) {
		final AbstractBooleanApplicationProperty property = new BooleanApplicationProperty( );

		assertThat( property.deserialize( "false" ), is( Boolean.FALSE ) );
		assertThat( property.deserialize( "true" ), is( Boolean.TRUE ) );
	}

}
