package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import kieker.diagnosis.architecture.ArchitectureTestConfiguration;
import kieker.diagnosis.architecture.service.properties.AbstractIntegerSystemProperty;
import kieker.diagnosis.architecture.service.properties.testclasses.IntegerSystemProperty;

/**
 * @author Nils Christian Ehmke
 */
@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ArchitectureTestConfiguration.class )
public final class IntegerSystemPropertyTest {

	@Test
	public void deserializationShouldWork( ) {
		final AbstractIntegerSystemProperty property = new IntegerSystemProperty( );

		assertThat( property.deserialize( "42" ), is( Integer.valueOf( 42 ) ) );
		assertThat( property.deserialize( "-10" ), is( Integer.valueOf( -10 ) ) );
	}

}
