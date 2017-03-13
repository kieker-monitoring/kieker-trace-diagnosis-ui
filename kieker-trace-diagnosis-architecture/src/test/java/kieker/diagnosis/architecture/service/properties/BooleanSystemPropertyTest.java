package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractBooleanSystemProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.BooleanSystemProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class BooleanSystemPropertyTest {

	@Test
	public void deserializationShouldWork( ) {
		final AbstractBooleanSystemProperty property = new BooleanSystemProperty( );

		assertThat( property.deserialize( "false" ), is( Boolean.FALSE ) );
		assertThat( property.deserialize( "true" ), is( Boolean.TRUE ) );
	}

}
